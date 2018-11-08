package com.aphrodite.transferbywifi.view;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.aphrodite.transferbywifi.R;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        /**
         * 绑定Activity(注:必须在setContentView之后)
         */
        ButterKnife.bind(this);
    }

    @OnClick(R.id.main_server)
    public void onServerClick() {
        Intent intent = new Intent(this, ServerActivity.class);
        startActivity(intent);
    }

    @OnClick(R.id.main_client)
    public void onClientClick() {
        Intent intent = new Intent(this, ClientActivity.class);
        startActivity(intent);
    }

}
