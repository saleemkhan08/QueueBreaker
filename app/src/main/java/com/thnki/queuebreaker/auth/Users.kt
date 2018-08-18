package com.thnki.queuebreaker.auth

class Users {
    public constructor()
    public constructor(phoneNumber: String, userType: String, restaurantId: String) {
        this.phoneNumber = phoneNumber
        this.userType = userType
        this.userId = phoneNumber.replace("[^a-zA-Z0-9]+", "")
        this.restaurantId = restaurantId
    }

    public constructor(phoneNumber: String) {
        this.phoneNumber = phoneNumber
        this.userId = phoneNumber.replace("[^a-zA-Z0-9]+", "")
    }

    var userId: String = ""
    var userType: String = ""
    var phoneNumber: String = ""
    var restaurantId: String = ""


}