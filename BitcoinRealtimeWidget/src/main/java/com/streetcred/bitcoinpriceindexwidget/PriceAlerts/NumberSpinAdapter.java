package com.streetcred.bitcoinpriceindexwidget.PriceAlerts;

import java.util.ArrayList;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class NumberSpinAdapter extends BaseAdapter {

    Context mContext;
    ArrayList<Item> mListItem;
    private LayoutInflater mInflater;

    public NumberSpinAdapter(Context context, ArrayList<Item> objects) {
        super();
        mInflater = LayoutInflater.from(context);
        mContext = context;
        mListItem = objects;
    }

    public int getCount() {
        // TODO Auto-generated method stub
        return 10;
    }

    public Item getItem(int position) {
        // TODO Auto-generated method stub
        return mListItem.get(position);
    }

    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return 0;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
            holder.name = (TextView) convertView
                    .findViewById(android.R.id.text1);
            holder.name.setTextColor(mContext.getResources().getColor(
                    android.R.color.black));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText("" + position);
        return convertView;
    }

    @Override
    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        // TODO Auto-generated method stub
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(
                    android.R.layout.simple_spinner_item, parent, false);
            holder.name = (TextView) convertView
                    .findViewById(android.R.id.text1);
            holder.name.setTextColor(mContext.getResources().getColor(
                    android.R.color.black));
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.name.setText("" + position);
        return convertView;
    }

    private class ViewHolder {
        TextView name;
    }

}
