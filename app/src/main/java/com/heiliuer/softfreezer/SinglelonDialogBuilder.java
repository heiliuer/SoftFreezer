package com.heiliuer.softfreezer;

import android.content.Context;

import com.google.common.base.Objects;
import com.google.common.base.Strings;

/**
 * Created by Administrator on 2016/3/8 0008.
 */
public class SinglelonDialogBuilder {

    public static final DiallogHandler buildLoadingDialog(Context context) {
        return new LoadingDialogHandler(context);

    }

    public static abstract class DiallogHandler<T> {

        private String key;

        public final void show(String key, T data) {
            showDialog(data);
            this.key = key;
        }


        public final boolean hide(String key) {
            boolean canAction = isKeyCanAction(key);
            if (canAction) {
                hideDialog();
            }
            return canAction;
        }

        private boolean isKeyCanAction(String key) {
            return Strings.isNullOrEmpty(key) || Objects.equal(this.key, key);
        }


        protected abstract void showDialog(T data);

        protected abstract void hideDialog();

    }
}
