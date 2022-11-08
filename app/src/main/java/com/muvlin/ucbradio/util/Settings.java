package com.muvlin.ucbradio.util;

public class Settings {
    public static String FORCE = "ucb_android_force";
    public static String REDIRECT = "ucb_android_redirect";
    public static String VERSION = "ucb_android_version";
    public static String URL = "ucb_url";
    private String redirect;
    private String version;
    private String url;
    private Boolean force;

    private static Settings mSettings;

    public static Settings getSettings(String redirect, String version, String url, Boolean force) {
        if (mSettings == null) {
            mSettings = new Settings(redirect, version, url, force);
        }
        return mSettings;
    }

    private Settings(String redirect, String version, String url, Boolean force) {
        this.redirect = redirect;
        this.version = version;
        this.url = url;
        this.force= force;
    }

    public String getRedirect() {
        return redirect;
    }

    public String getVersion() {
        return version;
    }

    public String getUrl() {
        return url;
    }

    public Boolean getForce() {
        return force;
    }
}
