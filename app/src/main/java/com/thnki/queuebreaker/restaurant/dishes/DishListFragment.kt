package com.thnki.queuebreaker.restaurant.dishes

import android.app.Activity.RESULT_OK
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.widget.LinearLayoutManager
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.StorageReference
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.CollectionListener
import com.thnki.queuebreaker.model.Progress
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.OnDismissListener
import com.thnki.queuebreaker.restaurant.OrderListener
import com.thnki.queuebreaker.restaurant.categories.MenuCategory
import com.thnki.queuebreaker.utils.TransitionUtil
import io.reactivex.Observable
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.edit_dish_options.*
import kotlinx.android.synthetic.main.fragment_dish_list.*

class DishListFragment : Fragment(), CollectionListener<Dishes>,
        OnDismissListener<Dishes>, OnEditDishListener, OnBackPressedListener {

    var restaurantId = ""
    var category: MenuCategory? = null
    private var dishList: ArrayList<Dishes>? = null
    private var dishRepository: FirestoreDishRepository? = null
    private var adapter: FirestoreDishesAdapter? = null
    private var currentDish: Dishes? = null
    private var storageRef: StorageReference? = null

    companion object {
        fun newInstance(restaurantId: String, category: MenuCategory?): DishListFragment {
            val dishFragment = DishListFragment()
            dishFragment.restaurantId = restaurantId
            dishFragment.category = category
            dishFragment.storageRef = FirebaseStorage.getInstance().reference
                    .child(restaurantId).child(category!!.id)
            return dishFragment
        }

        const val TAG = "DishListFragment"
        const val DISH_IMAGE_REQUEST_CODE = 123
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        return inflater.inflate(R.layout.fragment_dish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        setupDishRepo()
        addNewDish.setOnClickListener({ showAddDishOptions() })
        editDish.setOnClickListener({ showEditDishListFragment() })
        saveDish.setOnClickListener({ addDish() })
        cancelAdd.setOnClickListener({ cancelDishEdit() })
        editPhoto.setOnClickListener({ editDishPhoto() })
    }

    override fun onCollectionChanged(collection: ArrayList<Dishes>?) {
        this.dishList = collection
        dishesList.layoutManager = LinearLayoutManager(activity)
        adapter = FirestoreDishesAdapter(this, activity as OrderListener, collection)
        dishesList.adapter = adapter
        Progress.hide()
    }

    private fun setupDishRepo() {
        dishRepository = FirestoreDishRepository.getInstance(restaurantId, category?.id)
        dishRepository?.addCollectionListener(this, TAG)
    }

    private fun cancelDishEdit() {
        editOptionsVisibility(View.GONE)
    }

    private fun showAddDishOptions() {
        currentDish = null
        editOptionsVisibility(View.VISIBLE)
    }

    private fun editOptionsVisibility(visibility: Int) {
        TransitionUtil.slideTransition(editDishOptions as ViewGroup)
        editDishOptions.visibility = visibility
        dishName.setText(currentDish?.name)
        dishPrice.setText(currentDish?.price)
        dishDescription.setText(currentDish?.description)
        editDishImage.setImageURI(currentDish?.image)
    }

    private fun editDishPhoto() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, DISH_IMAGE_REQUEST_CODE)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, intent: Intent?) {
        super.onActivityResult(requestCode, resultCode, intent)
        if (requestCode == DISH_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            val uri: Uri = intent!!.data
            editDishImage.setImageURI(uri, activity)

            val fileRef = storageRef?.child(currentDish!!.id)

            fileRef?.putFile(uri)?.addOnSuccessListener {
                it.storage.downloadUrl.addOnSuccessListener {
                    currentDish!!.image = it.toString()
                    updateItem(currentDish!!)
                    adapter?.notifyItemImageUploaded(currentDish!!.id, it)
                }

            }?.addOnFailureListener {
                Log.d("ImageUpdate", "addOnFailureListener")
                adapter?.notifyItemImageUploadFailed(currentDish!!.id)
            }
            adapter?.notifyItemImageUploadStarted(currentDish!!.id, uri)
        }
    }

    private fun showEditDishListFragment() {
        if (dishList!!.size > 0) {
            val editDishFragment = EditDishListFragment.getInstance(this, dishList)
            editDishFragment.show(activity?.supportFragmentManager, EditDishListFragment.TAG)
        } else {
            ToastMsg.show(R.string.dish_list_is_empty)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        dishRepository?.removeCollectionListener(TAG)
    }

    private val disposableList: ArrayList<Disposable> = ArrayList()

    override fun onDismiss(collection: java.util.ArrayList<Dishes>) {
        dishRepository?.removeCollectionListener(TAG)
        val removalList = java.util.ArrayList<Dishes>(dishList)
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

    private fun updateCategories(it: Dishes, removalList: java.util.ArrayList<Dishes>) {
        updateItem(it)
        removalList.remove(it)
    }

    private fun removeCategories(removalList: java.util.ArrayList<Dishes>) {
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
        setupDishRepo()
    }

    private fun updateItem(dish: Dishes) {
        dishRepository?.updateItem(dish, {}, {
            throw RuntimeException("Dish ${dish.name} could not be updated")
        })
    }

    private fun deleteItem(category: Dishes) {
        dishRepository?.removeItem(category.id, {}, {
            throw RuntimeException("Dish ${category.name} could not be deleted")
        })
    }

    private fun onDishUpdated(dish: Dishes?) {
        editOptionsVisibility(View.GONE)
        Progress.show(R.string.saving)
        if (dish?.id == null) {
            dishRepository?.addItem(dish, {
                showMessage(getString(R.string.added))
                showAddedItem()
            }, {
                showMessage(dish?.name + " couldn't be added: ${it.message}")
            })
        } else {
            updateItem(dish)
        }
    }

    private fun showAddedItem() {

    }

    override fun onStop() {
        super.onStop()
        adapter?.removeOrderUpdateListener()
    }

    private fun showMessage(str: String) {
        Progress.hide()
        ToastMsg.show(str)
    }

    private fun addDish() {
        if (currentDish == null) {
            currentDish = Dishes()
        }
        currentDish?.name = dishName.text.toString()
        currentDish?.description = dishDescription.text.toString()
        currentDish?.price = dishPrice.text.toString()
        if (currentDish?.name == null || currentDish?.name!!.isEmpty()) {
            ToastMsg.show(R.string.please_enter_a_valid_name)
        } else if (currentDish?.price == null || currentDish?.price!!.isEmpty()) {
            ToastMsg.show(R.string.please_enter_a_valid_price)
        } else {
            onDishUpdated(currentDish)
        }
    }

    override fun editDish(dish: Dishes) {
        currentDish = dish
        editOptionsVisibility(View.VISIBLE)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        for (disposable in disposableList) {
            disposable.dispose()
        }
    }

    override fun onBackPressed(): Boolean {

        if (editDishOptions.visibility != View.GONE) {
            cancelDishEdit()
            return true
        }
        return false
    }
}