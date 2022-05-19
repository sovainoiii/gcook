package com.example.gcook

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import com.example.gcook.databinding.ActivitySignInBinding
import com.google.android.gms.auth.api.signin.*
import com.google.android.gms.common.api.ApiException
import com.google.firebase.auth.*
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class SignInActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySignInBinding
    private lateinit var gsc: GoogleSignInClient
    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivitySignInBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        auth = Firebase.auth

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("657912493261-ec7554gqjgbhf82pqmq62361hc7kbghi.apps.googleusercontent.com")
            .requestEmail()
            .build()
        gsc = GoogleSignIn.getClient(this, gso)

        binding.signInBtn.setOnClickListener {
            signIn()
        }

    }

    private fun signIn() {
        val signInIntent = gsc.signInIntent
        startActivityForResult(signInIntent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 100) {
            val task = GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account = task.getResult(ApiException::class.java)
                firebaseAuthWithGoogle(account.idToken!!)
            } catch (e: ApiException) {
                Toast.makeText(this, "Failed!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun firebaseAuthWithGoogle(idToken: String) {
        val credential = GoogleAuthProvider.getCredential(idToken, null)
        val db = FirebaseDatabase.getInstance().reference.child("users")
        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        val editor = sharedPref.edit()
        auth.signInWithCredential(credential)
            .addOnCompleteListener(this) {  task ->
                if(task.isSuccessful) {
                    val user = auth.currentUser
                    db.child(user!!.uid).get()
                        .addOnSuccessListener {
                            if(it.exists()) {
                                editor.putString("uId", it.child("uid").value.toString()).apply()
                                startActivity(Intent(this, HomeActivity::class.java))
                            } else {
                                updateUI(user)
                            }
                        }
                } else {
                    updateUI(null)
                }
            }
    }
    private fun updateUI(user: FirebaseUser?) {
        if(user != null) {
            val intent = Intent(this@SignInActivity, RegisterActivity::class.java)
            intent.putExtra("id", user.uid)
            intent.putExtra("avatarUrl", user.photoUrl.toString())
            intent.putExtra("email", user.email)
            intent.putExtra("displayName", user.displayName)
            startActivity(intent)
            finish()
        }
    }

}