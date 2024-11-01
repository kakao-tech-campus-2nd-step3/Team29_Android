package com.iguana.notetaking.pdf

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData

class PdfEditor(private val context: Context) {

    private val _isEditMode = MutableLiveData<Boolean>(false)
    val isEditMode: LiveData<Boolean> get() = _isEditMode

    // 편집 모드를 활성화 또는 비활성화
    fun toggleEditMode() {
        _isEditMode.value = !(_isEditMode.value ?: false)
    }

    // 새로운 텍스트 상자를 PDF 페이지에 추가
    fun addTextBox(parentView: ViewGroup): EditText {
        val editText = EditText(context).apply {
            setText("텍스트")
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)
            textSize = 16f

            // 중앙 위치로 초기화
            post {
                x = (parentView.width / 2 - width / 2).toFloat()
                y = (parentView.height / 2 - height / 2).toFloat()
            }

            // 터치 이벤트로 위치 이동 가능하게 설정
            setDraggable()
        }

        // 부모 뷰에 추가
        parentView.addView(editText)
        return editText
    }

    // 텍스트 상자를 터치로 이동할 수 있도록 설정
    private fun EditText.setDraggable() {
        var dX = 0f
        var dY = 0f
        var isDragging = false

        setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = view.x - event.rawX
                    dY = view.y - event.rawY
                    isDragging = false
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                    isDragging = true
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDragging) {
                        view.performClick()
                    }
                }
            }
            true
        }
    }

    // 텍스트 상자에 포커스를 주고 편집 모드로 진입
    fun enableTextBoxEditing(editText: EditText) {
        editText.apply {
            isFocusableInTouchMode = true
            requestFocus()
        }
    }
}
