package io.github.MoWei.Frozen.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import io.github.MoWei.Frozen.R;
import io.github.MoWei.Frozen.StaticData;
import io.github.MoWei.Frozen.Utils;


public class About extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        findViewById(R.id.coolapk_link).setOnClickListener(this);
        findViewById(R.id.github_link).setOnClickListener(this);
        findViewById(R.id.privacy_text).setOnClickListener(this);

    }

    @Override
    public void onResume() {
        super.onResume();
        findViewById(R.id.container).setBackground(StaticData.getBackgroundDrawable(this));
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.coolapk_link) {
            try {
                Intent intent = new Intent();
                intent.setClassName("com.coolapk.market", "com.coolapk.market.view.AppLinkActivity");
                intent.setAction("android.intent.action.VIEW");
                intent.setData(Uri.parse("coolmarket://u/24268987"));
                startActivity(intent);
            } catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.coolapk_link))));
            }
        } else if (id == R.id.github_link) {
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(getString(R.string.github_link))));
        } else if (id == R.id.privacy_text) {
            Utils.textDialog(this, R.string.privacy_title, R.string.privacy_content);
        }
    }
}