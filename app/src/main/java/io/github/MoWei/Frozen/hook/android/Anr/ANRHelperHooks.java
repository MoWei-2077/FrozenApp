package io.github.MoWei.Frozen.hook.android.Anr;

import java.lang.reflect.Method;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.Enum;
import io.github.MoWei.Frozen.hook.XpUtils;

public class ANRHelperHooks {
    final private static String TAG = "[ANR]";
    public final Integer findIndex(Class<?>[] parameterTypes, String clazz) {
        for (int i = 0; i < parameterTypes.length; i++)
            if (clazz.equals(parameterTypes[i].getName()))
                return i;
        return null;
    }

    public ANRHelperHooks(Config config, ClassLoader classLoader) {
        try {
            Class<?> AnrHelperClass = XposedHelpers.findClassIfExists("com.android.server.am.AnrHelper", classLoader);

            if (AnrHelperClass == null)
                return;

            for (Method method : AnrHelperClass.getDeclaredMethods()) {
                if ((method.getName().equals("appNotResponding") || method.getName().equals("deferAppNotResponding")) && method.getReturnType().equals(void.class)) { // 只处理void为返回类型的方法体
                    Integer index  = findIndex(method.getParameterTypes(), "com.android.server.am.ProcessRecord");
                    if (index  == null) { // 没找到进程记录就找Anr记录
                        Integer MIUIRecordIndex = findIndex(method.getParameterTypes(), "com.android.server.am.AnrHelper$AnrRecord");
                        if (MIUIRecordIndex != null) {
                            XposedBridge.hookMethod(method, new AbstractMethodHook() {
                                @Override
                                protected void beforeMethod(MethodHookParam param) {
                                    Object anrRecord = param.args[MIUIRecordIndex];
                                    if (anrRecord == null)
                                        return;
                                    Object app = XposedHelpers.getObjectField(anrRecord, "mApp");
                                    if (app == null)
                                        return;
                                    final int uid = config.getProcessRecordUid(app);// processRecord
                                    if (!config.managedApp.contains(uid))
                                        return;
                                    param.setResult(null);
                                    if (XpUtils.DEBUG_ANR)
                                        XpUtils.log(TAG, "跳过 ANR:" + XpUtils.getString(app, Enum.Field.processName));
                                }
                            });
                        }
                    } else {
                        XposedBridge.hookMethod(method, new AbstractMethodHook() {
                            @Override
                            protected void beforeMethod(MethodHookParam param) {
                                Object record = param.args[index];
                                if (record == null)
                                    return;
                                final int uid = config.getProcessRecordUid(record);// processRecord
                                if (!config.managedApp.contains(uid))
                                    return;
                                param.setResult(null);
                                if (XpUtils.DEBUG_ANR)
                                    XpUtils.log(TAG, "跳过 ANR:" + XpUtils.getString(record, Enum.Field.processName));
                            }
                        });
                    }
                }
            }

            if (XpUtils.DEBUG_ANR)
                XpUtils.log(TAG + "拦截应用无响应");
        } catch (Throwable throwable) {
            XpUtils.log(TAG, throwable.getMessage());
        }
    }
}