package io.github.MoWei.Frozen.hook.android.CachedAppOptimizer;

import android.os.Build;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.XpUtils;

public class DisableUseFreezerHook extends MethodHook {
    final private static String TAG = "[初始化]";
    Config config;
    public DisableUseFreezerHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }
    @Override
    public String getTargetClass() {
        return "com.android.server.am.CachedAppOptimizer";
    }

    @Override
    public String getTargetMethod() {
        return "useFreezer";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[0];
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return constantResult(false);
    }

    @Override
    public String successLog() {
        return TAG + " 屏蔽安卓开启暂停执行已缓存的应用成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.Q;
    }
}
