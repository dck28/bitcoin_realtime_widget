package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by denniskong on 10/5/13.
 */

import android.app.ActionBar;
import android.app.Fragment;
import android.appwidget.AppWidgetManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.widget.RemoteViews;
import android.widget.Toast;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragment {
    public static final String FRAGMENT_TAG = getFragmentTag(SettingsFragment.class);
    public boolean isChinese = true;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        if (isChinese){
            addPreferencesFromResource(R.xml.preferences_chinese);
        } else {
            addPreferencesFromResource(R.xml.preferences);
        }

        getActivity().getActionBar().setDisplayHomeAsUpEnabled(false);
        getActivity().getActionBar().setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
        getActivity().getActionBar().setDisplayShowTitleEnabled(true);

        findPreference("copy_bitcoin_address").setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
            @Override
            public boolean onPreferenceClick(Preference preference) {
                ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                ClipData clip = ClipData.newPlainText("Bitcoin Address", "13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH");
                clipboard.setPrimaryClip(clip);
                if (isChinese){
                    Toast.makeText(getActivity(), "複製成功. 很感激您的支持!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                } else {
                    Toast.makeText(getActivity(), "Address Copied. Thank You for Your Support!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                }
                return true;
            }
        });

        if (isChinese){
            DisplayThemePreference_Chinese();
            DisplayCurrencyPreference_Chinese();
        } else {
            DisplayThemePreference_English();
            DisplayCurrencyPreference_English();
        }
    }

    private void DisplayCurrencyPreference_English() {
        DisplayCurrencyPreferenceDialog currencyPreference = (DisplayCurrencyPreferenceDialog) findPreference("pref_currency");
        if (currencyPreference != null){
            if (currencyPreference.getValue() == null) {
                currencyPreference.setValueIndex(0);
            }
            currencyPreference.setTitle("Display Currency: " + currencyPreference.getValue());
            currencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_CURRENCY, newValue.toString())
                            .commit();
                    preference.setTitle("Display Currency: " + newValue.toString());
                    try{
                        RefreshData refresh = new RefreshData();
                        refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                    } catch (Exception e){
                        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.widget_layout);
                        remoteViews.setTextViewText(R.id.update_time, "* no connection");
                        AppWidgetManager.getInstance(getActivity()).updateAppWidget(new ComponentName(getActivity(), XBTRealtimeWidgetProvider.class), remoteViews);
                    }
                    return true;
                }
            });
        }
    }

    private void DisplayThemePreference_English() {
        DisplayThemePreferenceDialog themePreference = (DisplayThemePreferenceDialog) findPreference("pref_theme");
        if (themePreference != null){
            if (themePreference.getValue() == null) {
                themePreference.setValueIndex(0);
            }
            themePreference.setTitle("Theme: " + themePreference.getValue());
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_THEME, newValue.toString())
                            .commit();
                    preference.setTitle("Theme: " + newValue.toString());
                    try {
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.widget_layout);
                        ComponentName thisWidget = new ComponentName(getActivity(), XBTRealtimeWidgetProvider.class);
                        if (newValue.toString().equalsIgnoreCase("Navy"))
                            remoteViews.setInt(R.id.background, "setBackgroundColor",
                                    Color.parseColor("#DD2B3856"));
                        else if (newValue.toString().equalsIgnoreCase("Float"))
                            remoteViews.setInt(R.id.background, "setBackgroundColor",
                                    Color.TRANSPARENT);
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    private void DisplayCurrencyPreference_Chinese() {
        DisplayCurrencyPreferenceDialog currencyPreference = (DisplayCurrencyPreferenceDialog) findPreference("pref_currency_chinese");
        if (currencyPreference != null){
            if (currencyPreference.getValue() == null) {
                currencyPreference.setValueIndex(0);
            }
            currencyPreference.setTitle("顯示貨幣: " + currencyPreference.getValue());
            currencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue_Chinese) {
                    String newStringValue = convertCurrencyChineseToEnglish(newValue_Chinese.toString());
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_CURRENCY, newStringValue)
                            .commit();
                    preference.setTitle("顯示貨幣: " + newValue_Chinese.toString());
                    try{
                        RefreshData refresh = new RefreshData();
                        refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                    } catch (Exception e){
                        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.widget_layout);
                        remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                        AppWidgetManager.getInstance(getActivity()).updateAppWidget(new ComponentName(getActivity(), XBTRealtimeWidgetProvider.class), remoteViews);
                    }
                    return true;
                }
            });
        }
    }

    private String convertCurrencyChineseToEnglish(String newValue){
        if(newValue.equals("美元")){
            return "USD";
        } else if(newValue.equals("英鎊")){
            return "GBP";
        } else if(newValue.equals("歐元")){
            return "EUR";
        }
        return "USD";
    }

    private void DisplayThemePreference_Chinese() {
        DisplayThemePreferenceDialog themePreference = (DisplayThemePreferenceDialog) findPreference("pref_theme_chinese");
        if (themePreference != null){
            if (themePreference.getValue() == null) {
                themePreference.setValueIndex(0);
            }
            themePreference.setTitle("顯示主題: " + themePreference.getValue());
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_THEME, newValue.toString())
                            .commit();
                    preference.setTitle("顯示主題: " + newValue.toString());
                    try{
                        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(getActivity());
                        RemoteViews remoteViews = new RemoteViews(getActivity().getPackageName(), R.layout.widget_layout);
                        ComponentName thisWidget = new ComponentName(getActivity(), XBTRealtimeWidgetProvider.class);
                        if (newValue.toString().equalsIgnoreCase("深藍色"))
                            remoteViews.setInt(R.id.background, "setBackgroundColor",
                                    Color.parseColor("#DD2B3856"));
                        else if (newValue.toString().equalsIgnoreCase("漂浮"))
                            remoteViews.setInt(R.id.background, "setBackgroundColor",
                                    Color.TRANSPARENT);
                        appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        updateRateUsPreferenceView();
    }

    @Override
    public void onStop(){
        super.onStop();
    }

    public static String getFragmentTag (Class <? extends Fragment> fragmentClass){
        return fragmentClass.getSimpleName();
    }

    // Rate this widget / Feedback
    private void updateRateUsPreferenceView() {
        Preference rateUsPref = findPreference("prefer_rate_and_feedback");
        if (isPlayStoreAvailable(getActivity())) {
            if (rateUsPref == null) {
                rateUsPref = new Preference(getActivity());
                rateUsPref.setKey("prefer_rate_and_feedback");
                if (isChinese){
                    rateUsPref.setTitle("評論此應用程式");
                    rateUsPref.setSummary("請給予您的意見及其他功能建議");
                } else {
                    rateUsPref.setTitle("Rate this widget");
                    rateUsPref.setSummary("Give feedback & feature suggestions");
                }

                rateUsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                    @Override
                    public boolean onPreferenceClick(Preference preference) {
                        getActivity().startActivity(createOpenAppInPlayStoreIntent(getActivity()));
                        return true;
                    }
                });
                rateUsPref.setOrder(1);
                getPreferenceScreen().addPreference(rateUsPref);
            }
        } else {
            removePreference(rateUsPref);
        }
    }

    private static Boolean PLAY_STORE_AVAILABLE = null;
    public static final MessageFormat MARKET_APP_URL_TEMPLATE = new MessageFormat("market://details?id={0}", Locale.ENGLISH);

    public static boolean isPlayStoreAvailable(final Context context) {
        if (PLAY_STORE_AVAILABLE == null) {
            PackageManager packageManager = context.getPackageManager();
            PLAY_STORE_AVAILABLE = !packageManager.queryIntentActivities(createOpenPlayStoreIntent(), 0).isEmpty();
        }
        return PLAY_STORE_AVAILABLE;
    }

    public static Intent createOpenPlayStoreIntent() {
        return new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=dummy"));
    }

    public static Intent createOpenAppInPlayStoreIntent(final Context context) {
        final Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(MARKET_APP_URL_TEMPLATE
                .format(new String[]{context.getPackageName()})));
        intent.setFlags(intent.getFlags() | Intent.FLAG_ACTIVITY_NEW_TASK);
        return intent;
    }

    private void removePreference(Preference preference) {
        if (preference != null) {
            getPreferenceScreen().removePreference(preference);
        }
    }
}

