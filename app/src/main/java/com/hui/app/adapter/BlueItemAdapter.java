package com.hui.app.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RadioButton;
import android.widget.TextView;

import com.hui.app.R;
import com.hui.app.define.BlueItemDefine;

import java.util.ArrayList;
import java.util.List;

public class BlueItemAdapter extends BaseAdapter {

    private final LayoutInflater mInflater;
    private final List<BlueItemDefine> mData;
//    private static int mSelectedPosition = -1;

    public  BlueItemAdapter(Context context, List<BlueItemDefine> data) {
        mData = data;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int i) {
        return mData.get(i);
    }

    @Override
    public long getItemId(int i) {
        return i;
    }

    @Override
    public View getView(int i, View view, ViewGroup viewGroup) {
        ViewHolder holder;
        if (view == null) {
            view = mInflater.inflate(R.layout.bluetooth_device_item, viewGroup, false);
            holder = new ViewHolder();
            holder.blue_decibel = view.findViewById(R.id.blue_decibel);
            holder.blue_name = view.findViewById(R.id.blue_name);
            holder.device_id = view.findViewById(R.id.device_id);
            view.setTag(holder);
        }else {
            holder = (ViewHolder) view.getTag();
        }
        //        设置文本
        BlueItemDefine item = (BlueItemDefine) getItem(i);
        holder.blue_name.setText(item.getBlueName());
        String mac = item.getDeviceId().replaceAll("(.{2})(?!$)", "$1:");
        holder.device_id.setText(mac);
        holder.blue_decibel.setText(item.getBlueDecibel() + "");
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                mSelectedPosition = i;
//                notifyDataSetChanged(); // 刷新列表
//            }
//        });


        return view;
    }

//    public static int getSelectedPosition() {
//        return mSelectedPosition;
//
//    }

    static class ViewHolder {
        TextView blue_decibel;
        TextView blue_name;
        TextView device_id;
    }
}
