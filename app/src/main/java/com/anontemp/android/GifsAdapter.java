package com.anontemp.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.RequestManager;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.firebase.ui.storage.images.FirebaseImageLoader;
import com.google.firebase.storage.StorageReference;

import java.util.List;

/**
 * Created by jaydee on 16.07.17.
 */

public class GifsAdapter extends RecyclerView.Adapter {

    private final List<StorageReference> gifs;
    private final RequestManager glide;
    private GifAdapterInterface anInterface;

    public GifsAdapter(List<StorageReference> gifs, RequestManager glide, Context context) {
        this.gifs = gifs;
        this.glide = glide;
        this.anInterface = (GifAdapterInterface) context;
        setHasStableIds(true);
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.gif_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onViewRecycled(RecyclerView.ViewHolder viewHolder) {
        super.onViewRecycled(viewHolder);
        ViewHolder h = (ViewHolder) viewHolder;
        h.gifView.setImageDrawable(null);
        Glide.clear(h.gifView);

    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        final ViewHolder h = (ViewHolder) holder;
        StorageReference gifUrl = gifs.get(position);

//        h.progressBar.show();
        Glide.clear(h.gifView);

        glide.using(new FirebaseImageLoader()).load(gifUrl).asGif().crossFade().diskCacheStrategy(DiskCacheStrategy.SOURCE).into(h.gifView);

    }

    @Override
    public int getItemCount() {
        return gifs.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }


    public interface GifAdapterInterface {
        void onGifClick(StorageReference gifUri);
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final ImageView gifView;
//        public final ContentLoadingProgressBar progressBar;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            gifView = view.findViewById(R.id.gifView);
            mView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    anInterface.onGifClick(gifs.get(getAdapterPosition()));
                }
            });
//            progressBar = view.findViewById(R.id.progress);

        }

    }


}
