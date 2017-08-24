package com.wissen.mesut.j6_4addressbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_4addressbook.model.Kisi;
import com.wissen.mesut.j6_4addressbook.model.MyContext;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnYeni;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        listView = (ListView) findViewById(R.id.listView);
        btnYeni = (Button) findViewById(R.id.btnYeni);
        btnYeni.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                intent.putExtra("yeni", true);
                startActivity(intent);
            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Kisi seciliKisi = MyContext.Kisiler.get(position);
                Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                intent.putExtra("yeni", false);
                intent.putExtra("kisi", seciliKisi);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGetir();
    }

    public void dbGetir() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("kisiler");
        showProgressDialog("Lütfen Bekleyin", "Veri tabanı bağlantısı kuruluyor");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                ArrayList<Kisi> gelen = new ArrayList<Kisi>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Kisi g = postSnapshot.getValue(Kisi.class);
                    gelen.add(g);
                }
                if (gelen.size() == 0) return;
                ArrayAdapter<Kisi> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, gelen);
                listView.setAdapter(adapter);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void showProgressDialog(String title, String message) {
        if (dialog == null) {
            dialog = new ProgressDialog(this);
            dialog.setMessage(message);
            dialog.setTitle(title);
            dialog.setIndeterminate(true);
        }
        dialog.show();
    }

    public void hideProgressDialog() {
        if (dialog != null && dialog.isShowing()) {
            dialog.dismiss();
        }
    }
}
