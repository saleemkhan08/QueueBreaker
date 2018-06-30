package com.thnki.queuebreaker.restaurant.dishes

import com.thnki.queuebreaker.home.FirestoreDocumentModel

class Dishes : FirestoreDocumentModel() {
    var name: String? = null
    var description: String? = null
    var image: String? = null
    var price: String? = null

    override fun equals(other: Any?): Boolean {
        return other is Dishes && other.id == id
    }

    override fun hashCode(): Int {
        var result = name?.hashCode() ?: 0
        result = 31 * result + (description?.hashCode() ?: 0)
        result = 31 * result + (image?.hashCode() ?: 0)
        result = 31 * result + (price?.hashCode() ?: 0)
        return result
    }
}