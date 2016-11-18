package com.lichao.scancode.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.device.ScanManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

import com.lichao.scancode.MyApplication;

/**
 * Created by zblichao on 2016-03-11.
 */
public abstract class ScanBroadcastReceiver extends BroadcastReceiver {
    private SoundPool soundpool = null;
    private int soundid;
    private Vibrator mVibrator;
    private BarcodeParser barcodeParser;
    private ScanManager mScanManager;

    public ScanBroadcastReceiver() {
        super();
        soundpool = new SoundPool(1, AudioManager.STREAM_NOTIFICATION, 100); // MODE_RINGTONE
        soundid = soundpool.load("/etc/Scan_new.ogg", 1);
        mVibrator = (Vibrator) MyApplication.myApplication.getSystemService(Context.VIBRATOR_SERVICE);
        barcodeParser = new BarcodeParser();
    }

    public void initScanManger() {
        mScanManager = new ScanManager();
        mScanManager.openScanner();
        mScanManager.switchOutputMode(0);
    }

    public  void stopScanManager()
    {
        if(mScanManager != null) {
            mScanManager.stopDecode();
            mScanManager.closeScanner();
        }
    }

    public  void closeScanManager()
    {
        if(mScanManager != null) {
            mScanManager.closeScanner();
        }
    }

    public  void startScanManager()
    {
        if(mScanManager != null) {
            mScanManager.startDecode();
        }
    }
    @Override
    public void onReceive(Context context, Intent intent) {
        soundpool.play(soundid, 1, 1, 0, 0, 1);
        mVibrator.vibrate(100);

        byte[] barcode = intent.getByteArrayExtra("barocode");
        int barocodelen = intent.getIntExtra("length", 0);
        byte temp = intent.getByteExtra("barcodeType", (byte) 0);
        //android.util.Log.i("debug", "----codetype--" + temp);
        String barcodeStr = new String(barcode, 0, barocodelen);

        barcodeStr = "01045473270851201719013010SPO0986711";

        String type = barcodeParser.getBarcodeType(barcodeStr);

        android.util.Log.i("debug", type);

        onReceiveBarcode(type, barcodeStr);
        mScanManager.stopDecode();
    }

    public abstract void onReceiveBarcode(String type, String barcodeStr);


}
