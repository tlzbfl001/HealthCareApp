package kr.bodywell.android.view.note

import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kr.bodywell.android.R
import kr.bodywell.android.adapter.GalleryAdapter
import kr.bodywell.android.database.DBHelper.Companion.IMAGE
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentGalleryBinding
import kr.bodywell.android.model.Image
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.deleteFile
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.saveImage
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil
import kr.bodywell.android.util.PermissionUtil.CAMERA_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.STORAGE_REQUEST_CODE
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission

class GalleryFragment : Fragment() {
	private var _binding: FragmentGalleryBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	private lateinit var dataManager: DataManager
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private var dialog: Dialog? = null
	private var imageList = ArrayList<Image>()
	private var type = "NOTE"

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity(), NoteFragment())
			}
		}
		requireActivity().onBackPressedDispatcher.addCallback(this, callback)
	}

	override fun onCreateView(
		inflater: LayoutInflater, container: ViewGroup?,
		savedInstanceState: Bundle?
	): View {
		_binding = FragmentGalleryBinding.inflate(layoutInflater)

		setStatusBar(requireActivity(), binding.mainLayout)

		pLauncher = registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		){

		}

		dataManager = DataManager(activity)
		dataManager.open()

		val getImage = dataManager.getImage(type, selectedDate.toString())
		if(getImage.size > 0) binding.tvStart.text = "수정"
		for(i in 0 until getImage.size) imageList.add(getImage[i])

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity(), NoteFragment())
		}

		binding.cvUpload.setOnClickListener {
			if(checkCameraPermission(requireActivity())) {
				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_camera, null)

				val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
				val clPhoto = bottomSheetView.findViewById<ConstraintLayout>(R.id.clPhoto)

				clCamera.setOnClickListener {
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE) // 카메라 사용을위한 Intent 생성
					startActivityForResult(intent, CAMERA_REQUEST_CODE)
				}

				clPhoto.setOnClickListener {
					val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
					intent.type = "image/*"
					startActivityForResult(intent, STORAGE_REQUEST_CODE)
				}

				dialog!!.setContentView(bottomSheetView)
				dialog!!.show()
			}else {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_2)
				}else {
					pLauncher.launch(PermissionUtil.CAMERA_PERMISSION_1)
				}
			}
		}

		binding.cvSave.setOnClickListener {
			for(i in 0 until getImage.size) {
				var check = false
				for(j in 0 until imageList.size) {
					if(getImage[i].imageUri == imageList[j].imageUri) check = true
				}

				if(!check) {
					deleteFile(requireActivity(), getImage[i].imageUri)
					dataManager.deleteItem(IMAGE, "id", getImage[i].id)
				}
			}

			for(i in 0 until imageList.size) {
				if(imageList[i].bitmap != null) {
					Log.d(TAG, "bitmap: ${imageList[i].bitmap!!}")
					val result = saveImage(requireActivity(), imageList[i].bitmap!!)
					if(result != "") dataManager.insertImage(Image(type = type, imageUri = result, createdAt = selectedDate.toString()))
				}
			}

			if(getImage.size > 0) {
				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
			}else {
				Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
			}

			replaceFragment3(requireActivity(), NoteFragment())
		}

		photoView()

		return binding.root
	}

	private fun photoView() {
		if(imageList.size > 0) {
			val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
			binding.recyclerView.layoutManager = layoutManager
			val adapter = GalleryAdapter(requireActivity(), imageList)

			adapter.setOnLongClickListener(object : GalleryAdapter.OnLongClickListener {
				override fun onLongClick(pos: Int) {
					val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
						.setTitle("사진 삭제")
						.setMessage("정말 삭제하시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							imageList.removeAt(pos)
							adapter.notifyDataSetChanged()
							binding.recyclerView.adapter = adapter
							Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
						}
						.setNegativeButton("취소", null)
						.create()
					dialog.show()
				}
			})

			binding.recyclerView.adapter = adapter
		}
	}

	override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
		super.onActivityResult(requestCode, resultCode, data)
		if(resultCode == Activity.RESULT_OK){
			when(requestCode){
				CAMERA_REQUEST_CODE -> {
					if(data!!.extras?.get("data") != null){
						val bitmap = data.extras?.get("data") as Bitmap
						imageList.add(Image(type = type, bitmap = bitmap, createdAt = selectedDate.toString()))
						photoView()
					}
					dialog!!.dismiss()
				}
				STORAGE_REQUEST_CODE -> {
					val fileUri = data!!.data
					try{
						val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, fileUri)
						imageList.add(Image(type = type, bitmap = bitmap,  createdAt = selectedDate.toString()))
						photoView()
					}catch (e: Exception) {
						e.printStackTrace()
					}
					dialog!!.dismiss()
				}
			}
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}