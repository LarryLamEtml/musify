package musify.lamho.musify;

import android.media.MediaPlayer;
import android.os.Handler;
import android.widget.Toast;

import java.io.IOException;

/**
 * Created by lamho on 09.06.2017.
 */

public class MusicPlayer {
    private static final MusicPlayer ourInstance = new MusicPlayer();
    public static MediaPlayer mediaPlayer = new MediaPlayer();

    public static MusicPlayer getInstance() {
        return ourInstance;
    }
    private static String musicSource ="";
    private static int musicTime= 0;

    private Handler mHandler = new Handler();

    private MusicPlayer() {
    }



    public static void playMusic(String filePath)
    {
        musicSource = filePath;
        mediaPlayer.reset();


        try{

            mediaPlayer.setDataSource(musicSource);
            mediaPlayer.prepare();
            mediaPlayer.start();
            musicTime = 0;
        } catch (IOException e) {
            //Toast.makeText(, "Fichier illisible", Toast.LENGTH_LONG).show();
        }
    }

    public static String getMusicSource()
    {
        return musicSource;
    }
    public static int getMusicDuration()
    {
        return mediaPlayer.getDuration();
    }

    public static String getMusicName()
    {
        String name = "";
        if(musicSource!="")
        {
            String[] s =musicSource.split("/");
            name = s[s.length-1];
        }

        return name;
    }

    public static void pauseMusic()
    {
        mediaPlayer.pause();
        musicTime = mediaPlayer.getCurrentPosition();
    }

    public static void stopMusic()
    {
        mediaPlayer.reset();
        musicTime = 0;
        musicSource="";
    }

    public static void resumeMusic()
    {
        mediaPlayer.seekTo(musicTime);
        mediaPlayer.start();
    }
    public static void startMusicAtTime(int time)
    {
        mediaPlayer.seekTo(time);
        mediaPlayer.start();
    }
    public static void setMusicTime(int time)
    {
        mediaPlayer.seekTo(time);
    }

}

