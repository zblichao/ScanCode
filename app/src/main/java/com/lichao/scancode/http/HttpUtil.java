package com.lichao.scancode.http;


import com.lichao.scancode.entity.NameValuePair;
import com.lichao.scancode.util.StreamTools;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.List;


/**
 * Created by zblichao on 2016-03-10.
 */
public class HttpUtil {

    public static String uriAPI = "http://101.200.163.4/";

    public static String Post(String urlString,
                              List<NameValuePair> param) throws  IOException {
        String encoding = "UTF-8";
        String path = uriAPI + urlString;

        NameValuePair nameValuePair;
        StringBuffer sbparm = new StringBuffer();
        for (int i = 0; i < param.size(); i++) {
            nameValuePair = param.get(i);
            if (i == 0) {
                sbparm.append(String.format("%s=%s", nameValuePair.getName(),
                        URLEncoder.encode(nameValuePair.getValue(), "UTF-8")));
            } else {
                sbparm.append("&"
                        + String.format("%s=%s", nameValuePair.getName(),
                        URLEncoder.encode(nameValuePair.getValue(), "UTF-8")));
            }
        }
        byte[] data = sbparm.toString().getBytes(encoding);
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        //conn.setRequestMethod("POST");
        conn.setDoOutput(true);
        //application/x-javascript text/xml->xml数据 application/x-javascript->json对象 application/x-www-form-urlencoded->表单数据
       // conn.setRequestProperty("Content-Type", "text/plain; charset=" + encoding);
       // conn.setRequestProperty("Content-Length", String.valueOf(data.length));
        conn.setConnectTimeout(5 * 1000);
        OutputStream outStream = conn.getOutputStream();
        outStream.write(data);
        outStream.flush();
        outStream.close();
        System.out.println(conn.getResponseCode()); //响应代码 200表示成功
        if (conn.getResponseCode() == 200) {
            InputStream inStream = conn.getInputStream();
            String result = new String(StreamTools.streamToString(inStream));
            return result;
        }
        return "";
    }

    public static String Get(String urlString,
                             List<NameValuePair> params) throws  IOException {
        // 声明网址字符串
        String result = "";
        NameValuePair nameValuePair;
        StringBuffer sbparm = new StringBuffer();
        for (int i = 0; i < params.size(); i++) {
            nameValuePair = params.get(i);
            if (i == 0) {
                sbparm.append(String.format("%s=%s", nameValuePair.getName(),
                        URLEncoder.encode(nameValuePair.getValue(), "UTF-8")));
            } else {
                sbparm.append("&"
                        + String.format("%s=%s", nameValuePair.getName(),
                        URLEncoder.encode(nameValuePair.getValue(), "UTF-8")));
            }
        }
        String path = uriAPI + urlString;
        if (sbparm.length() > 0) {
            path += "?" + sbparm.toString();
        }
        URL url = new URL(path);
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setConnectTimeout(5 * 1000);
        conn.setRequestMethod("GET");
        InputStream inStream = conn.getInputStream();
        byte[] data = new byte[0];
        try {
            data = StreamTools.inputStream2Byte(inStream);
            result = new String(data);
            return result;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
}
