package kr.bodywell.android.util

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import java.io.ByteArrayOutputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat

class PermissionUtil {
    companion object {
        private const val REQUEST_CODE = 1
        const val CAMERA_REQUEST_CODE = 100
        const val STORAGE_REQUEST_CODE = 101

        private val COMMON = arrayOf(
            Manifest.permission.BODY_SENSORS,
            Manifest.permission.ACTIVITY_RECOGNITION
        )

        val BT_PERMISSION_1 = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.BLUETOOTH,
            Manifest.permission.BLUETOOTH_ADMIN,
        )

        @RequiresApi(Build.VERSION_CODES.S)
        val BT_PERMISSION_2 = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION,
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

        fun cameraRequest(context: Activity): Boolean {
            var check = true
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                for(permission in CAMERA_PERMISSION_2) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context, arrayOf(*CAMERA_PERMISSION_2),
                            REQUEST_CODE

                        )
                        check = false
                    }
                }
            }else {
                for(permission in CAMERA_PERMISSION_1) {
                    if (ContextCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(context, arrayOf(*CAMERA_PERMISSION_1),
                            REQUEST_CODE
                        )
                        check = false
                    }
                }
            }
            return check
        }

        @SuppressLint("SimpleDateFormat")
        fun randomFileName(): String {
            return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
        }

        @SuppressLint("Recycle")
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

        fun getImageUriWithAuthority(context: Context, uri: Uri?): String? {
            var inputStream: InputStream? = null
            if(uri?.authority != null) {
                try{
                    inputStream = context.contentResolver.openInputStream(uri)
                    val bmp = BitmapFactory.decodeStream(inputStream)
                    val bytes = ByteArrayOutputStream()
                    bmp.compress(Bitmap.CompressFormat.JPEG, 100, bytes)
                    val path = MediaStore.Images.Media.insertImage(context.contentResolver, bmp, "Title", null)
                    return Uri.parse(path).toString()
                }catch (e: FileNotFoundException) {
                    Log.i(CustomUtil.TAG, "$e")
                }finally {
                    try{
                        inputStream?.close()
                    }catch(e: IOException) {
                        Log.i(CustomUtil.TAG, "$e")
                    }
                }
            }
            return null
        }
    }
}