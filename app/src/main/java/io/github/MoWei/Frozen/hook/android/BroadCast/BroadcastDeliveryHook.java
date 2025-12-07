package io.github.MoWei.Frozen.hook.android.BroadCast;

import android.os.Build;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.XpUtils;
import io.github.MoWei.Frozen.hook.android.Utils.SystemChecker;
import io.github.MoWei.Frozen.hook.android.virtuals.BroadcastRecord;

public class BroadcastDeliveryHook extends MethodHook {
    final private String TAG = "广播";
    Config config;
    public BroadcastDeliveryHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }

    @Override
    public String getTargetClass() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU) ? "com.android.server.am.BroadcastQueueImpl" : "com.android.server.am.BroadcastQueue";
    }

    @Override
    public String getTargetMethod() {
        return "deliverToRegisteredReceiverLocked";
    }

    @Override
    public Object[] getTargetParam() {
        if (SystemChecker.isHuawei(classLoader))
            return new Object[] { "com.android.server.am.BroadcastRecord", "com.android.server.am.BroadcastFilter", boolean.class, int.class, "com.android.server.am.BroadcastRecordEx" };

        return new Object[] { "com.android.server.am.BroadcastRecord", "com.android.server.am.BroadcastFilter", boolean.class, int.class };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void beforeMethod(XC_MethodHook.MethodHookParam param) {
                Object record = param.args[0];
                if (record == null)
                    return;

                BroadcastRecord broadcastRecord = new BroadcastRecord(record);

                Object filter = param.args[1];
                if (filter == null)
                    return;

                Object receiver = XposedHelpers.getObjectField(filter, "receiverList");
                if (receiver == null)
                    return;

                Object app = XposedHelpers.getObjectField(receiver, "app");
                if (app == null)
                    return;

                int uid = XposedHelpers.getIntField(app,  "uid");
                if (!config.managedApp.contains(uid) || config.foregroundUid.contains(uid) || config.pendingUid.contains(uid))
                    return;
                if (XpUtils.DEBUG_BROADCAST_STATIC)
                    XpUtils.log(TAG, "静态广播已跳过 " + uid);
                broadcastRecord.skippedDelivery((int) param.args[3]);
                param.setResult(null);
            }
        };
    }

    @Override
    public String successLog() {
        return "初始化静态广播已成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.UPSIDE_DOWN_CAKE;
    }
}