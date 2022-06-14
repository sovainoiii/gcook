package com.example.gcook.Model

data class Food (
    var id:String = "",
    var name: String ="",
    var imageUrl: String = "",
    var timeUpdate: String = "",
    var des: String = "",
    var user: User = User(),
    var materials: ArrayList<Material> = ArrayList(),
    var steps: ArrayList<String> = ArrayList()
)
