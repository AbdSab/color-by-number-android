package com.test.colorbynumber;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.support.v4.graphics.ColorUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;



public class ColoringAcitvity extends AppCompatActivity  implements View.OnClickListener {

    private int current = 0;

    ColoringView canvas;
    GridView gridColors;
    LinearLayout palette;
    Button[] b_palette;

    private int colors_size;

    //Button Animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);


    //DragButton
    private ImageButton dragBut;

    int img;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_coloring);


        //Get ImageButton
        dragBut = findViewById(R.id.button);

        //Get Image
        Intent intent = getIntent();
        img = intent.getExtras().getInt("image");
        canvas = new ColoringView(this, img);
        canvas.setBackgroundColor(Color.WHITE);

        LinearLayout l = findViewById(R.id.custom_view_holder);
        l.addView(canvas);

        //gridColors = (GridView) findViewById(R.id.colorsGrid);
        //gridColors.setAdapter(new ButtonAdapter(this, colors));
        colors_size = canvas.colors.size();
        palette = (LinearLayout) findViewById(R.id.palette);

        b_palette = new Button[colors_size];
        for(int i =0; i < colors_size; i++){
            Integer c = canvas.colors.get(i);
            b_palette[i] = new Button(this);
            if( ColorUtils.calculateLuminance(c) < 0.5)
                b_palette[i].setTextColor(Color.WHITE);
            b_palette[i].setText(""+(i+1));
            if (android.os.Build.VERSION.SDK_INT >= 16) {
                LayerDrawable layerDrawable = (LayerDrawable) getResources()
                        .getDrawable(R.drawable.button_shape);
                GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable
                        .findDrawableByLayerId(R.id.button_palette);
                gradientDrawable.setColor(c);
                b_palette[i].setBackground(layerDrawable);
            }else{
                b_palette[i].setBackgroundColor(c);
            }
            b_palette[i].setOnClickListener(this);
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams((int)convertDpToPx(this, 40), (int)convertDpToPx(this, 40));
            params.setMargins(5, 5, 5, 5);
            b_palette[i].setLayoutParams (params );

            //b_palette[i].setHeight(b_palette[i].getMeasuredWidth());
            palette.addView(b_palette[i]);
        }
        tutorialDialog();
        loadButton(null);

    }
    @Override
    public void onClick(View v) {
        for (int i=0; i<colors_size; i++){
            if(v == b_palette[i]) canvas.selectedColor = canvas.colors.get(i);
            v.startAnimation(buttonClick);
        }
    }
    public float convertDpToPx(Context context, float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public void eraserButton(View v){
        canvas.selectedColor = Color.argb(0, 0, 0, 0);
    }
    public void centerButton(View v){
        canvas.mScaleFactor = 1f;
        canvas.vX = 0;
        canvas.vY = 0;
        canvas.mPosX = 0;
        canvas.mPosY = 0;
        canvas.invalidate();
    }
    public void dragModeButton(View v){
        canvas.dragMode = !canvas.dragMode;
        if(canvas.dragMode){
            dragBut.setImageResource(R.drawable.ic_paint_brush);
            return;
        }
        dragBut.setImageResource(R.drawable.ic_move);
    }

    public void saveButton(View v){
        String x = "";
        for(int i=0; i<canvas.image.length; i++){
            for(int j=0; j<canvas.image[i].length; j++){
                x += canvas.image[i][j] + ",";
            }
            x = removeLastChar(x);
            x += "/";
        }

        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putString("level_"+img, x);
        edit.apply();

        Toast.makeText(this, "Coloring Saved !",
                Toast.LENGTH_LONG).show();
    }

    public void loadButton(View v){
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        String x = pref.getString("level_"+img, "");
        if(!x.equals("")) {
            String[] xx = x.split("/");
            String[][] xxx = new String[xx.length][];
            for (int i = 0; i < xx.length; i++) {
                xxx[i] = xx[i].split(",");
            }

            for (int i = 0; i < xx.length; i++) {
                for (int j = 0; j < xxx[i].length; j++) {
                    canvas.image[i][j] = Integer.parseInt(xxx[i][j]);
                }
            }
            canvas.invalidate();
            Toast.makeText(this, "Coloring loaded !",
                    Toast.LENGTH_LONG).show();
        }

    }

    public void resetButton(View v){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Are you sure you want to clear all?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                canvas.image = new int[canvas.image.length][canvas.image[0].length];
                canvas.invalidate();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {

                // Do nothing
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();
        alert.show();
    }

    public String removeLastChar(String s) {
        if (s == null || s.length() == 0) {
            return s;
        }
        return s.substring(0, s.length()-1);
    }


    public void tutorialDialog() {

        final int[] tutos = {R.drawable.tuto1, R.drawable.tuto2, R.drawable.tuto3};

        final Dialog tuto = new Dialog(ColoringAcitvity.this);
        tuto.requestWindowFeature(Window.FEATURE_NO_TITLE); //before
        tuto.setContentView(R.layout.tutorial_dialog);

        final ImageView image_tut = tuto.findViewById(R.id.dialog_image);
        ImageButton next = tuto.findViewById(R.id.dialog_next);
        ImageButton back = tuto.findViewById(R.id.dialog_back);
        Button ok = tuto.findViewById(R.id.dialog_ok);

        next.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                current++;
                current = current % 3;
                image_tut.setImageResource(tutos[current]);
            }
        });

        back.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                current--;
                current = current % 3;
                image_tut.setImageResource(tutos[current]);
            }
        });

        ok.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                changeFirstTime();
                tuto.cancel();
            }
        });


        if(checkFirstTime())
            tuto.show();

    }

    public boolean checkFirstTime(){
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);

        Log.d("kjkj", "changeFirstTime: "+pref.getBoolean("first_time", true));
        return pref.getBoolean("first_time", true);
    }
    public void changeFirstTime(){
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("first_time", false);
        edit.apply();
        Log.d("kjkj", "changeFirstTime: "+pref.getBoolean("first_time", true));
    }


}