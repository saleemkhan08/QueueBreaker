package com.thnki.queuebreaker.restaurant

import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.TextView
import butterknife.ButterKnife
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.explore.ExploreFragment.RESTAURANT
import com.thnki.queuebreaker.home.explore.Restaurants
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.categories.EditMenuCategories
import com.thnki.queuebreaker.restaurant.categories.MenuCategoriesUpdateHandler
import com.thnki.queuebreaker.restaurant.dishes.Dishes
import com.thnki.queuebreaker.restaurant.dishes.EditDishListFragment
import com.thnki.queuebreaker.restaurant.dishes.Order
import com.thnki.queuebreaker.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_menu_kotlin.*


class MenuKotlinActivity : BaseActivity(), OrderListener, OnDismissListener<Order> {

    companion object {
        const val TAG = "MenuKotlinActivity"
    }

    private var currentRestaurant: Restaurants? = null
    private var orderList = LinkedHashMap<String, Order>()
    private var categoriesUpdateHandler: MenuCategoriesUpdateHandler? = null

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        ButterKnife.bind(this)
        setSupportActionBar(toolbar)
        setTitle()

        categoriesUpdateHandler = MenuCategoriesUpdateHandler.Builder()
                .setViewPager(menuViewPager)
                .setTabs(menuTabs)
                .setFragmentManager(supportFragmentManager)
                .build()
        categoriesUpdateHandler?.setupMenuViewPager(currentRestaurant!!.id)
    }

    private fun setTitle() {
        currentRestaurant = intent.getParcelableExtra(RESTAURANT)
        val restaurants = currentRestaurant
        if (restaurants != null) {
            toolbar.title = restaurants.name
        }
    }

    private var textCartItemCount: TextView? = null

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_kotlin, menu)
        setUpCartAction(menu)
        setUpEditCategoriesAction(menu)
        return true
    }

    private fun setUpEditCategoriesAction(menu: Menu) {
        val menuItem: MenuItem = menu.findItem(R.id.edit_categories)
        menuItem.actionView.setOnClickListener({ editMenuCategories() })
    }

    private fun setUpCartAction(menu: Menu) {
        val menuItem: MenuItem = menu.findItem(R.id.cart)
        textCartItemCount = menuItem.actionView.findViewById(R.id.cartBadge) as TextView
        menuItem.actionView.setOnClickListener({ showCartDialog() })
        setupBadge(orderList.size)
    }

    private fun setupBadge(size: Int) {
        if (size == 0) {
            textCartItemCount?.visibility = View.INVISIBLE
        } else {
            textCartItemCount?.text = size.toString()
            textCartItemCount?.visibility = View.VISIBLE
        }
    }

    private fun showCartDialog() {
        val dishList = ArrayList<Order>()
        for (order in orderList.values) {
            dishList.add(order)
        }

        if (dishList.size > 0) {
            val editDishFragment = EditDishListFragment.getInstance(this, dishList)
            editDishFragment.show(supportFragmentManager, TAG)
        } else {
            ToastMsg.show(R.string.order_list_is_empty)
        }
    }

    private fun editMenuCategories() {
        val fragment = EditMenuCategories.getInstance(categoriesUpdateHandler,
                currentRestaurant!!.id,
                categoriesUpdateHandler?.menuCategoryList)
        fragment.show(supportFragmentManager, EditMenuCategories.TAG)
    }

    override fun getContentView(): Int {
        return R.layout.activity_menu_kotlin
    }

    override fun onStop() {
        super.onStop()
        categoriesUpdateHandler?.dispose()
    }

    override fun onOrderPlaced(order: Order) {

    }

    override fun onDishAdded(dish: Dishes): Int {
        val order = Order()
        order.count = 1
        order.dishes = dish
        orderList[dish.id] = order
        setupBadge(orderList.size)
        return order.count
    }

    override fun onDishCountIncremented(dish: Dishes): Int {
        val order = orderList[dish.id]
        ++order!!.count
        orderList[dish.id] = order
        return order.count
    }

    override fun onDishCountDecremented(dish: Dishes): Int {
        val order = orderList[dish.id]
        --order!!.count
        orderList[dish.id] = order
        return order.count
    }

    override fun onDishRemoved(dish: Dishes) {
        orderList.remove(dish.id)
        setupBadge(orderList.size)
    }

    override fun onOrderCancelled(order: Order) {
        orderList.clear()
        setupBadge(orderList.size)
    }

    override fun getOrders(): LinkedHashMap<String, Order> {
        return orderList
    }

    override fun onDismiss(collection: ArrayList<Order>) {
        val newMap = LinkedHashMap<String, Order>()
        for (dishes in collection) {
            newMap[dishes.id] = orderList[dishes.id]!!
        }
        orderList.clear()
        orderList = newMap
    }

}
