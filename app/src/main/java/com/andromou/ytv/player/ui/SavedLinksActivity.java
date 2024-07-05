package com.andromou.ytv.player.ui;

import static com.andromou.ytv.player.utils.Util.ShowToast;

import android.annotation.SuppressLint;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.andromou.ytv.player.R;
import com.andromou.ytv.player.adapter.SavedLinksAdapter;
import com.andromou.ytv.player.data.SavedLink;
import com.andromou.ytv.player.database.SavedLinksSQLite;
import com.andromou.ytv.player.databinding.ActivitySavedLinksBinding;
import com.andromou.ytv.player.dialogs.BottomSheetDialog;
import com.andromou.ytv.player.dialogs.LinkInputDialogFragment;
import com.andromou.ytv.player.tasks.YoutubeDlTask;
import com.andromou.ytv.player.utils.Util;

import java.util.List;



public class SavedLinksActivity extends AppCompatActivity {

    private ActivitySavedLinksBinding binding;
    private static List<SavedLink> savedLinks;
    private static SavedLinksAdapter savedLinksAdapter;
    private static SavedLinksSQLite savedLinksSQLite;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySavedLinksBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setupListeners();
        loadData(this);
        setupRecyclerView();
        isHistoryEmpty();

    }


    @SuppressLint("NotifyDataSetChanged")
    private void setupRecyclerView() {
        binding.rvsavedLinkList.setLayoutManager(new LinearLayoutManager(this));
        savedLinksAdapter = new SavedLinksAdapter(this,savedLinks, savedLinksSQLite);
        binding.rvsavedLinkList.setAdapter(savedLinksAdapter);
        savedLinksAdapter.notifyDataSetChanged();
    }

    private void showLinkInputDialog() {
        LinkInputDialogFragment dialogFragment = LinkInputDialogFragment.newInstance();
        dialogFragment.show(getSupportFragmentManager(), "LinkInputDialogFragment");
    }
    private void setupListeners() {
        binding.fab.setOnClickListener(v -> showLinkInputDialog());

    }

    @SuppressLint("NotifyDataSetChanged")
    public void loadData(Context context) {
        savedLinksSQLite = new SavedLinksSQLite(context);
        savedLinks = savedLinksSQLite.getAllSavedLinks();

    }


    public void isHistoryEmpty() {
        boolean isEmpty = savedLinks.isEmpty();
        binding.llNosavedLink.setVisibility(isEmpty ? View.VISIBLE : View.GONE);
        binding.llShowsavedLink.setVisibility(isEmpty ? View.GONE : View.VISIBLE);
    }


    @SuppressLint("CheckResult")
    public void streamVideo(String url) {
        binding.progressBar.setVisibility(View.VISIBLE);
        YoutubeDlTask task = new YoutubeDlTask();
        task.execute(url).subscribe(result -> {
            if (result.getUrl() != null) {
                SavedLink savedLink = new SavedLink();
                savedLink.link = url;
                savedLink.title = result.getTitle();
                savedLink.newlink = result.getUrl();
                savedLinksSQLite.updateLink(savedLink);
                Intent intent = new Intent(getApplicationContext(), Media3PlayerActivity.class);
                intent.putExtra("url", result.getUrl());
                startActivity(intent);
            } else {
                runOnUiThread(() -> ShowToast(getApplicationContext(), "Retrieve available formats"));
            }
            runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));
        }, error -> {
            error.printStackTrace();
            runOnUiThread(() -> ShowToast(getApplicationContext(), "Retrieve available formats"));
            runOnUiThread(() -> binding.progressBar.setVisibility(View.GONE));
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.app_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_menu) {
            new BottomSheetDialog(this).showMenuDialog();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        super.onStop();
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    abstract static class SimpleTextWatcher implements TextWatcher {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void afterTextChanged(Editable s) {
        }
    }

    @SuppressLint("NonConstantResourceId")
    private void showPopupMenu(View view) {
        int position = binding.rvsavedLinkList.getChildAdapterPosition(view);
        SavedLink selectedLink = savedLinks.get(position);

        PopupMenu popupMenu = new PopupMenu(this, view);
        MenuInflater inflater = popupMenu.getMenuInflater();
        inflater.inflate(R.menu.saved_menu, popupMenu.getMenu());

        popupMenu.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()) {
                case R.id.delete_link:
                    deleteLink(selectedLink);
                    return true;
                case R.id.open_link:
                    openLink(selectedLink);
                    return true;
                case R.id.copy_link:
                    copyLink(selectedLink);
                    return true;
                default:
                    return false;
            }
        });

        popupMenu.show();
    }

    private void deleteLink(SavedLink link) {
        savedLinksSQLite.deleteLink(link.link);
        savedLinks.remove(link);
        savedLinksAdapter.notifyItemRemoved(savedLinks.indexOf(link));
        isHistoryEmpty();
    }

    private void openLink(SavedLink link) {
        String url = link.link;
        if (url.contains("yo-ut")) {
            ShowToast(SavedLinksActivity.this, "Youtube is Not Supported");
        } else if (!Util.isNetworkConnected(SavedLinksActivity.this)) {
            ShowToast(SavedLinksActivity.this, "Please Connect To Internet.!");
        } else {
            streamVideo(url);
        }
    }

    private void copyLink(SavedLink link) {
        ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("Copied URL", link.link);
        clipboardManager.setPrimaryClip(clipData);
        ShowToast(SavedLinksActivity.this, "URL Copied");
    }
}

