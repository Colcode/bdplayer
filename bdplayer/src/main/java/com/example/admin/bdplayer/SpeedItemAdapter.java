package com.example.admin.bdplayer;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class SpeedItemAdapter extends BaseAdapter {
    private ArrayList<SpeedModel> data;
    private LayoutInflater inflater;

    public SpeedItemAdapter(Context context, ArrayList<SpeedModel> models) {
        this.data = models;
        inflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return data.size();
    }

    @Override
    public Object getItem(int arg0) {
        return data.get(arg0);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        SpeedModel singleModel = data.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = inflater.inflate(R.layout.item_speed_select, null);
            holder.speed = (TextView) convertView.findViewById(R.id.speed);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.speed.setText(singleModel.speedText);
        if(singleModel.selected){
            holder.speed.setTextColor(Color.parseColor("#D81B60"));
        }else{
            holder.speed.setTextColor(Color.parseColor("#FFFFFF"));
        }
        return convertView;
    }

    class ViewHolder {
        TextView speed;
    }
}
