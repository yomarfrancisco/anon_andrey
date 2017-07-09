package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class DashBoard extends FullscreenController implements View.OnClickListener {

    @Override
    protected int init() {
        return R.layout.activity_board;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        findViewById(R.id.messageBoard).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.messageBoard:
                Intent intent = new Intent(DashBoard.this, MessageBoard.class);
                startActivity(intent);
                Helper.downToUpTransition(DashBoard.this);
        }
    }
}
