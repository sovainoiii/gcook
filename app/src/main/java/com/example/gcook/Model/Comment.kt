package com.example.gcook.Model

data class Comment(
    var id: String = "",
    var time: String = "0",
    var content: String = "",
    var user: User = User(),
    var food: Food = Food()
)
