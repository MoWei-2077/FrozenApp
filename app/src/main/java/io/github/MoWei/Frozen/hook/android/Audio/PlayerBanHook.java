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
public class PlayerBanHook extends MethodHook  {
    Config config;
    final static String TAG = "[音频]";
    public PlayerBanHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }
    @Override
    public String getTargetClass() {
        return "com.android.server.audio.PlaybackActivityMonitor";
    }

    @Override
    public String getTargetMethod() {
        return "checkBanPlayer";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[] { AudioPlaybackConfiguration.class, int.class };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                if ((boolean) param.getResult()) {
                    Object configuration = param.args[0];
                    if (configuration == null)
                        return;

                    AudioPlaybackConfigurationReflect reflect = new AudioPlaybackConfigurationReflect((AudioPlaybackConfiguration) configuration);

                    Handlers.audio.post(() -> {
                        int uid = reflect.getClientUid();
                        if (!config.managedApp.contains(uid)) return;

                        int interfaceId = reflect.getPlayerInterfaceId();

                        AudioHandler.call(uid, AudioHandler.PLAYER_STATE_PAUSED, interfaceId);
                    });
                }
            }
        };
    }
    @Override
    public String successLog() {
        return TAG + " 监听音频播放成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.Q;
    }
}
