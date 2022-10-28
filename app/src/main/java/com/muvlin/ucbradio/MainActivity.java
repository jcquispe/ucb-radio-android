package com.muvlin.ucbradio;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import com.airbnb.lottie.LottieAnimationView;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.MultiTransformation;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.load.resource.bitmap.CenterCrop;
import com.muvlin.ucbradio.client.AlbumClient;
import com.muvlin.ucbradio.client.AlbumInterface;
import com.muvlin.ucbradio.client.ShoucastInterface;
import com.muvlin.ucbradio.client.pojo.MetadataResponse;
import com.muvlin.ucbradio.player.PlaybackStatus;
import com.muvlin.ucbradio.player.RadioManager;
import com.muvlin.ucbradio.util.Settings;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.LinkedList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import io.reactivex.android.BuildConfig;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.CompositeDisposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import jp.wasabeef.glide.transformations.BlurTransformation;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MainActivity extends AppCompatActivity implements ServiceCallbacks {
    private Settings settings = Settings.getSettings("", "", "", false, false);

    AlbumInterface albumInterface;
    CompositeDisposable compositeDisposable = new CompositeDisposable();

    RadioManager radioManager;
    SeekBar volumen;
    AudioManager audioManager;
    Button pause, play, stop;
    TextView nowPlaying;
    ImageView albumArt, volBajo, volAlto, background;
    CardView albumCardView;

    LottieAnimationView equalizadorImageView;

    int refresh = 20000;
    String api_key;
    private String oldData = "";

    private Boolean isInterrupted = false;
    Thread t = null;
    private Boolean up = false, down = false;
    private String TAG = "MainActivity";
    Timer timer = null;
    Toolbar toolbar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        radioManager = RadioManager.with(MainActivity.this);

        play = findViewById(R.id.playButton);
        pause = findViewById(R.id.pauseButton);
        stop = findViewById(R.id.stopButton);

        pause.setEnabled(false);
        stop.setEnabled(false);

        equalizadorImageView = findViewById(R.id.equalizerImageView);

        nowPlaying = findViewById(R.id.ahoraSuenaTextView);
        albumArt = findViewById(R.id.albumArtImageView);
        albumCardView = findViewById(R.id.albumCardView);

        volumen = findViewById(R.id.volumenSeekBar);
        volBajo = findViewById(R.id.volBajoImageView);
        volAlto = findViewById(R.id.volAltoImageView);

        background = findViewById(R.id.backgroundImageView);

        if (getDeviceWidth() < 721) {
            volumen.setVisibility(View.GONE);
            volBajo.setVisibility(View.GONE);
            volAlto.setVisibility(View.GONE);
        }
        volumen();

        play.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioManager.playOrPause(settings.getUrl(), MainActivity.this);
                playAction();
            }
        });

        pause.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioManager.playOrPause(settings.getUrl(), MainActivity.this);
                pauseAction();
            }
        });

        stop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                radioManager.stop();
                stopAction();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.about:
                Intent intent = new Intent(MainActivity.this, AboutActivity.class);
                startActivity(intent);
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private int getDeviceWidth() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
        int width = displayMetrics.widthPixels;
        Log.w( TAG, "DEVICE_WIDTH " + width + "x" + height);
        return width;
    }

    private void playAction() {
        getMetadata();
        play.setEnabled(false);
        pause.setEnabled(true);
        stop.setEnabled(true);
        discoAnimation(equalizadorImageView, R.raw.yellow_wave, "PLAY");
        play.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        pause.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        stop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
    }

    private void pauseAction() {
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(true);
        discoAnimation(equalizadorImageView, R.raw.yellow_wave, "PAUSE");
        play.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        pause.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        stop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        timer.cancel();
        timer = null;
    }

    private void stopAction() {
        play.setEnabled(true);
        pause.setEnabled(false);
        stop.setEnabled(false);
        discoAnimation(equalizadorImageView, R.raw.yellow_wave, "STOP");
        play.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.yellow)));
        pause.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        stop.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.white)));
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    private void discoAnimation(LottieAnimationView imageView, int animation, String action) {
        switch (action) {
            case "PLAY":
                imageView.setAnimation(animation);
                imageView.setSpeed(0.5F);
                imageView.loop(true);
                imageView.playAnimation();
                break;
            case "PAUSE":
                imageView.pauseAnimation();
                break;
            default:
                imageView.setImageResource(0);
        }
    }

    private void isActive() {
        String appVersion = BuildConfig.VERSION_NAME;
        if (settings.getActive()) {
            if (settings.getForce() && !appVersion.equals(settings.getVersion())) {
                AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
                builder.setTitle(getResources().getString(R.string.updateTitle));
                builder.setMessage(getResources().getString(R.string.updateMessage));
                builder.setCancelable(false);
                builder.setPositiveButton(getResources().getString(R.string.irGooglePlay), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent viewIntent = new Intent("android.intent.action.VIEW", Uri.parse(settings.getRedirect()));
                        startActivity(viewIntent);
                    }
                });
                AlertDialog dialog = builder.create();
                dialog.show();
                dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
            }
        }
        else {
            AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
            builder.setTitle(getResources().getString(R.string.inactiveTitle));
            builder.setMessage(getResources().getString(R.string.inactiveMessage));
            builder.setCancelable(false);
            builder.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    finishAndRemoveTask();
                }
            });

            AlertDialog dialog = builder.create();
            dialog.show();
            dialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(getResources().getColor(R.color.yellow));
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        //radioManager.unbind();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        super.onResume();
        radioManager.bind();
        if (radioManager.getService() != null) {
            returningToStoppedApp(radioManager.getService().getStatus());
        }
        isActive();

        Retrofit retrofit1 = AlbumClient.getInstance();
        albumInterface = retrofit1.create(AlbumInterface.class);
        api_key = getResources().getString(R.string.api_key);
    }

    private void returningToStoppedApp(String playingStatus) {
        switch(playingStatus) {
            case PlaybackStatus.PAUSED:
                pauseAction();
                break;
            case PlaybackStatus.IDLE:
                stopAction();
                break;
            default:
                break;
        }
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
        //finish();
    }

    @Subscribe
    public void onEvent(String status){
        switch (status){
            case PlaybackStatus.LOADING:
                // loading
                break;
            case PlaybackStatus.ERROR:
                Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();
                break;

        }

    }

    private void volumen() {

        try {
            audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
            volumen.setMax(audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC));
            volumen.setProgress(audioManager.getStreamVolume(AudioManager.STREAM_MUSIC));

            volumen.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
                @Override            public void onStopTrackingTouch(SeekBar arg0) {
                }

                @Override            public void onStartTrackingTouch(SeekBar arg0) {
                }

                @Override            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2) {
                    audioManager.setStreamVolume(AudioManager.STREAM_MUSIC, progress, 0);
                }
            });
        }
        catch (Exception e) {
            e.printStackTrace();
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

    private void recuperarAlbumArt(String artist, String track) {
        compositeDisposable.add(albumInterface.getAlbumArt(api_key, artist, track)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Response<ResponseBody>>() {
                    @Override
                    public void accept(Response<ResponseBody> s) throws Exception {
                        JSONObject json = new JSONObject(s.body().string());
                        try {
                            JSONObject track = json.getJSONObject("track");
                            JSONObject album = track.getJSONObject("album");
                            JSONArray image = album.getJSONArray("image");
                            JSONObject art = image.getJSONObject(image.length() - 2);
                            String artUrl = art.getString("#text");
                            if (!art.equals("")) {
                                Glide.with(MainActivity.this).load(artUrl).into(albumArt);
                                List<Transformation> transforms = new LinkedList<>();
                                transforms.add(new CenterCrop());
                                transforms.add(new BlurTransformation(10,8));
                                MultiTransformation transformation = new MultiTransformation(transforms);
                                Glide.with(MainActivity.this).load(artUrl)
                                        .transform(transformation)
                                        .into(background);
                            }
                            else {
                                background.setImageResource(R.drawable.back);
                                albumArt.setImageResource(R.drawable.ucbradio);
                            }
                        } catch(Exception e) {
                            background.setImageResource(R.drawable.back);
                            albumArt.setImageResource(R.drawable.ucbradio);
                            Log.e("AlbumArt->", e.getMessage());
                        }
                    }
                }));
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN){
            down = true;
        } else if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
            up = true;
        }
        if (up) {
            volumen.setProgress(volumen.getProgress() + 1);
            up = false;
        }
        if (down) {
            volumen.setProgress(volumen.getProgress() - 1);
            down = false;
        }
        Log.e("KEY", "Progress " + volumen.getProgress());
        return true;
    }

    private void getMetadata() {
        final Handler handler = new Handler();
        //Timer timer = new Timer();
        if (timer == null) {
            timer = new Timer();
        }
        TimerTask doAsynchronousTask = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    public void run() {
                        try {
                            getData();
                        } catch (Exception e) {
                            Log.e(TAG, "getMetadata " + e.getMessage());
                        }
                    }
                });
            }
        };
        timer.schedule(doAsynchronousTask, 0, refresh);
    }

    public void getData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://stream.consultoradas.com")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ShoucastInterface myAPICall = retrofit.create(ShoucastInterface.class);
        Call<MetadataResponse> call = myAPICall.getMetadata();
        call.enqueue(new Callback<MetadataResponse>() {
            @Override
            public void onResponse(Call<MetadataResponse> call, Response<MetadataResponse> response) {
                if (response.code() != 200) {
                    Log.e(TAG, "getData response code " + response.code());
                }
                else {
                    String data = response.body().title;
                    Log.e(TAG, response.body().title);
                    nowPlaying.setText(data);
                    String[] metadata = data.split("-");
                    if (!oldData.equals(data)) {
                        recuperarAlbumArt(metadata[0].trim(), metadata[1].trim());
                        oldData = data;
                    }

                }
            }

            @Override
            public void onFailure(Call<MetadataResponse> call, Throwable t) {
                Log.e(TAG, "function getData FAILURE");
            }
        });
    }

    @Override
    public void cancelTimer() {
        if (timer != null) {
            timer.cancel();
            timer = null;
        }
    }

    @Override
    public void startTimer() {
        if (timer == null) {
            getMetadata();
        }
    }
}
