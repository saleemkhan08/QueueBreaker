package com.thnki.queuebreaker.restaurant.categories

interface CategoryListener {
    fun onCategoryChanged(category: MenuCategory)
}