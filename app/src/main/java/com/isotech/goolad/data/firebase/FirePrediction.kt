package com.isotech.goolad.data.firebase

import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isotech.goolad.data.model.Diagnosis
import com.isotech.goolad.data.model.Parameter
import com.isotech.goolad.utils.observesSnapshot
import io.reactivex.BackpressureStrategy
import io.reactivex.Completable
import io.reactivex.Flowable
import io.reactivex.Single
import java.util.*

class FirePrediction {

    companion object {
        val instance = FirePrediction()
    }

    val db = FirebaseFirestore.getInstance()

    fun postParameter(userId: String, parameter: Parameter): Completable {
        return Completable.create { e ->
            db.collection("users")
                .document(userId)
                .collection("prediction")
                .document(parameter.id)
                .set(parameter)
                .addOnSuccessListener { e.onComplete() }
                .addOnFailureListener { e.onError(Throwable(it.message)) }
        }
    }

    fun postPredictionResult(
        userId: String,
        predictionId: String,
        diagnosis: Diagnosis
    ): Completable {
        return Completable.create { e ->
            db.collection("users")
                .document(userId)
                .collection("prediction")
                .document(predictionId)
                .update(
                    mapOf(
                        "diagnosis" to diagnosis.diagnosis
                    )
                )
                .addOnSuccessListener { e.onComplete() }
                .addOnFailureListener { e.onError(Throwable(it.message)) }
        }
    }

    fun getPredictions(userId: String): Flowable<List<Parameter>>? {
        return Flowable.defer {
            db.collection("users")
                .document(userId)
                .collection("prediction")
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .observesSnapshot()
                .map {
                    it.map { snapshot ->
                        val timestamp = snapshot["timestamp"] as Timestamp
                        val date = timestamp.toDate()
                        Parameter(
                            snapshot["id"].toString(),
                            date,
                            snapshot["pregnancies"].toString().toInt(),
                            snapshot["glucose"].toString().toInt(),
                            snapshot["bloodPressure"].toString().toInt(),
                            snapshot["skinThickness"].toString().toInt(),
                            snapshot["insulin"].toString().toInt(),
                            snapshot["bmi"].toString().toDouble(),
                            snapshot["diabetesPedigreeFunction"].toString().toDouble(),
                            snapshot["age"].toString().toInt(),
                            snapshot["diagnosis"].toString()
                        )

                    }
                }
                .toFlowable(BackpressureStrategy.DROP)
        }

    }

    fun getPredictionById(userId: String, predictionId: String): Single<Parameter> {
        return Single.create { e ->
            db.collection("users")
                .document(userId)
                .collection("prediction")
                .document(predictionId)
                .get()
                .addOnSuccessListener { snapshot ->
                    val timestamp = snapshot["timestamp"] as Timestamp
                    val date = timestamp.toDate()
                    val parameter = Parameter(
                        snapshot["id"].toString(),
                        date,
                        snapshot["pregnancies"].toString().toInt(),
                        snapshot["glucose"].toString().toInt(),
                        snapshot["bloodPressure"].toString().toInt(),
                        snapshot["skinThickness"].toString().toInt(),
                        snapshot["insulin"].toString().toInt(),
                        snapshot["bmi"].toString().toDouble(),
                        snapshot["diabetesPedigreeFunction"].toString().toDouble(),
                        snapshot["age"].toString().toInt(),
                        snapshot["diagnosis"].toString()
                    )
                    e.onSuccess(parameter)
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }
}