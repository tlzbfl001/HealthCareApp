package com.makebodywell.bodywell.view.init

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.makebodywell.bodywell.databinding.ActivityPermissionBinding
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permission1
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permission2
import com.makebodywell.bodywell.util.PermissionUtil.Companion.permission3
import kotlin.system.exitProcess

class PermissionActivity : AppCompatActivity() {
    private var _binding: ActivityPermissionBinding? = null
    private val binding get() = _binding!!

    private var backWait:Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        _binding = ActivityPermissionBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.ivBack.setOnClickListener {
            startActivity(Intent(this@PermissionActivity, InitActivity::class.java))
        }

        binding.cvConfirm.setOnClickListener {
            if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                for(permission in permission3) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(*permission3), PERMISSION_REQUEST_CODE)
                    }
                }
            }else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                for(permission in permission2) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(*permission2), PERMISSION_REQUEST_CODE)
                    }
                }
            }else {
                for(permission in permission1) {
                    if (ContextCompat.checkSelfPermission(this, permission) != PackageManager.PERMISSION_GRANTED) {
                        ActivityCompat.requestPermissions(this, arrayOf(*permission1), PERMISSION_REQUEST_CODE)
                    }
                }
            }
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String?>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if(requestCode == PERMISSION_REQUEST_CODE && grantResults.isNotEmpty()) {
            startActivity(Intent(this@PermissionActivity, InputActivity::class.java))
        }
    }

    override fun onBackPressed() {
        if(System.currentTimeMillis() - backWait >= 2000) {
            backWait = System.currentTimeMillis()
            Toast.makeText(this, "뒤로가기 버튼을 한번 더 누르면 종료됩니다.", Toast.LENGTH_SHORT).show()
        } else {
            ActivityCompat.finishAffinity(this) // 액티비티 종료
            System.runFinalization()
            exitProcess(0) // 프로세스 종료
        }
    }

    companion object {
        private const val PERMISSION_REQUEST_CODE = 1
    }
}