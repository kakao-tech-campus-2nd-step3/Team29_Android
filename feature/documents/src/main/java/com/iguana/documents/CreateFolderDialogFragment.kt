package com.iguana.documents

import android.app.Dialog
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.Window
import android.view.WindowManager
import androidx.fragment.app.DialogFragment
import com.iguana.documents.databinding.DialogCreateFolderBinding

class CreateFolderDialogFragment(private val onFolderCreated: (String) -> Unit) : DialogFragment() {

    private lateinit var binding: DialogCreateFolderBinding

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val dialog = Dialog(requireContext())
        binding = DialogCreateFolderBinding.inflate(LayoutInflater.from(context))
        
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog.setContentView(binding.root)
        
        // 다이얼로그를 전체 화면으로 설정
        dialog.window?.apply {
            setLayout(
                WindowManager.LayoutParams.MATCH_PARENT,
                WindowManager.LayoutParams.MATCH_PARENT
            )
            clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND)
            setBackgroundDrawable(ColorDrawable(Color.WHITE))
        }

        setupClickListeners()
        
        return dialog
    }

    private fun setupClickListeners() {
        binding.btnConfirm.setOnClickListener {
            val folderName = binding.editTextFolderName.text.toString()
            if (folderName.isNotBlank()) {
                onFolderCreated(folderName)
                dismiss()
            }
        }

        binding.btnCancel.setOnClickListener {
            dismiss()
        }
    }
}