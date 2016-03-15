package com.heiliuer.softfreezer;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.google.common.base.Strings;

public class MainActivity extends AppCompatActivity {

    private Handler handler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        singlelonToast = new SinglelonToast(this);
        loadingDialogHandler = SinglelonDialogBuilder.buildLoadingDialog(this);
        initHandler();
        initSoftList();

        //获取root权限
        new Thread() {
            @Override
            public void run() {
                super.run();
                RootUtils.upgradeRootPermission(getPackageCodePath());
            }
        }.start();

        loadingDialogHandler.show("init", "正在加载应用");
    }

    private SinglelonDialogBuilder.DiallogHandler loadingDialogHandler;

    private SinglelonToast singlelonToast;

    public static final int MSG_SHOW_LOADING = 0x001;
    public static final int MSG_HIDE_LOADING = 0x002;
    private static final int MSG_TOAST = 0x003;

    private void initHandler() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case MSG_HIDE_LOADING:
                        loadingDialogHandler.hide(null);
                        break;
                    case MSG_SHOW_LOADING:
                        loadingDialogHandler.show(null, null);
                        break;
                    case MSG_TOAST:
                        singlelonToast.show((String) msg.obj);
                        break;
                }
            }
        };
    }

    private void postSingleLonToast(String text) {
        handler.sendMessage(handler.obtainMessage(MSG_TOAST, text));
    }


    public void initSoftList() {
        ListView listViewSofts = (ListView) findViewById(R.id.listViewSofts);
        final SoftListViewPagerAdapter adapter = new SoftListViewPagerAdapter(getApplicationContext());
        listViewSofts.setAdapter(adapter);
        adapter.refreshList();
        initSearchHandler(adapter);
        initSortHandler();
//        listViewSofts.setOnLongClickListener(new View.OnLongClickListener() {
//            @Override
//            public boolean onLongClick(View v) {
//                Integer position = (Integer) v.getTag();
//                if (position == null) {
//                    return false;
//                }
//                AppInfo appInfo = adapter.getAppInfo(position);
//                if (adapter.getPackageMgr().switchDisabledApp(appInfo)) {
//                    Toast.makeText(MainActivity.this, (appInfo.enabled ? "已启用 " : "已禁用 ") + appInfo.appName, Toast.LENGTH_LONG).show();
//                    adapter.notifyDataSetChanged();
//                } else {
//                    Toast.makeText(MainActivity.this, (appInfo.enabled ? "!禁用 " : "!启用 ") + appInfo.appName + " 失败", Toast.LENGTH_LONG).show();
//                }
//                return true;
//            }
//        });
        listViewSofts.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                final AppInfo appInfo = adapter.getAppInfo(position);
                loadingDialogHandler.show("switch", "正在处理");
                new Thread() {
                    @Override
                    public void run() {
                        if (adapter.getPackageMgr().switchDisabledApp(appInfo)) {
                            postSingleLonToast((appInfo.enabled ? "已启用 " : "已禁用 ") + appInfo.appName);
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    adapter.notifyDataSetChanged();
                                }
                            });
                        } else {
                            postSingleLonToast((appInfo.enabled ? "!禁用 " : "!启用 ") + appInfo.appName + " 失败");
                        }
                        handler.sendEmptyMessage(MSG_HIDE_LOADING);
                    }
                }.start();
            }
        });

        adapter.setOnSearchOrSortListener(new SoftListViewPagerAdapter.OnSearchOrSortListener() {
            @Override
            public void onSearch(SoftFilter softFilter) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyDataSetChanged();
                        loadingDialogHandler.hide("init");
                        loadingDialogHandler.hide("search");
                    }
                });
            }

            @Override
            public void onSort() {
            }
        });
    }


    public void initSortHandler() {
        Spinner spinnerSort = (Spinner) findViewById(R.id.spinnerSort);
        spinnerSort.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
    }

    public void initSearchHandler(final SoftListViewPagerAdapter adapter) {
        final EditText editText = (EditText) findViewById(R.id.editTextSearch);
        final SoftFilterKey softFilter = new SoftFilterKey();
        editText.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH) {
                    String key = editText.getText().toString();
                    Log.v("myLog", "action " + key);
                    loadingDialogHandler.show("search", "正在搜索");
                    if (Strings.isNullOrEmpty(key)) {
                        adapter.refreshList();
                    } else {
                        adapter.refreshList(softFilter.setKey(key), SoftListViewPagerAdapter.SOFT_SORT_DEFAULT);
                    }
                    hideIme(editText);
                    return true;
                }
                return false;
            }
        });
    }

    private void hideIme(EditText editText) {
        ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    @Override
    public void onBackPressed() {
        startActivity(new Intent(Intent.ACTION_MAIN).setFlags(
                Intent.FLAG_ACTIVITY_NEW_TASK)
                .addCategory(Intent.CATEGORY_HOME));
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
