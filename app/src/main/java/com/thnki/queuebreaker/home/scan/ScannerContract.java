package com.thnki.queuebreaker.home.scan;

public interface ScannerContract {

    void stopQrPreview();

    void startQrPreview();

    void onQrCodeDetected(String restaurantId);

}
