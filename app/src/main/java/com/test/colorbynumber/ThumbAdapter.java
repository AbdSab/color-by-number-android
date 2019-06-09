package com.test.colorbynumber;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import java.util.List;

/**
 * Created by omen on 24/06/2018.
 */

public class ThumbAdapter extends RecyclerView.Adapter<ThumbAdapter.Holder> {

    private Context mContext;
    private List<Thumb> mData;

    public ThumbAdapter(Context mContext, List<Thumb> mData) {
        this.mContext = mContext;
        this.mData = mData;
    }

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v;
        LayoutInflater inflater = LayoutInflater.from(mContext);
        v = inflater.inflate(R.layout.item_card_picture, parent, false);
        return new Holder(v);
    }

    @Override
    public void onBindViewHolder(Holder holder, final int position) {

        Bitmap bmp = BitmapFactory.decodeResource(holder.itemView.getResources(), mData.get(position).getImage());
        Bitmap bmpscale = Bitmap.createScaledBitmap(bmp, 100, 100, false);
        holder.pic.setImageBitmap(bmpscale);
        holder.pic.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                Intent i = new Intent(mContext, ColoringAcitvity.class);
                i.putExtra("image", mData.get(position).getImage());
                mContext.startActivity(i);
            }
        });
    }

    @Override
    public int getItemCount() {
        return mData.size();
    }

    public static class Holder extends RecyclerView.ViewHolder{

        ImageView pic;

        public Holder(View itemView) {
            super(itemView);
            pic = itemView.findViewById(R.id.image_gallery);
        }
    }

}
