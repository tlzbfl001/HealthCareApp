package kr.bodywell.android.view.home.food

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
import android.widget.Toast
import androidx.activity.OnBackPressedCallback
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AlertDialog
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.content.FileProvider
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import com.github.f4b6a3.uuid.UuidCreator
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.GalleryAdapter
import kr.bodywell.android.databinding.FragmentGalleryBinding
import kr.bodywell.android.model.Constant.DIETS
import kr.bodywell.android.model.Constant.FILES
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Food
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.dateTimeToIso
import kr.bodywell.android.util.CustomUtil.getDietFiles
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.replaceFragment4
import kr.bodywell.android.util.CustomUtil.saveFile
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.MyApp.Companion.dataManager
import kr.bodywell.android.util.MyApp.Companion.powerSync
import kr.bodywell.android.util.PermissionUtil.MEDIA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.MEDIA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkMediaPermission
import kr.bodywell.android.view.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.stream.Collectors

class GalleryFragment : Fragment() {
	private var _binding: FragmentGalleryBinding? = null
	private val binding get() = _binding!!

	private lateinit var callback: OnBackPressedCallback
	val viewModel: MainViewModel by activityViewModels()
	private lateinit var pLauncher: ActivityResultLauncher<Array<String>>
	private lateinit var cLauncher: ActivityResultLauncher<Intent>
//	private lateinit var dataManager: DataManager
	private var adapter: GalleryAdapter? = null
	private var bundle = Bundle()
	private var dialog: Dialog? = null
	private var fileAbsolutePath: String? = null
	private var pictureFlag = 0
	private var getDiets = Food()
	private var images = ArrayList<FileItem>()
	private var selectionList = ArrayList<String>()
	private var prevFileCnt = 0

