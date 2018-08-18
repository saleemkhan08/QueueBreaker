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
import com.thnki.queuebreaker.restaurant.ItemTouchHelperAdapter
import com.thnki.queuebreaker.restaurant.OnStartDragListener
import com.thnki.queuebreaker.restaurant.OrderListener
import com.thnki.queuebreaker.restaurant.SimpleItemTouchHelperCallback
import kotlinx.android.synthetic.main.fragment_edit_menu_categories.*

class OrdersListFragment : DialogFragment(), OnStartDragListener {

    private var itemTouchHelper: ItemTouchHelper? = null
    private var orderList: ArrayList<Order>? = ArrayList()
    var orderListener: OrderListener? = null
    var adapter: FirestoreOrderListAdapter? = null

    companion object {
        @JvmStatic
        fun getInstance(orderListener: OrderListener,
                        orderList: ArrayList<Order>?): OrdersListFragment {
            val editDishes = OrdersListFragment()
            editDishes.orderListener = orderListener
            editDishes.orderList = ArrayList(orderList)
            return editDishes
        }

        const val TAG = "OrdersListFragment"
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
        setupRecyclerView()
        setTitleAndSubmitText()
    }

    private fun setupRecyclerView() {

        adapter = FirestoreOrderListAdapter(orderList as ArrayList<Order>, this, orderListener)
        recyclerView.adapter = adapter
        val callback = SimpleItemTouchHelperCallback(adapter as ItemTouchHelperAdapter)
        itemTouchHelper = ItemTouchHelper(callback)
        itemTouchHelper?.attachToRecyclerView(recyclerView)
        recyclerView.adapter.notifyDataSetChanged()
    }

    private fun setTitleAndSubmitText() {
        dialogHeader.setText(R.string.orders)
        saveCategories.setText(R.string.place_order)
    }

    private fun saveCategoriesState() {
//        orderList?.forEachIndexed { i, _ ->
//            orderList!![i].setOrder(i)
//        }
//        onDismissListener?.onDismiss(orderList!!)
        orderListener?.onOrderPlaced(orderList)
        dismiss()
    }

    override fun onStartDrag(viewHolder: RecyclerView.ViewHolder) {
        itemTouchHelper?.startDrag(viewHolder)
    }

}
