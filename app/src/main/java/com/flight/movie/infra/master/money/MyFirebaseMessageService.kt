package com.flight.movie.infra.master.money

import android.util.Log
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

/**
 * create by colin
 * 2024/6/18
 */
class MyFirebaseMessageService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Log.d("MyFirebaseMessageService", "onNewToken: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {

    }
}