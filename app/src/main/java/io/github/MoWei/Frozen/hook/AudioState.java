package io.github.MoWei.Frozen.hook;

import java.util.Set;
import java.util.HashSet;

public class AudioState {
    private final int uid;
    private final Set<Integer> interfaceIds = new HashSet<>();

    public AudioState(int Uid) {
        this.uid = Uid;
    }
    public Set<Integer> GetInterfaceIds() {
        return interfaceIds; // 外部可以 add/remove
    }
}
