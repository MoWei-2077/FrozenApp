package io.github.MoWei.Frozen.base;

import java.lang.reflect.InvocationTargetException;

import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import io.github.MoWei.Frozen.hook.XpUtils;

public class AbstractReplaceHook extends XC_MethodReplacement {
    protected Object replaceMethod(MethodHookParam param) throws Throwable {
        return null;
    }

    @Override
    protected Object replaceHookedMethod(MethodHookParam param) throws Throwable {
        try {
            return replaceMethod(param);
        } catch (Throwable throwable) {
            XpUtils.log("Freezeit[Hook]:", throwable.getMessage());
        }
        try {
            return XposedBridge.invokeOriginalMethod(param.method, param.thisObject, param.args);
        } catch (InvocationTargetException ex) {
            throw ex.getTargetException();
        }
    }
}
