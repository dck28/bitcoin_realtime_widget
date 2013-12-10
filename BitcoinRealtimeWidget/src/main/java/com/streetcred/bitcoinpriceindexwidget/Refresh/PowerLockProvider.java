package com.streetcred.bitcoinpriceindexwidget.Refresh;

import android.content.Context;
import android.os.Handler;
import android.os.PowerManager;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by StreetCred Inc. on 11/8/13.
 */

public class PowerLockProvider {
    private static String NAME = null;
    private static volatile Map<String, PowerManager.WakeLock> lockMap = new HashMap<String, PowerManager.WakeLock>();

    public static PowerManager.WakeLock getLock(Context context, String name) {
        return acquireLock(context, name, null, null);
    }

    synchronized public static PowerManager.WakeLock acquireLock(Context context, String name, Long autoReleaseTime, Handler handler) {
        PowerManager.WakeLock wakeLock = lockMap.get(name);
        if (wakeLock == null) {
            PowerManager mgr =
                    (PowerManager) context.getSystemService(Context.POWER_SERVICE);

            wakeLock = mgr.newWakeLock(PowerManager.PARTIAL_WAKE_LOCK, getName(context));
            wakeLock.setReferenceCounted(true);
            lockMap.put(name, wakeLock);
        }
        if (!wakeLock.isHeld()) {
            wakeLock.acquire();
        }
        if (autoReleaseTime != null) {
            handler.postDelayed(new ReleaseLock(wakeLock), autoReleaseTime);
        }
        return wakeLock;
    }

    private static class ReleaseLock implements Runnable {
        private PowerManager.WakeLock wakeLock;

        private ReleaseLock(PowerManager.WakeLock wakeLock) {
            this.wakeLock = wakeLock;
        }

        @Override
        public void run() {
            release(wakeLock);
        }
    }

    public static void release(PowerManager.WakeLock lock) {
        release(lock, null);
    }

    public static synchronized void release(PowerManager.WakeLock lock, Handler handlerToCancel) {
        try {
            if (lock.isHeld()) {
                lock.release();
            }
            if (handlerToCancel != null) {
                handlerToCancel.removeCallbacksAndMessages(null);
            }
        } catch (Exception ignore) {}
    }

    private static String getName(Context context) {
        if (NAME == null) {
            return context.getPackageName() + ".powerLock";
        }
        return NAME;
    }
}
