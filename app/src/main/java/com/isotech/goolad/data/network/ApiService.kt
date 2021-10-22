package com.isotech.goolad.data.network

import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.data.model.Diagnosis
import io.reactivex.Observable
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiService {

    @POST("/")
    fun predict(@Body parameter: Parameter): Observable<Diagnosis>
}

val api: ApiService by lazy {
    Retrofit.Builder()
        .baseUrl("https://goolad-app.herokuapp.com")
        .addConverterFactory(GsonConverterFactory.create())
        .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
        .build()
        .create(ApiService::class.java)
}