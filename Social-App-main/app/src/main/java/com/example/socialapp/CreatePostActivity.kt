package com.example.socialapp

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.socialapp.daos.PostDao
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import kotlinx.android.synthetic.main.activity_create_post.*
import java.util.*

class CreatePostActivity : AppCompatActivity() {

    private lateinit var postDao: PostDao
    private val PICK_IMAGE_REQUEST = 71
//    private val imageUrl : String = ""
  //  var imageUrl : String? = null
   // val file_Uri : Uri = TODO()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_create_post)

        postDao = PostDao()


      //  chooseButton.setOnClickListener { launchGallery() }

//        postButton.setOnClickListener {
//            val input = postInput.text.toString().trim()
//
//            if(input.isNotEmpty()) {
//
//                postDao.addPost(input,imageUrl)
//
//                finish()
//            }
//            else{
//
//            }
//        }

        chooseButton.setOnClickListener {
            //check runtime permission
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M){
                if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) ==
                    PackageManager.PERMISSION_DENIED){
                    //permission denied
                    val permissions = arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE);
                    //show popup to request runtime permission
                    requestPermissions(permissions, CreatePostActivity.PERMISSION_CODE);
                }
                else{
                    //permission already granted
                    pickImageFromGallery();
                }
            }
            else{
                //system OS is < Marshmallow
                pickImageFromGallery();
            }
        }


        setUpRecyclerView()
    }

    private fun uploadImageToFirebase(fileUri: Uri ) {
        if (fileUri != null) {
            val fileName = UUID.randomUUID().toString() +".jpg"

            val database = FirebaseDatabase.getInstance()
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener(
                    OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                        taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                          val imageUrl = it.toString()

                            val input =  postInput.text.toString().trim()

                            if(input.isNotEmpty()) {

                            postDao.addPost(input,imageUrl)


            }





                        }
                    })

                ?.addOnFailureListener(OnFailureListener { e ->
                    print(e.message)
                })
        }
    }

    private fun pickImageFromGallery() {
        //Intent to pick image
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = "image/*"
        startActivityForResult(intent, IMAGE_PICK_CODE)
    }

    companion object {
        //image pick code
        private val IMAGE_PICK_CODE = 1000;
        //Permission code
        private val PERMISSION_CODE = 1001;
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when(requestCode){
            CreatePostActivity.PERMISSION_CODE -> {
                if (grantResults.size >0 && grantResults[0] ==
                    PackageManager.PERMISSION_GRANTED){
                    //permission from popup granted
                    pickImageFromGallery()
                }
                else{
                    //permission from popup denied
                    Toast.makeText(this, "Permission denied", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

//    private fun launchGallery() {
//        val intent = Intent()
//        intent.type = "image/*"
//        intent.action = Intent.ACTION_GET_CONTENT
//        startActivityForResult(Intent.createChooser(intent, "Select Picture"), PICK_IMAGE_REQUEST)
//
//    }


    private fun setUpRecyclerView() {

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == CreatePostActivity.IMAGE_PICK_CODE){
            imagepreview.setImageURI(data?.data)
            val file_uri = data?.data



//            postButton.setOnClickListener {
//                val input = postInput.text.toString().trim()
//                    // uploadImageToFirebase(file_uri)
            postButton.setOnClickListener {
                if (file_uri != null) {
                    uploadImageToFirebase(file_uri)
                }
                finish()
            }
//                    if(input.isNotEmpty()) {
//                        imageUrl?.let { it1 -> postDao.addPost(input, it1) }
//                        finish()
//                    }
//                    else{
//
//                    }
//            }

//            uploadbtn.setOnClickListener()
//            {
//                if (file_uri != null) {
//                    uploadImageToFirebase(file_uri)
//                }
//            }
        }
    }


}