	override fun onAttach(context: Context) {
		super.onAttach(context)
		callback = object : OnBackPressedCallback(true) {
			override fun handleOnBackPressed() {
				replaceFragment4(parentFragmentManager, FoodDailyEditFragment(), bundle)
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
		){}

		getDiets = arguments?.getParcelable(DIETS)!!
		bundle.putParcelable(DIETS, getDiets)
		bundle.putString("type", arguments?.getString("type").toString())

		lifecycleScope.launch {
			prevFileCnt = getDietFiles(selectedDate.toString()).size
			val getFiles = powerSync.getFiles("diet_id", getDiets.id) as ArrayList<FileItem>

			for(i in 0 until getFiles.size) {
				val imgPath = requireActivity().filesDir.toString() + "/" + getFiles[i].name
				val file = File(imgPath)
				if(file.exists()) images.add(getFiles[i])
			}
		}

		binding.btnBack.setOnClickListener {
			replaceFragment4(requireActivity().supportFragmentManager, FoodDailyEditFragment(), bundle)
		}

		binding.ivUpload.setOnClickListener {
			if(checkMediaPermission(requireActivity())) {
				if(prevFileCnt + images.size >= 20) {
					Toast.makeText(context, "등록 가능한 이미지 개수를 초과하였습니다.", Toast.LENGTH_SHORT).show()
				}else {
					dialog = BottomSheetDialog(requireActivity(), R.style.BottomSheetDialogTheme)
					val bottomSheetView = layoutInflater.inflate(R.layout.dialog_photo, null)

					val btnCamera = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnCamera)
					val btnGallery = bottomSheetView.findViewById<ConstraintLayout>(R.id.btnGallery)

					btnCamera.setOnClickListener {
						val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)

						val storageDir = requireActivity().getExternalFilesDir(Environment.DIRECTORY_PICTURES)	// 이미지 경로 지정
						val file = File.createTempFile(
							SimpleDateFormat("yyMMddhhmmSSS").format(Date()),
							".jpg",
							storageDir
						).apply {
							fileAbsolutePath = absolutePath // 절대경로 변수에 저장
						}

						// 사진이 저장될 경로를 관리할 Uri 객체 생성
						val contentUri = FileProvider.getUriForFile(requireActivity(), requireActivity().applicationContext.packageName + ".file_provider", file)

						intent.putExtra(MediaStore.EXTRA_OUTPUT, contentUri)
						cLauncher.launch(intent)
						pictureFlag = 1
					}

					btnGallery.setOnClickListener {
						val intent = Intent(Intent.ACTION_PICK)
						intent.type = "image/*"
						cLauncher.launch(intent)
						pictureFlag = 2
					}

					dialog!!.setContentView(bottomSheetView)
					dialog!!.show()
				}
			}else {
				if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
					pLauncher.launch(MEDIA_PERMISSION_2)
				}else {
					pLauncher.launch(MEDIA_PERMISSION_1)
				}
			}
		}

		val contract = ActivityResultContracts.StartActivityForResult()
		cLauncher = registerForActivityResult(contract){
			if(it?.resultCode == RESULT_OK){
				lifecycleScope.launch {
					if(pictureFlag == 1) { // 카메라
						val file1 = File(fileAbsolutePath)
						val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file1.absoluteFile))
						val bitmap = ImageDecoder.decodeBitmap(decode)
						val result = saveFile(requireActivity(), bitmap)
						if(result != "") {
							val file2 = File(requireActivity().filesDir, result)
							if(file2.length() in 1..1048575) { // 파일 크기 확인 후 저장
								val uuid = UuidCreator.getTimeOrderedEpoch()
								powerSync.insertDietFile(FileItem(id = uuid.toString(), name = result, dietId = getDiets.id))
								dataManager.updateTime2(dateTimeToIso(Calendar.getInstance()))
								images.add(FileItem(id = uuid.toString(), name = result, bitmap = bitmap))
								viewPhotos()
							}else {
								Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
								file2.delete()
							}
						}
						file1.delete()
					}else { // 갤러리
						val uri = it.data?.data
						if(uri != null) {
							val bitmap = getRotatedBitmap(requireActivity(), uri) // 이미지 회전하기
							val result = saveFile(requireActivity(), bitmap!!)

							if(result != "") {
								val file = File(requireActivity().filesDir, result)
								if(file.length() in 1..1048575) { // 파일 크기 확인 후 저장
									val uuid = UuidCreator.getTimeOrderedEpoch()
									powerSync.insertDietFile(FileItem(id = uuid.toString(), name = result, dietId = getDiets.id))
									dataManager.updateTime2(dateTimeToIso(Calendar.getInstance()))
									images.add(FileItem(id = uuid.toString(), name = result, bitmap = bitmap))
									viewPhotos()
								}else {
									Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
									file.delete()
								}
							}
						}
					}
				}

				dialog!!.dismiss()
			}
		}

		binding.tvSelect.setOnClickListener {
			if(images.size > 0) showButtonUI2() else Toast.makeText(context, "사진이 없습니다.", Toast.LENGTH_SHORT).show()
		}

		binding.tvDelete.setOnClickListener {
			if(selectionList.size > 0) {
				val dialog = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
					.setTitle("사진 삭제")
					.setMessage("정말 삭제하시겠습니까?")
					.setPositiveButton("확인") { _, _ ->
						lifecycleScope.launch {
							for(i in 0 until selectionList.size) {
								images.stream().filter { x -> x.id == selectionList[i] }.collect(Collectors.toList()).forEach { x ->
									images.remove(x)
									File(requireActivity().filesDir, x.name).delete()
								}
								powerSync.deleteItem(FILES, "id", selectionList[i])
							}

							showButtonUI1()
						}
						Toast.makeText(context, "삭제되었습니다.", Toast.LENGTH_SHORT).show()
					}
					.setNegativeButton("취소", null)
					.create()
				dialog.show()
			}else {
				Toast.makeText(context, "선택된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
			}
		}

		binding.tvCancel.setOnClickListener {
			showButtonUI1()
		}

		val layoutManager = GridLayoutManager(activity, 3)
		binding.recyclerView.layoutManager = layoutManager

		viewPhotos()

		return binding.root
	}

	private fun viewPhotos() {
		adapter = GalleryAdapter(viewModel, images)

		showButtonUI1()

		adapter!!.setOnLongClickListener(object : GalleryAdapter.OnLongClickListener {
			override fun onLongClick(pos: Int) {
				showButtonUI2()
			}
		})

		adapter!!.setOnClickListener(object : GalleryAdapter.OnClickListener {
			override fun onClick(pos: Int, checked: Boolean) {
				if(viewModel.imgSelectedVM.value == true) {
					if(checked) selectionList.add(images[pos].id) else selectionList.remove(images[pos].id)
				}
			}
		})

		binding.recyclerView.adapter = adapter
	}

	private fun showButtonUI1() {
		if(images.isEmpty()) {
			binding.ivUpload.visibility = View.VISIBLE
			binding.tvSelect.visibility = View.INVISIBLE
		}else {
			binding.ivUpload.visibility = View.VISIBLE
			binding.tvSelect.visibility = View.VISIBLE
		}

		binding.view2.visibility = View.GONE
		viewModel.setImgSelected(false)
		selectionList.clear()
		adapter!!.notifyDataSetChanged()
	}

	private fun showButtonUI2() {
		binding.ivUpload.visibility = View.GONE
		binding.tvSelect.visibility = View.GONE
		binding.view2.visibility = View.VISIBLE
		viewModel.setImgSelected(true)
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}