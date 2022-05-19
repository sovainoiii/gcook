package com.example.gcook

import android.app.ProgressDialog
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.gcook.Model.Food
import com.example.gcook.Model.Material
import com.example.gcook.Model.User
import com.example.gcook.databinding.ActivityAddFoodBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class AddFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddFoodBinding
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var listMaterial: ArrayList<Material>
    private lateinit var listStep: ArrayList<String>
    private val storageReference = FirebaseStorage.getInstance().getReference("images/")
    private val realtime = FirebaseDatabase.getInstance()
    private lateinit var food: Food
    private lateinit var progressDialog: ProgressDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityAddFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }

        binding.upImageFood.setOnClickListener {
            selectImage()
        }

        binding.addMaterial.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.item_materal_add, null)
            val delete = view.findViewById<Button>(R.id.delete_category)
            delete.setOnClickListener {
                binding.listAddMaterial.removeView(view)
            }
            binding.listAddMaterial.addView(view)
        }

        binding.addStep.setOnClickListener {
            val view = LayoutInflater.from(this).inflate(R.layout.item_step_add, null)
            val delete = view.findViewById<Button>(R.id.delete_step)
            delete.setOnClickListener {
                binding.listAddStep.removeView(view)
            }
            binding.listAddStep.addView(view)
        }

        binding.addFood.setOnClickListener {
            addFood()
        }

    }

    private fun addFood() {
        if(imageUri.toString() != "") {
            listMaterial = ArrayList()
            listStep = ArrayList()

            Log.d(ContentValues.TAG, imageUri.toString())

            for (i in 0 until binding.listAddMaterial.childCount) {
                val view: View = binding.listAddMaterial.getChildAt(i)
                val materialEdit = view.findViewById<EditText>(R.id.material)
                val qualityEdit = view.findViewById<EditText>(R.id.quality)
                val material = Material(
                    materialEdit.text.toString(),
                    qualityEdit.text.toString()
                )
                listMaterial.add(material)
            }

            for (i in 0 until binding.listAddStep.childCount) {
                val view: View = binding.listAddStep.getChildAt(i)
                val materialEdit = view.findViewById<EditText>(R.id.step)
                listStep.add(materialEdit.text.toString())
            }

            if(listMaterial.size > 0) {
                if(listStep.size > 0) {
                    addFoodToFirebase()
                    progressDialog = ProgressDialog(this)
                    progressDialog.setMessage("Đang thêm món ăn")
                    progressDialog.setCancelable(false)
                } else {
                    Toast.makeText(this, "Hãy thêm bước làm cho món ăn", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(this, "Hãy thêm nguyên liệu cho món ăn", Toast.LENGTH_SHORT).show()
            }
        } else {
            Toast.makeText(this, "Hãy chọn ảnh", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFoodToFirebase() {
        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        val uId = sharedPref.getString("uId", null).toString()
        val key = realtime.getReference("foods").push().key.toString()
        val file = storageReference.child("food_${System.currentTimeMillis()}")
        file.putFile(imageUri)
            .addOnProgressListener {
                progressDialog.show()
            }
            .addOnSuccessListener {
                file.downloadUrl
                    .addOnSuccessListener {image ->
                        realtime.reference.child("users")
                            .child(uId)
                            .get()
                            .addOnSuccessListener {
                                food = Food(
                                    id = key,
                                    name = binding.nameFood.text.toString().toLowerCase(),
                                    timeUpdate = System.currentTimeMillis().toString(),
                                    des = binding.desFood.text.toString(),
                                    materials = listMaterial,
                                    steps = listStep,
                                    user = it.getValue(User::class.java)!!,
                                    imageUrl = image.toString()
                                )
                                realtime.getReference("foods").child(key).setValue(food)
                                    .addOnSuccessListener {
                                        Toast.makeText(this, "Thêm thành công", Toast.LENGTH_SHORT).show()
                                        startActivity(Intent(this@AddFoodActivity, HomeActivity::class.java))
                                    }
                            }
                    }
                progressDialog.dismiss()
            }
    }

    private fun selectImage() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == 100) {
            imageUri = data?.data!!
            binding.imageFood.setImageURI(imageUri)
        }

    }

}