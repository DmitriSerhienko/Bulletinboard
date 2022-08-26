package com.DimasKach.bulletinboard.dialoghelper

import android.app.Activity
import android.app.AlertDialog
import com.DimasKach.bulletinboard.databinding.ProgressDialogLayoutBinding
import com.DimasKach.bulletinboard.databinding.SingDialogBinding

object ProgressDialog {
    fun createProgressDialog(act: Activity) : AlertDialog {
        val builder = AlertDialog.Builder(act)
        val binding = ProgressDialogLayoutBinding.inflate(act.layoutInflater)
        builder.setView(binding.root)
        val dialog = builder.create()
        dialog.setCancelable(false)
        dialog.show()
        return dialog
    }
}