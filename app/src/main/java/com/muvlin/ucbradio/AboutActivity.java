package com.muvlin.ucbradio;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.BlurMaskFilter;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import com.muvlin.ucbradio.util.Settings;

import java.text.SimpleDateFormat;
import java.util.Date;

public class AboutActivity extends AppCompatActivity {
    private Settings settings = Settings.getSettings("", "", "", false);

    ImageView muvlin, consultoradas, lapaz;
    TextView copyright, version;
    ImageButton facebook, web;
    Button compartir;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        toolbar.setTitle("Acerca de");
        toolbar.setTitleTextColor(Color.WHITE);
        toolbar.getNavigationIcon().setColorFilter(Color.WHITE, PorterDuff.Mode.SRC_ATOP);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        muvlin = findViewById(R.id.muvlinImageView);
        consultoradas = findViewById(R.id.consultoradasImageView);
        lapaz = findViewById(R.id.lapazImageView);
        copyright = findViewById(R.id.copyrightTextView);
        version = findViewById(R.id.versionTextView);
        version.setText("version " + BuildConfig.VERSION_NAME);

        facebook = findViewById(R.id.facebookButton);
        web = findViewById(R.id.webButton);

        compartir = findViewById(R.id.compartirButton);

        BlurMaskFilter mask = new BlurMaskFilter(20, BlurMaskFilter.Blur.NORMAL);

        compartir.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
                sharingIntent.setType("text/plain");
                String shareBody = getResources().getString(R.string.mensaje) + " " + settings.getRedirect();
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Compartir en"));
            }
        });

        muvlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.muvlinWeb)));
                startActivity(viewIntent);
            }
        });

        consultoradas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.consultoradasWeb)));
                startActivity(viewIntent);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.facebook)));
                startActivity(viewIntent);
            }
        });

        web.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(getResources().getString(R.string.web)));
                startActivity(viewIntent);
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();
        setCopyright();
    }

    private void setCopyright() {
        String thisYear = new SimpleDateFormat("yyyy").format(new Date());
        if (thisYear.equals(getResources().getString(R.string.gestion))) {
            copyright.setText(thisYear + " Reservados todos los derechos");
        }
        else {
            copyright.setText(getResources().getString(R.string.gestion) + " - " + thisYear + " Reservados todos los derechos");
        }

    }
}