package com.lichao.scancode.receiver;


import com.lichao.scancode.entity.NameValuePair;

import java.util.ArrayList;



import java.util.ArrayList;

/**
 * Created by bo gu on 3/1/2016.
 */
public class BarcodeParser {

    private EAN128Parser ean128Parser;

    public BarcodeParser () {
        ean128Parser = new EAN128Parser();
    }

    public String trimBarcode(String barcode) {
        barcode = barcode.replace("(", "");
        barcode = barcode.replace(")", "");
        return barcode;
    }

    // only supports: EAN13 Code128 HIBC
    public String getBarcodeType(String barcode) {
        boolean primary = false;
        boolean secondary = false;

        if ((barcode.length() == 13) && barcode.matches("[0-9]+")) {
            // EAN13
            return "EAN13";
        }
        else {
            if (barcode.indexOf("*") != -1) {
                if (barcode.indexOf("SPH") != -1) return "hospital-P";
                else return "hospital-S";
            }

            if (barcode.substring(0, 1).equals("+")) {
                if (barcode.substring(1, 2).matches("[A-z]")) return "HIBC-P";
                else return "HIBC-S";
            }
            else {
                try {
                    ArrayList <NameValuePair> list = ean128Parser.parseBarcodeToList(barcode);
                    for (int i = 0;  i < list.size(); i ++) {
                        if (list.get(i).getName().toString().equals("EAN-NumberOfTradingUnit"))
                            primary = true;
                        if (list.get(i).getName().toString().equals("expire"))
                            secondary = true;
                        if (list.get(i).getName().toString().equals("LOT"))
                            secondary = true;
                    }

                    if ((primary) && (secondary)) return "code128";
                    if (primary) return "code128-P";
                    if (secondary) return "code128-S";

                } catch (Exception e) {
                    e.printStackTrace();
                }

                if (barcode.length() == 16) return "code128";
                else return "code128-S";
            }
        }
    }
}
