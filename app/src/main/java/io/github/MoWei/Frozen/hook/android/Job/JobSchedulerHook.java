package io.github.MoWei.Frozen.hook.android.Job;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import io.github.MoWei.Frozen.hook.Config;
import io.github.MoWei.Frozen.base.AbstractMethodHook;
import io.github.MoWei.Frozen.base.MethodHook;
import io.github.MoWei.Frozen.Threads.Handlers;

import android.os.Build;
import java.util.List;


import io.github.MoWei.Frozen.hook.XpUtils;
public class JobSchedulerHook extends MethodHook {
    Config config;
    final static String TAG = "音频";
    public JobSchedulerHook(Config config, ClassLoader classLoader) {
        super(classLoader);
        this.config = config;
    }
    @Override
    public String getTargetClass() {
        return "com.android.server.job.JobSchedulerService";
    }

    @Override
    public String getTargetMethod() {
        return "scheduleAsPackage";
    }

    @Override
    public Object[] getTargetParam() {
        return new Object[] { "android.app.job.JobInfo", "android.app.job.JobWorkItem", int.class, String.class, int.class, String.class, String.class };
    }

    @Override
    public XC_MethodHook getTargetHook() {
        return new AbstractMethodHook() {
            @Override
            protected void afterMethod(MethodHookParam param) {
                int uid = (int) param.args[2];

                if (!config.managedApp.contains(uid) || config.foregroundUid.contains(uid) || config.pendingUid.contains(uid))
                    return;

                param.setResult(0);

                XposedHelpers.callMethod(param.thisObject, "cancelJobsForUid", uid, "uid frozen");
                if (XpUtils.DEBUG_JOB)
                    XpUtils.log(TAG, uid + "已清理应用执行Job");
            }
        };
    }
    @Override
    public String successLog() {
        return "屏蔽限制冻结应用执行Job成功";
    }
    @Override
    public int getMinVersion() {
        return Build.VERSION_CODES.S;
    }
}
