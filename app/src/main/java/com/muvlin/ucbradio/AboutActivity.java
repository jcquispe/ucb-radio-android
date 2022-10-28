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

import com.muvlin.ucbradio.client.APIClient;
import com.muvlin.ucbradio.client.APIInterface;
import com.muvlin.ucbradio.client.pojo.InfoResponse;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import retrofit2.Retrofit;

public class AboutActivity extends AppCompatActivity {

    APIInterface apiInterface;
    CompositeDisposable compositeDisposable = new CompositeDisposable();
    String muvlinWeb, consultoradasWeb;

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
                String shareBody = getResources().getString(R.string.mensaje);
                sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, getResources().getString(R.string.app_name));
                sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(sharingIntent, "Compartir en"));
            }
        });

        muvlin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(muvlinWeb));
                startActivity(viewIntent);
            }
        });

        consultoradas.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(consultoradasWeb));
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
        Retrofit retrofit = APIClient.getInstance();
        apiInterface = retrofit.create(APIInterface.class);
        recuperarData();
    }

    private void recuperarData() {
        compositeDisposable.add(apiInterface.getInfo()
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<InfoResponse>() {
                    @Override
                    public void accept(InfoResponse response) throws Exception {
                        //MOSTRAR DATA
                        muvlinWeb = response.muvlin;
                        consultoradasWeb = response.consultoradas;
                        copyright.setText(response.ucb + " Reservados todos los derechos");
                    }
                }));
    }
}