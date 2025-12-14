package io.github.MoWei.Frozen.hook.android.PendingIntent;

import android.os.Build;
import android.content.Intent;
import android.os.Bundle;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.XpUtils;
import android.os.IBinder;

public class PendingIntentHook extends MethodHook {
    final private static String TAG = "[后台意图]";
    private final Config config;

    public PendingIntentHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }

    @Override
    public String getTargetClass() {
        return "com.android.server.am.PendingIntentRecord";
    }

    @Override
    public String getTargetMethod() {
        return "sendInner";
    }

    @Override
    public Object[] getTargetParam() {
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) {
            return new Object[]{
                    "android.app.IApplicationThread", int.class, Intent.class, String.class, IBinder.class,
                    "android.content.IIntentReceiver", String.class, IBinder.class, String.class, int.class, int.class, int.class, Bundle.class
            };
        } else {
            return new Object[]{
                    int.class, Intent.class, String.class, IBinder.class, "android.content.IIntentReceiver",
                    String.class, IBinder.class, String.class, int.class, int.class, int.class, Bundle.class
            };
        }
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void beforeMethod(MethodHookParam param) {
                synchronized (XposedHelpers.getObjectField(XposedHelpers.getObjectField(param.thisObject, "controller"), "mLock")) {
                    if (XposedHelpers.getBooleanField(param.thisObject, "canceled"))
                        return;

                    Object key = XposedHelpers.getObjectField(param.thisObject, "key");
                    if (key == null)
                        return;

                    PendingIntentKey pendingIntentKey = new PendingIntentKey(key);

                    final Integer value = config.uidIndex.get(pendingIntentKey.packageName);
                    if (value != null && config.managedApp.contains(value)) {
                        synchronized(Config.IntentUid) {
                            if (!Config.IntentUid.contains(value)) {
                                Config.IntentUid.add(value);
                            }
                        }
                        if (XpUtils.DEBUG_PENDING_INTENT)
                            XpUtils.log(TAG, "后台意图 UID:" + value);
                    }
                }
            }
        };
    }
    @Override
    public String successLog() {
        return TAG + " 获取后台意图成功";
    }

    public int getMinVersion() {
        return Build.VERSION_CODES.Q;
    }
}