package com.streetcred.bitcoinpriceindexwidget;

import android.app.AlertDialog;
import android.content.Context;
import android.preference.ListPreference;
import android.util.AttributeSet;

public class DisplayThemePreferenceDialog extends ListPreference {

    public DisplayThemePreferenceDialog(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onPrepareDialogBuilder(AlertDialog.Builder builder) {
        super.onPrepareDialogBuilder(builder);
    }
}