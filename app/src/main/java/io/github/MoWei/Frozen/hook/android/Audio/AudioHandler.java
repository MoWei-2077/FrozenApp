package io.github.MoWei.Frozen.hook.android.Audio;

import java.util.Set;
import java.util.HashSet;
import java.util.Map;
import java.util.HashMap;

import java.util.concurrent.ConcurrentHashMap;

import io.github.MoWei.Frozen.hook.XpUtils;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.hook.AudioState;
public class AudioHandler {
    private static String TAG = "音频";
    public static final int PLAYER_STATE_RELEASED = 0;
    public static final int PLAYER_STATE_IDLE = 1;
    public static final int PLAYER_STATE_STARTED = 2;
    public static final int PLAYER_STATE_PAUSED = 3;
    public static final int PLAYER_STATE_STOPPED = 4;
    public static final Set<Integer> LISTEN_EVENT = Set.of(PLAYER_STATE_RELEASED, PLAYER_STATE_IDLE, PLAYER_STATE_STARTED, PLAYER_STATE_PAUSED, PLAYER_STATE_STOPPED);

    public static void call(int uid, int event, int interfaceId) {

        Set<Integer> set = Config.playingUid.computeIfAbsent(uid,  k -> ConcurrentHashMap.newKeySet());

        if (event == PLAYER_STATE_STARTED)
            set.add(interfaceId);
        else
            set.remove(interfaceId);

        if (set.isEmpty()) {
            Config.playingUid.remove(uid);
            if (XpUtils.DEBUG_AUDIO_UID)
                XpUtils.log(TAG,"应用: " + uid + "暂停播放");
        } else {
            if (XpUtils.DEBUG_AUDIO_UID)
                XpUtils.log(TAG,"应用: " + uid + "开始播放");
        }
    }
}