package com.santiagoalvarez_andrealiz.vestigium;

import android.hardware.usb.UsbInterface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;

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



        nombreslist = new ArrayList();
        usuarioslist = new ArrayList<>();

        arrayAdapter = new ArrayAdapter<>(this,android.R.layout.simple_list_item_1,nombreslist);
        listView.setAdapter(arrayAdapter);


        FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        databaseReference = FirebaseDatabase.getInstance().getReference();

        databaseReference.child("Usuarios").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nombreslist.clear();
                usuarioslist.clear();
                if (dataSnapshot.exists()){
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Usuarios usuarios = snapshot.getValue(Usuarios.class);
                        Log.d("nombre",usuarios.getNombre());
                        nombreslist.add(usuarios.getNombre());
                        usuarioslist.add(usuarios);
                    }
                }
                arrayAdapter.notifyDataSetChanged();
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
                databaseReference.child("Usuarios").child(uid).removeValue();
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
        databaseReference.child("Usuarios").child(usuarios.getId()).setValue(usuarios);
    }

}
