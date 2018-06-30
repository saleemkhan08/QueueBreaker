package com.thnki.queuebreaker.restaurant.dishes

import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.CollectionListener
import com.thnki.queuebreaker.model.Progress
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.MenuKotlinActivity
import com.thnki.queuebreaker.restaurant.OnDismissListener
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import java.util.*

class DishesUpdateHandler(currentRestaurantId: String, categoryId: String) :
        OnDismissListener<Dishes>, CollectionListener<Dishes> {

    private var firestoreDishesRepo: FirestoreDishRepository? = null

    private val tag = "MenuCategoriesUpdateHandler"
    var dishList: ArrayList<Dishes>? = ArrayList()
    private var disposableList: ArrayList<Disposable> = ArrayList()

    init {
        firestoreDishesRepo = FirestoreDishRepository.getInstance(currentRestaurantId, categoryId)
        firestoreDishesRepo?.addCollectionListener(this, tag)
    }

    override fun onCollectionChanged(collection: ArrayList<Dishes>?) {
        dishList = collection
        Progress.hide()
    }

    override fun onDismiss(collection: ArrayList<Dishes>) {
        firestoreDishesRepo?.removeCollectionListener(MenuKotlinActivity.TAG)

        val removalList = ArrayList<Dishes>(dishList)
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

    private fun updateCategories(it: Dishes, removalList: ArrayList<Dishes>) {
        updateItem(it)
        removalList.remove(it)
    }

    private fun removeCategories(removalList: ArrayList<Dishes>) {
        if (removalList.size > 0) {
            Observable.fromIterable(removalList)
                    .doOnNext {
                        deleteItem(it)
                    }
                    .doOnError {
                        ToastMsg.show(it.message)
                    }
                    .doOnComplete {
                        refreshList()
                    }
                    .doOnSubscribe {
                        disposableList.add(it)
                    }
                    .subscribe()
        } else {
            refreshList()
        }
    }

    private fun refreshList() {

    }

    private fun updateItem(dish: Dishes) {
        firestoreDishesRepo?.updateItem(dish, {}, {
            throw RuntimeException("Dish ${dish.name} could not be updated")
        })
    }

    private fun deleteItem(category: Dishes) {
        firestoreDishesRepo?.removeItem(category.id, {}, {
            throw RuntimeException("Dish ${category.name} could not be deleted")
        })
    }

    fun dispose() {
        firestoreDishesRepo?.removeCollectionListener(tag)
        for (disposable in disposableList) {
            disposable.dispose()
        }
    }


}