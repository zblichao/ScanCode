package com.lichao.scancode.receiver;

/**
 * Created by zblichao on 2016-03-11.
 */
public interface BarcodeReceiver {
    public void onReceiveBarcode(String type, String barcodeStr);
}
