package com.lichao.scancode.util;

import android.content.Context;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
/**
 * Created by zblichao on 2016-03-10.
 */

    public class ToastUtil {
    //    public static Toast toast;

    /**
     * 长时间显示消息
     *
     * @param context 上下文对象
     * @param message 消息内容
     */
    public static void showLongToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_LONG).show();

    }

    /**
     * 短时间显示消息
     *
     * @param context 上下文对象
     * @param message 消息内容
     */
    public static void showShortToast(Context context, String message) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show();

    }

    /**
     * 带图片消息提示
     *
     * @param context
     * @param ImageResourceId
     * @param text
     */
    public static void showShortImageToast(Context context, int ImageResourceId, CharSequence text) {
        //创建一个Toast提示消息
        Toast toast = Toast.makeText(context, text, Toast.LENGTH_LONG);
        //设置Toast提示消息在屏幕上的位置
        toast.setGravity(Gravity.CENTER, 0, 0);
        //获取Toast提示消息里原有的View
        View toastView = toast.getView();
        //创建一个ImageView
        ImageView img = new ImageView(context);
        img.setImageResource(ImageResourceId);
        //创建一个LineLayout容器
        LinearLayout ll = new LinearLayout(context);
        ll.setOrientation(LinearLayout.HORIZONTAL);
        //向LinearLayout中添加ImageView和Toast原有的View
        ll.addView(img);
        ll.addView(toastView);
        //将LineLayout容器设置为toast的View
        toast.setView(ll);
        //显示消息
        toast.show();
    }

}
