package com.ubademy_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ubademy_mobile.R;
import com.ubademy_mobile.activities.MessagingActivity;
import com.ubademy_mobile.services.data.Mensaje;

import java.util.List;

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.ViewHolder> {

    public static final int MSG_IZQUIERDA = 0;
    public static final int MSG_DERECHA = 1;

    private Context mContext;
    private List<Mensaje> mMensajes;
    private String username;

    public MessageAdapter(Context mContext, List<Mensaje> mMensajes, String username){
        this.mMensajes = mMensajes;
        this.mContext = mContext;
        this.username = username;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == MSG_DERECHA) {
            View view = LayoutInflater.from(mContext).inflate(R.layout.mensaje_derecha, parent, false);
            return new MessageAdapter.ViewHolder(view);
        }
        View view = LayoutInflater.from(mContext).inflate(R.layout.mensaje_izquierda, parent, false);
        return new MessageAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {

        Mensaje msj = mMensajes.get(position);
        holder.mensaje.setText(msj.getMensaje());
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);

    }

    @Override
    public int getItemCount() {
        return mMensajes.size();
    }

    @Override
    public int getItemViewType(int position){
        Log.d("user", username);
        return mMensajes.get(position).getEmisor().equals(username) ? MSG_DERECHA : MSG_IZQUIERDA;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView mensaje;
        public ImageView profile_image;

        public ViewHolder(View itemView) {
            super(itemView);
            mensaje = itemView.findViewById(R.id.mensaje);
            profile_image = itemView.findViewById(R.id.profile_image);
        }
    }

}
