package com.isotech.goolad.ui.auth

import android.util.Log
import androidx.lifecycle.MutableLiveData
import com.google.firebase.auth.FirebaseUser
import com.isotech.goolad.data.firebase.FireAuth
import com.isotech.goolad.data.model.User
import com.isotech.goolad.ui.common.BaseViewModel
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers

class AuthViewModel : BaseViewModel() {

    private val user = MutableLiveData<FirebaseUser?>()
    private val auth = FireAuth.instance
    val registerLoading = MutableLiveData<Boolean>()
    val loginLoading = MutableLiveData<Boolean>()
    val error = MutableLiveData<String>()


    init {
        user.value = auth.getCurrentUser()
        registerLoading.value = false
        loginLoading.value = false
    }

    fun getCurrentUser() = user


    fun createUserWithEmailPass(userModel: User, password: String) {
        registerLoading.value = true
        compositeDisposable.add(
            auth.createUserWithEmailPass(userModel, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    registerLoading.value = false
                    user.value = auth.getCurrentUser()
                }, { e ->
                    registerLoading.value = false
                    error.value = e.message
                    Log.e("registerVM", e.message.toString())
                })
        )
    }

    fun signInWithEmailPass(email: String, password: String) {
        loginLoading.value = true
        compositeDisposable.add(
            auth.signInWithEmailPass(email, password)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe({
                    loginLoading.value = false
                    user.value = auth.getCurrentUser()
                }, { e ->
                    loginLoading.value = false
                    error.value = e.message
                    Log.e("loginVM", e.message.toString())
                })
        )
    }


}