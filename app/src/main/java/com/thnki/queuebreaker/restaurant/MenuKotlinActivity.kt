package com.thnki.queuebreaker.restaurant

import android.content.Intent
import android.os.Bundle
import android.util.Log
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
import com.thnki.queuebreaker.restaurant.dishes.*
import com.thnki.queuebreaker.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_menu_kotlin.*


class MenuKotlinActivity : BaseActivity(), OrderListener {
    companion object {
        const val TAG = "MenuKotlinActivity"
    }

    private var orderUpdateListeners = ArrayList<OrderUpdateListener>()
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
        menuItem.actionView.setOnClickListener { editMenuCategories() }
    }

    private fun setUpCartAction(menu: Menu) {
        val menuItem: MenuItem = menu.findItem(R.id.cart)
        textCartItemCount = menuItem.actionView.findViewById(R.id.cartBadge) as TextView
        menuItem.actionView.setOnClickListener { showCartDialog() }
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
            val ordersListFragment = OrdersListFragment.getInstance(this, dishList)
            ordersListFragment.show(supportFragmentManager, TAG)
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

    override fun onOrderPlaced(orders: ArrayList<Order>?) {
        ToastMsg.show(R.string.order_placed)
    }

    override fun onDishAdded(dish: Dishes): Int {
        val order = Order()
        order.count = 1
        order.dishes = dish
        orderList[dish.id] = order
        setupBadge(orderList.size)
        notifyOrderUpdateListeners(order)
        return order.count
    }

    private fun notifyOrderUpdateListeners(order: Order?) {
        orderUpdateListeners.forEach {
            it.onOrderUpdate(order)
        }
    }

    override fun onDishCountIncremented(dish: Dishes): Int {
        val order = orderList[dish.id]
        ++order!!.count
        orderList[dish.id] = order
        notifyOrderUpdateListeners(order)

        Log.d("OrderUpdate", "onDishCountIncremented : ${order.count}")
        return order.count
    }

    override fun onDishCountDecremented(dish: Dishes): Int {
        val order = orderList[dish.id]
        --order!!.count
        orderList[dish.id] = order
        notifyOrderUpdateListeners(order)
        Log.d("OrderUpdate", "onDishCountDecremented : ${order.count}")
        return order.count
    }

    override fun onDishRemoved(dish: Dishes) {
        val order = orderList[dish.id]
        orderList.remove(dish.id)
        setupBadge(orderList.size)
        notifyOrderUpdateListeners(order)
    }

    override fun onOrderCancelled(order: Order) {
        orderList.clear()
        setupBadge(0)
        notifyOrderUpdateListeners(order)
    }

    override fun addOrderUpdateListener(orderUpdateListener: OrderUpdateListener) {
        Log.d("OrderUpdate", "addOrderUpdateListener : $orderUpdateListener")
        this.orderUpdateListeners.add(orderUpdateListener)
    }

    override fun removeOrderUpdateListener(orderUpdateListener: OrderUpdateListener) {
        Log.d("OrderUpdate", "removeOrderUpdateListener : $orderUpdateListener")
        this.orderUpdateListeners.remove(orderUpdateListener)
    }

    override fun getOrders(): LinkedHashMap<String, Order> {
        return orderList
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (activityResultListener != null) {
            Log.d("ImageTest", "onActivityResult Activity")
            activityResultListener?.onActivityResult(requestCode, resultCode, intent)
        }
    }

    override fun onBackPressed() {
        val fragments = supportFragmentManager.fragments
        if (fragments != null) {
            for (fragment in fragments) {
                if (!fragment.isVisible) continue
                if (fragment is OnBackPressedListener && (fragment as OnBackPressedListener).onBackPressed()) {
                    return
                }
            }
        }

        super.onBackPressed()
    }
}
