package com.anontemp.android;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.anontemp.android.misc.Helper;

import static com.anontemp.android.misc.Helper.ARG_PAGE_TYPE;
import static com.anontemp.android.misc.Helper.PAGE_TYPE_PRIVACY;
import static com.anontemp.android.misc.Helper.PAGE_TYPE_TERMS;

public class TermsAndPrivacy extends FullscreenController implements View.OnClickListener {

    TextView upperLink;
    TextView tempLoginLink;
    private int pageType = PAGE_TYPE_TERMS;

    @Override
    protected int init() {
        if (getIntent() != null && getIntent().getIntExtra(ARG_PAGE_TYPE, 0) > 0) {
            pageType = getIntent().getIntExtra(ARG_PAGE_TYPE, 0);

            if (pageType == PAGE_TYPE_PRIVACY) {
                return R.layout.activity_privacy;

            }

        }

        return R.layout.activity_terms;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        upperLink = findViewById(R.id.upperLink);
        upperLink.setOnClickListener(this);
        tempLoginLink = findViewById(R.id.tempLoginLink);
        tempLoginLink.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.upperLink:
                Intent intent = new Intent(TermsAndPrivacy.this, TermsAndPrivacy.class);
                if (pageType == PAGE_TYPE_TERMS) {
                    intent.putExtra(ARG_PAGE_TYPE, PAGE_TYPE_PRIVACY);

                }
                intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                Helper.downToUpTransition(TermsAndPrivacy.this);
                break;
            case R.id.tempLoginLink:
                intent = new Intent(TermsAndPrivacy.this, Login.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
                startActivity(intent);
                Helper.downToUpTransition(TermsAndPrivacy.this);
                break;


        }

    }
}
