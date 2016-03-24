package com.lichao.scancode.util;

import android.view.View;
import android.widget.EditText;

/**
 * Created by zblichao on 2016-03-12.
 */
public class ViewUtil {
    private View view;

    public ViewUtil(View view) {
        this.view = view;
    }

    public EditText setTextEditTextById(int id, String text) {
        if (text == null ||text.equals("null") ||text.equals(""))
            return null;
        EditText editText = (EditText) view.findViewById(id);
        editText.setText(text);
        return editText;
    }
}

