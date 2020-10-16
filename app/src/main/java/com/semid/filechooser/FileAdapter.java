package com.semid.filechooser;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.semid.filechooser.databinding.ItemFileBinding;
import com.semid.library.FileModel;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class FileAdapter extends RecyclerView.Adapter<FileAdapter.ItemHolder> {
    private Context context;
    private Listener listener;
    private List<FileModel> list;

    public FileAdapter(Context context, Listener listener) {
        this.context = context;
        this.listener = listener;

        list = new ArrayList<>();
    }

    @NonNull
    @Override
    public ItemHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemFileBinding binding = ItemFileBinding.inflate(LayoutInflater.from(context), parent, false);
        return new ItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ItemHolder holder, int position) {
        holder.bind(list.get(position));
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void removeItem(int position){
        list.remove(position);
        notifyItemRemoved(position);
    }

    public void updateList(List<FileModel> list) {
        this.list = list;
        notifyDataSetChanged();
    }

    class ItemHolder extends RecyclerView.ViewHolder {
        private ItemFileBinding binding;

        public ItemHolder(@NonNull ItemFileBinding binding) {
            super(binding.getRoot());

            this.binding = binding;
        }

        void bind(final FileModel model) {
            Glide.with(context)
                    .load(model.getFile())
                    .into(binding.coverImg);

            switch (model.getFileType()) {
                case PHOTO:
                    binding.typeBtn.setImageResource(R.drawable.ic_photo);
                    break;
                case VIDEO:
                    binding.typeBtn.setImageResource(R.drawable.ic_video);
                    break;
            }

            binding.getRoot().setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Log.e("onLongClick", model.getPath());

                    listener.onDelete(model);
                    return false;
                }
            });
        }
    }

    public interface Listener {
        void onDelete(FileModel fileModel);
    }
}
