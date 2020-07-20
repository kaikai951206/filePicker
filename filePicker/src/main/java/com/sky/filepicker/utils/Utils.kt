package com.sky.filepicker.utils

import android.annotation.SuppressLint
import android.content.Context
import android.graphics.*
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import android.util.DisplayMetrics
import android.util.Log
import android.view.WindowManager
import androidx.exifinterface.media.ExifInterface
import java.io.*
import java.math.BigDecimal
import java.text.DecimalFormat
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

object Utils {

    private var lastClickTime = 0L
    private const val MIN_TIME = 1000L

    /**
     * 防止过快点击
     */
    fun isFastDoubleClick(): Boolean {
        val time = System.currentTimeMillis()
        return if (time - lastClickTime < MIN_TIME) {
            true
        } else {
            lastClickTime = time
            false
        }
    }

    /**
     * 时间戳转日期
     */
    fun stampToDate(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = Date(long)
        return simpleDateFormat.format(date)
    }

    fun stampToDateYMD(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        val date = Date(long)
        return simpleDateFormat.format(date)
    }

    fun stampToDateMD(long: Long): String {
        val simpleDateFormat = SimpleDateFormat("MM-dd", Locale.getDefault())
        val date = Date(long)
        return simpleDateFormat.format(date)
    }



    /**
     * 日期转时间戳
     */
    fun dateToStamp(s: String): Int {
        val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
        val date = simpleDateFormat.parse(s)
        return (date.time / 1000).toInt()
    }

    /**
     * 获取手机分辨率
     */
    fun getPhoneDis(context: Context): Point {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getMetrics(displayMetrics)
        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels
        return Point(widthPixels, heightPixels)
    }

    /**
     * 获取手机真是分辨率
     */
    @SuppressLint("NewApi")
    fun getPhoneRealDis(context: Context): Point {
        val displayMetrics = DisplayMetrics()
        val windowManager = context.getSystemService(Context.WINDOW_SERVICE) as WindowManager
        windowManager.defaultDisplay.getRealMetrics(displayMetrics)
        val widthPixels = displayMetrics.widthPixels
        val heightPixels = displayMetrics.heightPixels
        return Point(widthPixels, heightPixels)
    }


    /**
     * 图片质量压缩
     * @param quality 图片的质量，0-100，数值越小质量越差
     */
    fun imgQualityCompressed(context: Context, quality: Int, originFile: File): File? {
        val originBitmap = BitmapFactory.decodeFile(originFile.absolutePath)
        val bos = ByteArrayOutputStream()
        originBitmap.compress(Bitmap.CompressFormat.JPEG, quality, bos)
        val resultFile =
            File(context.getExternalFilesDir("img"), "${System.currentTimeMillis()}.jpg")
        return try {
            val fos = FileOutputStream(resultFile)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
            resultFile
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**
     * 图片采样率压缩
     * @param inSampleSize  可以根据需求计算出合理的inSampleSize
     */
    fun imgSamplingCompressed(context: Context, inSampleSize: Int, originFile: File): File? {
        val options: BitmapFactory.Options = BitmapFactory.Options()
        //设置此参数是仅仅读取图片的宽高到options中，不会将整个图片读到内存中，防止oom
        options.inJustDecodeBounds = true
        val empryBitmap = BitmapFactory.decodeFile(originFile.absolutePath, options)
        options.inJustDecodeBounds = false
        options.inSampleSize = inSampleSize
        val resultBitmap = BitmapFactory.decodeFile(originFile.absolutePath, options)
        val bos = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val resultFile =
            File(context.getExternalFilesDir("img"), "${System.currentTimeMillis()}.jpg")
        return try {
            val fos = FileOutputStream(resultFile)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
            resultFile
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }

    /**缩放压缩
     * @param radio 缩放比例
     */
    fun imgScaleCompressed(context: Context, radio: Int, originFile: File): File? {
        val originBitmap = BitmapFactory.decodeFile(originFile.absolutePath)
        val resultBitmap = Bitmap.createBitmap(
            originBitmap.width / radio,
            originBitmap.height / radio,
            Bitmap.Config.ARGB_8888
        )
        val canvas = Canvas(resultBitmap)
        val rectF = RectF(
            0F,
            0F,
            (originBitmap.width / radio).toFloat(),
            (originBitmap.height / radio).toFloat()
        )
        canvas.drawBitmap(originBitmap, null, rectF, null)
        val bos = ByteArrayOutputStream()
        resultBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bos)
        val resultFile =
            File(context.getExternalFilesDir("img"), "${System.currentTimeMillis()}.jpg")
        return try {
            val fos = FileOutputStream(resultFile)
            fos.write(bos.toByteArray())
            fos.flush()
            fos.close()
            resultFile
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
            null
        } catch (e: IOException) {
            e.printStackTrace()
            null
        }
    }


    /**
     * 是否需要旋转图片
     */
    fun rotateIfRequired(bitmap: Bitmap, file: File): Bitmap {
        val exif = ExifInterface(file.path)
        return when (exif.getAttributeInt(
            ExifInterface.TAG_ORIENTATION,
            ExifInterface.ORIENTATION_NORMAL
        )) {
            ExifInterface.ORIENTATION_ROTATE_90 -> rotateBitmap(bitmap, 90)
            ExifInterface.ORIENTATION_ROTATE_180 -> rotateBitmap(bitmap, 180)
            ExifInterface.ORIENTATION_ROTATE_270 -> rotateBitmap(bitmap, 270)
            else -> bitmap
        }
    }

    private fun rotateBitmap(bitmap: Bitmap, degree: Int): Bitmap {
        val matrix = Matrix()
        matrix.postRotate(degree.toFloat())
        val rotatedBitmap =
            Bitmap.createBitmap(bitmap, 0, 0, bitmap.width, bitmap.height, matrix, true)
        bitmap.recycle()
        return rotatedBitmap
    }

    /**
     * uri转String
     */
    fun uriToStringPath(uri: Uri,context:Context): String {
        var filePath = ""
        val arr = arrayOf(MediaStore.Images.Media.DATA)
        val cursor = context.contentResolver?.query(uri, arr, null, null, null)
        if (cursor?.moveToFirst() != null) {
            val columnIndex = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DATA)
            filePath = cursor.getString(columnIndex)
        }
        cursor?.close()
        return filePath
    }

    /**
     * 获取手机缓存大小
     */

    fun getCacheSize(context: Context) :String{
//        var cacheSize = getFolderSize(context.cacheDir)
        var cacheSize :Long= 0
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            cacheSize += getFolderSize(context.externalCacheDir!!)
        }
        return getFormatSize(cacheSize)
    }

