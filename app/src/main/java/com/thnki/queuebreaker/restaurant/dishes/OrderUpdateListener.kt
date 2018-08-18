package com.thnki.queuebreaker.restaurant.dishes

interface OrderUpdateListener {
    fun onOrderUpdate(order: Order?)
}