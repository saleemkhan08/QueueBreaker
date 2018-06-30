package com.thnki.queuebreaker.restaurant.categories


import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.restaurant.OnDismissListener
import com.thnki.queuebreaker.restaurant.OnStartDragListener
import com.thnki.queuebreaker.restaurant.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_edit_menu_categories.*

class EditMenuCategories : DialogFragment(), OnStartDragListener {

    private var itemTouchHelper: ItemTouchHelper? = null

    private var collection: ArrayList<MenuCategory>? = ArrayList()

    var currentRestaurantId = ""

    var onDismissListener: OnDismissListener<MenuCategory>? = null

    companion object {
        @JvmStatic
        fun getInstance(onDismissListener: OnDismissListener<MenuCategory>?, currentRestaurantId: String, collection: List<MenuCategory>?): EditMenuCategories {
            val editMenuCategories = EditMenuCategories()
            editMenuCategories.onDismissListener = onDismissListener
            editMenuCategories.currentRestaurantId = currentRestaurantId
            editMenuCategories.collection = ArrayList(collection)
            return editMenuCategories
        }

        const val TAG = "EditMenuCategories"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.fragment_edit_menu_categories, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeDialog.setOnClickListener({ dismiss() })
        addNewCategory.setOnClickListener({ addCategory() })
        saveCategories.setOnClickListener({ saveCategoriesState() })
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = FirestoreMenuCategoryAdapter(collection, this)
        recyclerView.adapter = adapter
        val callback = SimpleItemTouchHelperCallback(adapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        adapter?.notifyDataSetChanged()

    }

    private fun saveCategoriesState() {
        Log.d("TestEquals", "saveCategoriesState")
        collection?.forEachIndexed { i, _ ->
            collection!![i].setOrder(i)
        }
        onDismissListener?.onDismiss(collection!!)
        dismiss()
    }

    private var adapter: FirestoreMenuCategoryAdapter? = null

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

    private fun addCategory() {
        val name = newCategory.text.toString()
        if (name.isEmpty()) {
            ToastMsg.show(R.string.please_enter_a_valid_name)
        } else {
            val size = collection?.size!!
            collection?.add(MenuCategory(name, size))
            scrollToPosition(size)
            adapter?.notifyItemInserted(size)
            newCategory.setText("")
        }
    }

    private fun scrollToPosition(position: Int) {
        recyclerView.layoutManager.scrollToPosition(position)
    }
}
