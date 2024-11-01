package com.iguana.notetaking

import android.view.View
import androidx.databinding.BindingAdapter

class BindingAdapters{

    companion object{
        @BindingAdapter("android:visibility")
        @JvmStatic// it is important
        fun setVisibility(target: View, visible: Boolean) {
            target.visibility = if (visible) View.VISIBLE else View.GONE
        }

    }

}