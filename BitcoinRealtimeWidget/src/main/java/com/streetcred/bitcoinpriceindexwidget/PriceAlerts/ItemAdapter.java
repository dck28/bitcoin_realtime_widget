package com.streetcred.bitcoinpriceindexwidget.PriceAlerts;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.streetcred.bitcoinpriceindexwidget.R;

public class ItemAdapter extends BaseAdapter {
    private Context mContext;
    private ArrayList<Item> mListItem;
    private LayoutInflater mInflater;

    public ItemAdapter(Context c, ArrayList<Item> obj) {
        mContext = c;
        mListItem = obj;
        mInflater = LayoutInflater.from(mContext);

    }

    public int getCount() {
        return mListItem.size();
    }

    public Object getItem(int position) {
        return mListItem.get(position);
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        final ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.row_item, null);
            holder.mRowItem1 = (TextView) convertView
                    .findViewById(R.id.tv_item_1);
            holder.mRowItem2 = (TextView) convertView
                    .findViewById(R.id.tv_item_2);
            holder.mRowItem3 = (TextView) convertView
                    .findViewById(R.id.tv_item_3);
            holder.mRowItem4 = (TextView) convertView
                    .findViewById(R.id.tv_item_4);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.mRowItem1.setText("" + mListItem.get(position).getNumber_1());
        holder.mRowItem2.setText("" + mListItem.get(position).getNumber_2());
        holder.mRowItem3.setText("" + mListItem.get(position).getNumber_3());
        holder.mRowItem4.setText("" + mListItem.get(position).getNumber_4());
        return convertView;
    }

    private class ViewHolder {
        TextView mRowItem1;
        TextView mRowItem2;
        TextView mRowItem3;
        TextView mRowItem4;
    }
}