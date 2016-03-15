package com.heiliuer.softfreezer;

import android.app.ProgressDialog;
import android.content.Context;

/**
 * Created by Administrator on 2016/3/8 0008.
 */
public class LoadingDialogHandler extends SinglelonDialogBuilder.DiallogHandler<String> {

    private Context context;

    public LoadingDialogHandler(Context context) {
        this.context = context;
    }

    private ProgressDialog loadingDialog;


    @Override
    protected void showDialog(String text) {
        if (loadingDialog == null) {
            loadingDialog = ProgressDialog.show(context, null, text, true, false);
        } else {
            loadingDialog.setMessage(text);
            loadingDialog.show();
        }
    }


    @Override
    protected void hideDialog() {
        if (loadingDialog != null) {
            loadingDialog.hide();
        }
    }
}
