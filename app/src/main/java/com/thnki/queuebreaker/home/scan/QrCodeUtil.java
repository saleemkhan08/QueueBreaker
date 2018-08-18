package com.thnki.queuebreaker.home.scan;


import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.util.SparseArray;
import android.view.SurfaceView;

import com.google.android.gms.vision.CameraSource;
import com.google.android.gms.vision.Detector;
import com.google.android.gms.vision.barcode.Barcode;
import com.google.android.gms.vision.barcode.BarcodeDetector;

import java.io.IOException;

public class QrCodeUtil implements Detector.Processor<Barcode> {
    private final Context context;
    private BarcodeDetector barcodeDetector;
    private CameraSource cameraSource;
    private ScannerContract contract;

    public QrCodeUtil(Context context, ScannerContract contract) {
        Log.d("CameraError", "QrCodeUtil");
        this.context = context;
        this.contract = contract;
    }

    public void init() {
        Log.d("CameraError", "QrCodeUtil : init");
        barcodeDetector = new BarcodeDetector.Builder(context).build();
        cameraSource = new CameraSource.Builder(context, barcodeDetector)
                .setAutoFocusEnabled(true)
                .setFacing(CameraSource.CAMERA_FACING_BACK)
                .setRequestedPreviewSize(1920, 1080)
                .build();
        barcodeDetector.setProcessor(this);
    }

    @SuppressLint("MissingPermission")
    public void startPreview(SurfaceView surfaceView) throws IOException {
        Log.d("CameraError", "QrCodeUtil startPreview surfaceView : " + surfaceView);
        cameraSource.start(surfaceView.getHolder());
    }

    public void stopPreview() {
        try {
            stopScanning();
            cameraSource.release();
        } catch (NullPointerException e) {
            Log.d("CameraError", e.getMessage());
        }
    }

    @Override
    public void receiveDetections(Detector.Detections<Barcode> detections) {
        Log.d("CameraError", "QrCodeUtil receiveDetections");
        final SparseArray<Barcode> qrCodes = detections.getDetectedItems();
        if (qrCodes.size() > 0) {
            contract.onQrCodeDetected(qrCodes.valueAt(0).displayValue);
        }
    }

    @Override
    public void release() {
        Log.d("CameraError", "QrCodeUtil release");
    }

    public void startScanning() {
        barcodeDetector.setProcessor(this);
    }

    public void stopScanning() {
        barcodeDetector.release();
    }


}
