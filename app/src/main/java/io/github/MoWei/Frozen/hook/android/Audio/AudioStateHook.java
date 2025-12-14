package io.github.MoWei.Frozen.hook.android.Audio;

import android.media.AudioPlaybackConfiguration;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.Threads.Handlers;
import android.os.Build;

import io.github.MoWei.Frozen.hook.XpUtils;

public class AudioStateHook extends MethodHook {
    Config config;
    final static String TAG = "[音频]";

    public AudioStateHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }

    @Override
    public String getTargetClass() {
        return AudioPlaybackConfiguration.class.getTypeName();
    }

    @Override
    public String getTargetMethod() {
        return "handleStateEvent";
    }

    @Override
    public Object[] getTargetParam() {
        Class<?> clazz = XposedHelpers.findClassIfExists(getTargetClass(), classLoader);
        return(XpUtils.tryLongestParams(clazz, getTargetMethod(), int.class).getParameterTypes());
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                if (!((boolean) param.getResult())) return;

                int event = (int) param.args[0];
                if (AudioHandler.LISTEN_EVENT.contains(event)) {
                    AudioPlaybackConfigurationReflect reflect = new AudioPlaybackConfigurationReflect((AudioPlaybackConfiguration) param.thisObject);

                    Handlers.audio.post(() -> {
                        int uid = reflect.getClientUid();
                        if (!config.managedApp.contains(uid)) return;

                        int interfaceId = reflect.getPlayerInterfaceId();


                        AudioHandler.call(uid, event, interfaceId);
                    });
                }
            }
        };
    }
    @Override
    public String successLog() {
        return TAG + " 监听播放状态成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.Q;
    }

}
