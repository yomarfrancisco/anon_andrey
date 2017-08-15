package com.anontemp.android;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.RequestManager;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.FirebaseStorage;

import java.util.List;

/**
 * Created by jaydee on 16.07.17.
 */

public class GifsAdapter extends RecyclerView.Adapter {

    private final List<String> gifs;
    private final RequestManager glide;

    public GifsAdapter(List<String> gifs, RequestManager glide) {
        this.gifs = gifs;
        this.glide = glide;
        setHasStableIds(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.tweet, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder h = (ViewHolder) holder;
        String gifUrl = gifs.get(position);
        glide.using(new FirebaseImageLoader()).load(FirebaseStorage.getInstance().getReference().child("gifs/" + gifUrl)).asGif().into(h.gifView);

    }


    @Override
    public int getItemCount() {
        return gifs.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView gifView;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            gifView = view.findViewById(R.id.gifView);

        }

    }


}
