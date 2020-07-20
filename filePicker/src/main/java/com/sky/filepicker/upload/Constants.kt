package com.sky.filepicker.upload

import android.Manifest

object Constants {
    const val UPLOAD_FILE_REQUEST = 6001
    const val UPLOAD_FILE_RESULT = 6002
    const val REQUEST_EXTERNAL_STORAGE = 6003
    val PERMISSIONS_STORAGE = arrayOf(
        Manifest.permission.READ_EXTERNAL_STORAGE
    )
}