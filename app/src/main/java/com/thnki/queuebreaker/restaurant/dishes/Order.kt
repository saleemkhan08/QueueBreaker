package com.thnki.queuebreaker.restaurant.dishes

import com.thnki.queuebreaker.home.FirestoreDocumentModel
import com.thnki.queuebreaker.model.User

class Order : FirestoreDocumentModel() {
    var dishes: Dishes? = null
    var count = 0
    var user: User? = null
}
