package com.muvlin.ucbradio;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.remoteconfig.FirebaseRemoteConfig;
import com.google.firebase.remoteconfig.FirebaseRemoteConfigSettings;
import com.muvlin.ucbradio.util.Settings;

public class SplashActivity extends AppCompatActivity {
    TextView version;
    private FirebaseRemoteConfig mFirebaseRemoteConfig;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);
        version = findViewById(R.id.versionTextView);
        version.setText("versión " + BuildConfig.VERSION_NAME);

        final Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(SplashActivity.this, MainActivity.class);
                finish();
                startActivity(intent);
            }
        }, 2000);
    }

    @Override
    protected void onResume() {
        super.onResume();
        getRemoteConfig();
    }

    private void getRemoteConfig() {
        if (isNetworkAvailable()) {
            mFirebaseRemoteConfig = FirebaseRemoteConfig.getInstance();
            FirebaseRemoteConfigSettings configSettings = new FirebaseRemoteConfigSettings.Builder()
                    .setMinimumFetchIntervalInSeconds(60)
                    .build();
            mFirebaseRemoteConfig.setConfigSettingsAsync(configSettings);

            mFirebaseRemoteConfig.fetchAndActivate().addOnCompleteListener(this, new OnCompleteListener<Boolean>() {
                @Override
                public void onComplete(@NonNull Task<Boolean> task) {
                    if (task.isSuccessful()) {
                        Settings sett = Settings.getSettings(
                                mFirebaseRemoteConfig.getString(Settings.REDIRECT),
                                mFirebaseRemoteConfig.getString(Settings.VERSION),
                                mFirebaseRemoteConfig.getString(Settings.URL),
                                mFirebaseRemoteConfig.getBoolean(Settings.FORCE));
                        Log.e("URL", sett.getUrl());
                    } else {
                        Log.e("RemoteConfigError", "Not able to get remote configuration");
                        Toast.makeText(SplashActivity.this, "ERROR al cargar la configuracion remota", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
        else {
            Toast.makeText(SplashActivity.this, "ERROR al cargar la configuración de la aplicación", Toast.LENGTH_LONG).show();
        }
    }

    public boolean isNetworkAvailable() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = null;
        if (cm != null) {
            networkInfo = cm.getActiveNetworkInfo();
        }
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
    }
}