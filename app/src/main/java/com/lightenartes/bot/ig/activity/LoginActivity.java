package com.lightenartes.bot.ig.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.lightenartes.bot.ig.CommonUtil;
import com.lightenartes.bot.ig.R;
import com.lightenartes.bot.ig.instagram.InstagramApp;

import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by diaxz <dias.arifin@gmail.com> on 10/24/15.
 */
public class LoginActivity extends AppCompatActivity {

    private InstagramApp mApp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ButterKnife.bind(this);

        mApp = new InstagramApp(this, CommonUtil.CLIENT_ID, CommonUtil.CLIENT_SECRET, CommonUtil.CALLBACK_URL);
        mApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
            @Override
            public void onSuccess() {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));
                finish();
            }

            @Override
            public void onFail(String error) {
                Toast.makeText(LoginActivity.this, error, Toast.LENGTH_SHORT).show();
            }
        });

        if (mApp.hasAccessToken()) {
            startActivity(new Intent(this, MainActivity.class));
            finish();
        }
    }

    @OnClick(R.id.button)
    void login() {
        mApp.authorize();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            String message = data.getStringExtra("result");
            if (resultCode == Activity.RESULT_OK) {
                mApp.getAccessToken(message);
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                Toast.makeText(LoginActivity.this, message, Toast.LENGTH_SHORT).show();
            }
        }
    }
}
