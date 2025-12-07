package io.github.MoWei.Frozen.Threads;

import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;

import io.github.MoWei.Frozen.hook.XpUtils;

public class Handlers {
    final static String TAG = "音频意图";
    public static final Handler audio = makeHandler("Audio");

    public static Handler makeHandler(String str) {
        return makeHandler(str, false);
    }

    public static Handler makeHandler(String str, boolean async) {
        if (async)
            return Handler.createAsync(makeLooper(str));
        else
            return new Handler(makeLooper(str));
    }

    public static Looper makeLooper(String str) {
        HandlerThread handlerThread = new HandlerThread(TAG + "-" + str);
        handlerThread.setUncaughtExceptionHandler((t, e) -> XpUtils.log(TAG, "线程 " + t.getName() + " 出现异常: " + e));
        handlerThread.start();
        return handlerThread.getLooper();
    }
}