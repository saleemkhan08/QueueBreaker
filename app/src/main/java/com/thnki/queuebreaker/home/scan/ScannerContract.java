package com.thnki.queuebreaker.home.scan;

public interface ScannerContract {
    void onQrCodeDetected(String restaurantId);
}
