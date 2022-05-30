package com.example.gcook.UI.Auth

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.bumptech.glide.Glide
import com.bumptech.glide.request.RequestOptions
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.Model.User
import com.example.gcook.databinding.ActivityRegisterBinding
import com.google.firebase.database.*

class RegisterActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegisterBinding
    private lateinit var db: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {

        binding = ActivityRegisterBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        db = FirebaseDatabase.getInstance().getReference("users")

        Glide.with(this)
            .load(intent.getStringExtra("avatarUrl"))
            .apply(RequestOptions.circleCropTransform())
            .into(binding.avt)
        binding.displayName.setText(intent.getStringExtra("displayName"))
        binding.email.setText(intent.getStringExtra("email"))

        val uId = intent.getStringExtra("id").toString()
        val email = intent.getStringExtra("email").toString()
        val  avatarUrl = intent.getStringExtra("avatarUrl").toString()

        //Shared Preferences
        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()

        binding.register.setOnClickListener {
            val displayName = binding.displayName.text.toString()
            if(displayName != "") {
                val user = User(uId,displayName, email,avatarUrl,"0")
                db.child(uId).setValue(user)
                    .addOnSuccessListener {
                        editor.putString("uId", intent.getStringExtra("id")).apply()
                        startActivity(Intent(this, HomeActivity::class.java))
                    }
                    .addOnFailureListener {
                        Toast.makeText(this@RegisterActivity, "Đăng ký không thành công!", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this@RegisterActivity, "Hãy nhập tên hiển thị", Toast.LENGTH_SHORT).show()
            }
        }
    }

}