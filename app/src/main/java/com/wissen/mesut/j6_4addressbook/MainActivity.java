package com.wissen.mesut.j6_4addressbook;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.wissen.mesut.j6_4addressbook.model.Kisi;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {
    Button btnYeni;
    ListView listView;
    ArrayList<Kisi> kisiler = new ArrayList<>();

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
                intent.putExtra("yeni",true);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (kisiler.size() == 0) return;
        ArrayAdapter<Kisi> adapter = new ArrayAdapter<>(MainActivity.this, android.R.layout.simple_list_item_1, kisiler);
        listView.setAdapter(adapter);
    }
}
