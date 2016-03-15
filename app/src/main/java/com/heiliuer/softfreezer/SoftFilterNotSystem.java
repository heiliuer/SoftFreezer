package com.heiliuer.softfreezer;

/**
 * Created by Administrator on 2016/2/26.
 */
public class SoftFilterNotSystem implements SoftFilter {

    @Override
    public boolean test(AppInfo appInfo) {
        return appInfo.isSystem();
    }
}
