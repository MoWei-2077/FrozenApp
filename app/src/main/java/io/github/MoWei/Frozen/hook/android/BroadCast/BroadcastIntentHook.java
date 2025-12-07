package io.github.MoWei.Frozen.hook.android.BroadCast;

import android.content.Intent;
import android.os.Build;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.hook.XpUtils;
import io.github.MoWei.Frozen.hook.Config;

public class BroadcastIntentHook {
    Config config;
    final static String TAG = "广播意图";
    public BroadcastIntentHook(Config config, ClassLoader classLoader) {
        this.config = config;
        try {
            Class<?> clazz = XposedHelpers.findClassIfExists("com.android.server.am.ActivityManagerService", classLoader);
            if (Build.VERSION.SDK_INT > Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {
                Class<?> controller = XposedHelpers.findClassIfExists("com.android.server.am.BroadcastController", classLoader);
                if (controller != null)
                    clazz = controller;
            }

            if (clazz == null) {
                XpUtils.log(TAG, "无法监听广播意图!");
                return;
            }

            Method targetMethod = null;
            for (Method method : clazz.getDeclaredMethods())
                if (method.getName().equals("broadcastIntentLocked") && (targetMethod == null || targetMethod.getParameterTypes().length < method.getParameterTypes().length))
                    targetMethod = method;

            if (targetMethod == null) {
                XpUtils.log(TAG, "无法监听广播意图!");
                return;
            }

            XposedBridge.hookMethod(targetMethod, new AbstractMethodHook() {
                @Override
                protected void beforeMethod(MethodHookParam param) {
                    int intentArgsIndex = 3;

                    int userIdIndex = 19;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.S_V2 && Build.VERSION.SDK_INT < Build.VERSION_CODES.UPSIDE_DOWN_CAKE)
                        userIdIndex = 20;
                    if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
                        userIdIndex = 21;

                    Intent intent = (Intent) param.args[intentArgsIndex];
                    int userId = (int) param.args[userIdIndex];
                    if (intent != null) {
                        String action = intent.getAction();

                        if (action == null || !action.endsWith(".android.c2dm.intent.RECEIVE") || action.equals("org.unifiedpush.android.connector.MESSAGE") || action.equals("com.meizu.flyme.push.intent.MESSAGE"))
                            return;

                        String packageName = (intent.getComponent() == null ? intent.getPackage() : intent.getComponent().getPackageName());

                        if (packageName == null)
                            return;
                        if (config.managedApp.contains(userId)) {
                            synchronized(Config.IntentUid) {
                                if (!Config.IntentUid.contains(userId)) {
                                    Config.IntentUid.add(userId);
                                }
                            }
                        }
                        if (XpUtils.DEBUG_BROADCAST_INTENT)
                            XpUtils.log(TAG, "广播意图 UID:" + userId);
                    }
                }
            });

            XpUtils.log(TAG, "监听广播意图");
        } catch (Throwable throwable) {
            XpUtils.log(TAG,  " -> 无法监听广播意图, 异常:" + throwable);
        }
    }
}