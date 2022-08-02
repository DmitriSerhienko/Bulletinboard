package com.DimasKach.bulletinboard.dialoghelper

import android.app.AlertDialog
import com.DimasKach.bulletinboard.MainActivity
import com.DimasKach.bulletinboard.R
import com.DimasKach.bulletinboard.accounthelper.AccountHelper
import com.DimasKach.bulletinboard.databinding.SingDialogBinding

class DialogHelper(act: MainActivity) {
    private val activ = act
    private val accHelper = AccountHelper(activ)

    fun createSingDialog(index: Int) {
        val builder = AlertDialog.Builder(activ)
        val binding = SingDialogBinding.inflate(activ.layoutInflater)
        if (index == DialogConst.SING_UP_STATE) {
            binding.tvSingTitle.text = activ.resources.getString(R.string.sing_up)
            binding.btSingUpIn.text = activ.resources.getString(R.string.sing_up_action)
        } else {
            binding.tvSingTitle.text = activ.resources.getString(R.string.sing_in)
            binding.btSingUpIn.text = activ.resources.getString(R.string.sing_in_action)
        }
        binding.btSingUpIn.setOnClickListener {
            if (index == DialogConst.SING_UP_STATE) {
                accHelper.singUpWithEmail(binding.edSingMail.text.toString(),
                    binding.edSingPassword.text.toString())
            } else {

            }

        }
        builder.setView(binding.root)
        builder.show()
    }
}