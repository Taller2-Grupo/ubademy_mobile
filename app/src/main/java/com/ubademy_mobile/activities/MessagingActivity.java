package com.ubademy_mobile.activities;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ubademy_mobile.Adapter.MessageAdapter;
import com.ubademy_mobile.R;
import com.ubademy_mobile.services.data.Mensaje;
import com.ubademy_mobile.view_models.tools.NotificadorKt;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class MessagingActivity extends AppCompatActivity {

    CircleImageView profile_image;
    TextView username;
    ImageButton btn_send;
    EditText text_send;
    MessageAdapter adapter;
    List<Mensaje> mensajes;

    RecyclerView rView;

    DatabaseReference reference;

    Intent intent;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messaging);

        Toolbar toolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        rView = findViewById(R.id.mensajes);
        rView.setHasFixedSize(true);

        LinearLayoutManager manager = new LinearLayoutManager(getApplicationContext());
        manager.setStackFromEnd(true);

        rView.setLayoutManager(manager);

        profile_image = findViewById(R.id.profile_image);
        username = findViewById(R.id.username);
        btn_send = findViewById(R.id.btn_send);
        text_send = findViewById(R.id.text_send);

        intent = getIntent();
        String otherUser = intent.getStringExtra("userid");

        String userid = getSharedPreferences(getString(R.string.prefs_file), Context.MODE_PRIVATE).getString("email", null);

        String nombreCompletoOther = intent.getStringExtra("nombre_completo");

        username.setText(nombreCompletoOther);
        profile_image.setImageResource(R.mipmap.ic_launcher);
        renderMensajes(userid, otherUser);

        btn_send.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String mensaje = text_send.getText().toString();
                if (!mensaje.equals("")) {
                    enviarMensaje(userid, otherUser, mensaje);
                }
                else{
                    Toast.makeText(MessagingActivity.this, "El mensaje está vacío", Toast.LENGTH_SHORT).show();
                }
                text_send.setText("");
            }
        });
    }

    private void enviarMensaje(String emisor, String receptor, String mensaje){
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();

        HashMap<String, Object> hashMap = new HashMap<>();
        hashMap.put("emisor", emisor);
        hashMap.put("receptor", receptor);
        hashMap.put("mensaje", mensaje);

        reference.child("Mensajes").push().setValue(hashMap);

        intent = getIntent();
        String userFullName = intent.getStringExtra("nombre_completo_usuario");

        NotificadorKt.notificar(receptor, "Mensaje de " + userFullName, mensaje);

    }

    private void renderMensajes(String myId, String otherUserId) {
        mensajes = new ArrayList<>();

        reference = FirebaseDatabase.getInstance().getReference("Mensajes");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mensajes.clear();
                for(DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    Mensaje msj = snapshot.getValue(Mensaje.class);

                    Log.d("receptor", msj.getReceptor());
                    Log.d("emisor", msj.getEmisor());

                    Boolean cond1 = msj.getReceptor().equals(myId);
                    Boolean cond2 = msj.getEmisor().equals(otherUserId);
                    Boolean cond3 = msj.getReceptor().equals(otherUserId);
                    Boolean cond4 = msj.getEmisor().equals(myId);
                    Log.d("cond1", cond1.toString());
                    Log.d("cond2", cond2.toString());
                    Log.d("cond3", cond3.toString());
                    Log.d("cond4", cond4.toString());
                    if ((msj.getReceptor().equals(myId) && msj.getEmisor().equals(otherUserId))
                     || (msj.getReceptor().equals(otherUserId) && msj.getEmisor().equals(myId))) {
                        Log.d("encontro", "matcheo");
                        mensajes.add(msj);
                    }

                    adapter = new MessageAdapter(MessagingActivity.this, mensajes, myId);
                    rView.setAdapter(adapter);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
