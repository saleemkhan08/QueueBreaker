package com.thnki.queuebreaker.restaurant

interface OnDismissListener<T> {
    fun onDismiss(collection:ArrayList<T>)
}