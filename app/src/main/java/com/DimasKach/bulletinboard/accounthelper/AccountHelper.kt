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

class AccountHelper(val activ: MainActivity) {

    private lateinit var signInClient: GoogleSignInClient

    fun singUpWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        sendEmailVerification(task.result?.user!!)
                        activ.uiUpdate(task.result?.user)
                    } else {
                        Toast.makeText(activ,
                            activ.resources.getString(R.string.sing_up_error),
                            Toast.LENGTH_LONG).show()
                        // Log.d("MyLog", "Exception : ${exception.errorCode}" )
                        if (task.exception is FirebaseAuthUserCollisionException) {
                            val exception = task.exception as FirebaseAuthUserCollisionException
                            if (exception.errorCode == FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE) {
//                                Toast.makeText(activ,
//                                    FirebaseAuthConstants.ERROR_EMAIL_ALREADY_IN_USE,
//                                    Toast.LENGTH_LONG).show()
                                linkEmailToG(email, password)
                            }
                        } else if (task.exception is FirebaseAuthInvalidCredentialsException) {
                            val exception =
                                task.exception as FirebaseAuthInvalidCredentialsException


                            if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {

                                Toast.makeText(activ,
                                    FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                        if (task.exception is FirebaseAuthWeakPasswordException) {
                            val exception =
                                task.exception as FirebaseAuthWeakPasswordException

                            if (exception.errorCode == FirebaseAuthConstants.ERROR_WEAK_PASSWORD) {

                                Toast.makeText(activ,
                                    FirebaseAuthConstants.ERROR_WEAK_PASSWORD,
                                    Toast.LENGTH_LONG).show()
                            }
                        }
                    }
                }
        }
    }


    fun singInWithEmail(email: String, password: String) {
        if (email.isNotEmpty() && password.isNotEmpty()) {
            activ.mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    activ.uiUpdate(task.result?.user)

                } else {
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {

                        val exception = task.exception as FirebaseAuthInvalidCredentialsException
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_INVALID_EMAIL) {
                            Toast.makeText(activ,
                                FirebaseAuthConstants.ERROR_INVALID_EMAIL,
                                Toast.LENGTH_LONG).show()
                        } else if (exception.errorCode == FirebaseAuthConstants.ERROR_WRONG_PASSWORD) {
                            Toast.makeText(activ,
                                FirebaseAuthConstants.ERROR_WRONG_PASSWORD,
                                Toast.LENGTH_LONG).show()
                        }
                    } else if (task.exception is FirebaseAuthInvalidUserException) {
                        val exception = task.exception as FirebaseAuthInvalidUserException
                        if (exception.errorCode == FirebaseAuthConstants.ERROR_USER_NOT_FOUND) {
                            Toast.makeText(activ,
                                FirebaseAuthConstants.ERROR_USER_NOT_FOUND,
                                Toast.LENGTH_LONG).show()
                        }
                    }
                    Toast.makeText(activ,
                        activ.resources.getString(R.string.sing_in_error),
                        Toast.LENGTH_LONG).show()
                }
            }

        }
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
        activ.startActivityForResult(intent, GoogleAccConst.GOOGLE_SIGN_IN_REQUEST_CODE)
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