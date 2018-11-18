package com.example.android.whatevertrash;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class newsadaptor extends RecyclerView.Adapter<newsadaptor.newsholder>{
    source[] list;

    @NonNull
    @Override
    public newsholder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view =  LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.recyclerviewlayout,viewGroup, false);
        return new newsholder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull newsholder newsholder, int i) {
        newsholder.title.setText(list[i].title);
        newsholder.description.setText(list[i].description);
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
        TextView title, description, url;
        public newsholder(View view) {
            super(view);
            title = (TextView) view.findViewById(R.id.newstitle);
            description = (TextView) view.findViewById(R.id.newsdescription);
        }
    }
}
