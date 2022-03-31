package io.nrbtech.rxandroidble.sample.example1a_background_scanning;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import androidx.annotation.RequiresApi;
import android.util.Log;

import io.nrbtech.rxandroidble.exceptions.BleScanException;
import io.nrbtech.rxandroidble.sample.SampleApplication;
import io.nrbtech.rxandroidble.scan.BackgroundScanner;
import io.nrbtech.rxandroidble.scan.ScanResult;

import java.util.List;

public class ScanReceiver extends BroadcastReceiver {

    @RequiresApi(26 /* Build.VERSION_CODES.O */)
    @Override
    public void onReceive(Context context, Intent intent) {
        BackgroundScanner backgroundScanner = SampleApplication.getRxBleClient(context).getBackgroundScanner();

        try {
            final List<ScanResult> scanResults = backgroundScanner.onScanResultReceived(intent);
            Log.i("ScanReceiver", "Scan results received: " + scanResults);
        } catch (BleScanException exception) {
            Log.w("ScanReceiver", "Failed to scan devices", exception);
        }
    }
}
