package com.thnki.queuebreaker.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Color
import android.os.*
import android.support.v4.content.ContextCompat
import android.util.Log
import android.view.View
import android.view.WindowManager
import com.google.firebase.auth.FirebaseAuth
import com.thnki.queuebreaker.R
import com.thnki.queuebreaker.auth.admin.AdminAuthActivity
import com.thnki.queuebreaker.home.scan.QrCodeUtil
import com.thnki.queuebreaker.home.scan.ScannerContract
import com.thnki.queuebreaker.model.ToastMsg
import com.thnki.queuebreaker.utils.BaseActivity
import kotlinx.android.synthetic.main.activity_home.*
import kotlinx.android.synthetic.main.search_bar.*

class HomeActivity : BaseActivity(), ScannerContract, FirebaseAuth.AuthStateListener {

    private var isExpanded = false
    private var qrCodeUtil: QrCodeUtil? = null
    private val auth = FirebaseAuth.getInstance()

    companion object {
        private const val VIBRATE_DURATION: Long = 100
        private const val CAMERA_PERMISSION_REQUEST_CODE = 123
    }

    override fun onQrCodeDetected(restaurantId: String?) {
        vibrate()
    }

    override fun getContentView(): Int {
        return R.layout.activity_home
    }

    override fun onPostCreate(savedInstanceState: Bundle?) {
        super.onPostCreate(savedInstanceState)
        Log.d("CameraError", "onPostCreate")
        changeStatusBarColor()
        setupFab()
        setupAppBar()
    }

    private fun setupLogoutButton() {
        auth.addAuthStateListener(this)
        logout.setOnClickListener {
            Log.d("MultiLaunchError", "HomeActivity : logout")
            FirebaseAuth.getInstance().signOut()
        }
    }

    override fun onAuthStateChanged(it: FirebaseAuth) {
        Log.d("MultiLaunchError", "HomeActivity : onAuthStateListener")
        if (it.currentUser == null) {
            startActivity(Intent(this, AdminAuthActivity::class.java))
            finish()
        }
    }

    private fun setupFab() {
        fab.setOnClickListener { _ ->
            Log.d("CameraError", "Fab Clicked")
            isExpanded = !isExpanded
            changeFabIcon()
            appBar.setExpanded(isExpanded)
        }
    }

    private fun setupAppBar() {
        appBar.addOnOffsetChangedListener({ appBarLayout, verticalOffset ->
            when {
                Math.abs(verticalOffset) == appBarLayout.totalScrollRange -> {
                    Log.d("CameraError", "verticalOffset: $verticalOffset = ${appBarLayout.totalScrollRange}")
                    stopCamera()
                }
                else -> {
                    if (!isExpanded) {
                        startCamera()
                        setToolbarVisibility(View.GONE)
                    }
                }
            }
        })
    }

    override fun onStart() {
        super.onStart()
        Log.d("CameraError", "onResume")
        setupLogoutButton()
        initQrScanner()
    }

    private fun initQrScanner() {
        Log.d("CameraError", "initQrScanner")
        if (cameraPermissionIsAvailable()) {
            surfaceView.setZOrderMediaOverlay(true)
            qrCodeUtil = QrCodeUtil(this, this)
            qrCodeUtil?.init()
            appBar.setExpanded(isExpanded)
        } else {
            requestCameraPermission()
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d("CameraError", "onResume")
        if (isExpanded) {
            qrCodeUtil?.startScanning()
        }
        Handler().postDelayed({
            qrCodeUtil?.startPreview(surfaceView)
        }, 500)
    }

    override fun onStop() {
        super.onStop()
        qrCodeUtil?.stopPreview()
        auth.removeAuthStateListener(this)
    }

    override fun onBackPressed() {
        if (isExpanded) {
            isExpanded = false
            appBar.setExpanded(isExpanded)
        } else {
            super.onBackPressed()
        }
    }

    private fun startCamera() {
        Log.d("CameraError", "startCamera")
        setToolbarVisibility(View.GONE)
        isExpanded = true
        changeFabIcon()
        qrCodeUtil?.startScanning()
    }


    private fun stopCamera() {
        Log.d("CameraError", "stopCamera")
        setToolbarVisibility(View.VISIBLE)
        isExpanded = false
        changeFabIcon()
        qrCodeUtil?.stopScanning()
    }

    private fun cameraPermissionIsAvailable(): Boolean {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
    }

    private fun requestCameraPermission() {
        Log.d("PermissionError", "requestCameraPermission")
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            requestPermissions(arrayOf(Manifest.permission.CAMERA), CAMERA_PERMISSION_REQUEST_CODE)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {
        Log.d("CameraError", "onRequestPermissionsResult")
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("CameraError", "onPermissionGranted")
                initQrScanner()
            } else {
                ToastMsg.show(R.string.camera_permission_justification_msg)
            }
        }
    }

    private fun changeFabIcon() {
        val res = if (isExpanded) {
            R.drawable.round_clear_24
        } else {
            R.drawable.round_crop_free_24
        }
        fab.setImageResource(res)
    }

    //HACK
    private fun setToolbarVisibility(visibility: Int) {
        if (exploreToolbar.visibility != visibility) {
            exploreToolbar.visibility = visibility
        }
    }

    @Suppress("DEPRECATION")
    private fun vibrate() {
        val vibrator = getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE))
        } else {
            vibrator.vibrate(VIBRATE_DURATION)
        }
    }

    private fun changeStatusBarColor() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.addFlags(WindowManager.LayoutParams.FLAG_DRAWS_SYSTEM_BAR_BACKGROUNDS)
            window.statusBarColor = Color.parseColor("#ffffff")
        }
    }

}
