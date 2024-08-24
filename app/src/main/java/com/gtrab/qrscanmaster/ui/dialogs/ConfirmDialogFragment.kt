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
        fun newInstance():ConfirmDialogFragment{
            return ConfirmDialogFragment().apply {
                isCancelable=false
            }
        }
    }


    //01 perzonalizar este dialog a futuro con MaterialAlertDialogBuilder
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val banderaBool= parentFragment as? ConfirmDialogListener
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle("Confirmar")
            .setMessage("¿Ver este qr?")
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