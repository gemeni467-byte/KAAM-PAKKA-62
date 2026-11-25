package com.example.util

import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.widget.Toast
import com.example.model.KaamConstants
import java.net.URLEncoder

object IntentUtils {
  fun dialPhone(context: Context, phone: String) {
    val clean = phone.filter { it.isDigit() }.takeLast(10)
    if (clean.isBlank()) return
    val intent = Intent(Intent.ACTION_DIAL, Uri.parse("tel:+91$clean"))
    try {
      context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
      Toast.makeText(context, "No phone dialer app found.", Toast.LENGTH_SHORT).show()
    }
  }

  fun openWhatsApp(context: Context, phone: String, message: String? = null) {
    val clean = phone.filter { it.isDigit() }.takeLast(10)
    if (clean.isBlank()) return
    val baseUrl = "https://wa.me/91$clean"
    val fullUrl =
      if (!message.isNullOrBlank()) {
        val encoded = URLEncoder.encode(message, "UTF-8")
        "$baseUrl?text=$encoded"
      } else {
        baseUrl
      }
    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(fullUrl))
    try {
      context.startActivity(intent)
    } catch (_: ActivityNotFoundException) {
      Toast.makeText(context, "No browser or WhatsApp app found.", Toast.LENGTH_SHORT).show()
    }
  }

  fun shareApp(context: Context) {
    val shareUrl = KaamConstants.PUBLIC_APP_URL
    val shareText =
      "Join Kaam Connect — Direct Work, Direct Contact, No Middleman!\n" +
        "Find verified industrial & construction workers or contractors across India.\n\n" +
        shareUrl
    val intent =
      Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_SUBJECT, "Kaam Connect — Direct Work, Direct Contact")
        putExtra(Intent.EXTRA_TEXT, shareText)
      }
    try {
      context.startActivity(Intent.createChooser(intent, "Share Kaam Connect"))
    } catch (_: ActivityNotFoundException) {
      Toast.makeText(context, "Unable to open share dialog.", Toast.LENGTH_SHORT).show()
    }
  }

  fun formatRelativeTime(createdAtMs: Long): String {
    if (createdAtMs <= 0L) return "Just now"
    val diffMs = (System.currentTimeMillis() - createdAtMs).coerceAtLeast(0L)
    val mins = diffMs / 60_000L
    return when {
      mins < 1 -> "Just now"
      mins < 60 -> "${mins}m ago"
      else -> "${mins / 60}h ${mins % 60}m ago"
    }
  }
}
