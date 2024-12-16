package kr.bodywell.android.view.note

import android.app.Activity.RESULT_OK
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.GalleryAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentGalleryBinding
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import kr.bodywell.android.view.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Date

class GalleryFragment : Fragment() {
	private var _binding: FragmentGalleryBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	val viewModel: MainViewModel by activityViewModels()
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var cLauncher: ActivityResultLauncher<Intent>
	private lateinit var dataManager: DataManager
	private var adapter: GalleryAdapter? = null
	private var imageList = ArrayList<FileItem>()
	private var selectionList = ArrayList<String>()
	private var dialog: Dialog? = null
	private var fileAbsolutePath: String? = null
	private var pictureFlag = 0

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment1(requireActivity().supportFragmentManager, NoteFragment())
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

		dataManager = DataManager(activity)
		dataManager.open()

		pLauncher = registerForActivityResult(
			ActivityResultContracts.RequestMultiplePermissions()
		){}

		lifecycleScope.launch {
//			val getAllFile = powerSync.getAllFile(selectedDate.toString()) // getNote해서 아이디통해서 파일가져와야됨
//			for(element in getAllFile) imageList.add(FileItem(name = element))
		}

		binding.clBack.setOnClickListener {
			replaceFragment3(requireActivity().supportFragmentManager, NoteFragment())
		}

		binding.tvUpload.setOnClickListener {
			if(checkCameraPermission(requireActivity())) {
				dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
				val bottomSheetView = layoutInflater.inflate(R.layout.dialog_get_photo, null)

				val clCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.clCamera)
				val clGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.clGallery)

				clCamera.setOnClickListener {
					val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

					val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
					val file = File.createTempFile(
						SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
						".jpg",
						storageDir
					).apply {
						fileAbsolutePath = absolutePath // 절대경로 변수에 저장
					}

					// 사진이 저장될 경로를 관리할 Uri 객체를 생성한다.
					val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".file_provider", file)

					intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
					cLauncher.launch(intent)
					pictureFlag = 1
				}

				clGallery.setOnClickListener {
					val intent = Intent(Intent.ACTION_PICK) // 갤러리에서 이미지를 선택하는 Intent 생성
					intent.type = "image/*"
					cLauncher.launch(intent)
					pictureFlag = 2
				}

				dialog!!.setContentView(bottomSheetView)
				dialog!!.show()
			}else {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					pLauncher.launch(CAMERA_PERMISSION_2)
				}else {
					pLauncher.launch(CAMERA_PERMISSION_1)
				}
			}
		}

		val contract1 = ActivityResultContracts.StartActivityForResult()
		cLauncher = registerForActivityResult(contract1){
			if(it?.resultCode == RESULT_OK){
				if(pictureFlag == 1) { // 카메라
					val file = File(fileAbsolutePath)
					val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file.absoluteFile)) // 카메라에서 찍은 사진을 디코딩
					val bitmap = ImageDecoder.decodeBitmap(decode) // 디코딩한 사진을 비트맵으로 변환
					imageList.add(FileItem(bitmap = bitmap))
					viewPhotos()
					file.delete()
					/** bitmap파일 저장 **/
				}else if(pictureFlag == 2) { // 갤러리
					val uri = it.data?.data // 선택한 이미지의 주소
					if(uri != null) { // 이미지 파일 읽어와서 설정하기
						val bitmap = getRotatedBitmap(requireActivity(), it.data?.data!!) // 이미지 회전하기
						imageList.add(FileItem(bitmap = bitmap))
						viewPhotos()
						/** bitmap파일 저장 **/
					}
				}

				dialog!!.dismiss()
			}
		}

		/*binding.cvSave.setOnClickListener {
			for(i in 0 until getImage.size) {
				var check = false
				for(j in 0 until imageList.size) {
					if(getImage[i].imageName == imageList[j].imageName) check = true
				}

				if(!check) {
					File(requireActivity().filesDir, getImage[i].imageName).delete()
					dataManager.deleteItem(IMAGE, "id", getImage[i].id)
				}
			}

			for(i in 0 until imageList.size) {
				if(imageList[i].bitmap != null) {
					val result = saveImage(requireActivity(), imageList[i].bitmap!!)
					if(result != "") dataManager.insertImage(Image(type = type, imageName = result, createdAt = selectedDate.toString()))
				}
			}

			if(getImage.size > 0) {
				Toast.makeText(context, "수정되었습니다.", Toast.LENGTH_SHORT).show()
			}else {
				Toast.makeText(context, "저장되었습니다.", Toast.LENGTH_SHORT).show()
			}

			replaceFragment3(requireActivity(), NoteFragment())
		}*/

		viewPhotos()

		return binding.root
	}

	private fun viewPhotos() {
		if(imageList.size > 0) {
			val layoutManager: RecyclerView.LayoutManager = GridLayoutManager(activity, 2)
			binding.recyclerView.layoutManager = layoutManager
			adapter = GalleryAdapter(viewModel, imageList)

			adapter!!.setOnLongClickListener(object : GalleryAdapter.OnLongClickListener {
				override fun onLongClick(pos: Int) {
					viewModel.setSelected(true)
					adapter!!.notifyDataSetChanged()

					/*val dialog = AlertDialog.Builder(context, R.style.AlertDialogStyle)
						.setTitle("사진 삭제")
						.setMessage("정말 삭제하시겠습니까?")
						.setPositiveButton("확인") { _, _ ->
							imageList.removeAt(pos)
							binding.recyclerView.adapter = adapter
							Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
						}
						.setNegativeButton("취소", null)
						.create()
					dialog.show()*/
				}
			})

			adapter!!.setOnClickListener(object : GalleryAdapter.OnClickListener {
				override fun onClick(pos: Int) {
					selectionList.add(imageList[pos].name)
					/** 삭제버튼 만들고 버튼누르면 selectionList 파일 전부 삭제 **/
				}
			})

			binding.recyclerView.adapter = adapter
		}
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}