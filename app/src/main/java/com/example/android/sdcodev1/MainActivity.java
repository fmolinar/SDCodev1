package com.example.android.sdcodev1;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;


public class MainActivity extends AppCompatActivity {

    public ImageButton mstartlogo;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        ConexionSQLiteHelper conn = new ConexionSQLiteHelper(this,"db_Keys",null,1);
        init();

    }

    @SuppressLint("WrongViewCast")
    public void init(){
        mstartlogo = (ImageButton)findViewById(R.id.startlogo);
        mstartlogo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent toy = new Intent(MainActivity.this, devices.class);

                MainActivity.this.startActivity(toy);

            }
        });


    }

}
