package com.wissen.mesut.j6_4addressbook;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_4addressbook.model.Kisi;

public class YeniActivity extends AppCompatActivity {
    Kisi gelenKisi;
    EditText txtAd, txtSoyad, txtTelefon, txtEmail;
    Button btnEkle, btnGuncelle;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_yeni);
        txtAd = (EditText) findViewById(R.id.txtAd);
        txtSoyad = (EditText) findViewById(R.id.txtSoyad);
        txtEmail = (EditText) findViewById(R.id.txtEmail);
        txtTelefon = (EditText) findViewById(R.id.txtTelefon);
        btnEkle = (Button) findViewById(R.id.btnEkle);
        btnGuncelle = (Button) findViewById(R.id.btnGuncelle);

        Intent intent = getIntent();
        final boolean durum = intent.getBooleanExtra("yeni", true);
        if (durum) {
            btnEkle.setVisibility(View.VISIBLE);
            btnGuncelle.setVisibility(View.GONE);
        } else {
            btnGuncelle.setVisibility(View.VISIBLE);
            btnEkle.setVisibility(View.GONE);

            String id = intent.getCharSequenceExtra("kisi").toString();
            database = FirebaseDatabase.getInstance();
            myRef = database.getReference().child("kisiler");
            Query query = myRef.child(id);
            showProgressDialog("Lütfen Bekleyin", "Kişi bilgisi alınıyor");
            query.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    hideProgressDialog();
                    gelenKisi = dataSnapshot.getValue(Kisi.class);
                    if (gelenKisi == null)
                        finish();
                    doldur(gelenKisi);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });

            //gelenKisi = (Kisi) intent.getSerializableExtra("kisi");
            //doldur(gelenKisi);
        }
        btnEkle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kisi kisi = new Kisi();
                kisi.setAd(txtAd.getText().toString());
                kisi.setMail(txtEmail.getText().toString());
                kisi.setSoyad(txtSoyad.getText().toString());
                kisi.setTelefon(txtTelefon.getText().toString());
                //MyContext.Kisiler.add(kisi);

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("kisiler");
                myRef.child(kisi.getId().toString()).setValue(kisi);

                Toast.makeText(YeniActivity.this, "Ekledi", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(YeniActivity.this, MainActivity.class));
                finish();
            }
        });
        btnGuncelle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Kisi guncellenecekKisi = new Kisi();
                /*for (int i = 0; i < MyContext.Kisiler.size(); i++) {
                    if (MyContext.Kisiler.get(i).getId().equals(gelenKisi.getId())) {
                        guncellenecekKisi = MyContext.Kisiler.get(i);
                        break;
                    }
                }*/
                guncellenecekKisi.setAd(txtAd.getText().toString());
                guncellenecekKisi.setMail(txtEmail.getText().toString());
                guncellenecekKisi.setSoyad(txtSoyad.getText().toString());
                guncellenecekKisi.setTelefon(txtTelefon.getText().toString());
                guncellenecekKisi.setId(gelenKisi.getId());

                database = FirebaseDatabase.getInstance();
                myRef = database.getReference().child("kisiler");
                myRef.child(guncellenecekKisi.getId()).setValue(guncellenecekKisi);

                Toast.makeText(YeniActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                //startActivity(new Intent(YeniActivity.this, MainActivity.class));
                finish();
            }
        });
    }

    private void doldur(Kisi gelenKisi) {
        txtTelefon.setText(gelenKisi.getTelefon());
        txtEmail.setText(gelenKisi.getMail());
        txtAd.setText(gelenKisi.getAd());
        txtSoyad.setText(gelenKisi.getSoyad());
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
