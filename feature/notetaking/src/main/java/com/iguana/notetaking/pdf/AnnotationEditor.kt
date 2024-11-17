package com.iguana.notetaking.pdf

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.Color
import android.graphics.drawable.GradientDrawable
import android.util.Log
import android.view.GestureDetector
import android.view.KeyEvent
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import com.iguana.domain.model.Annotation
import kotlin.math.roundToInt


interface AnnotationListener {
    fun onDrag(dragging: Boolean)
    fun onTextBoxClick(focused: Boolean)
    fun onTextEditingFinished(annotation: Annotation)
}

class AnnotationEditor(
    private val context: Context, private val listener: AnnotationListener, private val pageNumber: Int
) {
    companion object {
        private const val HANDLE_SIZE = 16
    }

    private var _currentEditText: EditText? = null // 현재 편집 중인 EditText
    var currentEditText: EditText? = null
        get() = _currentEditText

    private var resizing = false // 크기 조절 중인지 여부 확인용 변수
    private lateinit var resizeHandle: View

    private var isEditing: Boolean = false // 텍스트 편집 모드 여부
    var isDraggingOrResizing = false // 드래그 또는 크기 조정 여부

    // 새로운 텍스트 상자를 PDF 페이지에 추가
    fun addTextBox(parentView: ViewGroup, x: Float? = null, y: Float? = null): EditText {
        val editText = createEditText()

        // x와 y 좌표가 주어졌다면 해당 위치에 배치하고, 없다면 중앙에 배치
        editText.x = x ?: ((parentView.width - editText.width) / 2).toFloat()
        editText.y = y ?: ((parentView.height - editText.height) / 2).toFloat()

        // 인터랙션과 초기 설정
        enableTextBoxInteractions(editText)
        setupEditTextAppearance(editText)

        // 크기 조정 핸들러 추가
        addResizeHandle(editText, parentView)
        editText.post { updateHandlePosition(editText) }

        // MarginLayoutParams 설정
        editText.layoutParams = ViewGroup.MarginLayoutParams(
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT
        )

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

    @SuppressLint("ClickableViewAccessibility")
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
            _currentEditText = editText

            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    isDraggingOrResizing = false
                    resizing = false // 크기 조정 모드 종료
                    dX = (view.x - event.rawX).roundToInt()
                    dY = (view.y - event.rawY).roundToInt()
                    listener.onDrag(false)
                }
                MotionEvent.ACTION_MOVE -> {
                    isDraggingOrResizing = true
                    view.animate()
                        .x(event.rawX + dX)
                        .y(event.rawY + dY)
                        .setDuration(0)
                        .start()
                    updateHandlePosition(view as EditText) // 드래그 중 핸들러 위치 업데이트
                    view.parent.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_UP -> {
                    if (!isDraggingOrResizing) {
                        // 크기 조정이나 드래그가 없으면 부모 이벤트 허용
                        view.parent.requestDisallowInterceptTouchEvent(false)
                    }
                    updateHandlePosition(view as EditText)
                    updateAnnotationInfo()
                }
            }
            true
        }
    }

    private fun handleMove(view: View, event: MotionEvent, dX: Int, dY: Int) {
        if (!resizing) {
            view.animate().x((event.rawX + dX).toFloat()).y((event.rawY + dY).toFloat()).setDuration(0).start()
            updateHandlePosition(view as EditText)
            listener.onDrag(true)
        }
    }

    private fun handleActionUp() {
        listener.onDrag(false)
        updateAnnotationInfo()
        exitEditMode()
    }

    private fun selectAllText(editText: EditText) {
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
                    listener.onTextBoxClick(true)
                    if (isEditing) {
                        // 편집 모드일 때: 키패드와 커서 활성화
                        enableTextBoxEditing(editText)
                    } else {
                        // 비편집 모드일 때: 편집 모드로 전환
                        enterEditMode(editText)
                    }
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
        currentEditText?.let {
            updateAnnotationInfo()
            disableTextBoxEditing(it)
            resizeHandle.visibility = View.GONE
        }
        _currentEditText = null
    }

    // edit mode
    fun enterEditMode() {
        isEditing = true
        currentEditText?.let { enableTextBoxEditing(it) }
    }
    fun enterEditMode(editText: EditText) {
        isEditing = true
        _currentEditText = editText
        enableTextBoxEditing(editText)
    }

    // 편집 모드가 아닌 상태로 전환
    private fun disableTextBoxEditing(editText: EditText) {
        editText.apply {
            isFocusable = false
            isFocusableInTouchMode = false
            isCursorVisible = false // 커서 비활성화
            setEditModeBorder(editText, false) // 테두리 제거
        }
    }

    // 텍스트 상자에 포커스를 주고 편집 모드로 진입
    private fun enableTextBoxEditing(editText: EditText) {
        _currentEditText = editText // 현재 편집 중인 EditText로 설정
        editText.apply {
            isFocusable = true
            isFocusableInTouchMode = true
            isCursorVisible = true // 커서 활성화
            setEditModeBorder(editText, true) // 테두리 설정
            requestFocus()
        }
        resizeHandle.visibility = View.VISIBLE // 핸들러 표시

        // 키보드 강제 표시
        val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as android.view.inputmethod.InputMethodManager
        imm.showSoftInput(editText, android.view.inputmethod.InputMethodManager.SHOW_IMPLICIT)
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
        disableTextBoxEditing(editText)
    }


    // 현재 편집 중인 EditText의 텍스트, 크기, 위치 정보를 반환하는 함수
    private fun getCurrentAnnotationInfo(): Annotation? {
        currentEditText?.let { editText ->
            return Annotation(
                id = editText.tag as? Long ?: 0,
                content = editText.text.toString(),
                x = editText.x,
                y = editText.y,
                width = editText.width.toFloat(),
                height = editText.height.toFloat(),
                pageNumber = pageNumber
            )
        }
        return null
    }

    private fun updateAnnotationInfo() {
        getCurrentAnnotationInfo()?.let { listener.onTextEditingFinished(it) }
    }

    private fun addResizeHandle(editText: EditText, parentView: ViewGroup) {
        resizeHandle = View(context).apply {
            layoutParams = ViewGroup.LayoutParams(HANDLE_SIZE, HANDLE_SIZE)
            background = GradientDrawable().apply {
                shape = GradientDrawable.OVAL
                setColor(Color.GRAY)
            }
            visibility = View.VISIBLE // 항상 표시
        }

        parentView.addView(resizeHandle)
        updateHandlePosition(editText)

        resizeHandle.setOnTouchListener { _, event ->
            when (event.action) {
                MotionEvent.ACTION_DOWN -> {
                    resizing = true
                    isDraggingOrResizing = true // 크기 조정 시작
                    parentView.requestDisallowInterceptTouchEvent(true)
                }
                MotionEvent.ACTION_MOVE -> {
                    handleResize(editText, event)
                    updateHandlePosition(editText) // 크기 조정 후 핸들러 위치 업데이트
                }
                MotionEvent.ACTION_UP -> {
                    handleResizeEnd()
                    resizing = false
                    isDraggingOrResizing = false // 크기 조정 종료
                    updateAnnotationInfo()
                    parentView.requestDisallowInterceptTouchEvent(false)
                }
            }
            true
        }
    }
    private fun updateHandlePosition(editText: EditText) {
        // 핸들러를 EditText의 오른쪽 중앙에 위치시킴
        resizeHandle.x = editText.x + editText.width - HANDLE_SIZE / 2
        resizeHandle.y = editText.y + editText.height / 2 - HANDLE_SIZE / 2
    }

    private fun handleResize(view: View, event: MotionEvent) {
        val newWidth = (event.rawX - view.x).roundToInt()
        if (newWidth > 0) {
            view.layoutParams.width = newWidth
            view.layoutParams.height = ViewGroup.LayoutParams.WRAP_CONTENT
            view.requestLayout()
            updateHandlePosition(view as EditText)
        }
    }

    private fun handleResizeEnd() {
        resizing = false
        listener.onDrag(false)
        updateAnnotationInfo()
    }

}
