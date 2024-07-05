package com.andromou.ytv.player.dialogs;

import static com.andromou.ytv.player.utils.Util.ShowToast;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;

import com.andromou.ytv.player.R;

public class BottomSheetDialog {

    private Context context;

    public BottomSheetDialog(Context context) {
        this.context = context;
    }

    public void showMenuDialog() {
        com.google.android.material.bottomsheet.BottomSheetDialog dialog = new com.google.android.material.bottomsheet.BottomSheetDialog(context, R.style.BottomSheetDialogTheme);
        View view = LayoutInflater.from(context).inflate(R.layout.bottom_sheet_layout,
                (LinearLayout) ((Activity)context).findViewById(R.id.bottom_sheet_container));
        view.findViewById(R.id.about_container).setOnClickListener(v -> {
            //     showAboutDialog();
            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object

            try {
                Intent shareIntent = new Intent(Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.putExtra(Intent.EXTRA_SUBJECT, "Sage GPT");
                String shareMessage="Ask ChatGPT ❤\uD83E\uDD29\n\nApp Link:  ";
                shareMessage = shareMessage + "https://play.google.com/store/apps/details?id=" + appPackageName ;
                shareIntent.putExtra(Intent.EXTRA_TEXT, shareMessage);
                context.startActivity(Intent.createChooser(shareIntent, "choose one"));
            } catch(Exception e) {
                e.printStackTrace();
            }
            dialog.dismiss();
        });
        view.findViewById(R.id.privacy_policy_container).setOnClickListener(v -> {
            //open link in browser
            Intent browserIntent = new Intent(Intent.ACTION_VIEW, Uri.parse(context.getString(R.string.privacy_policy)));
            context.startActivity(browserIntent);
            dialog.dismiss();
        });
        view.findViewById(R.id.more_apps_container).setOnClickListener(v -> {
            //open google play store
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/developer?id=" + context.getString(R.string.google_developer_id))));
            } catch (android.content.ActivityNotFoundException anfe) {
                 ShowToast(context, "Error");
            }
            dialog.dismiss();
        });
        view.findViewById(R.id.update_container).setOnClickListener(v -> {

            final String appPackageName = context.getPackageName(); // getPackageName() from Context or Activity object
            try {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + appPackageName)));
            } catch (android.content.ActivityNotFoundException anfe) {
                context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + appPackageName)));
            }

            dialog.dismiss();
        });
        view.findViewById(R.id.contact_container).setOnClickListener(v -> {
            //open email client
            Intent intent = new Intent(Intent.ACTION_SENDTO);
            intent.setData(Uri.parse("mailto:"));
            intent.putExtra(Intent.EXTRA_EMAIL, new String[]{context.getString(R.string.developer_email)});
            intent.putExtra(Intent.EXTRA_SUBJECT, "All in One Video Downloader");
            context.startActivity(Intent.createChooser(intent, "Send Email"));
            dialog.dismiss();
        });

        dialog.setContentView(view);
        dialog.show();
    }

}