    /**
     *获取文件夹大小
     */
    fun getFolderSize(file: File): Long {
        var size: Long = 0
        try {
            val fileList = file.listFiles()
            for (num in fileList.indices) {
                size += if (fileList[num].isDirectory) {
                    getFolderSize(fileList[num])
                } else {
                    getFileSize(fileList[num])
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
        return size
    }

    /**
     * 获取文件大小
     */
    fun getFileSize(file:File):Long{
        var size:Long=0
        if(file.exists()){
            val fis = FileInputStream(file)
            size = fis.available().toLong()
        }
        return size
    }

    /**
     * 格式化大小
     */
    fun getFormatSize(size: Long): String {
        val kiloByte = size / 1024
        if (kiloByte < 1) {
            return "$size B"
        }
        val megaByte = kiloByte / 1024
        if (megaByte < 1) {
            val result1 = BigDecimal(kiloByte.toString())
            return result1.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "KB"
        }
        val gigaByte = megaByte / 1024
        if (gigaByte < 1) {
            val result2 = BigDecimal(megaByte.toString())
            return result2.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "MB"
        }
        val teraBytes = gigaByte / 1024
        if (teraBytes < 1) {
            val result3 = BigDecimal(gigaByte.toString())
            return result3.setScale(2, BigDecimal.ROUND_HALF_UP)
                .toPlainString() + "GB"
        }
        val result4 = BigDecimal(teraBytes);
        return result4.setScale(2, BigDecimal.ROUND_HALF_UP).toPlainString() + "TB"
    }

    /**
     * 清除缓存
     */
    fun clearCache(context:Context):Boolean{
//        deleteDir(context.cacheDir)
        if(Environment.getExternalStorageState() == Environment.MEDIA_MOUNTED){
            return deleteDir(context.externalCacheDir!!)
        }
        return false
    }

    /**
     * 删除文件
     */
    fun deleteDir(dir:File):Boolean{
        if(dir!=null && dir.isDirectory){
            val children =dir.list()
            for(num in children.indices){
                val success :Boolean= deleteDir(File(dir,children[num]))
                if(!success){
                    return false
                }
            }
        }
        return dir.delete()
    }

    /**
     * 获取目录下的所有文件和目录
     */
    fun getAllFiles(path:String):List<String>{
        val file = File(path)
        val files = file.listFiles()
        if(files==null){
            Log.d("file","空目录")
        }
        val list = ArrayList<String>()
        for(i in files.indices){
            list.add(files[i].absolutePath)
        }
        return list
    }

    /**
     * 获取文件最后修改时间
     */
    fun getFileLastModifyTime(file:File):String{
        var date:String = ""
        if(file.exists()){
            val time = file.lastModified()
            date = stampToDateMD(time)
        }
        return date
    }

    /**
     * 整数相除保留两位小数
     */
    fun divisionSaveTwoDecimals(a:Long,b:Long):Float{
        val df = DecimalFormat("0.00")
        val result: String = df.format(a.toFloat() / b)
        return result.toFloat()
    }



}