package com.gtrab.qrscanmaster.ui.dialogs

import android.app.AlertDialog
import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import com.gtrab.qrscanmaster.R

class PermissionDialogFragment : DialogFragment() {
    interface PermissionDialogListener {
        fun onDialogResult(result:Boolean)
    }

    companion object{
        fun newInstance(title:String, message:String):PermissionDialogFragment{
            return PermissionDialogFragment().apply {
                isCancelable=false
                arguments=Bundle().apply {
                    putString("title",title)
                    putString("message",message)
                }
            }

        }
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val title= arguments?.getString("title")
        val message= arguments?.getString("message")

        val banderaBool=activity as? PermissionDialogListener
        val dialog = AlertDialog.Builder(requireContext())
            .setTitle(title)
            .setMessage(message)
            .setIcon(R.drawable.edit_name)
            .setCancelable(false)
            .setPositiveButton(R.string.main_dlg_ok) { _, _ ->
                banderaBool?.onDialogResult(true)
                dismiss()
            }
            .create()
        return dialog
    }
}