package com.streetcred.bitcoinpriceindexwidget;

/**
 * Created by StreetCred Inc. on 10/5/13.
 */

import android.app.ActionBar;
import android.app.Activity;
import android.app.Fragment;
import android.app.NotificationManager;
import android.appwidget.AppWidgetManager;
import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.widget.RemoteViews;
import android.widget.Toast;

import com.streetcred.bitcoinpriceindexwidget.Notify.PriceOngoingNotification;
import com.streetcred.bitcoinpriceindexwidget.Refresh.RefreshIntervalManager;

import java.text.MessageFormat;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class SettingsFragment extends PreferenceFragment {
    public static final String FRAGMENT_TAG = getFragmentTag(SettingsFragment.class);

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Load the preferences from an XML resource
        if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("English")){
            addPreferencesFromResource(R.xml.preferences);
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("中文(繁體)")){
            addPreferencesFromResource(R.xml.preferences_chinese);
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("中文(简体)")){
            addPreferencesFromResource(R.xml.preferences_chinese_simplified);
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("zh_HK")){
            addPreferencesFromResource(R.xml.preferences_chinese);
            XBTWidgetApplication.getSharedPreferences()
                    .edit()
                    .putString(Constants.PREF_DISPLAY_LANGUAGE, "中文(繁體)")
                    .commit();
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("zh_TW")){
            addPreferencesFromResource(R.xml.preferences_chinese);
            XBTWidgetApplication.getSharedPreferences()
                    .edit()
                    .putString(Constants.PREF_DISPLAY_LANGUAGE, "中文(繁體)")
                    .commit();
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("zh_CN")){
            addPreferencesFromResource(R.xml.preferences_chinese_simplified);
            XBTWidgetApplication.getSharedPreferences()
                    .edit()
                    .putString(Constants.PREF_DISPLAY_LANGUAGE, "中文(简体)")
                    .commit();
        } else if (XBTWidgetApplication.getSharedPreferences()
                .getString(Constants.PREF_DISPLAY_LANGUAGE, Locale.getDefault().toString())
                .equalsIgnoreCase("zh")){
            addPreferencesFromResource(R.xml.preferences_chinese_simplified);
            XBTWidgetApplication.getSharedPreferences()
                    .edit()
                    .putString(Constants.PREF_DISPLAY_LANGUAGE, "中文(简体)")
                    .commit();
        } else {
            addPreferencesFromResource(R.xml.preferences);
            XBTWidgetApplication.getSharedPreferences()
                    .edit()
                    .putString(Constants.PREF_DISPLAY_LANGUAGE, "English")
                    .commit();
        }

        Activity activity = getActivity();
        if(activity != null){
            ActionBar actionBar = activity.getActionBar();
            if(actionBar != null){
                actionBar.setDisplayHomeAsUpEnabled(false);
                actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_STANDARD);
                actionBar.setDisplayShowTitleEnabled(true);

                if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    actionBar.setTitle("比特幣報價精靈");
                } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                    actionBar.setTitle("比特币报价精灵");
                } else {
                    actionBar.setTitle("Bitcoin Price Index Widget");
                }
            }
        }

        Preference donatePreference = findPreference("copy_bitcoin_address");
        if (donatePreference != null){

//      Flag of first clicks for donate and rate
            if (XBTWidgetApplication
                    .getSharedPreferences()
                    .getBoolean(Constants.PREF_DONATE_EVER_CLICKED, false) != true){
                donatePreference.setWidgetLayoutResource(R.layout.donate_layout_icon_off);
            } else {
                donatePreference.setWidgetLayoutResource(R.layout.donate_layout_icon);
            }

            donatePreference.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                @Override
                public boolean onPreferenceClick(Preference preference) {

                    XBTWidgetApplication
                            .getSharedPreferences().edit().putBoolean(Constants.PREF_DONATE_EVER_CLICKED, true).commit();

                    // Implement with bitcoin scheme URI
                    String bitcoinSchema = "bitcoin:";
                    String bitcoinReceiveAddress = "13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH"; //your address from wallet
                    String bitcoinAmount = "?amount=0.0015"; //Amount in BTC, 1$
                    String bitcoinURI = bitcoinSchema + bitcoinReceiveAddress + bitcoinAmount;
                    Intent bitcoinIntent = new Intent(Intent.ACTION_VIEW);
                    bitcoinIntent.setData(Uri.parse(bitcoinURI));
                    try {
                        startActivity(bitcoinIntent);
                    } catch (ActivityNotFoundException e) {
                        ClipboardManager clipboard = (ClipboardManager) getActivity().getSystemService(Context.CLIPBOARD_SERVICE);
                        ClipData clip = ClipData.newPlainText("Bitcoin Address", "13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH");
                        clipboard.setPrimaryClip(clip);
                        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")) {
                            Toast.makeText(getActivity(), "複製成功. 很感激您的支持!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")) {
                            Toast.makeText(getActivity(), "复制成功. 很感谢您的支持!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getActivity(), "Address Copied. Thank You for Your Support!\n13hwfZqGQrsNXEhx1riRpFog5JPdPJBLGH", Toast.LENGTH_LONG).show();
                        }
                    }

                    // Test Start of Price Alert Activity
                    // Intent priceAlertIntent = new Intent(getActivity(), PriceAlertActivity.class);
                    // startActivity(priceAlertIntent);
                    // End test
                    return true;
                }
            });
        }

        DisplayLanguagePreference(this, savedInstanceState);

        if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
            DisplayThemePreference_Chinese();
            DisplayCurrencyPreference_Chinese();
            DisplayDataSourcePreference_Chinese();
            DisplayRefreshIntervalPreference_Chinese();
            DisplayOngoingNotificationPreference();
        } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
            DisplayThemePreference_Chinese_Simplified();
            DisplayCurrencyPreference_Chinese_Simplified();
            DisplayDataSourcePreference_Chinese_Simplified();
            DisplayRefreshIntervalPreference_Chinese_Simplified();
            DisplayOngoingNotificationPreference();
        } else {
            DisplayThemePreference_English();
            DisplayCurrencyPreference_English();
            DisplayDataSourcePreference_English();
            DisplayRefreshIntervalPreference_English();
            DisplayOngoingNotificationPreference();
        }
    }

    private void DisplayRefreshIntervalPreference_English(){
        DisplayRefreshIntervalPreferenceDialog refreshIntervalPreference = (DisplayRefreshIntervalPreferenceDialog) findPreference("pref_refresh_interval");
        if (refreshIntervalPreference != null){
            refreshIntervalPreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_REFRESH_INTERVAL, "-1"));

            refreshIntervalPreference.setTitle("Refresh Rate: " + refreshIntervalPreference.getEntry());
            refreshIntervalPreference.setWidgetLayoutResource(R.layout.refresh_layout_icon);
            refreshIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_REFRESH_INTERVAL, newValue.toString())
                            .commit();

                    String newDisplayRate = convertToEnglish(newValue.toString());

                    preference.setTitle("Refresh Rate: " + newDisplayRate);
                    if ("-1".equalsIgnoreCase(newValue.toString())) {
                        RefreshIntervalManager.instance().cancelIntervalRefreshing(XBTWidgetApplication.instance);
                    } else {
                        RefreshIntervalManager.instance().startIntervalRefreshing(XBTWidgetApplication.instance, true);
                    }
                    return true;
                }

                private String convertToEnglish(String value){
                    if("-1".equalsIgnoreCase(value)){ return "None"; }
                    if("60000".equalsIgnoreCase(value)){ return "1 minute"; }
                    if("120000".equalsIgnoreCase(value)){ return "2 minutes"; }
                    if("180000".equalsIgnoreCase(value)){ return "3 minutes"; }
                    if("300000".equalsIgnoreCase(value)){ return "5 minutes"; }
                    if("480000".equalsIgnoreCase(value)){ return "8 minutes"; }
                    if("780000".equalsIgnoreCase(value)){ return "13 minutes"; }
                    if("1260000".equalsIgnoreCase(value)){ return "21 minutes"; }
                    return "None";
                }
            });
        }
    }

    private void DisplayRefreshIntervalPreference_Chinese(){
        DisplayRefreshIntervalPreferenceDialog refreshIntervalPreference = (DisplayRefreshIntervalPreferenceDialog) findPreference("pref_refresh_interval_chinese");
        if (refreshIntervalPreference != null){
            refreshIntervalPreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_REFRESH_INTERVAL, "-1"));

            refreshIntervalPreference.setTitle("更新頻率: " + refreshIntervalPreference.getEntry());
            refreshIntervalPreference.setWidgetLayoutResource(R.layout.refresh_layout_icon);
            refreshIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_REFRESH_INTERVAL, newValue.toString())
                            .commit();

                    String newDisplayRate = convertToChinese(newValue.toString());

                    preference.setTitle("更新頻率: " + newDisplayRate);
                    if ("-1".equalsIgnoreCase(newValue.toString())) {
                        RefreshIntervalManager.instance().cancelIntervalRefreshing(XBTWidgetApplication.instance);
                    } else {
                        RefreshIntervalManager.instance().startIntervalRefreshing(XBTWidgetApplication.instance, true);
                    }
                    return true;
                }

                private String convertToChinese(String value){
                    if("-1".equalsIgnoreCase(value)){ return "關"; }
                    if("60000".equalsIgnoreCase(value)){ return "1 分鐘"; }
                    if("120000".equalsIgnoreCase(value)){ return "2 分鐘"; }
                    if("180000".equalsIgnoreCase(value)){ return "3 分鐘"; }
                    if("300000".equalsIgnoreCase(value)){ return "5 分鐘"; }
                    if("480000".equalsIgnoreCase(value)){ return "8 分鐘"; }
                    if("780000".equalsIgnoreCase(value)){ return "13 分鐘"; }
                    if("1260000".equalsIgnoreCase(value)){ return "21 分鐘"; }
                    return "關";
                }
            });
        }
    }

    private void DisplayRefreshIntervalPreference_Chinese_Simplified(){
        DisplayRefreshIntervalPreferenceDialog refreshIntervalPreference = (DisplayRefreshIntervalPreferenceDialog) findPreference("pref_refresh_interval_chinese_simplified");
        if (refreshIntervalPreference != null){
            refreshIntervalPreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_REFRESH_INTERVAL, "-1"));

            refreshIntervalPreference.setTitle("更新频率: " + refreshIntervalPreference.getEntry());
            refreshIntervalPreference.setWidgetLayoutResource(R.layout.refresh_layout_icon);
            refreshIntervalPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_REFRESH_INTERVAL, newValue.toString())
                            .commit();

                    String newDisplayRate = convertToChineseSimplified(newValue.toString());

                    preference.setTitle("更新频率: " + newDisplayRate);
                    if ("-1".equalsIgnoreCase(newValue.toString())) {
                        RefreshIntervalManager.instance().cancelIntervalRefreshing(XBTWidgetApplication.instance);
                    } else {
                        RefreshIntervalManager.instance().startIntervalRefreshing(XBTWidgetApplication.instance, true);
                    }
                    return true;
                }

                private String convertToChineseSimplified(String value){
                    if("-1".equalsIgnoreCase(value)){ return "关"; }
                    if("60000".equalsIgnoreCase(value)){ return "1 分钟"; }
                    if("120000".equalsIgnoreCase(value)){ return "2 分钟"; }
                    if("180000".equalsIgnoreCase(value)){ return "3 分钟"; }
                    if("300000".equalsIgnoreCase(value)){ return "5 分钟"; }
                    if("480000".equalsIgnoreCase(value)){ return "8 分钟"; }
                    if("780000".equalsIgnoreCase(value)){ return "13 分钟"; }
                    if("1260000".equalsIgnoreCase(value)){ return "21 分钟"; }
                    return "关";
                }
            });
        }
    }

    private void DisplayOngoingNotificationPreference(){
        PreferenceManager preferenceManager = getPreferenceManager();
        if(preferenceManager != null){
            final CheckBoxPreference ongoingcheckboxPref = (CheckBoxPreference) getPreferenceManager().findPreference("ongoingcheckboxPref");
            if (ongoingcheckboxPref != null){

                if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                    ongoingcheckboxPref.setTitle("放上通知箱");
                } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                    ongoingcheckboxPref.setTitle("放上通知箱");
                } else {
                    ongoingcheckboxPref.setTitle("Ongoing Notification");
                }

                ongoingcheckboxPref.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                    public boolean onPreferenceChange(Preference preference, Object newValue) {
                        boolean newOngoingNotificationPreference = (Boolean) newValue;
                        XBTWidgetApplication.getSharedPreferences()
                                .edit()
                                .putBoolean(Constants.PREF_ONGOING_NOTIFICATION, newOngoingNotificationPreference)
                                .commit();
                        if (newOngoingNotificationPreference){
                            // Enable ongoing notifications
                            SharedPreferences pref = XBTWidgetApplication.getSharedPreferences();
                            PriceOngoingNotification.hit(getActivity(),
                                    pref.getString(Constants.PREF_LAST_UPDATED_PRICE, "0"),
                                    pref.getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"),
                                    pref.getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"),
                                    false);
                        } else {
                            // Cancel all current ongoing notifications
                            Activity activity = getActivity();
                            if(activity != null){
                                ((NotificationManager)activity.getSystemService(Context.NOTIFICATION_SERVICE)).cancelAll();
                            }
                        }
                        return true;
                    }
                });
            }
        }
    }

    private void DisplayDataSourcePreference_English() {
        DisplayDataSourcePreferenceDialog dataSourcePreference = (DisplayDataSourcePreferenceDialog) findPreference("pref_data_source");
        if (dataSourcePreference != null){
            dataSourcePreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));

            dataSourcePreference.setTitle("Data Source: " + dataSourcePreference.getValue());
            dataSourcePreferenceSetWidgetLayoutResource(dataSourcePreference, dataSourcePreference.getValue());
            dataSourcePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, newValue.toString())
                            .commit();
                    preference.setTitle("Data Source: " + newValue.toString());
                    dataSourcePreferenceSetWidgetLayoutResource(preference, newValue.toString());
                    new Thread(new Runnable() { public void run() {
                        try {
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            Activity activity = getActivity();
                            if(activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* no connection");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* no connection").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if(appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(new ComponentName(activity, XBTRealtimeWidgetProvider.class), remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayCurrencyPreference_English() {
        DisplayCurrencyPreferenceDialog currencyPreference = (DisplayCurrencyPreferenceDialog) findPreference("pref_currency");
        if (currencyPreference != null){
            currencyPreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));

            currencyPreference.setTitle("Display Currency: " + currencyPreference.getValue());
            currencyPreferenceSetWidgetLayoutResource(currencyPreference, currencyPreference.getValue());
            currencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_CURRENCY, newValue.toString())
                            .commit();
                    preference.setTitle("Display Currency: " + newValue.toString());
                    currencyPreferenceSetWidgetLayoutResource(preference, newValue.toString());
                    new Thread(new Runnable() { public void run() {
                        try {
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            Activity activity = getActivity();
                            if(activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* no connection");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* no connection").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if(appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(new ComponentName(activity, XBTRealtimeWidgetProvider.class), remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayThemePreference_English() {
        DisplayThemePreferenceDialog themePreference = (DisplayThemePreferenceDialog) findPreference("pref_theme");
        if (themePreference != null){
            themePreference.setValue(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_THEME, "Float"));

            themePreference.setTitle("Widget Theme: " + themePreference.getValue());
            themePreferenceSetWidgetLayoutResource(themePreference, themePreference.getValue());
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_THEME, newValue.toString())
                            .commit();
                    preference.setTitle("Widget Theme: " + newValue.toString());
                    themePreferenceSetWidgetLayoutResource(preference, newValue.toString());
                    SharedPreferences.Editor editor = XBTWidgetApplication.getSharedPreferences().edit();
                    try {
                        Activity activity = getActivity();
                        if(activity != null){
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                            if(appWidgetManager != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                ComponentName thisWidget = new ComponentName(activity, XBTRealtimeWidgetProvider.class);
                                if (newValue.toString().equalsIgnoreCase("Navy")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DD2B3856"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DD2B3856")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("Clementine")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DDEB5E00"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DDEB5E00")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("Float")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.TRANSPARENT);
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.TRANSPARENT).commit();
                                } else if (newValue.toString().equalsIgnoreCase("Glassy")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#AA383838"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#AA383838")).commit();
                                }
                                remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    private void DisplayLanguagePreference(final SettingsFragment thisFragment, final Bundle savedInstanceState) {
        DisplayLanguagePreferenceDialog languagePreference = (DisplayLanguagePreferenceDialog) findPreference("pref_language");
        if (languagePreference != null){

            languagePreference.setWidgetLayoutResource(R.layout.language_layout_icon);
            languagePreference.setValue(
                    XBTWidgetApplication
                            .getSharedPreferences()
                            .getString(Constants.PREF_DISPLAY_LANGUAGE, "English")); // Default English

            if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                languagePreference.setTitle("語言: " + languagePreference.getValue());
            } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                languagePreference.setTitle("语言: " + languagePreference.getValue());
            } else {
                languagePreference.setTitle("Language: " + languagePreference.getValue());
            }

            languagePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_DISPLAY_LANGUAGE, newValue.toString())
                            .commit();
                    if (newValue.toString().equalsIgnoreCase("English")){
                        preference.setTitle("Language: " + newValue.toString());
                    } else if (newValue.toString().equalsIgnoreCase("中文(繁體)")){
                        preference.setTitle("語言: " + newValue.toString());
                    } else if (newValue.toString().equalsIgnoreCase("中文(简体)")){
                        preference.setTitle("语言: " + newValue.toString());
                    }
                    new Thread(new Runnable() { public void run() {
                        try {
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }}).start();
                    thisFragment.onCreate(savedInstanceState);
                    thisFragment.onResume();

                    return true;
                }
            });
        }
    }

    private void DisplayCurrencyPreference_Chinese() {
        DisplayCurrencyPreferenceDialog currencyPreference = (DisplayCurrencyPreferenceDialog) findPreference("pref_currency_chinese");
        if (currencyPreference != null){
            currencyPreference.setValue(Util.convertCurrencyEnglishToChinese(
                    XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));

            currencyPreference.setTitle("顯示貨幣: " + currencyPreference.getValue());
            currencyPreferenceSetWidgetLayoutResource(currencyPreference, XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            currencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue_Chinese) {
                    String newStringValue = Util.convertCurrencyChineseToEnglish(newValue_Chinese.toString());
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_CURRENCY, newStringValue)
                            .commit();
                    preference.setTitle("顯示貨幣: " + newValue_Chinese.toString());
                    currencyPreferenceSetWidgetLayoutResource(preference, newStringValue);
                    new Thread(new Runnable() { public void run() {
                        try{
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e){
                            Activity activity = getActivity();
                            if( activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 綱絡未能連接").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                ComponentName componentName = new ComponentName(activity, XBTRealtimeWidgetProvider.class);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if (appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(componentName, remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayDataSourcePreference_Chinese() {
        DisplayDataSourcePreferenceDialog dataSourcePreference = (DisplayDataSourcePreferenceDialog) findPreference("pref_data_source_chinese");
        if (dataSourcePreference != null){
            dataSourcePreference.setValue(
                    XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));

            dataSourcePreference.setTitle("數據來源: " + dataSourcePreference.getValue());
            dataSourcePreferenceSetWidgetLayoutResource(dataSourcePreference, dataSourcePreference.getValue());
            dataSourcePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue_Chinese) {
                    String newStringValue = newValue_Chinese.toString();
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, newStringValue)
                            .commit();
                    preference.setTitle("數據來源: " + newValue_Chinese.toString());
                    dataSourcePreferenceSetWidgetLayoutResource(preference, newValue_Chinese.toString());
                    new Thread(new Runnable() { public void run() {
                        try{
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e){
                            Activity activity = getActivity();
                            if(activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* 綱絡未能連接");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 綱絡未能連接").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if(appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(new ComponentName(activity, XBTRealtimeWidgetProvider.class), remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayThemePreference_Chinese() {
        DisplayThemePreferenceDialog themePreference = (DisplayThemePreferenceDialog) findPreference("pref_theme_chinese");
        if (themePreference != null){
            themePreference.setValue(Util.convertThemeEnglishToChinese(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_THEME, "Float")));

            themePreference.setTitle("顯示主題: " + themePreference.getValue());
            themePreferenceSetWidgetLayoutResource(themePreference, XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_THEME, "Float"));
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    String newThemeInEnglish = Util.convertThemeChineseToEnglish(newValue.toString());
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_THEME, newThemeInEnglish)
                            .commit();
                    preference.setTitle("顯示主題: " + newValue.toString());
                    themePreferenceSetWidgetLayoutResource(preference, newThemeInEnglish);
                    SharedPreferences.Editor editor = XBTWidgetApplication.getSharedPreferences().edit();
                    try{
                        Activity activity = getActivity();
                        if(activity != null){
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                            if(appWidgetManager != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                ComponentName thisWidget = new ComponentName(activity, XBTRealtimeWidgetProvider.class);
                                if (newValue.toString().equalsIgnoreCase("深藍")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DD2B3856"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DD2B3856")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("金橘")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DDEB5E00"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DDEB5E00")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("漂浮")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.TRANSPARENT);
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.TRANSPARENT).commit();
                                } else if (newValue.toString().equalsIgnoreCase("磨砂玻璃")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#AA383838"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#AA383838")).commit();
                                }
                                remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    private void DisplayCurrencyPreference_Chinese_Simplified() {
        DisplayCurrencyPreferenceDialog currencyPreference = (DisplayCurrencyPreferenceDialog) findPreference("pref_currency_chinese_simplified");
        if (currencyPreference != null){
            currencyPreference.setValue(Util.convertCurrencyEnglishToChineseSimplified(
                    XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD")));

            currencyPreference.setTitle("显示货币: " + currencyPreference.getValue());
            currencyPreferenceSetWidgetLayoutResource(currencyPreference, XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_CURRENCY, "USD"));
            currencyPreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue_Chinese) {
                    String newStringValue = Util.convertCurrencyChineseSimplifiedToEnglish(newValue_Chinese.toString());
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_CURRENCY, newStringValue)
                            .commit();
                    preference.setTitle("显示货币: " + newValue_Chinese.toString());
                    currencyPreferenceSetWidgetLayoutResource(preference, newStringValue);
                    new Thread(new Runnable() { public void run() {
                        try{
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e){
                            Activity activity = getActivity();
                            if( activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* 纲络未能连接");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 纲络未能连接").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                ComponentName componentName = new ComponentName(activity, XBTRealtimeWidgetProvider.class);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if (appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(componentName, remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayDataSourcePreference_Chinese_Simplified() {
        DisplayDataSourcePreferenceDialog dataSourcePreference = (DisplayDataSourcePreferenceDialog) findPreference("pref_data_source_chinese_simplified");
        if (dataSourcePreference != null){
            dataSourcePreference.setValue(
                    XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, "Coindesk"));

            dataSourcePreference.setTitle("数据来源: " + dataSourcePreference.getValue());
            dataSourcePreferenceSetWidgetLayoutResource(dataSourcePreference, dataSourcePreference.getValue());
            dataSourcePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue_Chinese) {
                    String newStringValue = newValue_Chinese.toString();
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_DATA_SOURCE, newStringValue)
                            .commit();
                    preference.setTitle("数据来源: " + newValue_Chinese.toString());
                    dataSourcePreferenceSetWidgetLayoutResource(preference, newValue_Chinese.toString());
                    new Thread(new Runnable() { public void run() {
                        try{
                            RefreshData refresh = new RefreshData();
                            refresh.execute().get(10000, TimeUnit.MILLISECONDS);
                        } catch (Exception e){
                            Activity activity = getActivity();
                            if(activity != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                remoteViews.setTextViewText(R.id.update_time, "* 纲络未能连接");
                                XBTWidgetApplication.getSharedPreferences().edit().putString(Constants.SAVE_UPDATE_TIME_WIDGET_STATE, "* 纲络未能连接").commit();
                                remoteViews.setTextColor(R.id.price, Color.GRAY);
                                AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                                if(appWidgetManager != null){
                                    remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                    appWidgetManager.updateAppWidget(new ComponentName(activity, XBTRealtimeWidgetProvider.class), remoteViews);
                                }
                            }
                        }
                    }}).start();
                    return true;
                }
            });
        }
    }

    private void DisplayThemePreference_Chinese_Simplified() {
        DisplayThemePreferenceDialog themePreference = (DisplayThemePreferenceDialog) findPreference("pref_theme_chinese_simplified");
        if (themePreference != null){
            themePreference.setValue(Util.convertThemeEnglishToChineseSimplified(XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_THEME, "Float")));

            themePreference.setTitle("显示主题: " + themePreference.getValue());
            themePreferenceSetWidgetLayoutResource(themePreference, XBTWidgetApplication
                    .getSharedPreferences()
                    .getString(Constants.PREF_LAST_UPDATED_THEME, "Float"));
            themePreference.setOnPreferenceChangeListener(new Preference.OnPreferenceChangeListener() {
                @Override
                public boolean onPreferenceChange(Preference preference,
                                                  Object newValue) {
                    String newThemeInEnglish = Util.convertThemeChineseSimplifiedToEnglish(newValue.toString());
                    XBTWidgetApplication.getSharedPreferences()
                            .edit()
                            .putString(Constants.PREF_LAST_UPDATED_THEME, newThemeInEnglish)
                            .commit();
                    preference.setTitle("显示主题: " + newValue.toString());
                    themePreferenceSetWidgetLayoutResource(preference, newThemeInEnglish);
                    SharedPreferences.Editor editor = XBTWidgetApplication.getSharedPreferences().edit();
                    try{
                        Activity activity = getActivity();
                        if(activity != null){
                            AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(activity);
                            if(appWidgetManager != null){
                                RemoteViews remoteViews = new RemoteViews(activity.getPackageName(), R.layout.widget_layout);
                                ComponentName thisWidget = new ComponentName(activity, XBTRealtimeWidgetProvider.class);
                                if (newValue.toString().equalsIgnoreCase("深蓝")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DD2B3856"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DD2B3856")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("金橘")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#DDEB5E00"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#DDEB5E00")).commit();
                                } else if (newValue.toString().equalsIgnoreCase("漂浮")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.TRANSPARENT);
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.TRANSPARENT).commit();
                                } else if (newValue.toString().equalsIgnoreCase("磨砂玻璃")){
                                    remoteViews.setInt(R.id.background, "setBackgroundColor",
                                            Color.parseColor("#AA383838"));
                                    editor.putInt(Constants.SAVE_BACKGROUND_WIDGET_STATE, Color.parseColor("#AA383838")).commit();
                                }
                                remoteViews = Util.saveRemoteViewsState(XBTWidgetApplication.getSharedPreferences(), remoteViews, activity);
                                appWidgetManager.updateAppWidget(thisWidget, remoteViews);
                            }
                        }
                    } catch (Exception e){
                        e.printStackTrace();
                    }
                    return true;
                }
            });
        }
    }

    private void themePreferenceSetWidgetLayoutResource(Preference dialog, String theme){
        if (theme.equalsIgnoreCase("Float")){
            dialog.setWidgetLayoutResource(R.layout.theme_float);
        } else if (theme.equalsIgnoreCase("Navy")){
            dialog.setWidgetLayoutResource(R.layout.theme_navy);
        } else if (theme.equalsIgnoreCase("Clementine")){
            dialog.setWidgetLayoutResource(R.layout.theme_clementine);
        } else if (theme.equalsIgnoreCase("Glassy")){
            dialog.setWidgetLayoutResource(R.layout.theme_glassy);
        }
    }

    private void currencyPreferenceSetWidgetLayoutResource(Preference dialog, String currency){
        if (currency.equalsIgnoreCase("USD")){
            dialog.setWidgetLayoutResource(R.layout.currency_usd);
        } else if (currency.equalsIgnoreCase("CAD")){
            dialog.setWidgetLayoutResource(R.layout.currency_cad);
        } else if (currency.equalsIgnoreCase("GBP")){
            dialog.setWidgetLayoutResource(R.layout.currency_gbp);
        } else if (currency.equalsIgnoreCase("EUR")){
            dialog.setWidgetLayoutResource(R.layout.currency_eur);
        } else if (currency.equalsIgnoreCase("CHF")){
            dialog.setWidgetLayoutResource(R.layout.currency_chf);
        } else if (currency.equalsIgnoreCase("AUD")){
            dialog.setWidgetLayoutResource(R.layout.currency_aud);
        } else if (currency.equalsIgnoreCase("CNY")){
            dialog.setWidgetLayoutResource(R.layout.currency_cny);
        } else if (currency.equalsIgnoreCase("HKD")){
            dialog.setWidgetLayoutResource(R.layout.currency_hkd);
        } else if (currency.equalsIgnoreCase("TWD")){
            dialog.setWidgetLayoutResource(R.layout.currency_twd);
        } else if (currency.equalsIgnoreCase("INR")){
            dialog.setWidgetLayoutResource(R.layout.currency_inr);
        }
    }

    private void dataSourcePreferenceSetWidgetLayoutResource(Preference dialog, String dataSource){
        if (dataSource.equalsIgnoreCase("Coindesk")){
            dialog.setWidgetLayoutResource(R.layout.datasource_coindesk);
        } else if (dataSource.equalsIgnoreCase("Coinbase")){
            dialog.setWidgetLayoutResource(R.layout.datasource_coinbase);
        } else if (dataSource.equalsIgnoreCase("Bitstamp")){
            dialog.setWidgetLayoutResource(R.layout.datasource_bitstamp);
        } else if (dataSource.equalsIgnoreCase("BTC-E")){
            dialog.setWidgetLayoutResource(R.layout.datasource_btce);
        } else if (dataSource.equalsIgnoreCase("Bitfinex")){
            dialog.setWidgetLayoutResource(R.layout.datasource_bitfinex);
        } else if (dataSource.equalsIgnoreCase("Kraken")){
            dialog.setWidgetLayoutResource(R.layout.datasource_kraken);
        } else if (dataSource.equalsIgnoreCase("BTC China")){
            dialog.setWidgetLayoutResource(R.layout.datasource_btcchina);
        } else if (dataSource.equalsIgnoreCase("ANXBTC")){
            dialog.setWidgetLayoutResource(R.layout.datasource_anxbtc);
        } else if (dataSource.equalsIgnoreCase("itBit")){
            dialog.setWidgetLayoutResource(R.layout.datasource_itbit);
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
                final Activity activity = getActivity();
                if(activity != null){
                    rateUsPref = new Preference(activity);
                    rateUsPref.setKey("prefer_rate_and_feedback");

//      Flag of first clicks for donate and rate
                    if (XBTWidgetApplication
                            .getSharedPreferences()
                            .getBoolean(Constants.PREF_RATE_EVER_CLICKED, false) != true){
                        rateUsPref.setWidgetLayoutResource(R.layout.rate_layout_icon_off);
                    } else {
                        rateUsPref.setWidgetLayoutResource(R.layout.rate_layout_icon);
                    }

                    if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(繁體)")){
                        rateUsPref.setTitle("評論此應用程式");
                        rateUsPref.setSummary("請給予您的意見及其他功能建議");
                    } else if (XBTWidgetApplication.getSharedPreferences().getString(Constants.PREF_DISPLAY_LANGUAGE, "English").equalsIgnoreCase("中文(简体)")){
                        rateUsPref.setTitle("评论此应用程式");
                        rateUsPref.setSummary("请给予您的意见及其他功能建议");
                    } else {
                        rateUsPref.setTitle("Rate this widget");
                        rateUsPref.setSummary("Give feedback & feature suggestions");
                    }

                    rateUsPref.setOnPreferenceClickListener(new Preference.OnPreferenceClickListener() {
                        @Override
                        public boolean onPreferenceClick(Preference preference) {

                            XBTWidgetApplication
                                    .getSharedPreferences().edit().putBoolean(Constants.PREF_RATE_EVER_CLICKED, true).commit();

                            activity.startActivity(createOpenAppInPlayStoreIntent(getActivity()));
                            return true;
                        }
                    });
                    rateUsPref.setOrder(1);
                    getPreferenceScreen().addPreference(rateUsPref);
                }
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
            if(packageManager != null){
                PLAY_STORE_AVAILABLE = !packageManager.queryIntentActivities(createOpenPlayStoreIntent(), 0).isEmpty();
            }
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

