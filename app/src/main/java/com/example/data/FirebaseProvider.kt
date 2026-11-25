package com.example.data

import android.content.Context
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions
import com.google.firebase.firestore.FirebaseFirestore

object FirebaseProvider {
  private const val APP_NAME = "kaam-connect-app"

  private val options: FirebaseOptions by lazy {
    FirebaseOptions.Builder()
      .setApiKey("AIzaSyDcR-Ch_sWABCgieN_lbZ6ovjMrr476HIY")
      .setApplicationId("1:1044094197345:web:22f2e7f36ac6415f22d420")
      .setProjectId("kaam-connect-c4983")
      .setStorageBucket("kaam-connect-c4983.firebasestorage.app")
      .setGcmSenderId("1044094197345")
      .build()
  }

  @Synchronized
  fun getFirestore(context: Context): FirebaseFirestore {
    val appContext = context.applicationContext
    val existing = FirebaseApp.getApps(appContext).firstOrNull { it.name == APP_NAME }
    val app = existing ?: FirebaseApp.initializeApp(appContext, options, APP_NAME)
    return FirebaseFirestore.getInstance(app)
  }
}
