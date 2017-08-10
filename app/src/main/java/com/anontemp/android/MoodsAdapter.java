package com.anontemp.android;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.List;


/**
 * Created by jaydee on 16.07.17.
 */

public class MoodsAdapter extends RecyclerView.Adapter {

    private final List<String> moods = Arrays.asList(" 😀 ", " 😄 ", " 😁 ", " 😆 ", " 😂 ",
            " ☺️ ", " 😊 ", " 😇 ", " 😉 ", " 😌 ", " 😍 ", " 😘 ", " 🔞 ", " 😙 ", " 😚 ", " ❤️ ", " 💔 ",
            " 😋 ", " 😜 ", " 😝 ", " 😛 ", " 🤑 ", " 🤗 ", " 🤓 ", " 😎 ", " 😏 ", " 😒 ", " 😞 ",
            " 😔 ", " 😟 ", " 😕 ", " 🙁 ", " ☹️ ", " 😣 ", " 😖 ", " 😫 ", " 😩 ", " 😤 ", " 😡 ", " 😐 ", " 😑 ",
            " 😯 ", " 😦 ", " 😧 ", " 😮 ", " 😵 ", " 😳 ", " 😱 ", " 😰 ", " 😢 ", " 😥 ", " 😭 ", " 😓 ", " 😪 ",
            " 😴 ", " 🙄 ", " 🤔 ", " 😬 ", " 🤐 ", " 😷 ", " 🤒 ", " 🤕 ", " 😈 ", " 👿 ", " 💩 ", " 🙌 ",
            " 👏 ", " 🙏 ", " 👍 ", " 👎 ", " 👊 ", " ✊ ", " ✌️ ", " 🤘 ", " 👌 ", " 👇 ", " ☝️ ", " 💪 ", " 🖕 ",
            " 🙅 ", " 🙅‍♂️ ", " 🙆 ", " 🙆‍♂️ ", " 🙋 ", " 🙋‍♂️ ", " 💆 ", " 💆‍♂️ ", " 💃 ",
            " 👀 ", " 👩‍🎤 ", " 👨‍🎤 ", " 👩‍🏫 ", " 👨‍🏫 ", " 👩‍💻 ", " 👨‍💻 ", " 🙇‍♀️ ", " 🙇 ", " 💁 ", " 💁‍♂️ ", " 🚶‍♀️ ", " 🚶 ",
            " 🏃‍♀️ ", " 🏃 ", " 🍦 ", " 🍰 ", " 🍭 ", " 🍩 ", " ☕️ ", " 🍺 ", " 🍻 ", " 🍷 ", " 🍸 ", " 🍹 ", " 🍾 ",
            " 💸 ", " 🚬 ", " 🎉 ", " 💯 ");

    private final Context context;

    public MoodsAdapter(Context context) {
        this.context = context;

    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.mood_item, parent, false);
        return new ViewHolder(view);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {

        ViewHolder h = (ViewHolder) holder;
        String mood = moods.get(position);
        h.text.setText(mood);


    }

    @Override
    public int getItemCount() {
        return moods.size();
    }


    public class ViewHolder extends RecyclerView.ViewHolder {
        public final View mView;
        public final TextView text;


        public ViewHolder(View view) {
            super(view);
            mView = view;
            text = view.findViewById(R.id.text);

        }

    }


}
