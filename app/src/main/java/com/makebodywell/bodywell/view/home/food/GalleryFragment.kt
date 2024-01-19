package com.makebodywell.bodywell.view.home.food

import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.GalleryAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentGalleryBinding
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.note.NoteFragment
import java.io.FileNotFoundException
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.time.LocalDate

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private var bundle = Bundle()

    private var dataManager: DataManager? = null

    private var calendarDate = ""
    private var type = ""

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        calendarDate = arguments?.getString("calendarDate").toString()
        type = arguments?.getString("type").toString()
        bundle.putString("calendarDate", calendarDate)
        bundle.putString("type", type)

        binding.clBack.setOnClickListener {
            when(type) {
                "1" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
                "2" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
                "3" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
                "4" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
                "5" -> {
                    bundle.putString("data", "note")
                    replaceFragment2(requireActivity(), NoteFragment(), bundle)
                }
            }
        }

        binding.clCamera.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, CAMERA_REQUEST_CODE)
        }

        binding.clGallery.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = MediaStore.Images.Media.CONTENT_TYPE
            startActivityForResult(intent, STORAGE_REQUEST_CODE)
        }

        listView()

        return binding.root
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                CAMERA_REQUEST_CODE -> {
                    if(data?.extras?.get("data") != null){
                        val img = data.extras?.get("data") as Bitmap
                        val uri = saveFile(randomFileName(), "image/jpeg", img)
                        val image = Image(imageUri = uri.toString(), regDate = LocalDate.now().toString())
                        dataManager?.insertImage(image)
                        listView()
                    }else {
                        listView()
                    }
                }
                STORAGE_REQUEST_CODE -> {
                    val uri = data?.data
                    val image = Image(imageUri = uri.toString(), regDate = LocalDate.now().toString())
                    dataManager?.insertImage(image)
                    listView()
                }
            }
        }
    }

    private fun randomFileName(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }

    // 사진 저장
    private fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{
        // MediaStore 에 파일명, mimeType 을 지정
        val cv = ContentValues()
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        cv.put(MediaStore.Images.Media.IS_PENDING, 1)

        // MediaStore 에 파일을 저장
        val uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        try {
            if(uri != null){
                val descriptor = requireActivity().contentResolver.openFileDescriptor(uri, "w")
                val fos = FileOutputStream(descriptor?.fileDescriptor)
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)

                fos.close()
                cv.clear()

                cv.put(MediaStore.Images.Media.IS_PENDING, 0)
                requireActivity().contentResolver.update(uri, cv, null, null)
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

    private fun listView() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 3)
        binding.recyclerView.layoutManager = layoutManager

        val getImage = dataManager!!.getImage(type.toInt(), calendarDate)
        if(getImage.size > 0) {
            val adapter = GalleryAdapter(requireActivity(), getImage)
            binding.recyclerView.adapter = adapter
        }
    }

    companion object {
        private const val CAMERA_REQUEST_CODE = 98
        private const val STORAGE_REQUEST_CODE = 99
    }
}