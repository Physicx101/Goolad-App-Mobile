package com.isotech.goolad.ui.home

import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.content.ContextCompat
import androidx.core.widget.doOnTextChanged
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.firebase.auth.FirebaseAuth
import com.isotech.goolad.R
import com.isotech.goolad.data.firebase.FireAuth
import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.databinding.ActivityHomeBinding
import com.isotech.goolad.databinding.DialogAddParametersBinding
import com.isotech.goolad.databinding.DialogPredictionResultBinding
import com.isotech.goolad.ui.adapter.PredictionResultListAdapter
import com.isotech.goolad.ui.auth.AuthViewModel
import com.isotech.goolad.ui.auth.LoginActivity
import com.isotech.goolad.utils.autoID
import com.isotech.goolad.utils.getCurrentDateTime
import com.isotech.goolad.utils.hide
import com.isotech.goolad.utils.show
import kotlinx.android.synthetic.main.dialog_add_parameters.*
import kotlinx.android.synthetic.main.dialog_prediction_result.*

class HomeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHomeBinding

    private val authViewModel by lazy {
        ViewModelProviders.of(this)[AuthViewModel::class.java]
    }

    private val homeViewModel by lazy {
        ViewModelProviders.of(this)[HomeViewModel::class.java]
    }

    private val loadingDialog: AlertDialog by lazy {
        val dialog = AlertDialog.Builder(this)
            .setView(this.layoutInflater.inflate(R.layout.dialog_loading, null))
            .create()
        dialog.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog
    }

    private val auth = FireAuth.instance
    private lateinit var userId: String
    private var paramId: String = ""
    private var predictionList = emptyList<Parameter>()

    private lateinit var parameter: Parameter

    private val addParameterDialog by lazy {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        with(dialog) {
            val binding = DialogAddParametersBinding.inflate(LayoutInflater.from(context))
            setContentView(binding.root)
            with(binding) {
                inputPregnancies.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputGlucose.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputBloodPressure.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputSkinThickness.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputInsulin.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputBmi.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputPedigree.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
                inputAge.doOnTextChanged { _, _, _, _ ->
                    validate(binding)
                }
            }
        }
        dialog
    }

    private val predictionResultDialog by lazy {
        val dialog = BottomSheetDialog(this, R.style.BottomSheetDialog)
        with(dialog) {
            val binding = DialogPredictionResultBinding.inflate(LayoutInflater.from(context))
            setContentView(binding.root)
        }
        dialog
    }

    private val predictionResultAdapter = PredictionResultListAdapter(onPredictionClick())

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        initViews()
        initObservers()
    }

    private fun initViews() {
        with(binding) {
            homeViewModel.getPredictions()
            userId = auth.getCurrentUser()!!.uid
            homeViewModel.state.observe(this@HomeActivity, {
                it?.let {
                    textNameProfile.text = it.fullName
                }
            })
            fabAdd.setOnClickListener {
                openAddParameterDialog()
            }
            rvPrediction.adapter = predictionResultAdapter
            rvPrediction.layoutManager = LinearLayoutManager(this@HomeActivity)
        }
        binding.imgProfile.setOnClickListener {
            FirebaseAuth.getInstance().signOut()
            Intent(this, LoginActivity::class.java).apply {
                try {
                    finishAffinity()
                } catch (e: Exception) {
                    Log.e("Logout", e.message.toString())
                }
                startActivity(this)
            }
        }
    }

    private fun initObservers() {
        homeViewModel.state.observe(this, {
            it.error.let { error ->
                if (error.isNotEmpty()) {
                    Toast.makeText(this, error, Toast.LENGTH_SHORT).show()
                }
            }
            it.diagnosis.let { diagnosis ->
                if (diagnosis.diagnosis.isNotEmpty()) {
                    parameter.diagnosis = diagnosis.diagnosis
                    homeViewModel.postParameter(userId, parameter)
                    //homeViewModel.postPredictionResult(userId, paramId, diagnosis)
                    addParameterDialog.dismiss()
                    Handler(Looper.getMainLooper()).postDelayed({
                        binding.rvPrediction.findViewHolderForAdapterPosition(0)?.itemView?.performClick()
                    }, 200)

//                    homeViewModel.getPredictionById(userId, parameter.id)
//                    val currentPrediction = predictionList.filter { it.id == paramId }
//                      openPredictionDetail(currentPrediction[0], diagnosis.diagnosis)
                }
            }
            it.predictions?.let { liveData ->
                liveData.observe(this, { predictions ->
                    if (predictions.isNotEmpty()) {
                        predictionList = predictions
                        predictionResultAdapter.submitList(predictions)
                        binding.rvPrediction.show()
                        binding.layoutEmpty.hide()
                    } else {
                        binding.rvPrediction.hide()
                        binding.layoutEmpty.show()
                    }
                })
            }
//            it.prediction?.let { prediction ->
//                openPredictionDetail(prediction, prediction.diagnosis!!)
//            }
        })
        authViewModel.getCurrentUser().observe(this, { firebaseUser ->
            firebaseUser?.let {
                homeViewModel.getFullName(it.uid)
            }
        })
        homeViewModel.loading.observe(this, { isLoading ->
            if (isLoading) {
                binding.shimmerFullName.startShimmer()
            } else {
                binding.shimmerFullName.stopShimmer()
                binding.shimmerFullName.hide()
                binding.textNameProfile.show()
            }
        })
        homeViewModel.sheetLoading.observe(this, { isLoading ->
            if (isLoading) {
                loadingDialog.show()
            } else {
                loadingDialog.hide()
            }
        })
    }

    private fun openAddParameterDialog() {
        with(addParameterDialog) {
            show()
            setOnDismissListener {
                layout_dialog_add_parameters.post {
                    layout_dialog_add_parameters.scrollTo(0, 0)
                }
            }
            btn_add_parameters.setOnClickListener {
                val inputPregnancies = input_pregnancies.text.toString().trim().toInt()
                val inputGlucose = input_glucose.text.toString().trim().toInt()
                val inputBloodPressure = input_blood_pressure.text.toString().trim().toInt()
                val inputSkinThickness = input_skin_thickness.text.toString().trim().toInt()
                val inputInsulin = input_insulin.text.toString().trim().toInt()
                val inputBmi = input_bmi.text.toString().trim().toDouble()
                val inputPedigree = input_pedigree.text.toString().trim().toDouble()
                val inputAge = input_age.text.toString().trim().toInt()
                paramId = autoID()
                parameter = Parameter(
                    paramId,
                    getCurrentDateTime(),
                    inputPregnancies,
                    inputGlucose,
                    inputBloodPressure,
                    inputSkinThickness,
                    inputInsulin,
                    inputBmi,
                    inputPedigree,
                    inputAge
                )
                homeViewModel.sheetLoading.value = true
                homeViewModel.predict(parameter)
            }
        }
    }

    private fun validate(binding: DialogAddParametersBinding) {
        with(binding) {
            val inputPregnancies = inputPregnancies.text.toString().trim()
            val inputGlucose = inputGlucose.text.toString().trim()
            val inputBloodPressure = inputBloodPressure.text.toString().trim()
            val inputSkinThickness = inputSkinThickness.text.toString().trim()
            val inputInsulin = inputInsulin.text.toString().trim()
            val inputBmi = inputBmi.text.toString().trim()
            val inputPedigree = inputPedigree.text.toString().trim()
            val inputAge = inputAge.text.toString().trim()
            btnAddParameters.isEnabled =
                inputPregnancies.isNotEmpty() && inputGlucose.isNotEmpty() && inputBloodPressure.isNotEmpty() && inputSkinThickness.isNotEmpty() && inputInsulin.isNotEmpty() && inputBmi.isNotEmpty() && inputPedigree.isNotEmpty() && inputAge.isNotEmpty()
        }
    }

    private fun openPredictionDetail(parameter: Parameter, diagnosis: String = "") {
        with(predictionResultDialog) {
            show()
            val finalDiagnosis: String = if (parameter.diagnosis!!.isEmpty()) {
                diagnosis
            } else {
                parameter.diagnosis!!
            }
            if (finalDiagnosis.contains("tidak", true)) {
                img_prediction_detail.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_check
                    )
                )
                text_prediction_result_detail.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.green
                    )
                )
            } else {
                img_prediction_detail.setImageDrawable(
                    ContextCompat.getDrawable(
                        context,
                        R.drawable.ic_uncheck
                    )
                )
                text_prediction_result_detail.setTextColor(
                    ContextCompat.getColor(
                        context,
                        R.color.red
                    )
                )
            }
            text_prediction_result_detail.text = finalDiagnosis
            text_prediction_pregnancies_detail.text = parameter.pregnancies.toString() + " kali"
            text_prediction_glucose_detail.text = parameter.glucose.toString() + " mg/dL"
            text_prediction_blood_pressure_detail.text = parameter.bloodPressure.toString() + " mmHg"
            text_prediction_skin_thickness_detail.text = parameter.skinThickness.toString() + " mm"
            text_prediction_insulin_detail.text = parameter.insulin.toString() + " muU/ml"
            text_prediction_bmi_detail.text = parameter.bmi.toString() + " Kg/m²"
            text_prediction_pedigree_detail.text = parameter.diabetesPedigreeFunction.toString()
            text_prediction_age_detail.text = parameter.age.toString() + " tahun"
            setOnDismissListener {
                layout_dialog_prediction_detail.post {
                    layout_dialog_prediction_detail.scrollTo(
                        0,
                        0
                    )
                }
            }
        }
    }

    private fun onPredictionClick(): (Parameter) -> Unit = { parameter ->
        openPredictionDetail(parameter)
    }
}