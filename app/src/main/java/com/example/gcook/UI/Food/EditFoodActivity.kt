package com.example.gcook.UI.Food

import android.app.ProgressDialog
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.bumptech.glide.Glide
import com.example.gcook.Model.Food
import com.example.gcook.Model.Material
import com.example.gcook.Model.User
import com.example.gcook.R
import com.example.gcook.UI.Home.HomeActivity
import com.example.gcook.databinding.ActivityEditFoodBinding
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage

class EditFoodActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditFoodBinding
    private val database = FirebaseDatabase.getInstance()
    private lateinit var foodId: String
    private lateinit var userId: String
    private lateinit var food: Food
    private var imageUri: Uri = Uri.EMPTY
    private lateinit var listMaterial: ArrayList<Material>
    private lateinit var listStep: ArrayList<String>
    private lateinit var progressDialog: ProgressDialog
    private val storageReference = FirebaseStorage.getInstance().getReference("images/")

    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityEditFoodBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.back.setOnClickListener {
            finish()
        }
        foodId = intent.getStringExtra("foodId").toString()

        val sharedPref = getSharedPreferences("GCookPref", Context.MODE_PRIVATE)
        userId = sharedPref.getString("uId", null).toString()

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

        loadEdit()
    }

    private fun loadEdit() {
        database.reference.child("foods").child(foodId).get()
            .addOnSuccessListener {
                val food = it.getValue(Food::class.java)
                Glide.with(this)
                    .load(food!!.imageUrl)
                    .into(binding.imageFood)

                binding.nameFood.setText(food.name)

                binding.desFood.setText(food.des)

//              Load danh sach nguyen lieu
                val listMaterial: ArrayList<Material> = ArrayList()
                for(materialItem in it.child("materials").children) {
                    val material = materialItem.getValue(Material::class.java)
                    listMaterial.add(material!!)
                }
                loadMaterial(listMaterial)

//              Load danh sach buoc lam
                val listStep: ArrayList<String> = ArrayList()
                for(stepItem in it.child("steps").children) {
                    listStep.add(stepItem!!.value.toString())
                }

                binding.upImageFood.setOnClickListener {
                    selectImage()
                }

                loadStep(listStep)

                binding.editFood.setOnClickListener {
                    editFood()
                }

            }
    }

    private fun editFood() {
        listMaterial = ArrayList()
        listStep = ArrayList()

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
            val stepEdit = view.findViewById<EditText>(R.id.step)
            listStep.add(stepEdit.text.toString())
        }

        if(listMaterial.size > 0 && listStep.size > 0) {
            progressDialog = ProgressDialog(this)
            progressDialog.setMessage("Đang sửa công thức")
            progressDialog.setCancelable(false)
            addFoodToFirebase(imageUri)
        } else {
            Toast.makeText(this, "Điền đầy đủ thông tin", Toast.LENGTH_SHORT).show()
        }
    }

    private fun addFoodToFirebase(imageUri: Uri) {
        progressDialog.show()
        if(imageUri.toString().isEmpty()) {
            database.getReference("foods/$foodId/imageUrl").get()
                .addOnSuccessListener { imageUrl ->
                    database.getReference("users/$userId").get()
                        .addOnSuccessListener {
                            val user = it.getValue(User::class.java)
                            food = Food(
                                id = foodId,
                                name = binding.nameFood.text.toString().toLowerCase(),
                                timeUpdate = System.currentTimeMillis().toString(),
                                des = binding.desFood.text.toString(),
                                materials = listMaterial,
                                steps = listStep,
                                user = user!!,
                                imageUrl = imageUrl.value.toString()
                            )
                            database.getReference("foods").child(foodId).setValue(food)
                                .addOnSuccessListener {
                                    Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show()
                                    progressDialog.dismiss()
                                    startActivity(Intent(this@EditFoodActivity, HomeActivity::class.java))
                                }
                        }
                }
        } else {
            val file = storageReference.child("food_${System.currentTimeMillis()}")
            file.putFile(imageUri)
                .addOnProgressListener {
                    progressDialog.show()
                }
                .addOnSuccessListener {
                    file.downloadUrl
                        .addOnSuccessListener {image ->
                            database.reference.child("users").child(userId)
                                .get()
                                .addOnSuccessListener {
                                    food = Food(
                                        id = foodId,
                                        name = binding.nameFood.text.toString().toLowerCase(),
                                        timeUpdate = System.currentTimeMillis().toString(),
                                        des = binding.desFood.text.toString(),
                                        materials = listMaterial,
                                        steps = listStep,
                                        user = it.getValue(User::class.java)!!,
                                        imageUrl = image.toString()
                                    )
                                    database.getReference("foods").child(foodId).setValue(food)
                                        .addOnSuccessListener {
                                            Toast.makeText(this, "Sửa thành công", Toast.LENGTH_SHORT).show()
                                            progressDialog.dismiss()
                                            startActivity(Intent(this@EditFoodActivity, HomeActivity::class.java))
                                        }
                                }
                        }
                }
        }
    }

    private fun loadMaterial(listMaterial: ArrayList<Material>) {
        for (i in 0 until listMaterial.size) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_materal_add, null)
            view.findViewById<EditText>(R.id.material).setText(listMaterial[i].name)
            view.findViewById<EditText>(R.id.quality).setText(listMaterial[i].quality)
            binding.listAddMaterial.addView(view)
        }
    }

    private fun loadStep(listStep: ArrayList<String>) {
        for (i in 0 until listStep.size) {
            val view = LayoutInflater.from(this).inflate(R.layout.item_step_add, null)
            view.findViewById<EditText>(R.id.step).setText(listStep[i])
            binding.listAddStep.addView(view)
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