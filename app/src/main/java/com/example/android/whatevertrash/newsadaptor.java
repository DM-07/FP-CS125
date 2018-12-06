package com.example.android.whatevertrash;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.RippleDrawable;
import android.support.annotation.NonNull;
import android.support.constraint.ConstraintLayout;
import android.support.design.animation.Positioning;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import java.util.List;

public class newsadaptor extends RecyclerView.Adapter<newsadaptor.newsholder>{
    source[] list;
    Context adaptorcontext;


    @NonNull
    @Override
    public newsholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerviewlayout,viewGroup, false);
        return new newsholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final newsholder newsholder, final int i) {
        newsholder.title.setText(list[i].title);
        newsholder.distance.setText(String.valueOf(list[i].distance[0]) + " M");
        newsholder.cardconstraint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent describeintent = new Intent(adaptorcontext, descriptionpage.class);
                describeintent.putExtra("locationtitle", list[i].title);
                describeintent.putExtra("locationdescription", list[i].description);
                adaptorcontext.startActivity(describeintent);
            }
        });
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
        CardView cardView;
        ConstraintLayout cardconstraint;
        public newsholder(View view) {
            super(view);
            title = view.findViewById(R.id.newstitle);
            distance = view.findViewById(R.id.newsdescription);
            cardView = view.findViewById(R.id.locationcard);
            cardconstraint = view.findViewById(R.id.cardconstraint);
            adaptorcontext = view.getContext();
        }
    }
}
