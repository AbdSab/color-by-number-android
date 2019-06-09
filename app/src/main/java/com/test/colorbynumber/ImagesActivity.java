package com.test.colorbynumber;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

import static java.security.AccessController.getContext;

public class ImagesActivity extends AppCompatActivity {



    List<Thumb> thumb;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_images);



        //Get All Images
        thumb = new ArrayList<>();
        Field[] fields=R.drawable.class.getFields();
        for(int count=0; count < fields.length; count++){
            if(fields[count].getName().contains("pixel_")) {
                thumb.add(new Thumb(getResources().getIdentifier(fields[count].getName(), "drawable", getPackageName())));
            }
        }


        RecyclerView r = findViewById(R.id.recycler_view);
        ThumbAdapter tA = new ThumbAdapter(this, thumb);
        r.setLayoutManager(new GridLayoutManager(this, 2));
        r.setAdapter(tA);


    }


}
