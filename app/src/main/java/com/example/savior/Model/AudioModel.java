package com.example.savior.Model;

public class AudioModel {

    private String username,audio;

    public AudioModel() {
    }

    public AudioModel(String username, String audio) {
        this.username = username;
        this.audio = audio;
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
