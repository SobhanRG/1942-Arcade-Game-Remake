package com.demo.game1942;

import com.almasb.fxgl.audio.AudioPlayer;
import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.media.MediaPlayer;

public class MusicManager {

    private static MusicManager instance;
    private MediaPlayer backgroundMusic;
    private double musicVolume = 0.5;
    private double soundVolume = 0.7;

    private MusicManager() {}

    public static MusicManager getInstance() {
        if (instance == null) {
            instance = new MusicManager();
        }
        return instance;
    }

    public void playBackgroundMusic() {
//        try {
//            stopMusic();
//
//            backgroundMusic = FXGL.getAssetLoader().loadMusic("background_music.wav");
//            backgroundMusic.
//
//        }
    }

    public void playSound(String soundFile) {
        try {
            FXGL.play(soundFile);
        } catch (Exception e) {
            System.err.println("Error playing sound: " + soundFile + e.getMessage());
        }
    }

    public void stopMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.stop();
            System.out.println("Music stopped");
        }
    }

    public void pauseMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.pause();
            System.out.println("Music paused");
        }
    }

    public void resumeMusic() {
        if (backgroundMusic != null) {
            backgroundMusic.play();
            System.out.println("Music resumed");
        }
    }

    public void setMusicVolume(double volume) {
        this.musicVolume = Math.max(0, Math.min(1, volume));
        if (backgroundMusic != null) {
            backgroundMusic.setVolume(musicVolume);
        }
    }

    public void setSoundVolume(double volume) {
        this.soundVolume = Math.max(0, Math.min(1, volume));
    }

    public double getSoundVolume() {
        return soundVolume;
    }

    public double getMusicVolume() {
        return musicVolume;
    }





}
