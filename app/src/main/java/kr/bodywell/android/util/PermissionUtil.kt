package kr.bodywell.android.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.annotation.RequiresApi
import androidx.core.content.ContextCompat
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat

object PermissionUtil {
    const val REQUEST_CODE = 1
    const val CAMERA_REQUEST_CODE = 2
    const val STORAGE_REQUEST_CODE = 3

    val BT_PERMISSION_1 = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION
    )

    @RequiresApi(Build.VERSION_CODES.S)
    val BT_PERMISSION_2 = arrayOf(
        Manifest.permission.ACCESS_FINE_LOCATION,
        Manifest.permission.BLUETOOTH_SCAN,
        Manifest.permission.BLUETOOTH_CONNECT
    )

    private val CAMERA_PERMISSION_1 = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_EXTERNAL_STORAGE,
        Manifest.permission.WRITE_EXTERNAL_STORAGE
    )

    @RequiresApi(Build.VERSION_CODES.TIRAMISU)
    private val CAMERA_PERMISSION_2 = arrayOf(
        Manifest.permission.CAMERA,
        Manifest.permission.READ_MEDIA_IMAGES
    )

    fun checkBtPermissions(context: Activity): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
               && ContextCompat.checkSelfPermission(context,
                Manifest.permission.BLUETOOTH_CONNECT) == PackageManager.PERMISSION_GRANTED
        }else {
            ContextCompat.checkSelfPermission(context,
                Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
        }
    }

    fun checkAlarmPermissions(context: Context): Boolean {
        return if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED
        }else if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            ContextCompat.checkSelfPermission(context, Manifest.permission.SCHEDULE_EXACT_ALARM) == PackageManager.PERMISSION_GRANTED
        }else true
    }

    @SuppressLint("SimpleDateFormat")
    fun randomFileName(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }

    fun saveFile(context: Context, mimeType:String, bitmap: Bitmap): Uri?{
        // MediaStore 에 파일명, mimeType 을 지정
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, randomFileName())
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        cv.put(MediaStore.Images.Media.IS_PENDING, 1)

        // MediaStore 에 파일을 저장
        val uri = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        try {
            if(uri != null){
                val descriptor = context.contentResolver.openFileDescriptor(uri, "w")
                val fos = FileOutputStream(descriptor?.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                fos.close()
                cv.clear()

                cv.put(MediaStore.Images.Media.IS_PENDING, 0)
                context.contentResolver.update(uri, cv, null, null)
            }
        } catch(e: FileNotFoundException) {
            e.printStackTrace()
        } catch (e: IOException) {
            e.printStackTrace()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        return uri
    }
}