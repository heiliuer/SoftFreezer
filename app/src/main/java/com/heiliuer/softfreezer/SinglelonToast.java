package com.heiliuer.softfreezer;

import android.content.Context;
import android.widget.Toast;

/**
 * Created by Administrator on 2016/3/9 0009.
 */
public class SinglelonToast {
    private Context context;

    public SinglelonToast(Context context) {
        this.context = context;
    }

    private Toast toast;

    public void show(String text) {
        if (toast == null) {
            Toast.makeText(context, text, Toast.LENGTH_SHORT).show();
        }else{
            toast.setText(text);
            toast.show();
        }
    }
}
