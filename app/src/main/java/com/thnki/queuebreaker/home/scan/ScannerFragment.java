package com.thnki.queuebreaker.home.scan;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.VibrationEffect;
import android.os.Vibrator;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.content.ContextCompat;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.ordering.OrderingActivity;

import java.io.IOException;

import butterknife.BindView;
import butterknife.ButterKnife;

public class ScannerFragment extends Fragment implements ScannerContract {

    private static final int CAMERA_PERMISSION_REQUEST_CODE = 123;
    private static final long VIBRATE_DURATION = 100;

    @BindView(R.id.scanner_view)
    SurfaceView surfaceView;

    private QrCodeUtil qrCodeUtil;

    public static ScannerFragment getInstance() {
        return new ScannerFragment();
    }

    public ScannerFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.d("PermissionError", "onCreateView");
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.scanner_fragment, container, false);
        ButterKnife.bind(this, parentView);
        qrCodeUtil = new QrCodeUtil(getActivity(), this);
        return parentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initQrScanner();
    }

    public void initQrScanner() {
        Log.d("PermissionError", "initQrScanner");
        if (cameraPermissionIsAvailable()) {
            qrCodeUtil.init(surfaceView);
        } else {
            requestCameraPermission();
        }
    }

    @Override
    public void startQrPreview() {
        Log.d("PermissionError", "startQrPreview");
        if (cameraPermissionIsAvailable()) {
            try {
                qrCodeUtil.startPreview(surfaceView);
            } catch (IOException e) {
                showMsg(R.string.something_went_wrong);
            }
        }
    }

    @Override
    public void onQrCodeDetected(String restaurantId) {
        vibrate();
        Log.d("PermissionError", "onQrCodeDetected : " + restaurantId);
        Intent intent = new Intent(getActivity(), OrderingActivity.class);
        intent.putExtra(OrderingActivity.RESTAURANT_ID, restaurantId);
        startActivity(intent);
    }

    private void vibrate() {
        Vibrator vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        if (vibrator != null) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                vibrator.vibrate(VibrationEffect.createOneShot(VIBRATE_DURATION, VibrationEffect.DEFAULT_AMPLITUDE));
            } else {
                //deprecated in API 26
                vibrator.vibrate(VIBRATE_DURATION);
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();
        stopQrPreview();
    }

    @Override
    public void stopQrPreview() {
        Log.d("PermissionError", "stopQrPreview");
        if (cameraPermissionIsAvailable()) {
            qrCodeUtil.stopPreview();
        }
    }

    @Override
    public void setUserVisibleHint(boolean isVisibleToUser) {
        super.setUserVisibleHint(isVisibleToUser);
        if (isVisibleToUser && isResumed()) {
            if (!cameraPermissionIsAvailable()) {
                requestCameraPermission();
            }
        }
    }

    public boolean cameraPermissionIsAvailable() {
        return ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED;
    }

    public void requestCameraPermission() {
        Log.d("PermissionError", "requestCameraPermission");
        requestPermissions(new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {

        Log.d("PermissionError", "onRequestPermissionsResult");
        if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("PermissionError", "onPermissionGranted");
                initQrScanner();
                startQrPreview();
            } else {
                showMsg(R.string.camera_permission_justification_msg);
            }
        }
    }

    public void showMsg(int resId) {
        Toast.makeText(getActivity(), resId, Toast.LENGTH_LONG).show();
    }

}
