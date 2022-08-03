package com.DimasKach.bulletinboard.accounthelper

import android.widget.Toast
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.R
import com.google.firebase.auth.FirebaseUser

class AccountHelper(act:MainActivity) {
    private val activ = act

    fun singUpWithEmail(email:String, password: String ){
        if(email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener {
                task ->
                if (task.isSuccessful){
                    sendEmailVerification(task.result?.user!!)
                    activ.uiUpdate(task.result?.user)
                } else {
                    Toast.makeText(activ,activ.resources.getString(R.string.sing_up_error), Toast.LENGTH_LONG).show()
                }
            }
        }

    }
    fun singInWithEmail(email:String, password: String ){
        if(email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener {
                    task ->
                if (task.isSuccessful){
                    activ.uiUpdate(task.result?.user)

                } else {
                    Toast.makeText(activ,activ.resources.getString(R.string.sing_in_error), Toast.LENGTH_LONG).show()
                }
            }
        }

    }

    private fun sendEmailVerification(user: FirebaseUser){
        user.sendEmailVerification().addOnCompleteListener { task ->
            if(task.isSuccessful){
                Toast.makeText(activ,activ.resources.getString(R.string.send_verification_done), Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activ,activ.resources.getString(R.string.send_verification_error), Toast.LENGTH_LONG).show()
            }
        }
    }
}