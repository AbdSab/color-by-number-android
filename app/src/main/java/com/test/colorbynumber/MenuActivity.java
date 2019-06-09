package com.test.colorbynumber;

import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.drawable.LayerDrawable;
import android.net.Uri;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;


import com.startapp.android.publish.adsCommon.StartAppAd;
import com.startapp.android.publish.adsCommon.StartAppSDK;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;


public class MenuActivity extends AppCompatActivity implements View.OnClickListener {

    public int current = 0;

    //Button Animation
    private AlphaAnimation buttonClick = new AlphaAnimation(1F, 0.2F);

    //Animations
    private Animation upToDown,  downToUp, leftToRight, zoom;


    enum B {PLAY, GALLERY, SHARE, RATE, MORE}
    private Button[] buttons = new Button[5];
    private TextView title;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //StartApp
        StartAppSDK.init(this, getString(R.string.packageStartApp), true);
        StartAppAd.enableAutoInterstitial();

        setContentView(R.layout.activity_menu);

        Log.d("MD5_CODE", "onCreate: " +mdyns(getPackageName()));
        if(!mdyns(getPackageName()).equals("f8a54d6e140db60c394855629dd8198")){
            MenuActivity.this.finish();
            android.os.Process.killProcess(android.os.Process.myPid());
        }



        //Ads
        gdprDialog();

        //Animations
        upToDown = AnimationUtils.loadAnimation(this, R.anim.uptodown);
        downToUp = AnimationUtils.loadAnimation(this, R.anim.downtoup);
        leftToRight = AnimationUtils.loadAnimation(this, R.anim.lefttoright);
        zoom = AnimationUtils.loadAnimation(this, R.anim.zoom);


        title = findViewById(R.id.title_menu);
        buttons[B.PLAY.ordinal()] = findViewById(R.id.menu_play);
        buttons[B.GALLERY.ordinal()] = findViewById(R.id.menu_tutorial);
        buttons[B.SHARE.ordinal()] = findViewById(R.id.menu_share);
        buttons[B.RATE.ordinal()] = findViewById(R.id.menu_rate);
        buttons[B.MORE.ordinal()] = findViewById(R.id.menu_more);

        buttons[0].setOnClickListener(this);

        buttons[B.PLAY.ordinal()].setAnimation(zoom);
        buttons[B.GALLERY.ordinal()].setAnimation(upToDown);
        buttons[B.SHARE.ordinal()].setAnimation(upToDown);
        buttons[B.RATE.ordinal()].setAnimation(downToUp);
        buttons[B.MORE.ordinal()].setAnimation(downToUp);
        title.setAnimation(leftToRight);

        //Button Colors
        if (android.os.Build.VERSION.SDK_INT >= 16) {
            for(int i=1; i<=4; i++) {
                LayerDrawable layerDrawable = (LayerDrawable) getResources().getDrawable(R.drawable.button_shape);
                //GradientDrawable gradientDrawable = (GradientDrawable) layerDrawable.findDrawableByLayerId(R.id.button_palette);
                //Random rnd = new Random();
                //gradientDrawable.setColor(Color.argb(255, rnd.nextInt(256), rnd.nextInt(256), rnd.nextInt(256)));
                buttons[i].setBackground(layerDrawable);
                buttons[i].setOnClickListener(this);
            }
        }
    }
    @Override
    public void onClick(View v) {
        v.startAnimation(buttonClick);
        if(v == buttons[B.PLAY.ordinal()]){
             Intent intent = new Intent(this, ImagesActivity.class);
             startActivity(intent);
        }else if(v == buttons[B.SHARE.ordinal()]){
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = "\nCheck this best Color By Number Game !\n\n";
            sAux = sAux + "https://fr.aptoide.com/store/crea-app-store";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "Choose"));

        }else if(v == buttons[B.GALLERY.ordinal()]){
            tutorialDialog();

        }else if(v == buttons[B.MORE.ordinal()]){

            Intent intent = new Intent(Intent.ACTION_VIEW);
            intent.setData(Uri.parse("https://fr.aptoide.com/store/crea-app-store"));
            startActivity(intent);

        }else if(v == buttons[B.RATE.ordinal()]){
            Uri uri = Uri.parse("https://fr.aptoide.com/store/crea-app-store");
            Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
            // To count with Play market backstack, After pressing back button,
            // to taken back to our application, we need to add following flags to intent.
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
            try {
                startActivity(goToMarket);
            } catch (ActivityNotFoundException e) {
                startActivity(new Intent(Intent.ACTION_VIEW,
                        Uri.parse("https://fr.aptoide.com/store/crea-app-store")));
            }
        }
    }

    public void goToVoid(){
        Intent intent = new Intent(this, ImagesActivity.class);
        startActivity(intent);
    }

    public void tutorialDialog() {

        final int[] tutos = {R.drawable.tuto1, R.drawable.tuto2, R.drawable.tuto3};

        final Dialog tuto = new Dialog(MenuActivity.this);
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
                tuto.cancel();
            }
        });

        tuto.show();

    }


    public String mdyns(String s) {
        try {
            // Create MD5 Hash
            MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
            digest.update(s.getBytes());
            byte messageDigest[] = digest.digest();

            // Create Hex String
            StringBuffer hexString = new StringBuffer();
            for (int i=0; i<messageDigest.length; i++)
                hexString.append(Integer.toHexString(0xFF & messageDigest[i]));
            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return "";
    }

    public void gdprDialog() {

        AlertDialog.Builder builder = new AlertDialog.Builder(this);

        builder.setTitle("Confirm");
        builder.setMessage("Do you want to comply with the gdpr consent for better advertising experience ?");

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {

            public void onClick(DialogInterface dialog, int which) {
                // Do nothing but close the dialog
                StartAppSDK.setUserConsent (MenuActivity.this,
                        "pas",
                        System.currentTimeMillis(),
                        true);

                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("gdpr", true);
                edit.apply();

                changeFirstTime();
                dialog.dismiss();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {

            @Override
            public void onClick(DialogInterface dialog, int which) {
                StartAppSDK.setUserConsent (MenuActivity.this,
                        "pas",
                        System.currentTimeMillis(),
                        false);

                SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
                SharedPreferences.Editor edit = pref.edit();
                edit.putBoolean("gdpr", false);
                edit.apply();

                changeFirstTime();
                dialog.dismiss();
            }
        });

        AlertDialog alert = builder.create();



        if(checkFirstTime())
            alert.show();
        else{
            SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
            StartAppSDK.setUserConsent (MenuActivity.this, "pas",System.currentTimeMillis(),pref.getBoolean("gdpr", true));
        }

    }

    public boolean checkFirstTime(){
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        return pref.getBoolean("first_time_gdpr", true);
    }
    public void changeFirstTime(){
        SharedPreferences pref = getSharedPreferences("config", MODE_PRIVATE);
        SharedPreferences.Editor edit = pref.edit();
        edit.putBoolean("first_time_gdpr", false);
        edit.apply();
    }
}
