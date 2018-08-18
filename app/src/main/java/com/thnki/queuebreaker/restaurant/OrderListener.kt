package com.thnki.queuebreaker.restaurant

import com.thnki.queuebreaker.restaurant.dishes.Dishes
import com.thnki.queuebreaker.restaurant.dishes.Order
import com.thnki.queuebreaker.restaurant.dishes.OrderUpdateListener

interface OrderListener {
    fun onOrderPlaced(order: ArrayList<Order>?)
    fun onDishAdded(dish: Dishes):Int
    fun onDishRemoved(dish: Dishes)
    fun onDishCountIncremented(dish: Dishes):Int
    fun onDishCountDecremented(dish: Dishes):Int
    fun onOrderCancelled(order: Order)
    fun getOrders():LinkedHashMap<String, Order>
    fun addOrderUpdateListener(orderUpdateListener: OrderUpdateListener)
    fun removeOrderUpdateListener(orderUpdateListener: OrderUpdateListener)
}
