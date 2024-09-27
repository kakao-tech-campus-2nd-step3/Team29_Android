package com.iguana.notetaking

import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class NotetakingActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_notetaking)

        // Intent에서 PDF URI 받기
        val pdfUriString = intent.getStringExtra("PDF_URI")
        if (pdfUriString != null) {
            val pdfUri = Uri.parse(pdfUriString)
            val pdfViewerFragment = PdfViewerFragment.newInstance(pdfUri)

            supportFragmentManager.beginTransaction().replace(R.id.pdf_fragment_container, pdfViewerFragment)
                .commit()

            val sideBarFragment = SideBarFragment()
            supportFragmentManager.beginTransaction()
                .replace(R.id.side_bar_container, sideBarFragment)
                .commit()
        } else {
            Log.e("NotetakingActivity", "PDF URI is null in Activity") // URI가 null인 경우 로그
        }
    }
}