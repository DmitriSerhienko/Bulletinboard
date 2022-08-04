package com.DimasKach.bulletinboard.dialoghelper

import android.app.AlertDialog
import android.view.View
import android.widget.Toast
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.accounthelper.AccountHelper
import com.DimasKach.bulletinboard.databinding.SingDialogBinding

class DialogHelper(act: MainActivity) {
    private val activ = act
    val accHelper = AccountHelper(activ)

    fun createSingDialog(index: Int) {
        val builder = AlertDialog.Builder(activ)
        val binding = SingDialogBinding.inflate(activ.layoutInflater)
        builder.setView(binding.root)

        setDialogState(index, binding)

        val dialog = builder.create()
        binding.btSingUpIn.setOnClickListener {
            setOnClickSingUpIn(index, binding, dialog)

        }
        binding.btForgetP.setOnClickListener {
            setOnClickResetPassword(binding, dialog)

        }
        binding.btGoogleSignIn.setOnClickListener {
            accHelper.singInWithGoogle()
            dialog.dismiss()
        }

        dialog.show()
    }

    private fun setOnClickResetPassword(binding: SingDialogBinding, dialog: AlertDialog?) {
        if (binding.edSingMail.text.isNotEmpty()) {
            activ.mAuth.sendPasswordResetEmail(binding.edSingMail.text.toString())
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Toast.makeText(activ, R.string.email_reset_pas_was_sent, Toast.LENGTH_LONG)
                            .show()
                    }

                }
            dialog?.dismiss()
        } else {
            binding.tvDialogMessage.visibility = View.VISIBLE
        }
    }

    private fun setOnClickSingUpIn(index: Int, binding: SingDialogBinding, dialog: AlertDialog?) {
        dialog?.dismiss()
        if (index == DialogConst.SING_UP_STATE) {
            accHelper.singUpWithEmail(binding.edSingMail.text.toString(),
                binding.edSingPassword.text.toString())
        } else {
            accHelper.singInWithEmail(binding.edSingMail.text.toString(),
                binding.edSingPassword.text.toString())
        }
    }

    private fun setDialogState(index: Int, binding: SingDialogBinding) {
        if (index == DialogConst.SING_UP_STATE) {
            binding.tvSingTitle.text = activ.resources.getString(R.string.sing_up)
            binding.btSingUpIn.text = activ.resources.getString(R.string.sing_up_action)
        } else {
            binding.tvSingTitle.text = activ.resources.getString(R.string.sing_in)
            binding.btSingUpIn.text = activ.resources.getString(R.string.sing_in_action)
            binding.btForgetP.visibility = View.VISIBLE
        }
    }
}