package com.wissen.mesut.j6_4addressbook;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.wissen.mesut.j6_4addressbook.model.Kisi;

import java.util.ArrayList;

import de.cketti.mailto.EmailIntentBuilder;

public class MainActivity extends AppCompatActivity {
    Button btnYeni;
    ListView listView;
    FirebaseDatabase database;
    DatabaseReference myRef;
    ProgressDialog dialog;
    BaseAdapter baseAdapter;
    LayoutInflater layoutInflater;
    ArrayList<Kisi> gelen;

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
        layoutInflater = layoutInflater.from(this);

        baseAdapter = new BaseAdapter() {
            @Override
            public int getCount() {
                if (gelen == null)
                    return 0;
                return gelen.size();
            }

            @Override
            public Object getItem(int i) {
                return null;
            }

            @Override
            public long getItemId(int i) {
                return 0;
            }

            @Override
            public View getView(int i, View view, ViewGroup viewGroup) {
                if (view == null)
                    view = layoutInflater.inflate(R.layout.mylist_item, null);
                Kisi basilacakKisi = gelen.get(i);
                TextView txtad = view.findViewById(R.id.viewTxtAd);
                TextView txtsoyad = view.findViewById(R.id.viewTxtSoyad);
                Button btnara = view.findViewById(R.id.viewBtnAra);
                Button btnmail = view.findViewById(R.id.viewBtnMail);

                final int pos = i;
                btnara.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (TextUtils.isEmpty(gelen.get(pos).getTelefon())) {
                            Toast.makeText(MainActivity.this, R.string.msgtelefonnumarasiyok, Toast.LENGTH_SHORT).show();
                        } else {
                            Intent intent = new Intent(Intent.ACTION_CALL);
                            intent.setData(Uri.parse("tel:" + gelen.get(pos).getTelefon()));

                            if (ActivityCompat.checkSelfPermission(MainActivity.this, android.Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                                Toast.makeText(MainActivity.this, R.string.msgyetkiyok, Toast.LENGTH_SHORT).show();

                                ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.CALL_PHONE}, 100);
                                return;
                            }
                            startActivity(intent);
                        }
                    }
                });
                btnmail.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        //Intent intent = new Intent(Intent.ACTION_SEND);
                        //intent.setType("text/plain");
                        /*intent.setType("message/rfc822");
                        intent.putExtra(Intent.EXTRA_EMAIL, gelen.get(pos).getMail());
                        intent.putExtra(Intent.EXTRA_SUBJECT, "Bu Mail AdressBook Uygulamasından");
                        intent.putExtra(Intent.EXTRA_TEXT, "Deneme");
                        startActivity(Intent.createChooser(intent, "Mail Gönder"));*/
                        Kisi kisi = gelen.get(pos);
                        if (TextUtils.isEmpty(kisi.getMail())) {
                            Toast.makeText(MainActivity.this, "E mail adresi yok", Toast.LENGTH_SHORT).show();
                        } else {
                            EmailIntentBuilder.from(MainActivity.this)
                                    .to(kisi.getMail())
                                    .subject("Bu Mail AdressBook Uygulamasından")
                                    .body(String.format("%s %s %s", kisi.getAd(), kisi.getSoyad(), kisi.getTelefon()))
                                    .start();
                        }
                    }
                });
                txtad.setText(basilacakKisi.getAd());
                txtsoyad.setText(basilacakKisi.getSoyad());
                txtad.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        Intent intent = new Intent(MainActivity.this, YeniActivity.class);
                        intent.putExtra("yeni", false);
                        intent.putExtra("kisi", gelen.get(pos).getId());
                        startActivity(intent);
                    }
                });
                txtad.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View view) {
                        showProgressDialog("Lütfen Bekleyin","Siliniyor");
                        database = FirebaseDatabase.getInstance();
                        myRef = database.getReference().child("kisiler").child(gelen.get(pos).getId());
                        myRef.removeValue();
                        dbGetir();
                        return true;
                    }
                });
                return view;
            }
        };
        listView.setAdapter(baseAdapter);

    }

    @Override
    protected void onStart() {
        super.onStart();
        dbGetir();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100) {
            boolean izinVerildiMi = grantResults[0] == PackageManager.PERMISSION_GRANTED;
            if (!izinVerildiMi)
                Toast.makeText(this, "Arama özelliğini kullanmak istiyorsan arama iznini uygulamaya vermelisin", Toast.LENGTH_LONG).show();
        }
    }

    public void dbGetir() {
        database = FirebaseDatabase.getInstance();
        myRef = database.getReference().child("kisiler");
        showProgressDialog("Lütfen Bekleyin", "Veri tabanı bağlantısı kuruluyor");
        myRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                hideProgressDialog();
                gelen = new ArrayList<>();
                for (DataSnapshot postSnapshot : dataSnapshot.getChildren()) {
                    Kisi g = postSnapshot.getValue(Kisi.class);
                    gelen.add(g);
                }
                if (gelen.size() == 0) return;
                /*ArrayAdapter<Kisi> adapter = new ArrayAdapter<>(MainActivity.this, R.layout.mylist_item, gelen);*/
                baseAdapter.notifyDataSetChanged();

                MediaPlayer player = MediaPlayer.create(MainActivity.this, R.raw.bell);
                player.start();
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
