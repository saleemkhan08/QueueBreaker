package com.thnki.queuebreaker.restaurant.dishes

import android.content.Intent

interface ActivityResultListener {
    fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?)
}