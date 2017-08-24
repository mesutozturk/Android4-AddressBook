package com.wissen.mesut.j6_4addressbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.wissen.mesut.j6_4addressbook.model.Kisi;

public class YeniActivity extends AppCompatActivity {
    Kisi gelenKisi;
    EditText txtAd, txtSoyad, txtTelefon, txtEmail;
    Button btnEkle, btnGuncelle;

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
        boolean durum = intent.getBooleanExtra("yeni", true);
        if (durum) {
            btnEkle.setVisibility(View.VISIBLE);
            btnGuncelle.setVisibility(View.GONE);
        } else {
            btnGuncelle.setVisibility(View.VISIBLE);
            btnEkle.setVisibility(View.GONE);
        }

    }
}
