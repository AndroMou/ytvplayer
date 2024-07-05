package com.andromou.ytv.player.utils;

import static android.content.Context.CLIPBOARD_SERVICE;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.net.ConnectivityManager;
import android.os.IBinder;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import com.andromou.ytv.player.R;
import com.andromou.ytv.player.data.SavedLink;

public final class Util {

    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null;
    }

    public static void hideSoftKeyboard(Activity activity, IBinder token) {
        InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(
                Activity.INPUT_METHOD_SERVICE);
        if (inputMethodManager != null && token != null) {
            inputMethodManager.hideSoftInputFromWindow(
                    token, 0);
        }
    }

    public static void copyLink(SavedLink link, Context context) {
        ClipboardManager clipboardManager = (ClipboardManager) context.getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied URL", link.link);
        clipboardManager.setPrimaryClip(clipData);
        ShowToast(context, "URL Copied");
    }


    public static void ShowToast(Context context, String message) {
        // Inflate custom layout for toast
        LayoutInflater inflater = LayoutInflater.from(context);
        View layout = inflater.inflate(R.layout.custom_toast_layout, null);

        // Set message to the custom layout
        TextView text = layout.findViewById(R.id.text);
        text.setText(message);

        // Create and show the toast
        Toast toast = new Toast(context.getApplicationContext());
        toast.setGravity(Gravity.CENTER, 0, 0);
        toast.setDuration(Toast.LENGTH_SHORT);
        toast.setView(layout);
        toast.show();
    }

}
