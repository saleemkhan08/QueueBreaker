package com.thnki.queuebreaker.utils

import com.thnki.queuebreaker.restaurant.categories.MenuCategory

interface RemoveItemHandler {
    fun onRemoveItemTriggered(listener: RemoveItemListener, category: MenuCategory, position: Int)
}