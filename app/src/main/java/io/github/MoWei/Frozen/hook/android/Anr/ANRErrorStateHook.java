package io.github.MoWei.Frozen.hook.android.Anr;

import android.content.pm.ApplicationInfo;
import android.os.Build;

import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.Enum;
import io.github.MoWei.Frozen.hook.XpUtils;

/**
 * ANR相关Hook.
 */
public class ANRErrorStateHook extends MethodHook {
    private final Config config;

    public ANRErrorStateHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }

    @Override
    public String getTargetClass() {
        return "com.android.server.am.ProcessErrorStateRecord";
    }

    @Override
    public String getTargetMethod() {
        return "appNotResponding";
    }


    @Override
    public Object[] getTargetParam() {
        Object[] parameterTypes = findParameterTypes();
        if (Objects.nonNull(parameterTypes))
            return parameterTypes;

        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.TIRAMISU)
            return new Object[]{
                    String.class, ApplicationInfo.class, String.class,
                    "com.android.server.wm.WindowProcessController", boolean.class, "com.android.internal.os.TimeoutRecord",
                    ExecutorService.class, boolean.class, boolean.class, Future.class
            };

        return new Object[]{
                String.class, ApplicationInfo.class, String.class,
                "com.android.server.wm.WindowProcessController", boolean.class, String.class, boolean.class
        };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void beforeMethod(MethodHookParam param) {
                // ANR进程
                Object app = XposedHelpers.getObjectField(param.thisObject, "mApp");
                if (app == null)
                    return;
                final int uid = config.getProcessRecordUid(app);// processRecord
                if (!config.managedApp.contains(uid))
                    return;
                param.setResult(null);
                if (XpUtils.DEBUG_ANR)
                    XpUtils.log("Frozen[AnrHook]:", "跳过 ANR:" + XpUtils.getString(app, Enum.Field.processName));
            }
        };
    }

    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.S;
    }

    @Override
    public String successLog() {
        return "拦截应用状态无响应";
    }
}