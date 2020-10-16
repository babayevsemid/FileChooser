package com.semid.filechooser;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

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

        void bind(FileModel model) {

            switch (model.getFileType()) {
                case PHOTO:
                    binding.typeBtn.setImageResource(R.drawable.ic_photo);
                    break;
                case VIDEO:
                    binding.typeBtn.setImageResource(R.drawable.ic_video);
                    break;
            }
        }
    }

    public interface Listener {
        void onDelete(File file);
    }
}
