package com.streetcred.bitcoinpriceindexwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.content.res.TypedArray;
import android.preference.ListPreference;
import android.util.AttributeSet;
import android.widget.ListAdapter;

import com.streetcred.bitcoinpriceindexwidget.adapters.ImageArrayAdapter;

public class DisplayCurrencyPreferenceDialog extends ListPreference {
    private int[] resourceIds = null;

    public DisplayCurrencyPreferenceDialog(Context context, AttributeSet attrs) {
        super(context, attrs);

        TypedArray typedArray = context.obtainStyledAttributes(attrs,
                R.styleable.ImageListPreference);

        String[] imageNames = context.getResources().getStringArray(
                typedArray.getResourceId(typedArray.getIndexCount()-1, -1));

        resourceIds = new int[imageNames.length];

        for (int i=0;i<imageNames.length;i++) {
            String imageName = imageNames[i].substring(
                    imageNames[i].indexOf('/') + 1,
                    imageNames[i].lastIndexOf('.'));

            resourceIds[i] = context.getResources().getIdentifier(imageName,
                    null, context.getPackageName());
        }

        typedArray.recycle();
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        int index = findIndexOfValue(getSharedPreferences().getString(
                getKey(), "1"));

        ListAdapter listAdapter = new ImageArrayAdapter(getContext(),
                R.layout.listitem, getEntries(), resourceIds, index);

        // Order matters.
        builder.setAdapter(listAdapter, this);
        super.onPrepareDialogBuilder(builder);
    }
}