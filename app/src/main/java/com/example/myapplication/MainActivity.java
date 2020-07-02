package com.example.myapplication;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

public class MainActivity extends AbstractActivity {

    private ImageView circle, square, triangle;
    private int greyColor, turquoiseColor;
    private int selectedShape;
    private LinearLayout sizeLL;
    private Button drawB;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        circle = findViewById(R.id.circleIV);
        square = findViewById(R.id.squareIV);
        triangle = findViewById(R.id.triangleIV);
        sizeLL = findViewById(R.id.sizeLL);
        drawB = findViewById(R.id.drawButton);

        turquoiseColor = Color.argb(255, 0, 255, 255);
        greyColor = Color.argb(255, 102, 102, 102);
    }

    public void onClickShape(View view) {

        if (sizeLL.getVisibility() == View.INVISIBLE) {
            sizeLL.setVisibility(View.VISIBLE);
            drawB.setVisibility(View.VISIBLE);
        }

        switch (view.getId()) {

            case R.id.circleIV:
                circle.setColorFilter(turquoiseColor);
                square.setColorFilter(greyColor);
                triangle.setColorFilter(greyColor);
                selectedShape = R.id.circleIV;
                break;

            case R.id.squareIV:
                circle.setColorFilter(greyColor);
                square.setColorFilter(turquoiseColor);
                triangle.setColorFilter(greyColor);
                selectedShape = R.id.squareIV;
                break;

            case R.id.triangleIV:
                circle.setColorFilter(greyColor);
                square.setColorFilter(greyColor);
                triangle.setColorFilter(turquoiseColor);
                selectedShape = R.id.triangleIV;
                break;
        }
    }

    public void onClickDraw(View view) {
        Intent i = new Intent(this, ShapeActivity.class);
        i.putExtra("shape", selectedShape);
        int size = Integer.parseInt(((EditText) findViewById(R.id.sizeET)).getText().toString());
        if (size > 100) {
            size = 100;
            Toast.makeText(this, getResources().getString(R.string.size_to_big_toast), Toast.LENGTH_LONG).show();
        } else if (size < 1) {
            size = 1;
            Toast.makeText(this, getResources().getString(R.string.size_too_small), Toast.LENGTH_LONG).show();
        }
        i.putExtra("size", size);
        startActivity(i);
    }
}
