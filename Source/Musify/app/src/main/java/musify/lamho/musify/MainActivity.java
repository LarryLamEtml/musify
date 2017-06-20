package musify.lamho.musify;
import android.Manifest;
import android.app.ActionBar;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.content.ContextCompat;
import android.support.v4.widget.ListViewAutoScrollHelper;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.TranslateAnimation;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static java.security.AccessController.getContext;

public class MainActivity extends AppCompatActivity {

    final String MUSIFY_PATH = Environment.getExternalStorageDirectory() +
            File.separator + "musify";
    final String MUSIC_PATH = MUSIFY_PATH +
            File.separator + "music";

    private Handler mHandler = new Handler();
    private ViewFlipper viewFlipper;
    private float lastX;
    SearchActivity searchActivity;
    PlaylistActivity playlistActivity;
    public static Intent myIntent;

    EditText editText;
    ArrayList<String> listmp3;
    static int REQUEST_MUSIC_PATH = 1;

    int musicPosition = 0;
    //Variables pour savoir si le bouton est cliqué
    boolean isReplayPressed = false;
    boolean isShufflePressed = false;
    boolean isPlayPressed = false;

    MusicPlayer musicPlayer;

    int musicTime = 0;

    boolean isMaximised = false;
    ArrayAdapter<String> _arrayAdapter;

    List<Integer> lastTab;//list pour stocker le tab d'avant
    boolean isAlreadyBack=false;

    ListView listview;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        createAppFolder();

        searchActivity = new SearchActivity();
        playlistActivity = new PlaylistActivity();

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);

        musicPlayer = MusicPlayer.getInstance();




        if (getIntent().hasExtra("isMusicPlaying")) {
            String a = getIntent().getExtras().getString("isMusicPlaying");
            if (a.equals("true")) {
                musicPosition=-1;
                setMusicMode(true);
            } else {
                setMusicMode(false);
            }
        }


        initialiseSeekBar();
        updateSeekbar();
        setButtonOnTouchListener();
        displayAllMusic();

        lastTab = new ArrayList<Integer>();
        lastTab.add(-1);
        playlistClick(findViewById(R.id.btnPlaylist));
        //par défaut page sur la musique
       // maximiseClick(findViewById(R.id.btnMaximise));

    }
    @Override
    public void onBackPressed()
    {

        if(isAlreadyBack)
        {
            super.onBackPressed();
        }else {


            switch (lastTab.get(lastTab.size() - 2)) {
                case 0:
                    searchClick(findViewById(R.id.btnSearch));
                    isAlreadyBack = true;
                    break;
                case 1:
                    playlistClick(findViewById(R.id.btnPlaylist));
                    isAlreadyBack = true;
                    break;
                case 2:
                    explorerClick(findViewById(R.id.btnExplorer));
                    isAlreadyBack = true;
                    break;
                case 3:
                    maximiseClick(findViewById(R.id.btnMaximise));
                    isAlreadyBack = true;
                    break;
                case -1:
                    super.onBackPressed();
                    break;
                default:
                    super.onBackPressed();
                    break;
            }
        }

    }
public void setMusicMode(boolean isTrue)
{
    final SeekBar musicBar = (SeekBar) findViewById(R.id.musicBar);

    if(isTrue)
    {
        musicBar.setMax(MusicPlayer.getMusicDuration());
        findViewById(R.id.btnPlay).setBackgroundResource(R.drawable.pause);//affiche l'icone play
        TextView title = (TextView) findViewById(R.id.musicTitle);//affiche l'icone play
        title.setText(MusicPlayer.getMusicName());
        isPlayPressed = true;
    }else
    {
        findViewById(R.id.btnPlay).setBackgroundResource(R.drawable.play);//affiche l'icone play
        isPlayPressed = false;
    }

}
public void setButtonOnTouchListener() {
}

