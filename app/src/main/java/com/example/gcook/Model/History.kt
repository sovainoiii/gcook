package com.example.gcook.Model

data class History(
    var id: String = "",
    var food: Food = Food(),
    var user: User = User()
)
