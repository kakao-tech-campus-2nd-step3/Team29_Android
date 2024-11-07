package com.iguana.notetaking.pdf

import android.content.Context
import android.graphics.Color
import android.view.MotionEvent
import android.view.ViewGroup
import android.widget.EditText
import kotlin.math.roundToInt


interface AnnotationListener {
    fun onDrag(dragging: Boolean)
    fun onTextBoxClick(focused: Boolean)
}

class AnnotationEditor(
    private val context: Context, private val listener: AnnotationListener
) {

    // 새로운 텍스트 상자를 PDF 페이지에 추가
    fun addTextBox(parentView: ViewGroup): EditText {
        val editText = EditText(context).apply {
            setText("텍스트")
            setBackgroundColor(Color.TRANSPARENT)
            layoutParams = ViewGroup.LayoutParams(
                ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT
            )

            setPadding(16, 16, 16, 16)
            setTextColor(Color.BLACK)
            textSize = 16f

            // 중앙 위치로 초기화 (Int 단위)
            post {
                x = ((parentView.width / 2 - width / 2)).toFloat()
                y = ((parentView.height / 2 - height / 2)).toFloat()
            }

            // 터치 이벤트로 위치 이동 가능하게 설정
            setDraggable()

            // 클릭 이벤트 리스너 설정하여 인터페이스 메서드 호출
            setOnClickListener {
                listener.onTextBoxClick(true) // 텍스트 상자가 클릭될 때 호출
            }
        }

        parentView.addView(editText)
        return editText
    }

    // 텍스트 상자를 터치로 이동할 수 있도록 설정 (Int 단위)
    private fun EditText.setDraggable() {
        var dX = 0
        var dY = 0

        setOnTouchListener { view, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    dX = (view.x - event.rawX).roundToInt()
                    dY = (view.y - event.rawY).roundToInt()
                    listener.onDrag(false) // 드래그 시작 전
                }

                MotionEvent.ACTION_MOVE -> {
                    view.animate().x((event.rawX + dX).roundToInt().toFloat())
                        .y((event.rawY + dY).roundToInt().toFloat()).setDuration(0).start()
                    listener.onDrag(true) // 드래그 시작 전
                }

                MotionEvent.ACTION_UP -> {
                    listener.onDrag(false) // 드래그 시작 전
                    view.performClick()
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
