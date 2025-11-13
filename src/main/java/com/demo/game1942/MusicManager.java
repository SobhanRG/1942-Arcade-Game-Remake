package com.demo.game1942;

import com.almasb.fxgl.audio.Music;
import com.almasb.fxgl.dsl.FXGL;
import javafx.scene.media.Media;
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



    }
}
