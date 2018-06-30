package com.thnki.queuebreaker.restaurant.categories

import android.support.design.widget.TabLayout
import android.support.v4.app.FragmentManager
import android.support.v4.view.ViewPager
import android.util.Log
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.CollectionListener
import com.thnki.queuebreaker.model.Progress
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.MenuKotlinActivity
import com.thnki.queuebreaker.restaurant.MenuViewPagerAdapter
import com.thnki.queuebreaker.restaurant.OnDismissListener
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*

class MenuCategoriesUpdateHandler :
        OnDismissListener<MenuCategory>, CollectionListener<MenuCategory>, TabLayout.OnTabSelectedListener {

    var categoryListener: CategoryListener? = null
    var currentMenuCategory: MenuCategory? = null

    private var supportFragmentManager: FragmentManager? = null
    private var menuViewPager: ViewPager? = null
    private var menuTabs: TabLayout? = null
    private val tag = "MenuCategoriesUpdateHandler"
    var menuCategoryList: ArrayList<MenuCategory>? = ArrayList()
    private var firestoreMenuRepo: FirestoreMenuCategoriesRepository? = null
    private var disposableList: ArrayList<Disposable> = ArrayList()
    private var currentRestaurantId = ""

    override fun onCollectionChanged(collection: ArrayList<MenuCategory>?) {
        menuCategoryList = collection
        menuViewPager?.adapter = MenuViewPagerAdapter(supportFragmentManager, collection, currentRestaurantId)
        setUpTabs()
        menuTabs?.addOnTabSelectedListener(this)
        addMenus(collection)
        Progress.hide()
    }

    fun setupMenuViewPager(restaurantId: String) {
        currentRestaurantId = restaurantId
        firestoreMenuRepo = FirestoreMenuCategoriesRepository.getInstance(restaurantId)
        firestoreMenuRepo?.addCollectionListener(this, tag)
    }

    private fun setUpTabs() {
        menuViewPager?.addOnPageChangeListener(TabLayout.TabLayoutOnPageChangeListener(menuTabs))
        menuTabs?.addOnTabSelectedListener(TabLayout.ViewPagerOnTabSelectedListener(menuViewPager))
    }

    private fun addMenus(collection: List<MenuCategory>?) {
        menuTabs?.removeAllTabs()
        menuTabs?.tabMode = TabLayout.MODE_SCROLLABLE
        for (category in collection!!) {
            val tab = menuTabs?.newTab()
            tab?.tag = category
            tab?.text = category.name
            menuTabs?.addTab(tab!!)
        }
    }

    override fun onDismiss(collection: ArrayList<MenuCategory>) {
        Log.d("TestRxKotlin", "onDismiss")
        firestoreMenuRepo?.removeCollectionListener(MenuKotlinActivity.TAG)

        val removalList = ArrayList<MenuCategory>(menuCategoryList)
        Progress.show(R.string.saving)
        Observable.fromIterable(collection)
                .doOnNext {
                    updateCategories(it, removalList)
                }
                .doOnComplete {
                    removeCategories(removalList)
                }
                .doOnError {
                    ToastMsg.show(it.message)
                }
                .doOnSubscribe {
                    disposableList.add(it)
                }
                .subscribe()
    }

    private fun updateCategories(it: MenuCategory, removalList: ArrayList<MenuCategory>) {
        Log.d("TestRxKotlin", "updateCategories ${it.name}")
        if (it.id == null) {
            addItem(it)
        } else {
            updateItem(it)
            removalList.remove(it)
        }
    }

    private fun removeCategories(removalList: ArrayList<MenuCategory>) {
        Log.d("TestRxKotlin", "removeCategories")
        for (category in removalList) {
            Log.d("TestRxKotlin", category.name + ": " + category.id)
        }
        if (removalList.size > 0) {
            Observable.fromIterable(removalList)
                    .doOnNext {
                        Log.d("TestRxKotlin", "removeCategories doOnNext ${it.name}")
                        deleteItem(it)
                    }
                    .doOnError {
                        ToastMsg.show(it.message)
                    }
                    .doOnComplete {
                        refreshTabs()
                    }
                    .doOnSubscribe {
                        disposableList.add(it)
                    }
                    .subscribe()
        } else {
            refreshTabs()
        }
    }

    private fun refreshTabs() {
        setupMenuViewPager(currentRestaurantId)
        Log.d("TestRxKotlin", "removeCategories doOnComplete")
    }

    private fun addItem(category: MenuCategory) {
        firestoreMenuRepo?.addItem(category, {}, {
            throw RuntimeException("Category ${category.name} could not be added")
        })
    }

    private fun updateItem(category: MenuCategory) {
        firestoreMenuRepo?.updateItem(category, {}, {
            throw RuntimeException("Category ${category.name} could not be updated")
        })
    }

    private fun deleteItem(category: MenuCategory) {
        firestoreMenuRepo?.removeItem(category.id, {}, {
            throw RuntimeException("Category ${category.name} could not be deleted")
        })
    }

    fun dispose() {
        firestoreMenuRepo?.removeCollectionListener(tag)
        for (disposable in disposableList) {
            disposable.dispose()
        }
    }

    class Builder{

        var categoriesHandler = MenuCategoriesUpdateHandler()
        fun setViewPager(menuViewPager: ViewPager?): Builder {
            categoriesHandler.setViewPager(menuViewPager)
            return this
        }

        fun setTabs(menuTabs: TabLayout?): Builder {
            categoriesHandler.setMenuTabs(menuTabs)
            return this
        }

        fun setFragmentManager(supportFragmentManager: FragmentManager?): Builder {
            categoriesHandler.setFragmentManager(supportFragmentManager)
            return this
        }

        fun build(): MenuCategoriesUpdateHandler? {
            return categoriesHandler
        }

    }

    private fun setFragmentManager(supportFragmentManager: FragmentManager?) {
        this.supportFragmentManager = supportFragmentManager
    }

    private fun setMenuTabs(menuTabs: TabLayout?) {
        this.menuTabs = menuTabs
    }

    private fun setViewPager(menuViewPager: ViewPager?) {
        this.menuViewPager = menuViewPager
    }

    override fun onTabReselected(tab: TabLayout.Tab?) {
    }

    override fun onTabUnselected(tab: TabLayout.Tab?) {
    }

    override fun onTabSelected(tab: TabLayout.Tab?) {
        currentMenuCategory = tab?.tag as MenuCategory?
        categoryListener?.onCategoryChanged(currentMenuCategory!!)
    }
}