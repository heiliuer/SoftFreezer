package com.heiliuer.softfreezer;

/**
 * Created by Administrator on 2016/2/26.
 */
public class SoftFilterKey implements SoftFilter {
    private String key;

    public String getKey() {
        return key;
    }

    public SoftFilterKey setKey(String key) {
        this.key = key;
        return this;
    }

    @Override
    public boolean test(AppInfo appInfo) {
        return appInfo.isMatchKey(this.key);
    }
}
