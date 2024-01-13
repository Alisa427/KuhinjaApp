package com.example.kitchenApp

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.kitchenApp.adapters.OrdersAdapter
import com.example.kitchenApp.models.Order
import com.example.kitchenApp.models.OrderItem
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MainActivity_Kuhar : AppCompatActivity() {

    var nurl: String? = null
    private lateinit var ordersRecyclerView: RecyclerView
    var currentOrderList: ArrayList<Order> = ArrayList()
    var acceptedOrderList: ArrayList<Order> = ArrayList()
    var readyOrderList: ArrayList<Order> = ArrayList()

    fun createLists(){
        var orderItems = arrayListOf<OrderItem>(OrderItem("Margarita", 0, true),
            OrderItem("Pileći sendvič", 0, true),
            OrderItem("Begova corba", 1, true),
            OrderItem("Pahuljice", 0, true),
            OrderItem("Pileća salata", 2, true),
            OrderItem("Tartufi", 10, true))
        this.currentOrderList = arrayListOf(Order(1, false, orderItems),
            Order(2, false, orderItems),
            Order(3, true, orderItems),
            Order(4, true, orderItems),
            Order(5, false, orderItems),
            Order(6, false, orderItems))
    }
    fun fixCookVisibility(checkList: ArrayList<Order>){
        //Funkcija koja prikazuje ikonu kuhara kada nema kartica i mijenja male brojeve od kartica u desnom ćošku
        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)

        val imgCook = findViewById<ImageView>(R.id.imgCook)
        val txtNoCards = findViewById<TextView>(R.id.txtNoCards)
        if (checkList.isEmpty()) {
            imgCook.visibility = View.VISIBLE
            txtNoCards.visibility = View.VISIBLE
        } else {
            imgCook.visibility = View.INVISIBLE
            txtNoCards.visibility = View.INVISIBLE
        }

        txtNumberOfOrderCards.text = currentOrderList.size.toString()
        txtAcceptedCards.text = acceptedOrderList.size.toString()
        txtReadyCards.text = readyOrderList.size.toString()
    }

    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {

        val bazica = Firebase.firestore
        val komentar = hashMapOf(
            "text" to "Konobyyyy trebaš miiii!!!1!1!!",    //PORUKA ZA KONOBYA!!!!!!!!!!!!!!!!!
            "procitano" to false)


        super.onCreate(savedInstanceState)
        setContentView(R.layout.kitchen_activity) //android.R.
        createLists()
        fixCookVisibility(currentOrderList)
        //  Buttons Orders, Accepted and Ready :
        val btnAccept = findViewById<Button>(R.id.btnAccept)
        val btnOrders = findViewById<Button>(R.id.btnOrders)
        val btnReady = findViewById<Button>(R.id.btnReady)

        val buttonKonobar = findViewById<Button>(R.id.buttonKonobar) //PORUKA ZA KONOBARA !!!!!!!!!!!!!!
        buttonKonobar.setOnClickListener {
            val baza = Firebase.firestore.collection("notifikacije")
            val docRef = baza.document("notifikacije")
            docRef.update("kuhar_notifikacija", true)
                .addOnSuccessListener {
                    Log.d("kuar", "DocumentSnapshot successfully updated!")
                }
                .addOnFailureListener { e ->
                    Log.w("kuar", "Error updating document", e)
                }

            Toast.makeText(this, "Pozvali ste konobara!", Toast.LENGTH_SHORT).show()

            /*  docRef.get()
                .addOnSuccessListener { document ->
                    if (document != null) {
                        Log.d("kuar", "DocumentSnapshot data: ${document.get("kuhar_notifikacija").toString()}")
                    } else {
                        Log.d("kuar", "No such document")
                    }
                }*/
        }

        btnOrders.isActivated = true
        btnAccept.isActivated = false

        btnOrders.setOnClickListener {
            btnOrders.isActivated = true
            btnAccept.isActivated = false
            btnReady.isActivated = false
            getUserData()
        }

        btnAccept.setOnClickListener {
            btnAccept.isActivated = true
            btnOrders.isActivated = false
            btnReady.isActivated = false
            getUserDataAcc()
        }

        btnReady.setOnClickListener {
            btnOrders.isActivated = false
            btnAccept.isActivated = false
            btnReady.isActivated = true
            var adapterReady = OrdersAdapter(this.readyOrderList,2) { order, item, back ->
                run {
                    if(back=="from DayActivity to Acc"){
                        this.readyOrderList.remove(order)
                        if (acceptedOrderList.indexOfFirst { it.id == order.id } != -1) {
                            acceptedOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                        } else {
                            this.acceptedOrderList.add(order)
                        }
                    }
                    ordersRecyclerView.adapter?.notifyDataSetChanged()
                }
                fixCookVisibility(readyOrderList)
            }
            fixCookVisibility(readyOrderList)
            ordersRecyclerView.adapter = adapterReady
        }

        ordersRecyclerView = findViewById(R.id.orderItemsRecyclerView)
        ordersRecyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        ordersRecyclerView.setHasFixedSize(true)

        getUserData()
    }


    //Funkcije za izmjenu nizova:
    private fun getUserData() { //Pokreće se odlaskom na window Narudžbe
        var adapter = OrdersAdapter(this.currentOrderList, 0) { order, item, back ->
            run {
                if(back == "") {
                    if (item.orderName == "0") {//!accepted &&
                        if (acceptedOrderList.indexOfFirst { it.id == order.id } != -1) {
                            acceptedOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                        } else {
                            acceptedOrderList.add(order)
                        }
                        this.currentOrderList.remove(order)
                    } else {
                        orderItemInteraction(currentOrderList, acceptedOrderList, order, item)
                    }
                }

                ordersRecyclerView.adapter?.notifyDataSetChanged()
                fixCookVisibility(currentOrderList)
            }
        }
        ordersRecyclerView.adapter = adapter
        fixCookVisibility(currentOrderList)
    }

    private fun getUserDataAcc() { //Pokreće se odlaskom na window Prihvaćeno
        var adapter = OrdersAdapter(this.acceptedOrderList,1) { order, item, back ->
            run {
                if(back=="") {
                    if (item.orderName == "0") {//accepted &&
                        this.acceptedOrderList.remove(order)
                        if (readyOrderList.indexOfFirst { it.id == order.id } != -1) {
                            readyOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                        } else {
                            this.readyOrderList.add(order)
                        }
                    } else {
                        orderItemInteraction(acceptedOrderList, readyOrderList, order, item)
                    }
                }
                else if(back=="from Acc to Orders"){
                    this.acceptedOrderList.remove(order)
                    if (currentOrderList.indexOfFirst { it.id == order.id } != -1) {
                        currentOrderList.find { it.id == order.id }?.orderItems?.addAll(order.orderItems)
                    } else {
                        this.currentOrderList.add(order)
                    }
                    this.currentOrderList.sortBy { it.id}
                }
                ordersRecyclerView.adapter?.notifyDataSetChanged()

                fixCookVisibility(acceptedOrderList)
            }
        }
        ordersRecyclerView.adapter = adapter
        fixCookVisibility(acceptedOrderList)
    }


    //Funkcija koja briše narudžbe(item-e) u karticama prvog niza
    //i prebacuje ih u drugi niz:
    private fun orderItemInteraction(orderedArray: ArrayList<Order>, acceptedArray: ArrayList<Order>,
                                     order: Order, item: OrderItem){

        var txtAcceptedCards = findViewById<TextView>(R.id.txtAcceptedCards)
        var txtNumberOfOrderCards = findViewById<TextView>(R.id.txtNumberOfOrderCards)
        var txtReadyCards = findViewById<TextView>(R.id.txtReadyCards)

        var orderItemListTemp = ArrayList<OrderItem>(order.orderItems.toList())
        var orderTemp = Order(order.id, order.status, orderItemListTemp)

        var arrayTemp : ArrayList<OrderItem> = ArrayList()
        arrayTemp.add(item)
        var orderTempAcc = Order(order.id, order.status, arrayTemp)

        if(acceptedArray.indexOfFirst { it.id == order.id }!=-1){
            acceptedArray.find { it.id == order.id }?.orderItems?.add(item)
            orderTemp.orderItems.remove(item)
            orderedArray.find { it.id == order.id }?.orderItems=orderTemp.orderItems
        }
        else{
            orderTemp.orderItems.remove(item)
            orderedArray.find { it.id == order.id }?.orderItems=orderTemp.orderItems
            acceptedArray.add(orderTempAcc)
        }
        txtAcceptedCards.text = acceptedOrderList.size.toString()
        txtNumberOfOrderCards.text = currentOrderList.size.toString()
        txtReadyCards.text = readyOrderList.size.toString()
    }

}


