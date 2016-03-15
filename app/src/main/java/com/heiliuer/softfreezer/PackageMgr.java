package com.heiliuer.softfreezer;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;

import com.google.common.base.Function;
import com.google.common.collect.Lists;

import java.io.DataOutputStream;
import java.util.List;

/**
 * Created by Administrator on 2016/2/26.
 */
public class PackageMgr {

    private Context context;

    private PackageManager packageManager;

    public PackageMgr(Context context) {
        this.context = context;
        packageManager = context.getPackageManager();
    }

    public List<AppInfo> getInstalledPackages() {
        return Lists.transform(packageManager.getInstalledPackages(0), new Function<PackageInfo, AppInfo>() {
            @Override
            public AppInfo apply(PackageInfo packageInfo) {
                return new AppInfo(packageInfo, packageManager);
            }
        });
    }

    public boolean switchDisabledApp(AppInfo appInfo) {
        {
            Process process = null;
            DataOutputStream os = null;
            try {
                String cmd = "pm " + (appInfo.enabled ? "disable " : "enable ") + appInfo.packageName;
                process = Runtime.getRuntime().exec("su"); //切换到root帐号
                os = new DataOutputStream(process.getOutputStream());
                os.writeBytes(cmd + "\n");
                os.writeBytes("exit\n");
                os.flush();
                process.waitFor();
            } catch (Exception e) {
                return false;
            } finally {
                try {
                    if (os != null) {
                        os.close();
                    }
                    process.destroy();
                } catch (Exception e) {
                }
            }
            appInfo.enabled = !appInfo.enabled;
            return true;
        }

    }
}
