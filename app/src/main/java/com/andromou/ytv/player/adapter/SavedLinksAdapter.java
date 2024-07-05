package com.andromou.ytv.player.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.PopupMenu;
import androidx.recyclerview.widget.RecyclerView;

import com.andromou.ytv.player.R;
import com.andromou.ytv.player.data.SavedLink;
import com.andromou.ytv.player.database.SavedLinksSQLite;
import com.andromou.ytv.player.databinding.SavedLinkItemBinding;
import com.andromou.ytv.player.ui.Media3PlayerActivity;
import com.andromou.ytv.player.ui.SavedLinksActivity;
import com.andromou.ytv.player.utils.Util;

import java.util.List;

import static com.andromou.ytv.player.utils.Util.ShowToast;

public class SavedLinksAdapter extends RecyclerView.Adapter<SavedLinksAdapter.SavedLinkItem> {

    private final List<SavedLink> savedLinks;
    private final Context context;
    private final SavedLinksSQLite savedLinksSQLite;

    public SavedLinksAdapter(Context context, List<SavedLink> savedLinks, SavedLinksSQLite savedLinksSQLite) {
        this.context = context;
        this.savedLinks = savedLinks;
        this.savedLinksSQLite = savedLinksSQLite;
    }

    @NonNull
    @Override
    public SavedLinkItem onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        SavedLinkItemBinding itemBinding = SavedLinkItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new SavedLinkItem(itemBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull SavedLinkItem holder, int position) {
        holder.bind(savedLinks.get(position));
    }

    @Override
    public int getItemCount() {
        return savedLinks.size();
    }

    class SavedLinkItem extends RecyclerView.ViewHolder {

        private final SavedLinkItemBinding itemBinding;

        SavedLinkItem(SavedLinkItemBinding binding) {
            super(binding.getRoot());
            itemBinding = binding;

            itemBinding.getRoot().setOnClickListener(v -> {
                String url = savedLinks.get(getBindingAdapterPosition()).link;
                String newUrl = savedLinks.get(getBindingAdapterPosition()).newlink;

                if (!Util.isNetworkConnected(context)) {
                    ShowToast(context, "Please Connect To Internet.!");
                } else {
                    if (newUrl != null) {
                        Intent intent = new Intent(context, Media3PlayerActivity.class);
                        intent.putExtra("url", newUrl);
                        context.startActivity(intent);
                    } else {
                        streamVideo(url);
                    }
                }
            });

            itemBinding.rowSavedMenu.setOnClickListener(v -> showPopupMenu(itemBinding.getRoot()));
        }

        void bind(SavedLink savedLink) {
            itemBinding.textViewTitle.setText(savedLink.title);
            itemBinding.textViewLink.setText(savedLink.link);
        }

        @SuppressLint("NonConstantResourceId")
        private void showPopupMenu(View view) {
            int position = getBindingAdapterPosition();
            SavedLink selectedLink = savedLinks.get(position);

            PopupMenu popupMenu = new PopupMenu(context, view);
            popupMenu.getMenuInflater().inflate(R.menu.saved_menu, popupMenu.getMenu());

            popupMenu.setOnMenuItemClickListener(item -> {
                switch (item.getItemId()) {
                    case R.id.delete_link:
                        deleteLink(selectedLink);
                        return true;
                    case R.id.open_link:
                        openLink(selectedLink);
                        return true;
                    case R.id.copy_link:
                        Util.copyLink(selectedLink, context);
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
            notifyItemRemoved(getBindingAdapterPosition());
            ((SavedLinksActivity) context).isHistoryEmpty();
        }

        private void openLink(SavedLink link) {
            String url = link.link;
            if (url.contains("yo-ut")) {
                ShowToast(context, "Youtube is Not Supported");
            } else if (!Util.isNetworkConnected(context)) {
                ShowToast(context, "Please Connect To Internet.!");
            } else {
                streamVideo(url);
            }
        }

        private void streamVideo(String url) {
            ((SavedLinksActivity) context).streamVideo(url);
        }
    }
}
