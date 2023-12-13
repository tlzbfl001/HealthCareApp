package com.makebodywell.bodywell.view.home.food

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.makebodywell.bodywell.adapter.GalleryAdapter
import com.makebodywell.bodywell.database.DataManager
import com.makebodywell.bodywell.databinding.FragmentGalleryBinding
import com.makebodywell.bodywell.model.Image
import com.makebodywell.bodywell.util.CustomUtil.Companion.replaceFragment2
import com.makebodywell.bodywell.view.note.NoteFragment
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.time.LocalDate

class GalleryFragment : Fragment() {
    private var _binding: FragmentGalleryBinding? = null
    private val binding get() = _binding!!

    private lateinit var callback: OnBackPressedCallback

    private var bundle = Bundle()

    private var dataManager: DataManager? = null

    private var calendarDate = ""
    private var type = ""

    private val cameraCode = 2
    private val storageCode = 3

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentGalleryBinding.inflate(layoutInflater)

        dataManager = DataManager(activity)
        dataManager!!.open()

        initView()
        setupList()

        return binding.root
    }

    private fun initView() {
        calendarDate = arguments?.getString("calendarDate").toString()
        type = arguments?.getString("type").toString()
        bundle.putString("calendarDate", calendarDate)
        bundle.putString("type", type)

        binding.clBack.setOnClickListener {
            when(type) {
                "breakfast" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
                "lunch" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
                "dinner" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
                "snack" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
                "note" -> {
                    bundle.putString("data", "noteData")
                    replaceFragment2(requireActivity(), NoteFragment(), bundle)
                }
            }
        }

        binding.clCamera.setOnClickListener {
            getCamera()
        }

        binding.clGallery.setOnClickListener {
            getGallery()
        }
    }

    private fun setupList() {
        val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 3)
        binding.recyclerView.layoutManager = layoutManager

        val getFoodImage = dataManager!!.getImage(type, calendarDate)
        if(getFoodImage.size > 0) {
            val adapter = GalleryAdapter(requireActivity(), getFoodImage)
            binding.recyclerView.adapter = adapter
        }
    }

    private fun getCamera() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(intent, cameraCode)
    }

    private fun getGallery() {
        val intent = Intent(Intent.ACTION_PICK)
        intent.type = MediaStore.Images.Media.CONTENT_TYPE
        startActivityForResult(intent, storageCode)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(resultCode == Activity.RESULT_OK){
            when(requestCode){
                cameraCode -> {
                    if(data?.extras?.get("data") != null){
                        val img = data.extras?.get("data") as Bitmap
                        val uri = saveFile(randomFileName(), "image/jpeg", img)
                        val foodImage = Image(imageUri = uri.toString(), type = type, regDate = LocalDate.now().toString())

                        dataManager?.insertImage(foodImage)
                        setupList()
                    }else {
                        setupList()
                    }
                }
                storageCode -> {
                    val uri = data?.data
                    val foodImage = Image(imageUri = uri.toString(), type = type, regDate = LocalDate.now().toString())

                    dataManager?.insertImage(foodImage)
                    setupList()
                }
            }
        }
    }

    private fun randomFileName(): String {
        return SimpleDateFormat("yyyyMMddHHmmss").format(System.currentTimeMillis())
    }

    // 사진 저장
    private fun saveFile(fileName:String, mimeType:String, bitmap: Bitmap): Uri?{
        val cv = ContentValues()

        // MediaStore 에 파일명, mimeType 을 지정
        cv.put(MediaStore.Images.Media.DISPLAY_NAME, fileName)
        cv.put(MediaStore.Images.Media.MIME_TYPE, mimeType)
        cv.put(MediaStore.Images.Media.IS_PENDING, 1)

        // MediaStore 에 파일을 저장
        val uri = requireActivity().contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, cv)
        if(uri != null){
            val scriptor = requireActivity().contentResolver.openFileDescriptor(uri, "w")

            val fos = FileOutputStream(scriptor?.fileDescriptor)

            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos)
            fos.close()

            cv.clear()

            // IS_PENDING 을 초기화
            cv.put(MediaStore.Images.Media.IS_PENDING, 0)
            requireActivity().contentResolver.update(uri, cv, null, null)
        }
        return uri
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        callback = object : OnBackPressedCallback(true) {
            override fun handleOnBackPressed() {
                when(type) {
                    "breakfast" -> replaceFragment2(requireActivity(), FoodBreakfastFragment(), bundle)
                    "lunch" -> replaceFragment2(requireActivity(), FoodLunchFragment(), bundle)
                    "dinner" -> replaceFragment2(requireActivity(), FoodDinnerFragment(), bundle)
                    "snack" -> replaceFragment2(requireActivity(), FoodSnackFragment(), bundle)
                    "note" -> {
                        bundle.putString("data", "noteData")
                        replaceFragment2(requireActivity(), NoteFragment(), bundle)
                    }
                }
            }
        }
        requireActivity().onBackPressedDispatcher.addCallback(this, callback)
    }

    override fun onDetach() {
        super.onDetach()
        callback.remove()
    }
}