package com.example.android.sdcodev1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    public ImageButton mstartlogo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        init();

    }

    @SuppressLint("WrongViewCast")
    public void init(){
        mstartlogo = (ImageButton)findViewById(R.id.startlogo);
        mstartlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(MainActivity.this,devices.class);

                startActivity(toy);

            }
        });


    }

}
