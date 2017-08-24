package com.wissen.mesut.j6_4addressbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.wissen.mesut.j6_4addressbook.model.Kisi;
import com.wissen.mesut.j6_4addressbook.model.MyContext;

public class YeniActivity extends AppCompatActivity {
    Kisi gelenKisi;
    EditText txtAd, txtSoyad, txtTelefon, txtEmail;
    Button btnEkle, btnGuncelle;
    FirebaseDatabase database;
    DatabaseReference myRef;

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

            gelenKisi = (Kisi) intent.getSerializableExtra("kisi");
            doldur(gelenKisi);
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
                for (int i = 0; i < MyContext.Kisiler.size(); i++) {
                    if (MyContext.Kisiler.get(i).getId().equals(gelenKisi.getId())) {
                        guncellenecekKisi = MyContext.Kisiler.get(i);
                        break;
                    }
                }
                guncellenecekKisi.setAd(txtAd.getText().toString());
                guncellenecekKisi.setMail(txtEmail.getText().toString());
                guncellenecekKisi.setSoyad(txtSoyad.getText().toString());
                guncellenecekKisi.setTelefon(txtTelefon.getText().toString());
                Toast.makeText(YeniActivity.this, "Güncellendi", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(YeniActivity.this, MainActivity.class));
            }
        });
    }

    private void doldur(Kisi gelenKisi) {
        txtTelefon.setText(gelenKisi.getTelefon());
        txtEmail.setText(gelenKisi.getMail());
        txtAd.setText(gelenKisi.getAd());
        txtSoyad.setText(gelenKisi.getSoyad());
    }
}
