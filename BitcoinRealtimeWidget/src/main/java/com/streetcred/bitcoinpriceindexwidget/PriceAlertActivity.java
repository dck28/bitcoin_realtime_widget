package com.streetcred.bitcoinpriceindexwidget;

import java.util.ArrayList;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;

import com.streetcred.bitcoinpriceindexwidget.PriceAlerts.Item;
import com.streetcred.bitcoinpriceindexwidget.PriceAlerts.ItemAdapter;
import com.streetcred.bitcoinpriceindexwidget.PriceAlerts.NumberSpinAdapter;

public class PriceAlertActivity extends Activity {
    public static final int DIALOG_DETAIL_ITEM = 0;
    public static final String PRICE_ALERT_PREF = "price_alert_preference";
    public static final String ITEM_COUNT = "item_count";
    public static final String ITEM_NUMBER = "item_number";
    ListView mListView;
    Button mBtnAddItem;
    ItemAdapter mAdapter;

    int flag = 0;
    int posSelect;
    ArrayList<Item> mListItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.price_alert_activity);
        getSharePref();
        setView();
    }

    public void setView() {
        mListView = (ListView) findViewById(R.id.lv_item);
        mAdapter = new ItemAdapter(getApplicationContext(), mListItem);
        mListView.setAdapter(mAdapter);
        mListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                    long arg3) {
                // TODO Auto-generated method stub
                posSelect = arg2;
                flag = 2;
                showDialog(DIALOG_DETAIL_ITEM);
            }
        });
        mBtnAddItem = (Button) findViewById(R.id.btn_add);
        mBtnAddItem.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                flag = 1;
                showDialog(DIALOG_DETAIL_ITEM);
            }
        });
    }

    public void addItem(Item item) {
        mListItem.add(item);
        mAdapter.notifyDataSetChanged();
        savePriceAlertToSharedPref();
    }

    public void editItem(Item item, int pos) {
        if (mListItem.size() > pos) {
            mListItem.get(pos).setNumber_1(item.getNumber_1());
            mListItem.get(pos).setNumber_2(item.getNumber_2());
            mListItem.get(pos).setNumber_3(item.getNumber_3());
            mListItem.get(pos).setNumber_4(item.getNumber_4());
        }
        mAdapter.notifyDataSetChanged();
        savePriceAlertToSharedPref();
    }

    public void deleteItem(int pos) {
        if (mListItem.size() > pos) {
            mListItem.remove(pos);
        }
        mAdapter.notifyDataSetChanged();
        savePriceAlertToSharedPref();
    }

    public void savePriceAlertToSharedPref() {
        Editor mEditor;
        mEditor = getSharedPreferences(PRICE_ALERT_PREF, Context.MODE_PRIVATE)
                .edit();
        mEditor.putInt(ITEM_COUNT, mListItem.size());
        for (int i = 0; i < mListItem.size(); i++) {
            mEditor.putInt(ITEM_NUMBER + (int) (i * 4), mListItem.get(i)
                    .getNumber_1());
            mEditor.putInt(ITEM_NUMBER + (int) (i * 4 + 1), mListItem.get(i)
                    .getNumber_2());
            mEditor.putInt(ITEM_NUMBER + (int) (i * 4 + 2), mListItem.get(i)
                    .getNumber_3());
            mEditor.putInt(ITEM_NUMBER + (int) (i * 4 + 3), mListItem.get(i)
                    .getNumber_4());
        }
        mEditor.commit();
    }

    public void getSharePref() {
        SharedPreferences savedSession = getSharedPreferences(PRICE_ALERT_PREF,
                Context.MODE_PRIVATE);
        mListItem = new ArrayList<Item>();
        int item_count = savedSession.getInt(ITEM_COUNT, 0);
        for (int i = 0; i < item_count; i++) {
            mListItem.add(new Item(savedSession.getInt(ITEM_NUMBER
                    + (int) (i * 4), 0), savedSession.getInt(ITEM_NUMBER
                    + (int) (i * 4 + 1), 0), savedSession.getInt(ITEM_NUMBER
                    + (int) (i * 4 + 2), 0), savedSession.getInt(ITEM_NUMBER
                    + (int) (i * 4 + 3), 0)));
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
//        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    protected Dialog onCreateDialog(int id) {
        Dialog mDialog = new Dialog(this);
        switch (id) {
            case DIALOG_DETAIL_ITEM:
                Log.d("trungpt", " 1");
                mDialog.setContentView(R.layout.detail_item);
                mDialog.setTitle(getString(R.string.dialog_title_detail_item));
                LinearLayout mLnAdd = (LinearLayout) mDialog
                        .findViewById(R.id.ln_add);
                LinearLayout mLnDelete = (LinearLayout) mDialog
                        .findViewById(R.id.ln_delete);
                Button mBtnAdd = (Button) mDialog.findViewById(R.id.btn_add);
                Button mBtnEdit = (Button) mDialog.findViewById(R.id.btn_edit);
                Button mBtnDelete = (Button) mDialog.findViewById(R.id.btn_delete);
                Log.d("trungpt", " 2");
                if (flag == 1) {
                    mLnAdd.setVisibility(View.VISIBLE);
                } else {
                    mLnDelete.setVisibility(View.VISIBLE);
                }
                Log.d("trungpt", " 3");
                final Spinner mSpinner1 = (Spinner) mDialog
                        .findViewById(R.id.spinner_1);
                ArrayList<Item> mListItemSpinner1 = new ArrayList<Item>();
                NumberSpinAdapter mAdapter1 = new NumberSpinAdapter(
                        getApplicationContext(), mListItemSpinner1);
                mSpinner1.setAdapter(mAdapter1);

                final Spinner mSpinner2 = (Spinner) mDialog
                        .findViewById(R.id.spinner_2);
                ArrayList<Item> mListItemSpinner2 = new ArrayList<Item>();
                NumberSpinAdapter mAdapter2 = new NumberSpinAdapter(
                        getApplicationContext(), mListItemSpinner2);
                mSpinner2.setAdapter(mAdapter2);

                final Spinner mSpinner3 = (Spinner) mDialog
                        .findViewById(R.id.spinner_3);
                ArrayList<Item> mListItemSpinner3 = new ArrayList<Item>();
                NumberSpinAdapter mAdapter3 = new NumberSpinAdapter(
                        getApplicationContext(), mListItemSpinner3);
                mSpinner3.setAdapter(mAdapter3);

                final Spinner mSpinner4 = (Spinner) mDialog
                        .findViewById(R.id.spinner_4);
                ArrayList<Item> mListItemSpinner4 = new ArrayList<Item>();
                NumberSpinAdapter mAdapter4 = new NumberSpinAdapter(
                        getApplicationContext(), mListItemSpinner4);
                mSpinner4.setAdapter(mAdapter4);
                Log.d("trungpt", " 4");
                if (flag != 1) {
                    mSpinner1.setSelection(mListItem.get(posSelect).getNumber_1());
                    mSpinner2.setSelection(mListItem.get(posSelect).getNumber_2());
                    mSpinner3.setSelection(mListItem.get(posSelect).getNumber_3());
                    mSpinner4.setSelection(mListItem.get(posSelect).getNumber_4());
                }
                Log.d("trungpt", " 5");
                mBtnAdd.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        addItem(new Item(mSpinner1.getSelectedItemPosition(),
                                mSpinner2.getSelectedItemPosition(), mSpinner3
                                .getSelectedItemPosition(), mSpinner4
                                .getSelectedItemPosition()));
                        removeDialog(DIALOG_DETAIL_ITEM);
                    }
                });

                mBtnEdit.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        editItem(
                                new Item(mSpinner1.getSelectedItemPosition(),
                                        mSpinner2.getSelectedItemPosition(),
                                        mSpinner3.getSelectedItemPosition(),
                                        mSpinner4.getSelectedItemPosition()),
                                posSelect);
                        removeDialog(DIALOG_DETAIL_ITEM);
                    }
                });

                mBtnDelete.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        // TODO Auto-generated method stub
                        deleteItem(posSelect);
                        removeDialog(DIALOG_DETAIL_ITEM);
                    }
                });

                mDialog.setOnDismissListener(new OnDismissListener() {

                    @Override
                    public void onDismiss(DialogInterface dialog) {
                        // TODO Auto-generated method stub
                        removeDialog(DIALOG_DETAIL_ITEM);
                    }
                });
                Log.d("trungpt", " 6");
                return mDialog;
        }
        return null;
    }

}
