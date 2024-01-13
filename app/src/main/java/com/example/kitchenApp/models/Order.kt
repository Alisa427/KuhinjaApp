package com.example.kitchenApp.models

data class Order(var id: Int, var status: Boolean, var orderItems: ArrayList<OrderItem>) //True za za take away
