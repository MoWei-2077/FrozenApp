package io.github.MoWei.Frozen.hook.android.BroadCast;


import android.os.Build;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.XpUtils;
import io.github.MoWei.Frozen.hook.android.Utils.SystemChecker;


public class BroadcastSkipHook extends MethodHook {
    final static String TAG = "广播";
    Config config;
    public BroadcastSkipHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }

    @Override
    public String getTargetClass() {
        return "com.android.server.am.BroadcastSkipPolicy";
    }

    @Override
    public String getTargetMethod() {
        return "shouldSkipMessage";
    }

    @Override
    public Object[] getTargetParam() {
        if (SystemChecker.isVivo(classLoader))
            return new Object[] { "com.android.server.am.BroadcastRecord", "com.android.server.am.BroadcastFilter", boolean.class, int.class, "com.android.server.am.IVivoBroadcastQueueModern" };
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.BAKLAVA)
            return new Object[] { "com.android.server.am.BroadcastRecord", "com.android.server.am.BroadcastFilter", boolean.class };
        else
            return new Object[] { "com.android.server.am.BroadcastRecord", "com.android.server.am.BroadcastFilter"};
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                if (param.getResult() != null)
                    return;

                Object filter = param.args[1];
                if (filter == null)
                    return;

                Object receiver = XposedHelpers.getObjectField(filter, "receiverList");
                if (receiver == null)
                    return;

                Object app = XposedHelpers.getObjectField(receiver, "app");
                if (app == null)
                    return;

                int uid = XposedHelpers.getIntField(app, "uid");

                if (!config.managedApp.contains(uid) || config.foregroundUid.contains(uid) || config.pendingUid.contains(uid))
                    return;
                if (XpUtils.DEBUG_BROADCAST_DYNAMIC)
                    XpUtils.log(TAG, "跳过动态广播: " + uid);
                param.setResult("Skipping deliver [Frozen]: frozen process");
            }
        };
    }

    @Override
    public String successLog() {
        return "Hook广播已成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }
}