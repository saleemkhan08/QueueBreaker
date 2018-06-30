package com.thnki.queuebreaker.restaurant.dishes

import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.helper.ItemTouchHelper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.Window
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.home.FirestoreDocumentModel
import com.thnki.queuebreaker.restaurant.OnDismissListener
import com.thnki.queuebreaker.restaurant.OnStartDragListener
import com.thnki.queuebreaker.restaurant.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_edit_menu_categories.*

class EditDishListFragment<T : FirestoreDocumentModel> : DialogFragment(), OnStartDragListener {

    private var itemTouchHelper: ItemTouchHelper? = null
    private var adapter: FirestoreDishesListAdapter<T>? = null

    private var dishList: ArrayList<T>? = ArrayList()

    var onDismissListener: OnDismissListener<T>? = null

    companion object {
        @JvmStatic
        fun <T : FirestoreDocumentModel> getInstance(onDismissListener: OnDismissListener<T>?,
                                                     dishList: ArrayList<T>?): EditDishListFragment<T> {
            val editDishes = EditDishListFragment<T>()
            editDishes.onDismissListener = onDismissListener
            editDishes.dishList = ArrayList(dishList)
            return editDishes
        }

        const val TAG = "EditDishListFragment"
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?,
                              savedInstanceState: Bundle?): View? {
        val window = dialog.window
        window?.requestFeature(Window.FEATURE_NO_TITLE)
        dialog.setCanceledOnTouchOutside(false)
        return inflater.inflate(R.layout.fragment_edit_dish_list, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        closeDialog.setOnClickListener({ dismiss() })
        saveCategories.setOnClickListener({ saveCategoriesState() })
        recyclerView.setHasFixedSize(true)
        recyclerView.layoutManager = LinearLayoutManager(activity)
        adapter = FirestoreDishesListAdapter(dishList, this)

        recyclerView.adapter = adapter
        val callback = SimpleItemTouchHelperCallback(adapter!!)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        adapter?.notifyDataSetChanged()

        setTitleAndSubmitText()
    }

    private fun setTitleAndSubmitText() {
        if (dishList!![0] is Dishes) {
            dialogHeader.setText(R.string.edit_dishes_list)
            saveCategories.setText(R.string.save)
        } else {
            dialogHeader.setText(R.string.orders)
            saveCategories.setText(R.string.place_order)
        }

    }

    private fun saveCategoriesState() {
        dishList?.forEachIndexed { i, _ ->
            dishList!![i].setOrder(i)
        }
        onDismissListener?.onDismiss(dishList!!)
        dismiss()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

}
