package com.example.playlistmaker.sharing.data

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.playlistmaker.R
import com.example.playlistmaker.sharing.domain.api.SharingRepository
import java.lang.Exception

class SharingRepositoryImpl(
    private val context: Context
) : SharingRepository {
    override fun shareApp() {
        val intent = Intent(Intent.ACTION_SEND)
        intent.type = "text/plain"
        intent.putExtra(Intent.EXTRA_TEXT, context.getString(R.string.shareApp))
        context.startActivity(intent)
    }

    override fun sendSupportEmail() {
        val feedbackIntent = Intent().apply {
            action = Intent.ACTION_SENDTO
            data = Uri.parse("mailto:")
            putExtra(Intent.EXTRA_EMAIL, arrayOf(context.getString(R.string.sendToEmail)))
            putExtra(Intent.EXTRA_SUBJECT, context.getString(R.string.sendHeader))
            putExtra(Intent.EXTRA_TEXT, context.getString(R.string.sendText))
        }
        try {
            context.startActivity(feedbackIntent)
        } catch (e: Exception) {
            Toast.makeText(
                context,
                context.getString(R.string.mail_app_not_found),
                Toast.LENGTH_LONG
            ).show()
        }

    }

    override fun openAgreementUrl() {
        val intent = Intent(Intent.ACTION_VIEW)
        intent.data = Uri.parse(context.getString(R.string.agreementUrl))
        context.startActivity(intent)
    }
}
