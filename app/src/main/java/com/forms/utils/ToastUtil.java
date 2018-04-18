package com.forms.utils;

import android.content.Context;
import android.widget.Toast;

/**
 *
 * @author Administrator
 * @date 2017/12/27 0027
 */
public class ToastUtil {

    private static Toast toast;

    /**
     * @param text
     */
    public static void show(Context context, String text) {
        if (toast == null) {
            toast = Toast.makeText(context, text, Toast.LENGTH_SHORT);
        } else {
            toast.setText(text);//如果不为空，则直接改变当前toast的文本
        }
        toast.show();
    }

}
