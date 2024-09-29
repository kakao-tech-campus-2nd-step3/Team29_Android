package com.iguana.notetaking

import android.net.Uri
import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter

class PdfPageAdapter(
    fragment: Fragment,
    private val pdfUri: Uri,
    private val pageCount: Int
) : FragmentStateAdapter(fragment) {

    override fun getItemCount(): Int = pageCount

    override fun createFragment(position: Int): Fragment {
        return PdfPageFragment.newInstance(pdfUri, position)
    }
}
