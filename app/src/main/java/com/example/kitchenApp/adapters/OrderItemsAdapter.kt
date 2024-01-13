package com.example.kitchenApp.adapters
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenApp.R
import com.example.kitchenApp.models.Order
import com.example.kitchenApp.models.OrderItem

class OrderItemsAdapter(private val orderItemsList: ArrayList<OrderItem>,
                        private val activated:Int, //n=0, acc=1, ready=2
                        private val activated2:Boolean,
                        private val onItemButtonClicked: ((itemClicked: OrderItem)->Unit)): RecyclerView.Adapter<OrderItemsAdapter.OrdersItemsViewHolder>(){

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrdersItemsViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_list,parent, false )
        return OrdersItemsViewHolder(itemView)
    }

    override fun getItemCount(): Int {
        return orderItemsList.size
    }

    override fun onBindViewHolder(holder: OrdersItemsViewHolder, position: Int) {
        val currentItem = orderItemsList[position]
        holder.orderName.text = currentItem.orderName
        holder.btnAcceptOrder.isActivated = false
        holder.btnAcceptOrder.visibility = View.VISIBLE
        if(activated == 1){
            holder.btnAcceptOrder.isActivated = true
            holder.btnAcceptOrder.text ="SPREMNO"
            val boja = Color.parseColor("#72bb52")
            holder.btnAcceptOrder.setTextColor(Color.argb(255, Color.red(boja), Color.green(boja), Color.blue(boja)))
        }
        else if(activated == 2){
            holder.btnAcceptOrder.isActivated = false
            holder.btnAcceptOrder.text ="Vrati u narudžbe"
            holder.btnAcceptOrder.visibility = View.INVISIBLE
            val boja = Color.parseColor("#5F5F5F")
            holder.btnAcceptOrder.setTextColor(Color.argb(255, Color.red(boja), Color.green(boja), Color.blue(boja)))

        }
        if(activated2){
            val boja = Color.parseColor("#acaaa9")
            holder.btnAcceptOrder.setBackgroundColor(Color.argb(255, Color.red(boja), Color.green(boja), Color.blue(boja)))
        }


        holder.btnAcceptOrder.setOnClickListener {
            onItemButtonClicked(currentItem)
        }
    }

    class OrdersItemsViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){

        val orderName: TextView = itemView.findViewById(R.id.txtVOrder)
        val btnAcceptOrder: Button = itemView.findViewById(R.id.btnAcceptOrder)

    }

}