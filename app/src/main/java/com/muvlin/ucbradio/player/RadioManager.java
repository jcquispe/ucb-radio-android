package com.muvlin.ucbradio.player;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;

import com.muvlin.ucbradio.ServiceCallbacks;

import org.greenrobot.eventbus.EventBus;

public class RadioManager {
    private static RadioManager instance = null;
    private static RadioService service;
    private Context context;
    private boolean serviceBound;

    private RadioManager(Context context) {
        this.context = context;
        serviceBound = false;
    }

    public static RadioManager with(Context context) {
        if (instance == null)
            instance = new RadioManager(context);
        return instance;
    }

    public RadioService getService(){
        return service;
    }

    public void playOrPause(String streamUrl, ServiceCallbacks callback){
        service.playOrPause(streamUrl);
        service.setCallbacks(callback);
    }

    public void stop(){
        service.stop();
    }

    public boolean isPlaying() {
        if (service != null)
            return service.isPlaying();
        else
            return false;
    }

    public void bind() {
        Intent intent = new Intent(context, RadioService.class);
        context.bindService(intent, serviceConnection, Context.BIND_AUTO_CREATE);
        if(service != null)
            EventBus.getDefault().post(service.getStatus());
    }

    public void unbind() {
        context.unbindService(serviceConnection);
    }

    private ServiceConnection serviceConnection = new ServiceConnection() {

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder binder) {
            service = ((RadioService.LocalBinder) binder).getService();
            serviceBound = true;
        }

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            serviceBound = false;
        }
    };
}
