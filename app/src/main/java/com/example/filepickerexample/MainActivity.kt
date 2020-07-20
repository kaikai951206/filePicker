package com.example.filepickerexample

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.sky.filepicker.upload.Constants
import com.sky.filepicker.upload.LocalUpdateActivity
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        tv.setOnClickListener {
            val intent = Intent(this, LocalUpdateActivity::class.java)
            intent.putExtra("maxNum", 3)
            startActivityForResult(
                intent,
                Constants.UPLOAD_FILE_REQUEST
            )
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Constants.UPLOAD_FILE_RESULT && requestCode == Constants.UPLOAD_FILE_REQUEST) {
            val pathList = data?.getStringArrayListExtra("pathList")
            if (pathList != null && pathList.isNotEmpty()) {
                for (path in pathList) {
                    Log.d("filePicker", "$path")
                }
            }
        }
    }

}