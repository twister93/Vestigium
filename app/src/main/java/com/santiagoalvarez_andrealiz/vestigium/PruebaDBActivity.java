package com.santiagoalvarez_andrealiz.vestigium;

import android.content.Context;
import android.hardware.usb.UsbInterface;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.santiagoalvarez_andrealiz.vestigium.model.Usuarios;

import java.util.ArrayList;

public class PruebaDBActivity extends AppCompatActivity {

    private EditText etNombre,etCorreo,etTelefono;
    private ListView listView;
    private ArrayList<String> nombreslist;
    private ArrayList<Usuarios> usuarioslist;
    private DatabaseReference databaseReference;
    private ArrayAdapter arrayAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prueba_db);

        etNombre = findViewById(R.id.etNombre);
        etCorreo = findViewById(R.id.etCorreo);
        etTelefono = findViewById(R.id.etTelefono);

        listView = findViewById(R.id.listView);


        // nombreslist = new ArrayList();
        usuarioslist = new ArrayList<>();

       /* arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,nombreslist);
        listView.setAdapter(arrayAdapter); */

        final UsuarioAdapter usuarioAdapter = new UsuarioAdapter(this, usuarioslist);
        listView.setAdapter(usuarioAdapter);


        FirebaseDatabase.getInstance(); //.setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //nombreslist.clear();
                usuarioslist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Usuarios usuarios = snapshot.getValue(Usuarios.class);
                        Log.d("nombre",usuarios.getNombre());
                 //       nombreslist.add(usuarios.getNombre());
                        usuarioslist.add(usuarios);
                    }
                }
                usuarioAdapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {

            @Override
            public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
                String uid = usuarioslist.get(position).getId();
                usuarioslist.remove(position);
                nombreslist.remove(position);
                databaseReference.child("usuarios").child(uid).removeValue();
                return false;
            }
        });
    }

    public void guardarClicked (View view){
        Usuarios usuarios = new Usuarios (databaseReference.push().getKey(),
                etNombre.getText().toString(),
                etTelefono.getText().toString(),
                etCorreo.getText().toString(),
                "url foto");


        databaseReference.child("usuarios").child(usuarios.getId()).setValue(usuarios);
    }

    class UsuarioAdapter extends ArrayAdapter<Usuarios>{

        public UsuarioAdapter(@NonNull Context context, ArrayList<Usuarios> data){
            super(context, R.layout.list_item, data);
        }
        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent){

            LayoutInflater inflater = LayoutInflater.from(getContext());
            View item = inflater.inflate(R.layout.list_item,null);

            Usuarios usuarios = getItem(position);

            TextView nombre = item.findViewById(R.id.tvName);
            nombre.setText(usuarios.getNombre());

            TextView correo = item.findViewById(R.id.tvEmail);
            correo.setText(usuarios.getCorreo());

            TextView telefono = item.findViewById(R.id.tvphone);
            telefono.setText(usuarios.getTelefono());

            return item;
        }
    }
}
