package com.lichao.scancode.receiver;



import com.lichao.scancode.entity.NameValuePair;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by bo gu on 3/10/2016.
 */
public class HIBCParser {
    public HIBCParser() {

    }

    public Date getHIBCDate(String barcode) {
        String flag;
        Date date = null;
        String dtStart;

        try {
            if (barcode.substring(0, 1).matches("[2-7]")) {
                flag = barcode.substring(0, 1);

                if (flag.equals("2")) {
                    dtStart = barcode.substring(1, 7);
                    SimpleDateFormat format = new SimpleDateFormat("MMddyy");
                    date = format.parse(dtStart);
                }
                if (flag.equals("3")) {
                    dtStart = barcode.substring(1, 7);
                    SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                    date = format.parse(dtStart);
                }
                if (flag.equals("4")) {
                    dtStart = barcode.substring(1, 9);
                    SimpleDateFormat format = new SimpleDateFormat("yyMMddHH");
                    date = format.parse(dtStart);
                }
                if (flag.equals("5")) {
                    dtStart = barcode.substring(1, 6);
                    SimpleDateFormat format = new SimpleDateFormat("yyD");
                    date = format.parse(dtStart);
                }
                if (flag.equals("6")) {
                    dtStart = barcode.substring(1, 6);
                    SimpleDateFormat format = new SimpleDateFormat("yyD");
                    date = format.parse(dtStart);
                }
            }
            else {
                dtStart = barcode.substring(0, 4);
                SimpleDateFormat format = new SimpleDateFormat("MMyy");
                date = format.parse(dtStart);
            }
        } catch (ParseException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

        return date;
    }

    public ArrayList<NameValuePair> HIBCSecondaryParser(String barcode) {
        ArrayList<NameValuePair> results = new ArrayList<NameValuePair>();
        String flag;
        Date date = null;
        String dtStart;
        String quantity;

        if (barcode.substring(0, 4).equals("+$$+")) {
            barcode = barcode.substring(4);
            try {
                if (barcode.substring(0, 1).matches("[2-7]")) {
                    flag = barcode.substring(0, 1);

                    if (flag.equals("2")) {
                        dtStart = barcode.substring(1, 7);
                        SimpleDateFormat format = new SimpleDateFormat("MMddyy");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(7);
                    }
                    if (flag.equals("3")) {
                        dtStart = barcode.substring(1, 7);
                        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(7);
                    }
                    if (flag.equals("4")) {
                        dtStart = barcode.substring(1, 9);
                        SimpleDateFormat format = new SimpleDateFormat("yyMMddHH");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(9);
                    }
                    if (flag.equals("5")) {
                        dtStart = barcode.substring(1, 6);
                        SimpleDateFormat format = new SimpleDateFormat("yyD");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(6);
                    }
                    if (flag.equals("6")) {
                        dtStart = barcode.substring(1, 6);
                        SimpleDateFormat format = new SimpleDateFormat("yyD");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(8);
                    }
                    if (flag.equals("7")) {
                        barcode = barcode.substring(1);
                        if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                            results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));
                    }
                }
                else {
                    dtStart = barcode.substring(0, 4);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    date = format.parse(dtStart);
                    barcode = barcode.substring(4);
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                results.add(new NameValuePair("expire", format.format(date)));
                if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                    results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return results;
        }
        if (barcode.substring(0, 3).equals("+$+")) {
            results.add(new NameValuePair("LOT", barcode.substring(3, barcode.length() - 2)));
            return results;
        }
        if (barcode.substring(0, 3).equals("+$$")) {
            barcode = barcode.substring(3);
            if (barcode.substring(0, 1).equals("8")) {
                results.add(new NameValuePair("quantity", String.valueOf(Integer.parseInt(barcode.substring(1, 3)))));
                barcode = barcode.substring(3);
            }
            if (barcode.substring(0, 1).equals("9")) {
                results.add(new NameValuePair("quantity", String.valueOf(Integer.parseInt(barcode.substring(1, 6)))));
                barcode = barcode.substring(6);
            }

            try {
                if (barcode.substring(0, 1).matches("[2-7]")) {
                    flag = barcode.substring(0, 1);

                    if (flag.equals("2")) {
                        dtStart = barcode.substring(1, 7);
                        SimpleDateFormat format = new SimpleDateFormat("MMddyy");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(7);
                    }
                    if (flag.equals("3")) {
                        dtStart = barcode.substring(1, 7);
                        SimpleDateFormat format = new SimpleDateFormat("yyMMdd");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(7);
                    }
                    if (flag.equals("4")) {
                        dtStart = barcode.substring(1, 9);
                        SimpleDateFormat format = new SimpleDateFormat("yyMMddHH");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(9);
                    }
                    if (flag.equals("5")) {
                        dtStart = barcode.substring(1, 6);
                        SimpleDateFormat format = new SimpleDateFormat("yyD");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(6);
                    }
                    if (flag.equals("6")) {
                        dtStart = barcode.substring(1, 6);
                        SimpleDateFormat format = new SimpleDateFormat("yyD");
                        date = format.parse(dtStart);
                        barcode = barcode.substring(8);
                    }
                    if (flag.equals("7")) {
                        barcode = barcode.substring(1);
                        if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                            results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));
                    }
                }
                else {
                    dtStart = barcode.substring(0, 4);
                    SimpleDateFormat format = new SimpleDateFormat("MMyy");
                    date = format.parse(dtStart);
                    barcode = barcode.substring(4);
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                results.add(new NameValuePair("expire", format.format(date)));
                if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                    results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return results;
        }
        if (barcode.substring(0, 2).equals("+$")) {
            if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));
            return results;
        }
        if (barcode.substring(0, 1).equals("+")) {
            try {
                dtStart = barcode.substring(1, 6);
                SimpleDateFormat format = new SimpleDateFormat("yyD");
                date = format.parse(dtStart);
                barcode = barcode.substring(6);

                format = new SimpleDateFormat("yyyy-MM-dd");
                results.add(new NameValuePair("expire", format.format(date)));
                if (!barcode.substring(0, barcode.length() - 2).isEmpty())
                    results.add(new NameValuePair("LOT", barcode.substring(0, barcode.length() - 2)));

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            return results;
        }
        return results;
    }
}

