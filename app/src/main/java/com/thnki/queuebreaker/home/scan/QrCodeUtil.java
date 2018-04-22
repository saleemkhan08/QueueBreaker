package com.thnki.queuebreaker.home.scan;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QrCodeUtil implements SurfaceHolder.Callback, Detector.Processor<Barcode> {
    private final Context context;
    private CameraSource cameraSource;
    private ScannerContract contract;

    QrCodeUtil(Context context, ScannerContract contract) {
        this.context = context;
        this.contract = contract;
    }

    void init(SurfaceView surfaceView) {
        Log.d("PermissionError", "init surfaceView : " + surfaceView);
        BarcodeDetector barcodeDetector = new BarcodeDetector.Builder(context).build();
        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1920, 1080)
                .build();
        surfaceView.getHolder().addCallback(this);
        barcodeDetector.setProcessor(this);
    }

    @Override
    public void surfaceCreated(SurfaceHolder surfaceHolder) {
        Log.d("PermissionError", "surfaceCreated");
        contract.startQrPreview();
    }

    @Override
    public void surfaceChanged(SurfaceHolder surfaceHolder, int i, int i1, int i2) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder surfaceHolder) {
        cameraSource.stop();
    }

    @SuppressLint("MissingPermission")
    void startPreview(SurfaceView surfaceView) throws IOException {
        Log.d("PermissionError", "startPreview surfaceView : " + surfaceView);
        cameraSource.start(surfaceView.getHolder());
    }

    void stopPreview() {
        if (cameraSource != null) {
            cameraSource.release();
        }
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
        if (qrCodes.size() > 0) {
            contract.onQrCodeDetected(qrCodes.valueAt(0).displayValue);
        }
    }

    @Override
    public void release() {

    }
}
