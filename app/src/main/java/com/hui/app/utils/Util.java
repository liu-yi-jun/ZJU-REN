package com.hui.app.utils;

import android.content.Context;
import android.content.DialogInterface;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.hui.app.ECBLE;

public class Util {
    public static void showAlertRSDialog(Context context) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle("提示");
        builder.setMessage("设备发生短路异常，已经停止刺激。请检查问题后，按复位键复位设备");
        builder.setPositiveButton("复位", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // 确认按钮点击事件
                ECBLE.writeBLECharacteristicValue("RS", false);
                Toast.makeText(context, "短路复位已发送", Toast.LENGTH_SHORT).show();
                dialog.dismiss();
            }
        });
        AlertDialog dialog = builder.create();
        dialog.setCancelable(false);
        dialog.show();
    }
}
