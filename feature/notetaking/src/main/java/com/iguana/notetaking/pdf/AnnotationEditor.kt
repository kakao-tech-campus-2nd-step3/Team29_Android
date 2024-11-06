package com.iguana.notetaking.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.PointF.length
import android.graphics.drawable.GradientDrawable
import android.text.Selection.setSelection
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.google.android.material.color.utilities.MaterialDynamicColors.background
import com.iguana.notetaking.pdf.model.AnnotationUIModel
import kotlin.math.roundToInt


interface AnnotationListener {
    fun onDrag(dragging: Boolean)
    fun onTextBoxClick(focused: Boolean)
    fun onTextEditingFinished(editText: EditText)
}

class AnnotationEditor(
    private val context: Context, private val listener: AnnotationListener
) {

    private var currentEditText: EditText? = null // 현재 편집 중인 EditText

    // 새로운 텍스트 상자를 PDF 페이지에 추가
    fun addTextBox(parentView: ViewGroup): EditText {
        val editText = createEditText()
        centerTextBoxInView(parentView, editText)
        enableTextBoxInteractions(editText)
        setupEditTextAppearance(editText)
        parentView.addView(editText)
        return editText
    }

    private fun createEditText(): EditText {
        return EditText(context).apply {
            setText("텍스트")
            setBackgroundColor(Color.TRANSPARENT)
            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)
            textSize = 16f
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
            )

            // Backspace/삭제 처리하여 텍스트만 지우고 커서 활성화 유지
            setOnKeyListener { _, keyCode, event ->
                if (keyCode == KeyEvent.KEYCODE_DEL && event.action == KeyEvent.ACTION_DOWN) {
                    if (text.isEmpty()) {
                        // 기본 동작 무시하여 전체 EditText 삭제 방지
                        return@setOnKeyListener true
                    }
                }
                false
            }

            imeOptions = EditorInfo.IME_FLAG_NO_EXTRACT_UI
        }
    }

    private fun centerTextBoxInView(parentView: ViewGroup, editText: EditText) {
        editText.post {
            editText.x = ((parentView.width - editText.width) / 2).toFloat()
            editText.y = ((parentView.height - editText.height) / 2).toFloat()
        }
    }

    private fun enableTextBoxInteractions(editText: EditText) {
        val gestureDetector = createGestureDetector(editText)

        var dX = 0
        var dY = 0

        editText.setOnTouchListener { view, event ->
            // 우선적으로 GestureDetector 이벤트를 처리
            if (gestureDetector.onTouchEvent(event)) {
                return@setOnTouchListener true
            }

            // 드래그 기능 처리
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = (view.x - event.rawX).roundToInt()
                    dY = (view.y - event.rawY).roundToInt()
                    listener.onDrag(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    view.animate().x((event.rawX + dX).toFloat())
                        .y((event.rawY + dY).toFloat())
                        .setDuration(0).start()
                    listener.onDrag(true)
                }
                MotionEvent.ACTION_UP -> {
                    listener.onDrag(false)
                    view.performClick()
                }
            }
            true
        }
    }

    private fun selectAllText(editText: EditText) {
        Log.d("AnnotationEditor", "Entering editing mode")
        editText.selectAll()
        enableTextBoxEditing(editText)
    }

    private fun createGestureDetector(editText: EditText): GestureDetector {
        return GestureDetector(context, object : GestureDetector.SimpleOnGestureListener() {
                override fun onDoubleTap(e: MotionEvent): Boolean {
                    Log.d("AnnotationEditor", "Double tap detected")
                    selectAllText(editText)
                    return true
                }

                override fun onSingleTapConfirmed(e: MotionEvent): Boolean {
                    Log.d("AnnotationEditor", "Single tap detected")
                    enableTextBoxEditing(editText)
                    listener.onTextBoxClick(true)
                    return true
                }

            override fun onLongPress(e: MotionEvent) {
                Log.d("AnnotationEditor", "Long press detected")
                selectAllText(editText)
            }
        })
    }

    // exit editing mode
    fun exitEditMode() {
        Log.d("AnnotationEditor", "Exiting editing mode(exitEditMode)")
        currentEditText?.let {
            listener.onTextEditingFinished(it) // 편집 종료 리스너 호출
            disableTextBoxEditing(it)
        }
        currentEditText = null
    }

    // edit mode
    fun enterEditMode() {
        currentEditText?.let { enableTextBoxEditing(it) }
    }

    // 편집 모드가 아닌 상태로 전환
    private fun disableTextBoxEditing(editText: EditText) {
        Log.d("AnnotationEditor", "Exiting editing mode(disableTextBoxEditing)")
        editText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false // 커서 비활성화
            setEditModeBorder(editText, false) // 테두리 제거
        }
    }

    // 텍스트 상자에 포커스를 주고 편집 모드로 진입
    private fun enableTextBoxEditing(editText: EditText) {
        currentEditText = editText // 현재 편집 중인 EditText로 설정
        editText.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isCursorVisible = true // 커서 활성화
            setEditModeBorder(editText, true) // 테두리 설정
            requestFocus()
        }
    }

    // 편집 모드 테두리 설정 메서드
    private fun setEditModeBorder(editText: EditText, isEditMode: Boolean) {
        if (isEditMode) {
            val border = GradientDrawable().apply {
                setStroke(1, Color.GRAY) // 편집 모드 시 테두리
                cornerRadius = 0f
            }
            editText.background = border
        } else {
            editText.background = null // 기본 상태에서는 배경을 제거
        }
    }

    private fun setupEditTextAppearance(editText: EditText) {
        // 초기 상태에서 커서 활성화
        enableTextBoxEditing(editText)
    }


    // 현재 편집 중인 EditText의 텍스트, 크기, 위치 정보를 반환하는 함수
    fun getCurrentAnnotationInfo(): AnnotationUIModel? {
        currentEditText?.let { editText ->
            return AnnotationUIModel(
                content = editText.text.toString(),
                x = editText.x,
                y = editText.y,
                width = editText.width.toFloat(),
                height = editText.height.toFloat()
            )
        }
        return null
    }

}
