package com.example.android.whatevertrash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.design.animation.Positioning;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;

import java.util.List;

public class newsadaptor extends RecyclerView.Adapter<newsadaptor.newsholder>{
    source[] list;

    private int lastAnimatedPosition = -1;

    @NonNull
    @Override
    public newsholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerviewlayout,viewGroup, false);
        return new newsholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsholder newsholder, int i) {
        newsholder.title.setText(list[i].title);
        newsholder.distance.setText(String.valueOf(list[i].distance[0]) + " M");
    }

    @Override
    public int getItemCount() {
        return list.length;
    }

    public newsadaptor(source[] list) {
        this.list = list;
    }

    public void update(source[] list) {
        if (list.length == 0) {
            return;
        }
        this.list = list;
        notifyDataSetChanged();
    }

    public class newsholder extends RecyclerView.ViewHolder {
        TextView title, description, distance;
        public newsholder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.newstitle);
            distance = (TextView) view.findViewById(R.id.newsdescription);
        }
    }
}
