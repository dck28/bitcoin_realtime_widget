package com.streetcred.bitcoinpriceindexwidget;

import android.app.Activity;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by denniskong on 10/5/13.
 */
public class MainActivity extends Activity {

    public static final String ACTION_SETTINGS = MainActivity.class.getName() + ".ACTION_SETTINGS";
    private static final String SETTINGS_FRAG = SettingsFragment.FRAGMENT_TAG;
    private static Map<String, ApplicationAction> applicationActionMap = new HashMap<String, ApplicationAction>();
    private boolean clearingStack = false;

    static{
        addApplicationAction(new ShowSettings());
    }

    static void addApplicationAction (ApplicationAction applicationAction){
        applicationActionMap.put(applicationAction.getName(), applicationAction);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main);
        replaceMainFragment(getFragmentManager(), new SettingsFragment(), SETTINGS_FRAG, this);

    }

    static class ShowSettings extends OpenFragmentApplicationAction {
        ShowSettings() {
            super(ACTION_SETTINGS, SETTINGS_FRAG);
        }

        @Override
        protected Fragment createFragment(Intent intent) {
            return new SettingsFragment();
        }
    }

    abstract static class OpenFragmentApplicationAction extends ApplicationAction {

        private final String fragmentTag;

        protected OpenFragmentApplicationAction(String name, String fragmentTag) {
            super(name);
            this.fragmentTag = fragmentTag;
        }

        @Override
        protected void doAction(MainActivity activity, Intent intent) {
            if (validate()) {
                final FragmentManager fragmentManager = activity.getFragmentManager();
                if (isStackShouldBeCleared(intent)){
                    activity.clearActivitiesStack();
                }
                replaceMainFragment(fragmentManager, createFragment(intent), getFragmentTag(), activity);
            }
        }

        protected boolean isStackShouldBeCleared(Intent intent) {
            return intent.getBooleanExtra(Constants.FLAG_CLEAR_STACK, false);
        }

        protected abstract Fragment createFragment(Intent intent);
        protected boolean validate() {
            return true;
        }

        public String getFragmentTag() {
            return fragmentTag;
        }
    }

    abstract static class ApplicationAction {
        private final String name;

        protected ApplicationAction(String name) {
            this.name = name;
        }

        public String getName() {
            return name;
        }

        protected abstract void doAction(MainActivity activity, Intent intent);

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            ApplicationAction that = (ApplicationAction) o;

            return name.equals(that.name);

        }

        @Override
        public int hashCode() {
            return name.hashCode();
        }
    }

    public void clearActivitiesStack() {
        FragmentManager fragmentManager = getFragmentManager();
        try {
            clearingStack = true;
            int count = fragmentManager.getBackStackEntryCount();
            while (count-- > 0) {
                popBackStackImmediate(fragmentManager);
            }
        } finally {
            clearingStack = false;
        }
    }

    public static void popBackStackImmediate(FragmentManager fragmentManager) {
        try {
            fragmentManager.popBackStackImmediate();
        } catch (Exception ex) {
        }
    }

    public static void replaceMainFragment(final FragmentManager fragmentManager, Fragment fragment, final String tag, Activity activity) {
        InputMethodManager im = (InputMethodManager) activity.getSystemService(Context.INPUT_METHOD_SERVICE);
        final View currentFocus = activity.getWindow().getCurrentFocus();
        if (currentFocus != null) {
            im.hideSoftInputFromWindow(currentFocus.getApplicationWindowToken(), 0);
        }
        FragmentTransaction ft = fragmentManager.beginTransaction();
        setTransactionAnimation(ft);
        ft.replace(R.id.fragment_place, fragment, tag);
        ft.addToBackStack(null);
        ft.commitAllowingStateLoss();
    }

    private static void setTransactionAnimation(FragmentTransaction ft) {
        ft.setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out, android.R.animator.fade_in, android.R.animator.fade_out);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            handleBackKeyPress(true);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void handleBackKeyPress(boolean finishIfStackEmpty) {
        Fragment fragment = getFragmentManager().findFragmentById(R.id.fragment_place);
        onBackKeyPressed(finishIfStackEmpty);
    }

    protected void onBackKeyPressed(boolean finishIfStackEmpty) {
        final int backStackCount = getFragmentManager().getBackStackEntryCount();
        if (backStackCount == 1) {
            if (finishIfStackEmpty) {
                moveTaskToBack(true);
            }
            return;
        }
        if (backStackCount != 0) {
            popBackStackImmediate(getFragmentManager());
        }
    }
}