public void displayAllMusic()
{
        //Récupère et affiche la liste des musiques


    listmp3 = playlistActivity.getListMusic(MUSIC_PATH);

    ArrayList<String> listMusicNames = new ArrayList<String>();

    listview = (ListView)findViewById(R.id.listMusic);
    listview.setChoiceMode(ListView.CHOICE_MODE_SINGLE);

    for(String music:listmp3)
    {
        String[] s = music.split("/");
        String[] st = s[s.length-1].split("\\.");

        listMusicNames.add(st[0]);
    }
    // Set Array-Adapter
    _arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, listMusicNames);
    listview.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE);

    listview.setAdapter(_arrayAdapter);


    listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view,int position, long id) {

            musicPosition = position;
            musicPlayer.playMusic(listmp3.get(position));
            setMusicMode(true);
        }
    });
}
    /* ajout d'items
    listmp3.add("your string");;
private void changeBackgroundColor(int index)
    {

        for (int i = 0; i < listview.getChildCount(); i++) {
            //listview.getChildAt(i).setBackgroundColor(Color.WHITE);
            listItems.get(i).setBackgroundColor(Color.TRANSPARENT);
        }
        listItems.get(index).setBackgroundColor(getResources().getColor(R.color.colorBlue,null));
    }*/


    private void updateSeekbar()
    {

        final SeekBar musicBar=(SeekBar) findViewById(R.id.musicBar);
        MainActivity.this.runOnUiThread(new Runnable() {

            @Override
            public void run() {
                if(!musicPlayer.getMusicSource().equals("")){
                    String separator=":";
                    int mCurrentPosition = musicPlayer.mediaPlayer.getCurrentPosition();
                    int duration = mCurrentPosition;

                    musicBar.setProgress(mCurrentPosition);
                    long minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                    duration -= TimeUnit.MINUTES.toMillis(minutes);
                    long seconds = TimeUnit.MILLISECONDS.toSeconds(duration);
                    EditText actualTime = (EditText)findViewById(R.id.actualTimeText);
                    EditText fullTime = (EditText)findViewById(R.id.fullTimeText);
                    if(seconds<10)
                    {
                        separator=":0";
                    }
                    actualTime.setText(minutes+separator+seconds);

                    duration = musicPlayer.mediaPlayer.getDuration();

                    minutes = TimeUnit.MILLISECONDS.toMinutes(duration);
                    duration -= TimeUnit.MINUTES.toMillis(minutes);
                    seconds = TimeUnit.MILLISECONDS.toSeconds(duration);

                    if(seconds<10)
                    {
                        separator=":0";
                    }else
                    {
                        separator=":";
                    }
                    fullTime.setText(minutes+separator+seconds);

                }
                mHandler.postDelayed(this, 250);
            }
        });
    }


    private void createAppFolder()
    {
        File folder = new File(MUSIFY_PATH);
        boolean success = true;
        if (!folder.exists()) {
            success = folder.mkdirs();
        }
        if (success) {
            File musicFolder = new File(MUSIC_PATH);
            musicFolder.mkdirs();
        } else {
            //check for permission
            if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED){
                //ask for permission
                requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
            }
        }
    }

    private SeekBar volumeSeekbar;
    private AudioManager audioManager;
    /**
     * To initialize the seek bar
     */
    private void initialiseSeekBar() {

        final SeekBar musicBar=(SeekBar) findViewById(R.id.musicBar);
        //music bar
        musicBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                if(isPlayPressed)
                {
                    musicPlayer.startMusicAtTime(musicBar.getProgress());
                }else
                {
                    musicPlayer.setMusicTime(musicBar.getProgress());
                }
            }
        });

        //Volume bar

        volumeSeekbar = (SeekBar)findViewById(R.id.volumeBar);
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        volumeSeekbar.setMax(audioManager
                .getStreamMaxVolume(AudioManager.STREAM_MUSIC));
        volumeSeekbar.setProgress(audioManager
                .getStreamVolume(AudioManager.STREAM_MUSIC));


        volumeSeekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener()
        {
            @Override
            public void onStopTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onStartTrackingTouch(SeekBar arg0)
            {
            }

            @Override
            public void onProgressChanged(SeekBar arg0, int progress, boolean arg2)
            {
                audioManager.setStreamVolume(AudioManager.STREAM_MUSIC,
                        progress, 0);
            }
        });
    }
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_VOLUME_UP)
        {

            int index = volumeSeekbar.getProgress();
            volumeSeekbar.setProgress(index + 1);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_VOLUME_DOWN)
        {
            int index = volumeSeekbar.getProgress();
            volumeSeekbar.setProgress(index - 1);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
    public void getScreenSize()
    {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        int height = displayMetrics.heightPixels;
    }
/*
        public boolean onTouchEvent(MotionEvent touchevent) {
            switch (touchevent.getAction()) {

                case MotionEvent.ACTION_DOWN:
                    lastX = touchevent.getX();
                    break;
                case MotionEvent.ACTION_UP:
                    float currentX = touchevent.getX();

                    // Handling left to right screen swap.
                    if (lastX < currentX) {

                        // If there aren't any other children, just break.
                        if (viewFlipper.getDisplayedChild() == 0)
                            break;

                        // Next screen comes in from left.
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_left);
                        // Current screen goes out from right.
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_right);

                        // Display next screen.
                        viewFlipper.showNext();
                    }

                    // Handling right to left screen swap.
                    if (lastX > currentX) {

                        // If there is a child (to the left), kust break.
                        if (viewFlipper.getDisplayedChild() == 1)
                            break;

                        // Next screen comes in from right.
                        viewFlipper.setInAnimation(this, R.anim.slide_in_from_right);
                        // Current screen goes out from left.
                        viewFlipper.setOutAnimation(this, R.anim.slide_out_to_left);

                        // Display previous screen.
                        viewFlipper.showPrevious();
                    }
                    break;
            }
            return false;
        }
*/

    public void searchClick(View view)
    {
        View playlist = findViewById(R.id.btnPlaylist);
        View explorer = findViewById(R.id.btnExplorer);

        //Récupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.VISIBLE);
        settings.setVisibility(View.GONE);

        view.setBackgroundResource(R.drawable.searchblue);
        playlist.setBackgroundResource(R.drawable.playlist);
        explorer.setBackgroundResource(R.drawable.folder);

        minimiseClick(view);

        viewFlipper.setDisplayedChild(1);
        InputListener();
        lastTab.add(0);
        isAlreadyBack=false;

    }
    //Recherche les musique apres l'envoi et cache le clavier lors du changement de focus
    public void InputListener()
    {
        editText= (EditText) findViewById(R.id.input_search);//Récupère l'EditText

        //Réagit aux actions de l'input
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener()
        {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event)
            {
                if(actionId == EditorInfo.IME_ACTION_DONE)//Si l'on clique sur envoyer (du clavier)
                {
                    search(editText.getText().toString());//Rechercher les musiques
                    return true; // consume.
                }
                return false; // pass on to other listeners.
            }
        });
        //Lors du changement de focus
        editText.setOnFocusChangeListener(new View.OnFocusChangeListener()
        {
            @Override
            public void onFocusChange(View v, boolean hasFocus)
            {
                //Si le focus est sur l'input
                if(hasFocus)
                {
                    //Faire la recherche
                    search(editText.getText().toString());
                }else
                {
                    //Cacher le clavier
                    hideKeyboard(editText);
                }
            }
        });
    }
    //Cache le clavier
    private void hideKeyboard(EditText editText)
    {
        InputMethodManager imm= (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    //Recherche si des musiques contiennent le mot clé
    public void search(String word)
    {
        searchActivity.search(word);
        TextView edit = (TextView) findViewById(R.id.contentText);
        edit.setText(word);
    }

    public void playlistClick(View view)
    {
        //Récupère les boutons de navigations
        View explorer = findViewById(R.id.btnExplorer);
        View search = findViewById(R.id.btnSearch);

        //Change la couleur des icones
        view.setBackgroundResource(R.drawable.playlistblue);
        explorer.setBackgroundResource(R.drawable.folder);
        search.setBackgroundResource(R.drawable.search);

        //Ràcupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);

        minimiseClick(view);

        //Efface le contenu de l'input
        ((EditText)input_search).setText("");
        lastTab.add(1);

        viewFlipper.setDisplayedChild(2);
        isAlreadyBack=false;

    }

    public void explorerClick(View view)
    {
        //Récupère tout les boutons
        View playlist = findViewById(R.id.btnPlaylist);
        View search = findViewById(R.id.btnSearch);

        //Change leur couleur
        view.setBackgroundResource(R.drawable.folderblue);//Définit l'icone actuelle en bleu
        playlist.setBackgroundResource(R.drawable.playlist);//Met en noir
        search.setBackgroundResource(R.drawable.search);//met en noir

        //Ràcupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);

        minimiseClick(view);

        //Efface le contenu de l'input
        ((EditText)input_search).setText("");
/*
        View v = LayoutInflater.from(getApplication()).inflate(R.layout.activity_explorer, null);
        LinearLayout linearLayout = (LinearLayout)v.findViewById(R.id.itemsLayout);*/
        lastTab.add(2);

        viewFlipper.setDisplayedChild(3);
        isAlreadyBack=false;

    }

    public void openExplorer(View view)
    {
        myIntent = new Intent(MainActivity.this, ListFileActivity.class);
        startActivityForResult(myIntent, REQUEST_MUSIC_PATH);
    }

    /*Media player button handling*/
    public void shuffleClick(View view)
    {
        if(isShufflePressed)//Si le bouton est déjà cliqué
        {
            view.setBackgroundResource(R.drawable.random);//met en noir
            isShufflePressed=false;

        }else
        {
            view.setBackgroundResource(R.drawable.randomblue);//met en blue (Selectionné)
            isShufflePressed=true;

        }
    }
    public void replayClick(View view)
    {
        if(isReplayPressed)//Si le bouton est déjà cliqué
        {
            view.setBackgroundResource(R.drawable.replay);//met en noir
            musicPlayer.mediaPlayer.setLooping(false);
            isReplayPressed=false;

        }else
        {
            view.setBackgroundResource(R.drawable.replayblue);//met en blue (Selectionné)
            musicPlayer.mediaPlayer.setLooping(true);
            isReplayPressed=true;

        }

    }
    public void nextClick(View view)
    {

        if(musicPosition<listmp3.size()-1&&musicPosition!=-1)
        {
            musicPosition++;

            musicPlayer.playMusic(listmp3.get(musicPosition));
            setMusicMode(true);

            //listview.performItemClick(listview.getChildAt(musicPosition), musicPosition, listview.getItemIdAtPosition(musicPosition));//Simule un click pour activer la method onItemClick pour changer la couleur du background
        }


    }
    public void previousClick(View view)
    {
        if(musicPosition>0)
        {
            //joue lA MUSIQUE PRéCédante
            musicPosition--;
            //listview.performItemClick(listview.getChildAt(musicPosition), musicPosition, listview.getItemIdAtPosition(musicPosition));//Simule un click pour activer la method onItemClick pour changer la couleur du background
            musicPlayer.playMusic(listmp3.get(musicPosition));

        }else
        {
            //Recommence la musique
            musicPlayer.startMusicAtTime(0);

        }
        setMusicMode(true);
    }
    public void maximiseClick(View view)
    {
        //Cache le bouton actuel et affiche l'autre
        view.setVisibility(View.GONE);
        View btnMinimise = findViewById(R.id.btnMinimise);
        View btnPlay = findViewById(R.id.btnPlay);
        View btnPrevious = findViewById(R.id.btnPrevious);
        View btnNext = findViewById(R.id.btnNext);
        View btnShuffle = findViewById(R.id.btnShuffle);
        View btnReplay = findViewById(R.id.btnReplay);
        View volumeBar = findViewById(R.id.volumeBar);


        //Récupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);

        btnShuffle.setVisibility(View.VISIBLE);
        btnReplay.setVisibility(btnMinimise.VISIBLE);


        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
        fadeIn.setDuration(1000);

        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
        //fadeOut.setStartOffset(200); //Se lance en décalé
        fadeOut.setDuration(200);

        view.startAnimation(fadeOut);


        int distanceX = -360;
        int distanceY = -60;

        btnPlay.animate().translationX(distanceX).start();
        btnPrevious.animate().translationX(distanceX).start();
        btnNext.animate().translationX(distanceX).start();
        btnPlay.animate().translationY(distanceY).start();
        btnPrevious.animate().translationY(distanceY).start();
        btnNext.animate().translationY(distanceY).start();


        resizeButton(60,40);


        btnReplay.startAnimation(fadeIn);
        btnShuffle.startAnimation(fadeIn);
        volumeBar.startAnimation(fadeIn);
        volumeBar.setVisibility(View.VISIBLE);

        viewFlipper.setDisplayedChild(0);

        View playlist = findViewById(R.id.btnPlaylist);
        View explorer = findViewById(R.id.btnExplorer);
        View search = findViewById(R.id.btnSearch);

        search.setBackgroundResource(R.drawable.search);
        playlist.setBackgroundResource(R.drawable.playlist);
        explorer.setBackgroundResource(R.drawable.folder);
        isMaximised=true;
        lastTab.add(3);
        isAlreadyBack=false;

    }

    public void resizeButton(int sizeOne, int sizeTwo)
{

    View btnPlay = findViewById(R.id.btnPlay);
    View btnPrevious = findViewById(R.id.btnPrevious);
    View btnNext = findViewById(R.id.btnNext);

    //redimensionner les boutons
    final float scale = getResources().getDisplayMetrics().density;
    int heightDp = (int) (sizeOne * scale + 0.5f);
    ViewGroup.LayoutParams params = btnPlay.getLayoutParams();
    params.height = heightDp;
    params.width = heightDp;
    btnPlay.setLayoutParams(params);
    btnPlay.requestLayout();

    heightDp = (int) (sizeTwo * scale + 0.5f);
    params = btnPrevious.getLayoutParams();
    params.width = heightDp;
    params.height = heightDp;
    btnPrevious.setLayoutParams(params);
    btnPrevious.requestLayout();

    params = btnNext.getLayoutParams();
    params.width = heightDp;
    params.height = heightDp;

    btnNext.setLayoutParams(params);
    btnNext.requestLayout();
}
    public void minimiseClick(View view)
    {
        if(isMaximised) {
            View btnMaximise = findViewById(R.id.btnMaximise);
            View btnPlay = findViewById(R.id.btnPlay);
            View btnPrevious = findViewById(R.id.btnPrevious);
            View btnNext = findViewById(R.id.btnNext);
            View btnShuffle = findViewById(R.id.btnShuffle);
            View btnReplay = findViewById(R.id.btnReplay);
            View volumeBar = findViewById(R.id.volumeBar);


            Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator()); //add this
            fadeIn.setDuration(1000);

            Animation fadeOut = new AlphaAnimation(1, 0);
            fadeOut.setInterpolator(new AccelerateInterpolator()); //and this
            //fadeOut.setStartOffset(200); //Se lance en décalé
            fadeOut.setDuration(200);


            btnMaximise.startAnimation(fadeIn);
            btnMaximise.setVisibility(View.VISIBLE);

            btnShuffle.startAnimation(fadeOut);
            btnReplay.startAnimation(fadeOut);
            volumeBar.startAnimation(fadeOut);

            volumeBar.setVisibility(View.GONE);
            btnShuffle.setVisibility(View.GONE);
            btnReplay.setVisibility(View.GONE);


            int distanceX = 10;
            int distanceY = 10;

            btnPlay.animate().translationX(distanceX).start();
            btnPrevious.animate().translationX(distanceX).start();
            btnNext.animate().translationX(distanceX).start();
            btnPlay.animate().translationY(distanceY).start();
            btnPrevious.animate().translationY(distanceY).start();
            btnNext.animate().translationY(distanceY).start();

            resizeButton(70,50);

            isMaximised=false;
        }
    }

    public void playClick(View view)
    {

        if(isPlayPressed)//Si le bouton est déjà cliqué
        {
            view.setBackgroundResource(R.drawable.play);//met en noir
            musicPlayer.pauseMusic();
            isPlayPressed=false;

        }else
        {
            if(!musicPlayer.getMusicSource().equals("")) {
                view.setBackgroundResource(R.drawable.pause);//affiche l'icone play
                musicPlayer.resumeMusic();
                isPlayPressed = true;
            }
        }

    }



}
