package com.example.gcook.Model

import android.text.Editable

data class User(
    var uId: String = "",
    var displayName: String ="",
    var email: String = "",
    var avatarUrl: String = "",
    var rule: String = "0"
)