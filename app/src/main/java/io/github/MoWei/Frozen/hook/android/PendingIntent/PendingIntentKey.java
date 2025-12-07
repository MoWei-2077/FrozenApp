package io.github.MoWei.Frozen.hook.android.PendingIntent;

import de.robv.android.xposed.XposedHelpers;

public class PendingIntentKey {
    final Object instance;
    final public String packageName;
    final public int userId;

    public PendingIntentKey(Object key) {
        this.instance = key;
        this.packageName = (String) XposedHelpers.getObjectField(key, "packageName");
        this.userId = XposedHelpers.getIntField(key, "userId");
    }
}