package com.iguana.documents

import android.app.Dialog
import android.os.Bundle
import androidx.fragment.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import com.iguana.documents.R

class CreateFolderDialogFragment(private val onFolderCreated: (String) -> Unit) : DialogFragment() {

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialogView = LayoutInflater.from(requireContext()).inflate(R.layout.dialog_create_folder, null)
        val editTextFolderName = dialogView.findViewById<EditText>(R.id.editTextFolderName)

        return AlertDialog.Builder(requireContext())
            .setTitle("폴더 생성")
            .setView(dialogView)
            .setPositiveButton("확인") { _, _ ->
                val folderName = editTextFolderName.text.toString()
                if (folderName.isNotBlank()) {
                    onFolderCreated(folderName)
                }
            }
            .setNegativeButton("취소", null)
            .create()
    }
}