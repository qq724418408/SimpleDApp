package com.forms.view.dialog;

import android.app.Dialog;
import android.content.Context;
import android.text.TextUtils;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.forms.utils.ScreenUtils;
import com.forms.xxxapp.R;

/**
 * Created by bubbly on 2017/12/6.
 */

public class DialogUtil {

    public static Dialog pictureDialog(Context context, View.OnClickListener l){
        return new XDialog.Builder(context).setContentView(R.layout.dialog_select_picture_camera)
                .setWidthAndHeight(-1, -2)
                .setOnClickListener(R.id.tvPicture, l)
                .setOnClickListener(R.id.tvCamera, l)
                .setOnClickListener(R.id.tvCancel, l)
                .formBottom(true)
                .create();
    }

    public static Dialog confirmDialog(Context context, String msg, final DialogClick dialogClick) {
        final XDialog dialog = new XDialog.Builder(context)
                .setContentView(R.layout.dialog_confirm_no_title)
                .setWidthAndHeight(ScreenUtils.dip2px(context, 280), ViewGroup.LayoutParams.WRAP_CONTENT)
                .create();
        TextView tvMsg = dialog.getView(R.id.tvMsg);
        TextView tvOk = dialog.getView(R.id.tvOk);
        TextView tvCancel = dialog.getView(R.id.tvCancel);
        if (!TextUtils.isEmpty(msg)) {
            tvMsg.setText(msg);
        }
        tvOk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogClick.onOkClick(v, dialog);
            }
        });
        tvCancel.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                dialogClick.onCancelClick(v, dialog);
            }
        });
        return dialog;
    }

    public interface DialogClick {
        void onOkClick(View view, XDialog dialog);
        void onCancelClick(View view, XDialog dialog);
    }
}
