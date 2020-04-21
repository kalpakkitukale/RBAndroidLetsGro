package com.ramanbyte.emla.firebase

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.ramanbyte.data_layer.SharedPreferencesDatabase.KEY_DEVICE_INSTANCE_ID
import com.ramanbyte.data_layer.SharedPreferencesDatabase.setStringPref
import com.ramanbyte.utilities.AppLog


/**
 * @author Niraj Naware <niraj.n@ramanbyte.com>
 * @since 16/3/20
 */
class FirebaseMessageService : FirebaseMessagingService() {


    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        AppLog.debugLog("On Message Received -------- $remoteMessage")
        remoteMessage.apply {

            if (notification != null) {
                AppLog.debugLog("Message Notification Body: ${notification!!.body}")
            }
            // Check if message contains a data payload.
            data.isNotEmpty().let {
                AppLog.debugLog("Message data payload: $data")
            }

            // Check if message contains a notification payload.
            notification?.let {
                AppLog.debugLog("Message Notification Body: ${it.body}")
                /*NotificationUtils(this).showNotification(it.body!!)*/
            }


        }
    }

    override fun onNewToken(newToken: String) {
        super.onNewToken(newToken)
        setStringPref(KEY_DEVICE_INSTANCE_ID, newToken)
        AppLog.debugLog("New Token -------- $newToken")
    }
}