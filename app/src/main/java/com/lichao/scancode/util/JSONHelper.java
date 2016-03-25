package com.lichao.scancode.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import java.util.LinkedList;
import java.util.List;

/**
 * Created by zblichao on 2016-03-15.
 */
public class JSONHelper {
    public static JSONObject JSONTokener(String JSON) throws JSONException {

        JSONTokener jsonParser = new JSONTokener(JSON);
        // 此时还未读取任何json文本，直接读取就是一个JSONObject对象。
        // 如果此时的读取位置在"name" : 了，那么nextValue就是"yuanzhifei89"（String）
        // person.getJSONArray("phone");
        JSONObject object = (JSONObject) jsonParser.nextValue();
        return object;

    }

    public static List<JSONObject> JSONArray(String JSON) throws JSONException {
        List<JSONObject> res = new LinkedList<JSONObject>();
        JSONArray jsonArray = new JSONArray(JSON);
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject item = jsonArray.getJSONObject(i);
            res.add(item);
        }
        return res;
    }
}
