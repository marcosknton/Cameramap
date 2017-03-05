package com.example.michus.cameramap;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.net.URISyntaxException;

public class detalle extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detalle);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        TextView direccion=(TextView) findViewById(R.id.Tvdireccion);
        ImageView imagen=(ImageView) findViewById(R.id.Ivimagen);
        String sdireccion=getIntent().getStringExtra("direccion");
        String sruta=getIntent().getStringExtra("ruta");
        Log.i("!!!!!!!!",sruta);
        Glide.with(this).load(sruta).into(imagen);
        direccion.setText(sdireccion);

    }

}
