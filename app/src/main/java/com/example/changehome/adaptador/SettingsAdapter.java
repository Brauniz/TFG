package com.example.changehome.adaptador;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.example.changehome.R;
import com.example.changehome.modelo.entidades.Setting;
import java.util.ArrayList;
import java.util.List;

public class SettingsAdapter extends RecyclerView.Adapter<SettingsAdapter.SettingViewHolder> {

    private Context context;
    private List<Setting> settingList;
    private OnSettingClickListener listener;

    // Interface para manejar clicks en los items
    public interface OnSettingClickListener {
        void onSettingClick(Setting setting, int position);
        void onSettingLongClick(Setting setting, int position);
    }

    public SettingsAdapter(Context context, List<Setting> settingList) {
        this.context = context;
        this.settingList = settingList != null ? settingList : new ArrayList<>();
    }

    public void setOnSettingClickListener(OnSettingClickListener listener) {
        this.listener = listener;
    }

    @NonNull
    @Override
    public SettingViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(context).inflate(R.layout.setting_card, parent, false);
        return new SettingViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull SettingViewHolder holder, int position) {
        Setting setting = settingList.get(position);
        holder.bind(setting);
    }

    @Override
    public int getItemCount() {
        return settingList.size();
    }

    // Métodos para gestionar la lista
    public void updateList(List<Setting> newList) {
        this.settingList.clear();
        if (newList != null) {
            this.settingList.addAll(newList);
        }
        notifyDataSetChanged();
    }

    public void addSetting(Setting setting) {
        settingList.add(setting);
        notifyItemInserted(settingList.size() - 1);
    }

    public void removeSetting(int position) {
        if (position >= 0 && position < settingList.size()) {
            settingList.remove(position);
            notifyItemRemoved(position);
        }
    }

    public Setting getSetting(int position) {
        if (position >= 0 && position < settingList.size()) {
            return settingList.get(position);
        }
        return null;
    }

    public class SettingViewHolder extends RecyclerView.ViewHolder {
        private ImageView icon;
        private TextView txtName;

        public SettingViewHolder(@NonNull View itemView) {
            super(itemView);
            icon = itemView.findViewById(R.id.icon);
            txtName = itemView.findViewById(R.id.txtName);
        }

        public void bind(Setting setting) {
            // Mostrar nombre del setting
            if (setting.getName() != null && !setting.getName().isEmpty()) {
                txtName.setText(setting.getName());
            } else {
                txtName.setText("Sin nombre");
            }

            // Cargar imagen con Glide
            if (setting.getImageUrl() != null && !setting.getImageUrl().isEmpty()) {
                Glide.with(context)
                        .load(setting.getImageUrl())
                        .apply(new RequestOptions()
                                .placeholder(R.drawable.unselected_settings)
                                .error(R.drawable.error_icon))
                        .into(icon);
            } else {
                // Usar icono por defecto si no hay imagen
                icon.setImageResource(R.drawable.error_icon);
            }

            // Configurar click listeners
            itemView.setOnClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onSettingClick(setting, getAdapterPosition());
                }
            });

            itemView.setOnLongClickListener(v -> {
                if (listener != null && getAdapterPosition() != RecyclerView.NO_POSITION) {
                    listener.onSettingLongClick(setting, getAdapterPosition());
                    return true;
                }
                return false;
            });
        }
    }
}