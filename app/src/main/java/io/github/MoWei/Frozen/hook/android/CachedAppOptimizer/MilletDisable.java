package io.github.MoWei.Frozen.hook.android.CachedAppOptimizer;

import android.os.Build;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.hook.XpUtils;
public class MilletDisable extends MethodHook  {
    final private static String TAG = "[初始化]";
    Config config;
    public MilletDisable(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }
    @Override
    public String getTargetClass() {
        return "com.android.server.am.CachedAppOptimizer";
    }

    @Override
    public String getTargetMethod() {
        return "updateUseFreezer";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[0];
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                XposedHelpers.setBooleanField(param.thisObject,  "mPowerMilletEnable", false);
            }
        };
    }

    @Override
    public String successLog() {
        return TAG + " 屏蔽Millet调用成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.R;
    }
}
