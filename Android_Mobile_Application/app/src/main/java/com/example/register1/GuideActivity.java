package com.example.register1;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class GuideActivity extends AppCompatActivity {

    private CardView blackRot, esca, leafBlight;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);

        blackRot = findViewById(R.id.black_rot);
        esca = findViewById(R.id.esca);
        leafBlight = findViewById(R.id.leaf_blight);

        blackRot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, BlackRotActivity.class);
                startActivity(intent);
            }
        });
        esca.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, EscaActivity.class);
                startActivity(intent);
            }
        });
        leafBlight.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GuideActivity.this, LeafBlightActivity.class);
                startActivity(intent);
            }
        });
    }
}