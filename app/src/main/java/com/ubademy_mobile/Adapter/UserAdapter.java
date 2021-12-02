package com.ubademy_mobile.Adapter;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ubademy_mobile.R;
import com.ubademy_mobile.activities.MessagingActivity;
import com.ubademy_mobile.services.data.Mensaje;
import com.ubademy_mobile.services.data.Usuario;

import java.util.List;

public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder> {

    private Context mContext;
    private List<Usuario> mUsuarios;
    private String userid;
    private Boolean isChat;
    private String userFullName;

    String ultimoMsj;

    public UserAdapter(Context mContext, List<Usuario> mUsuarios, Boolean isChat, String userid, String userFullName){
        this.mUsuarios = mUsuarios;
        this.mContext = mContext;
        this.userid = userid;
        this.isChat = isChat;
        this.userFullName = userFullName;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(mContext).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Usuario user = mUsuarios.get(position);
        holder.username.setText(user.getNombre() + " " + user.getApellido());
        holder.profile_image.setImageResource(R.mipmap.ic_launcher);

        if (isChat) {
            lastMessage(user.getUsername(), holder.last_msg);
        }
        else {
            holder.last_msg.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view){
                Intent intent = new Intent(mContext, MessagingActivity.class);
                intent.putExtra("userid", user.getUsername());
                intent.putExtra("nombre_completo", user.getNombre() + " " + user.getApellido());
                intent.putExtra("nombre_completo_usuario", userFullName);
                mContext.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mUsuarios.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView username;
        public ImageView profile_image;
        private TextView last_msg;

        public ViewHolder(View itemView) {
            super(itemView);
            username = itemView.findViewById(R.id.username);
            profile_image = itemView.findViewById(R.id.profile_image);
            last_msg = itemView.findViewById(R.id.last_msj);
        }
    }

    private void lastMessage(String otherId, TextView last_msj) {
        ultimoMsj = "";
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Mensajes");

        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    Mensaje msj = snap.getValue(Mensaje.class);
                    if ((msj.getReceptor().equals(userid) && msj.getEmisor().equals(otherId)) ||
                    msj.getReceptor().equals(otherId) && msj.getEmisor().equals(userid)) {
                        ultimoMsj = msj.getMensaje();
                    }
                }
                last_msj.setText(ultimoMsj);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

}
