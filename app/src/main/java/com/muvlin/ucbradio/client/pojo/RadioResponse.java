package com.muvlin.ucbradio.client.pojo;

public class RadioResponse {
    public Android android;
    public Data data;
    public Inactive inactive;
    public Ios ios;
    public Update update;
    public String url;

    public RadioResponse(Android android, Data data, Inactive inactive, Ios ios, Update update, String url) {
        this.android = android;
        this.data = data;
        this.inactive = inactive;
        this.ios = ios;
        this.update = update;
        this.url = url;
    }

    public RadioResponse() {
    }

    public class Android{
        public boolean active;
        public boolean force;
        public String redirect;
        public String version;

        public Android() {
        }

        public Android(boolean active, boolean force, String redirect, String version) {
            this.active = active;
            this.force = force;
            this.redirect = redirect;
            this.version = version;
        }
    }

    public class Data{
        public String facebook;
        public String web;
        public int refresh;

        public Data() {
        }

        public Data(String facebook, String web, int refresh) {
            this.facebook = facebook;
            this.web = web;
            this.refresh = refresh;
        }
    }

    public class Inactive{
        public String message;
        public int time;
        public String title;

        public Inactive() {
        }

        public Inactive(String message, int time, String title) {
            this.message = message;
            this.time = time;
            this.title = title;
        }
    }

    public class Ios{
        public boolean active;
        public boolean force;
        public String redirect;
        public String version;

        public Ios() {
        }

        public Ios(boolean active, boolean force, String redirect, String version) {
            this.active = active;
            this.force = force;
            this.redirect = redirect;
            this.version = version;
        }
    }

    public class Update{
        public String message;
        public int time;
        public String title;

        public Update() {
        }

        public Update(String message, int time, String title) {
            this.message = message;
            this.time = time;
            this.title = title;
        }
    }
}
