package com.andromou.ytv.player.dialogs;


import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;

import com.andromou.ytv.player.R;
import com.andromou.ytv.player.data.SavedLink;
import com.andromou.ytv.player.database.SavedLinksSQLite;
import com.andromou.ytv.player.ui.SavedLinksActivity;

public class LinkInputDialogFragment extends DialogFragment {

    private EditText titleET, urlET;
    private ClipboardManager clipboardManager;

    public static LinkInputDialogFragment newInstance() {
        return new LinkInputDialogFragment();
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(@Nullable Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(requireActivity());
        LayoutInflater inflater = requireActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.dialog_link_input, null);

        titleET = view.findViewById(R.id.input_dialog_edittext_title);
        urlET = view.findViewById(R.id.input_dialog_edittext_url);

        // Initialize ClipboardManager
        clipboardManager = (ClipboardManager) requireActivity().getSystemService(Context.CLIPBOARD_SERVICE);

        // Handle deep link data
        handleDeepLink();

        builder.setView(view)
                .setTitle("Enter Link Details")
                .setPositiveButton("Save", (dialog, which) -> {
                    String title = titleET.getText().toString().trim();
                    String url = urlET.getText().toString().trim();
                    handleSaveClick(title, url);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss());

        return builder.create();
    }

    private void handleDeepLink() {
        // Handle app link (deep link) if there's any data
        Intent appLinkIntent = requireActivity().getIntent();
        Uri appLinkData = appLinkIntent.getData();

        if (appLinkData != null) {
            String deepLinkUrl = appLinkData.toString();
            urlET.setText(deepLinkUrl);

        }

        // Handle clipboard content
        if (clipboardManager != null) {
            ClipData clipData = clipboardManager.getPrimaryClip();
            if (clipData != null && clipData.getItemCount() > 0) {
                ClipData.Item item = clipData.getItemAt(0);
                CharSequence clipText = item.getText();
                if (clipText != null) {
                    String clipboardText = clipText.toString();
                    if (!clipboardText.isEmpty() && Patterns.WEB_URL.matcher(clipboardText).matches()) {
                        urlET.setText(clipboardText);
                    }
                }
            }
        }
    }

    private void handleSaveClick(String title, String url) {
        if (TextUtils.isEmpty(url)) {
            Toast.makeText(requireContext(), "URL cannot be empty", Toast.LENGTH_SHORT).show();
            return;
        }

        if (!Patterns.WEB_URL.matcher(url).matches()) {
            Toast.makeText(requireContext(), "Please enter a valid URL", Toast.LENGTH_SHORT).show();
            return;
        }

        SavedLink savedLink = new SavedLink();
        savedLink.title = title;
        savedLink.link = url.trim();
        new SavedLinksSQLite(requireContext()).addLinkToTable(savedLink);
        Intent intent = new Intent(requireContext(), SavedLinksActivity.class);
        startActivity(intent);
        requireActivity().finish();
    }


}
