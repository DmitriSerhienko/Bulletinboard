package com.DimasKach.bulletinboard.accounthelper

import android.util.Log
import android.widget.Toast
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.constants.FirebaseAuthConstants
import com.DimasKach.bulletinboard.dialoghelper.GoogleAccConst
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.*
import java.lang.Exception

class AccountHelper(val activ: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    fun singUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activ.mAuth.createUserWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                singUpWithEmailSuccessful(task.result.user!!)
                            } else {
                                singUpWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }
    private fun singUpWithEmailSuccessful(user: FirebaseUser){
        sendEmailVerification(user)
        activ.uiUpdate(user)
    }
    private fun singUpWithEmailException(e: Exception, email: String, password: String ){

        if (e is FirebaseAuthUserCollisionException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
                linkEmailToG(email, password)
            }
        } else if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(activ, FirebaseAuthConstants.ERROR_INVALID_EMAIL, Toast.LENGTH_LONG).show()
            }
        }
        if (e is FirebaseAuthWeakPasswordException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {
                Toast.makeText(activ, FirebaseAuthConstants.ERROR_WEAK_PASSWORD, Toast.LENGTH_LONG).show()
            }
        }
    }


    fun singInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activ.mAuth.signInWithEmailAndPassword(email, password)
                        .addOnCompleteListener { task ->
                            if (task.isSuccessful) {
                                activ.uiUpdate(task.result?.user)
                            } else {
                                singInWithEmailException(task.exception!!, email, password)
                            }
                        }
                }
            }
        }
    }

    private fun singInWithEmailException(e: Exception, email: String, password: String ){
        if (e is FirebaseAuthInvalidCredentialsException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                Toast.makeText(activ,
                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                    Toast.LENGTH_LONG).show()
            } else if (e.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                Toast.makeText(activ,
                    FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                    Toast.LENGTH_LONG).show()
            }
        } else if (e is FirebaseAuthInvalidUserException) {
            if (e.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                Toast.makeText(activ,
                    FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                    Toast.LENGTH_LONG).show()
            }
        }
        Toast.makeText(activ,
            activ.resources.getString(R.string.sing_in_error),
            Toast.LENGTH_LONG).show()
    }

    private fun linkEmailToG(email: String, password: String) {
        val credential = EmailAuthProvider.getCredential(email, password)
        if (activ.mAuth.currentUser != null) {
            activ.mAuth.currentUser?.linkWithCredential(credential)?.addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Toast.makeText(activ,
                        activ.resources.getString(R.string.link_done),
                        Toast.LENGTH_LONG).show()
                }
            }
        } else {
            Toast.makeText(activ, activ.resources.getString(R.string.enter_to_g), Toast.LENGTH_LONG)
                .show()
        }
    }

    private fun getSignInClient(): GoogleSignInClient {
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken(activ.getString(R.string.default_web_client_id)).requestEmail().build()
        return GoogleSignIn.getClient(activ, gso)
    }

    fun singInWithGoogle() {
        signInClient = getSignInClient()
        val intent = signInClient.signInIntent
        activ.googleSignInLauncher.launch(intent)
    }

    fun signOutG() {
        getSignInClient().signOut()
    }

    fun signInFirebaseWithGoogle(token: String) {
        val credential = GoogleAuthProvider.getCredential(token, null)
        activ.mAuth.currentUser?.delete()?.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                activ.mAuth.signInWithCredential(credential).addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activ, "Вхід виконано", Toast.LENGTH_LONG).show()
                        activ.uiUpdate(task.result?.user)
                    } else {
                        Log.d("MyLog", "Google Sign In Exception : ${task.exception}")
                    }
                }
            }
        }

    }

    private fun sendEmailVerification(user: FirebaseUser) {
        user.sendEmailVerification().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(activ,
                    activ.resources.getString(R.string.send_verification_done),
                    Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activ,
                    activ.resources.getString(R.string.send_verification_error),
                    Toast.LENGTH_LONG).show()
            }
        }
    }

    fun signInAnonymously(listener: Listener) {
        activ.mAuth.signInAnonymously().addOnCompleteListener { task ->
            if (task.isSuccessful) {
                listener.onComplete()
                Toast.makeText(activ, " Вы вошли как гость", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(activ, " Не удалось войти как гость", Toast.LENGTH_LONG).show()
            }
        }
    }

    interface Listener {
        fun onComplete()
    }


}