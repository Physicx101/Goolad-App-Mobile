package com.isotech.goolad.ui.home

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.isotech.goolad.data.firebase.FireAuth
import com.isotech.goolad.data.firebase.FirePrediction
import com.isotech.goolad.data.model.Diagnosis
import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.data.repository.ApiRespository
import com.isotech.goolad.ui.common.BaseViewModel
import com.isotech.goolad.utils.toLiveData
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class HomeViewModel : BaseViewModel() {

    private val auth = FireAuth.instance
    private val pred = FirePrediction.instance
    private var repo = ApiRespository()
    val state = MutableLiveData<HomeState>()
    val loading = MutableLiveData<Boolean>()
    val sheetLoading = MutableLiveData<Boolean>()


    init {
        state.value = HomeState()
        loading.value = false
        sheetLoading.value = false
    }

    fun getFullName(userId: String) {
        loading.value = true
        compositeDisposable.add(
            auth.getFullName(userId)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false
                    state.value = state.value!!.copy(fullName = it)
                }, {
                    loading.value = false
                    Log.e("fullName", it.message.toString())
                })
        )
    }

    fun postParameter(userId: String, parameter: Parameter) {
        compositeDisposable.add(
            pred.postParameter(userId, parameter)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false
                }, {
                    Log.e("Post Parameter", it.message.toString())
                    loading.value = false
                })
        )
    }

    fun postPredictionResult(userId: String, predictionId: String, diagnosis: Diagnosis) {
        loading.value = true
        compositeDisposable.add(
            pred.postPredictionResult(userId, predictionId, diagnosis)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loading.value = false
                }, {
                    Log.e("Post Result", it.message.toString())
                    loading.value = false
                })
        )
    }

    fun predict(parameter: Parameter) {
        compositeDisposable.add(
            repo.predict(parameter)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sheetLoading.value = false
                    state.value = state.value!!.copy(diagnosis = it)
                }, {
                    Log.e("Prediction", it.message.toString())
                    sheetLoading.value = false
                })
        )
    }


    fun getPredictions() {
        pred.getPredictions(auth.getCurrentUser()!!.uid)?.let {
            state.value = state.value!!.copy(predictions = it.toLiveData())
        }
    }

    fun getPredictionById(userId: String, predictionId: String) {
        sheetLoading.value = true
        compositeDisposable.add(
            pred.getPredictionById(userId, predictionId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    sheetLoading.value = false
                    state.value = state.value!!.copy(prediction = it)
                }, {
                    sheetLoading.value = false
                    Log.e("Get Prediction", it.message.toString())
                })
        )
    }
}


data class HomeState(
    var fullName: String = "",
    var diagnosis: Diagnosis = Diagnosis(""),
    var error: String = "",
    var predictions: LiveData<List<Parameter>>? = null,
    var prediction: Parameter? = null
)