package com.heiliuer.softfreezer;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.drawable.Drawable;

/**
 * Created by Administrator on 2016/2/26.
 */
public class AppInfo {

    public String appName = "";
    public String packageName = "";
    public String versionName = "";
    public int versionCode = 0;
    public Drawable appIcon = null;
    public boolean system;
    public String key;
    public boolean enabled;
    private PackageInfo packageInfo;

    public AppInfo(PackageInfo packageInfo, PackageManager pm) {
        this.packageInfo = packageInfo;
        packageName = packageInfo.packageName;
        ApplicationInfo applicationInfo = packageInfo.applicationInfo;
        appName = applicationInfo.loadLabel(pm).toString();
        versionName = packageInfo.versionName;
        versionCode = packageInfo.versionCode;
        appIcon = applicationInfo.loadIcon(pm);
        this.key = PinYinUtil.getFirstSpell(appName, true) + "#"
                + PinYinUtil.getPingYin(appName, true) + "#" + packageName + "#" + appName;
        this.enabled=applicationInfo.enabled;
    }

    public boolean isSystem() {
        return (packageInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0;
    }

    /**
     * @param key
     * @return
     */
    public boolean isMatchKey(String key) {
        return this.key.indexOf(key) != -1;
    }

}
