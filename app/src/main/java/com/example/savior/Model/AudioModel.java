package com.example.savior.Model;

public class AudioModel {

    private String username,audio,latlong;

    public AudioModel() {
    }

    public AudioModel(String username, String audio, String latlong) {
        this.username = username;
        this.audio = audio;
        this.latlong = latlong;
    }

    public String getlatlong() {
        return latlong;
    }

    public void setlatlong(String latlong) {
        this.latlong = latlong;
    }

    public String getusername() {
        return username;
    }

    public void setusername(String username) {
        this.username = username;
    }

    public String getaudio() {
        return audio;
    }

    public void setaudio(String audio) {
        this.audio = audio;
    }
}
