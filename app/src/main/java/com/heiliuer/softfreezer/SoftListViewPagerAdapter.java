package com.heiliuer.softfreezer;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.common.collect.Lists;

import java.util.List;

/**
 * Created by Administrator on 2016/2/26.
 */
public class SoftListViewPagerAdapter extends BaseAdapter {

    private PackageMgr packageMgr;

    private Context context;

    private LayoutInflater inflater;

    public PackageMgr getPackageMgr() {
        return packageMgr;
    }

    public SoftListViewPagerAdapter(Context context) {
        this.context = context;
        inflater = LayoutInflater.from(context);
        packageMgr = new PackageMgr(context);
        appInfoListAll = packageMgr.getInstalledPackages();
        appInfoListFiltered = Lists.newArrayList();
    }

    private List<AppInfo> appInfoListFiltered, appInfoListAll;

    public AppInfo getAppInfo(int position) {
        return appInfoListFiltered == null ? null : appInfoListFiltered.get(position);
    }

    public static final SoftFilter SOFT_FILTER_DEFAULT = new SoftFilter() {
        private SoftFilter noSystem = new SoftFilterNotSystem();

        @Override
        public boolean test(AppInfo appInfo) {
            return true;
        }
    };

    public static final SoftSort SOFT_SORT_DEFAULT = new SoftSortTime();

    public void refreshList() {
        refreshList(SOFT_FILTER_DEFAULT, SOFT_SORT_DEFAULT);
    }

    public void refreshList(final SoftFilter softFilter, final SoftSort softSort) {
        new Thread() {
            @Override
            public void run() {
                appInfoListFiltered.clear();
                for (AppInfo appInfo : appInfoListAll) {
                    if (softFilter.test(appInfo)) {
                        appInfoListFiltered.add(appInfo);
                    }
                }
                if (onSearchOrSortListener != null) {
                    onSearchOrSortListener.onSearch(softFilter);
                }
            }
        }.start();
    }

    private OnSearchOrSortListener onSearchOrSortListener;

    public void setOnSearchOrSortListener(OnSearchOrSortListener onSearchOrSortListener) {
        this.onSearchOrSortListener = onSearchOrSortListener;
    }

    public static interface OnSearchOrSortListener {
        public void onSearch(SoftFilter softFilter);

        public void onSort();
    }

    @Override
    public int getCount() {
        return appInfoListFiltered == null ? 0 : appInfoListFiltered.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.list_item, null);
        }
        convertView.setTag(position);
        ImageView imgIcon = (ImageView) convertView.findViewById(R.id.imgIcon);
        TextView textAppName = (TextView) convertView.findViewById(R.id.textAppName);
        TextView textPackage = (TextView) convertView.findViewById(R.id.textPackage);
        AppInfo appInfo = appInfoListFiltered.get(position);
        imgIcon.setImageDrawable(appInfo.appIcon);
        textAppName.setText(appInfo.appName);
        textPackage.setText(appInfo.packageName);
        textAppName.setTextColor(appInfo.enabled ? 0xff333333 : 0xffff3333);
        return convertView;
    }
}

