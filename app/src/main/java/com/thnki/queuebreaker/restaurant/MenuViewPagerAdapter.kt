package com.thnki.queuebreaker.restaurant

import android.support.v4.app.Fragment
import android.support.v4.app.FragmentManager
import android.support.v4.app.FragmentStatePagerAdapter
import com.thnki.queuebreaker.restaurant.categories.MenuCategory
import com.thnki.queuebreaker.restaurant.dishes.DishListFragment

class MenuViewPagerAdapter(fm: FragmentManager?,
                           private val categoryList: List<MenuCategory>?,
                           private val restaurantId: String) : FragmentStatePagerAdapter(fm) {

    override fun getItem(position: Int): Fragment {
        return DishListFragment.newInstance(restaurantId, categoryList?.get(position))
    }

    override fun getCount(): Int {
        return categoryList!!.size
    }

}