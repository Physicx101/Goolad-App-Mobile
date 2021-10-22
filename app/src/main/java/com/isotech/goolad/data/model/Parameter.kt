package com.isotech.goolad.data.model

import com.google.gson.annotations.SerializedName
import com.isotech.goolad.utils.getCurrentDateTime
import java.util.*

data class Parameter(
    val id: String = "",
    val timestamp: Date = getCurrentDateTime(),
    @SerializedName("Pregnancies") val pregnancies: Int,
    @SerializedName("Glucose") val glucose: Int,
    @SerializedName("BloodPressure") val bloodPressure: Int,
    @SerializedName("SkinThickness") val skinThickness: Int,
    @SerializedName("Insulin") val insulin: Int,
    @SerializedName("BMI") val bmi: Double,
    @SerializedName("DiabetesPedigreeFunction") val diabetesPedigreeFunction: Double,
    @SerializedName("Age") val age: Int,
    @SerializedName("Diagnosis") val diagnosis: String? = ""
)

//    fun Timestamp.toDate(): Date {
//        val milliseconds = this.seconds * 1000 + this.nanoseconds / 1000000
//        val sdf = SimpleDateFormat("dd MMM yyyy • H:MM", Locale.getDefault())
//        val netDate = Date(milliseconds)
//        return netDate
//    }
