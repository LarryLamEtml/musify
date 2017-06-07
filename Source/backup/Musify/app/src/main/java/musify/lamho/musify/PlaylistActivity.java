package musify.lamho.musify;

import java.io.File;
import java.util.ArrayList;

/**
 * Created by lamho on 07.06.2017.
 */

public class PlaylistActivity {

    public PlaylistActivity()
    {
    }

    //Récupère une liste de toutes les music
    public ArrayList<String> getListMusic(String path)
    {
        ArrayList<String> listMusic = new ArrayList<>();
        String[] extensions = { "mp3" };

        File file = new File(path);
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            if (files != null && files.length > 0) {
                for (File f : files) {
                    if (f.isDirectory()) {
                        getListMusic(f.getAbsolutePath());
                    } else {
                        for (int i = 0; i < extensions.length; i++) {
                            if (f.getAbsolutePath().endsWith(extensions[i])) {
                                listMusic.add(f.getAbsolutePath());
                            }
                        }
                    }
                }
            }
        }
        return listMusic;
    }
}
