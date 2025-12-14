package io.github.MoWei.Frozen.hook.android.Audio;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.Threads.Handlers;

import android.media.AudioPlaybackConfiguration;
import android.os.Build;
import java.util.List;


import io.github.MoWei.Frozen.hook.XpUtils;
public class AudioFocusHook extends MethodHook  {
    Config config;
    final static String TAG = "[音频]";
    public AudioFocusHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }
    @Override
    public String getTargetClass() {
        return "com.android.server.audio.MediaFocusControl";
    }

    @Override
    public String getTargetMethod() {
        return "notifyExtPolicyFocusGrant_syncAf";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[] { "android.media.AudioFocusInfo", int.class };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                Object afi = param.args[0];
                if (afi == null) return;

                // https://android.googlesource.com/platform/frameworks/base/+/refs/heads/master/media/java/android/media/AudioFocusInfo.java
                int uid = XposedHelpers.getIntField(afi, "mClientUid");
                if (!config.managedApp.contains(uid)) return;
                config.AudioFocusUid.clear();

                if (XpUtils.DEBUG_AUDIO_UID)
                    XpUtils.log(TAG, uid + "已获取音频焦点");
                config.AudioFocusUid.add(uid);
            }
        };
    }
    @Override
    public String successLog() {
        return TAG + " 监听焦点音频成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.Q;
    }
}
