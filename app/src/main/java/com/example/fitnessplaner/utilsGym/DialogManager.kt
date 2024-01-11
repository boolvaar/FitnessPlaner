package com.example.fitnessplaner.utilsGym

import android.app.AlertDialog
import android.content.Context
import com.example.fitnessplaner.R

object DialogManager {
    fun showDialog(context:Context, mId:Int, listener: Listener){
        val builder = AlertDialog.Builder(context)
        var dialog: AlertDialog ? = null
        builder.setTitle(R.string.alert)
        builder.setMessage(mId)
        builder.setNegativeButton(R.string.reset) { _,_ ->
            listener.OnClick()
            dialog?.dismiss()
        }
        builder.setPositiveButton(R.string.no){ _,_ ->
            dialog?.dismiss()
        }
        dialog = builder.create()
        dialog.show()
    }

    interface Listener{
        fun OnClick()
    }
}