package com.thnki.queuebreaker.utils

import com.thnki.queuebreaker.restaurant.categories.MenuCategory

interface RemoveItemListener {
    fun removeItem(position: Int)
    fun undoRemoval(category: MenuCategory, position: Int)
}