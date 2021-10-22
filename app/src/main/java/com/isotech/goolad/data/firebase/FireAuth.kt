package com.isotech.goolad.data.firebase

import android.util.Log
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.isotech.goolad.data.model.User
import io.reactivex.Completable
import io.reactivex.Single

class FireAuth {

    companion object {
        val instance = FireAuth()
    }

    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private var userModel: User? = null
    var fullName: String = ""
    var userRole: String = ""

    fun getCurrentUser(): FirebaseUser? = auth.currentUser

    fun getFullName(userId: String): Single<String> {
        return Single.create { e ->
            db.collection("users")
                .document(userId)
                .get()
                .addOnSuccessListener {
                    val name = it["name"].toString()
                    e.onSuccess(name)
                    fullName = name
                }
                .addOnFailureListener {
                    e.onError(Throwable(it.message))
                }
        }
    }


    fun createUserWithEmailPass(userModel: User, password: String): Completable {
        return Completable.create { e ->
            auth.createUserWithEmailAndPassword(userModel.email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        e.onComplete()
                        val user = HashMap<String, Any>()
                        user["id"] = auth.currentUser!!.uid
                        user["name"] = userModel.name
                        user["email"] = userModel.email
                        user["phoneNum"] = userModel.phoneNum
                        db.collection("users")
                            .document(auth.currentUser!!.uid)
                            .set(user)
                            .addOnSuccessListener {
                                e.onComplete()
                                this.userModel = userModel
                            }
                            .addOnFailureListener { e.onError(Throwable(it.message)) }
                    } else {
                        Log.e("register", task.exception.toString())
                        e.onError(Throwable("Register Failed: ${task.exception}"))
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
        }
    }

    fun signInWithEmailPass(email: String, password: String): Completable {
        return Completable.create { e ->
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) e.onComplete()
                    else {
                        Log.e("login", task.exception.toString())
                        e.onError(Throwable("Sign In Failed: ${task.exception}"))
                    }
                }
                .addOnFailureListener { it.printStackTrace() }
        }
    }


}