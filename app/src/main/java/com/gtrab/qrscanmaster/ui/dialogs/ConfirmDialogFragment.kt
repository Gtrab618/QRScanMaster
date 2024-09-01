package com.gtrab.qrscanmaster.ui.dialogs
import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.gtrab.qrscanmaster.R

class ConfirmDialogFragment: DialogFragment() {
    interface ConfirmDialogListener{
        fun onDialogResult(result: Boolean)
    }

    companion object{
        fun newInstance(title:String, message:String):ConfirmDialogFragment{
            return ConfirmDialogFragment().apply {
                isCancelable=false
                arguments=Bundle().apply {
                    putString("title",title)
                    putString("message",message)
                }
            }
        }
    }


    //01 perzonalizar este dialog a futuro con MaterialAlertDialogBuilder
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title= arguments?.getString("title")
        val message= arguments?.getString("message")

        val banderaBool= parentFragment as? ConfirmDialogListener
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.edit_name)
            .setCancelable(false)
            .setPositiveButton("Sí") { _, _ ->
               banderaBool?.onDialogResult(true)
            }
            .setNegativeButton("No") { _, _ ->
                banderaBool?.onDialogResult(false)
            }
            .create()
        return dialog
    }

}