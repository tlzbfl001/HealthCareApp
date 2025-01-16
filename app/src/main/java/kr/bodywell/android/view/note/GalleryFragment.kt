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
import android.util.Log
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
import androidx.recyclerview.widget.RecyclerView
import com.github.f4b6a3.uuid.UuidCreator
import com.google.android.material.bottomsheet.BottomSheetDialog
import kotlinx.coroutines.launch
import kr.bodywell.android.R
import kr.bodywell.android.adapter.GalleryAdapter
import kr.bodywell.android.database.DataManager
import kr.bodywell.android.databinding.FragmentGalleryBinding
import kr.bodywell.android.model.Constant.FILES
import kr.bodywell.android.model.Constant.HAPPY
import kr.bodywell.android.model.FileItem
import kr.bodywell.android.model.Note
import kr.bodywell.android.util.CalendarUtil.selectedDate
import kr.bodywell.android.util.CustomUtil.TAG
import kr.bodywell.android.util.CustomUtil.dateTimeToIso1
import kr.bodywell.android.util.CustomUtil.getRotatedBitmap
import kr.bodywell.android.util.CustomUtil.getUUID
import kr.bodywell.android.util.CustomUtil.powerSync
import kr.bodywell.android.util.CustomUtil.replaceFragment1
import kr.bodywell.android.util.CustomUtil.replaceFragment3
import kr.bodywell.android.util.CustomUtil.saveFile
import kr.bodywell.android.util.CustomUtil.setStatusBar
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_1
import kr.bodywell.android.util.PermissionUtil.CAMERA_PERMISSION_2
import kr.bodywell.android.util.PermissionUtil.checkCameraPermission
import kr.bodywell.android.view.MainViewModel
import java.io.File
import java.text.SimpleDateFormat
import java.time.LocalDateTime
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
	private lateinit var dataManager: DataManager
	private var adapter: GalleryAdapter? = null
	private var dialog: Dialog? = null
	private var imageList = ArrayList<FileItem>()
	private var selectionList = ArrayList<String>()
	private var fileAbsolutePath: String? = null
	private var pictureFlag = 0
	private var dietId: String? = null
	private var noteId: String? = null

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

		dietId = arguments?.getString("dietId")
		noteId = arguments?.getString("noteId")

		lifecycleScope.launch {
			if(dietId == null) {
				val getFiles = powerSync.getFiles("note_id", noteId!!)
				for(element in getFiles) imageList.add(FileItem(id = element.id, name = element.name))
			}
		}

		binding.btnBack.setOnClickListener {
			replaceFragment3(requireActivity().supportFragmentManager, NoteFragment())
		}

		binding.ivUpload.setOnClickListener {
			if(checkCameraPermission(requireActivity())) {
				if(imageList.size > 20) {
					Toast.makeText(context, "사진은 하루에 20장까지 등록할 수 있습니다.", Toast.LENGTH_SHORT).show()
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
					pLauncher.launch(CAMERA_PERMISSION_2)
				}else {
					pLauncher.launch(CAMERA_PERMISSION_1)
				}
			}
		}

		val contract = ActivityResultContracts.StartActivityForResult()
		cLauncher = registerForActivityResult(contract){
			if(it?.resultCode == RESULT_OK){
				lifecycleScope.launch {
					if(noteId == "") {
						val uuid = UuidCreator.getTimeOrderedEpoch()
						powerSync.insertNote(Note(id = getUUID(), title = "", content = "", emotion = HAPPY, date = selectedDate.toString(),
							createdAt = dateTimeToIso1(Calendar.getInstance()), updatedAt = dateTimeToIso1(Calendar.getInstance())))
						noteId = uuid.toString()
					}

					if(pictureFlag == 1) { // 카메라
						val file1 = File(fileAbsolutePath)
						val decode = ImageDecoder.createSource(requireActivity().contentResolver, Uri.fromFile(file1.absoluteFile))
						val bitmap = ImageDecoder.decodeBitmap(decode)
						val result = saveFile(requireActivity(), bitmap)
						if(result != "") {
							val file2 = File(requireActivity().filesDir, result)
							if(file2.length() in 1..1048575) { // 파일 크기 확인 후 저장
								val uuid = UuidCreator.getTimeOrderedEpoch()
								powerSync.insertNoteFile(FileItem(id = uuid.toString(), name = result, noteId = noteId!!))
								dataManager.updateFileTime(dateTimeToIso1(Calendar.getInstance()))
								imageList.add(FileItem(id = uuid.toString(), name = result, bitmap = bitmap))
								viewPhotos()
							}else {
								Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
							}
							file2.delete()
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
									powerSync.insertNoteFile(FileItem(id = uuid.toString(), name = result, noteId = noteId!!))
									dataManager.updateFileTime(dateTimeToIso1(Calendar.getInstance()))
									imageList.add(FileItem(id = uuid.toString(), name = result, bitmap = bitmap))
									viewPhotos()
								}else {
									Toast.makeText(context, "파일 크기가 허용되는 한도를 초과하여 파일을 저장할 수 없습니다.", Toast.LENGTH_SHORT).show()
								}
								file.delete()
							}
						}
					}
				}

				dialog!!.dismiss()
			}
		}

		viewPhotos()

		return binding.root
	}

	private fun viewPhotos() {
		val layoutManager = GridLayoutManager(activity, 3)
		binding.recyclerView.layoutManager = layoutManager
		adapter = GalleryAdapter(viewModel, imageList)

		showButtonUI1()

		binding.tvSelect.setOnClickListener {
			if(imageList.size > 0) showButtonUI2() else Toast.makeText(context, "사진이 없습니다.", Toast.LENGTH_SHORT).show()
		}

		adapter!!.setOnLongClickListener(object : GalleryAdapter.OnLongClickListener {
			override fun onLongClick(pos: Int) {
				showButtonUI2()
			}
		})

		adapter!!.setOnClickListener(object : GalleryAdapter.OnClickListener {
			override fun onClick(pos: Int, checked: Boolean) {
				if(viewModel.pictureSelectedVM.value == true) {
					if(checked) selectionList.add(imageList[pos].id) else selectionList.remove(imageList[pos].id)
				}
			}
		})

		binding.recyclerView.adapter = adapter

		binding.tvDelete.setOnClickListener {
			if(selectionList.size > 0) {
				val dialog = AlertDialog.Builder(requireActivity(), R.style.AlertDialogStyle)
					.setTitle("사진 삭제")
					.setMessage("정말 삭제하시겠습니까?")
					.setPositiveButton("확인") { _, _ ->
						lifecycleScope.launch {
							for(i in 0 until selectionList.size) {
								imageList.stream().filter { x -> x.id == selectionList[i] }.collect(Collectors.toList()).forEach { x ->
									imageList.remove(x)
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
	}

	private fun showButtonUI1() {
		if(imageList.isEmpty()) {
			binding.ivUpload.visibility = View.VISIBLE
			binding.tvSelect.visibility = View.INVISIBLE
		}else {
			binding.ivUpload.visibility = View.VISIBLE
			binding.tvSelect.visibility = View.VISIBLE
		}

		binding.view2.visibility = View.GONE
		viewModel.setPictureSelected(false)
		selectionList.clear()
		adapter!!.notifyDataSetChanged()
	}

	private fun showButtonUI2() {
		binding.ivUpload.visibility = View.GONE
		binding.tvSelect.visibility = View.GONE
		binding.view2.visibility = View.VISIBLE
		viewModel.setPictureSelected(true)
	}

	override fun onDetach() {
		super.onDetach()
		callback.remove()
	}
}