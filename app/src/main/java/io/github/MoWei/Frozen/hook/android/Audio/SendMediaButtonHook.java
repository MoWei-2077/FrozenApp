package io.github.MoWei.Frozen.hook.android.Audio;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.XpUtils;
public class SendMediaButtonHook {
    Config config;
    final static private String TAG = "[媒体]";
    public SendMediaButtonHook(Config config, ClassLoader classLoader) {
        this.config = config;

        Class<?> targetClass = XposedHelpers.findClassIfExists("com.android.server.media.MediaSessionRecord$SessionCb", classLoader);
        if (targetClass == null)
            return;

        String fieldName = null;

        for (Field field : targetClass.getDeclaredFields()) {
            if (field.getType().getName().equals("com.android.server.media.MediaSessionRecord")) {
                fieldName = field.getName();
                break;
            }
        }

        if (fieldName == null) {
            XpUtils.log(TAG, "无法监听媒体按键!");
            return;
        }

        List<Method> methods = new ArrayList<>();
        for (Method method : targetClass.getDeclaredMethods()) {
            String methodName = method.getName();
            if (methodName.equals("sendMediaButton") || methodName.equals("play") || methodName.equals("playFromMediaId") || methodName.equals("playFromSearch") || methodName.equals("playFromUri") || methodName.equals("next") || methodName.equals("previous") || methodName.equals("seekTo"))
                methods.add(method);
        }

        for (Method method : methods) {
            try {
                String finalFieldName = fieldName;
                XposedBridge.hookMethod(method, new AbstractMethodHook() {
                    @Override
                    protected void beforeMethod(MethodHookParam param) {
                        Object record = XposedHelpers.getObjectField(param.thisObject, finalFieldName);
                        if (record == null)
                            return;
                        int uid = XposedHelpers.getIntField(record, "mOwnerUid");
                        if (XpUtils.DEBUG_AUDIO_INTENT)
                            XpUtils.log(TAG, "媒体意图 UID:" + uid);
                        if (config.managedApp.contains(uid)) {
                            synchronized (Config.IntentUid) {
                                if (!Config.IntentUid.contains(uid)) {
                                    Config.IntentUid.add(uid);
                                }
                            }
                        }
                    }
                });

            } catch (Throwable throwable) {
                XpUtils.log(TAG,method.getName() + " -> Hook失败!");
            }
        }

        XpUtils.log(TAG + " 监听媒体播放事件成功");
    }
}
