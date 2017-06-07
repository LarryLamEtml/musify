package musify.lamho.musify;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayout;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.io.File;
import java.util.ArrayList;

import static android.R.attr.duration;

public class MainActivity extends AppCompatActivity {

    private ViewFlipper viewFlipper;
    private float lastX;
    SearchActivity searchActivity;
    PlaylistActivity playlistActivity;

    EditText editText;
    ArrayList<String> listmp3;
    String path = "Phone\\musify\\Music";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();

        searchActivity = new SearchActivity();
        playlistActivity = new PlaylistActivity();

        listmp3=  playlistActivity.getListMusic(path);

        viewFlipper = (ViewFlipper) findViewById(R.id.viewFlipper);
        playlistClick(findViewById(R.id.btnPlaylist));
/*
        //Récupère et affiche la liste des musiques

        GridLayout layout =  (GridLayout)findViewById(R.id.content_search);
        listmp3 = new ArrayList<String>();
        listmp3 = getListMusic(path);
        for (String music:listmp3) {
            TextView textView = new TextView(this);
            textView.setText(music);
            layout.addView(textView);
        }

        */
    }
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

        viewFlipper.setDisplayedChild(0);
        InputListener();

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



        //Efface le contenu de l'input
        ((EditText)input_search).setText("");
        viewFlipper.setDisplayedChild(1);

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


        //Efface le contenu de l'input
        ((EditText)input_search).setText("");
        viewFlipper.setDisplayedChild(2);

        //setContentView(R.layout.activity_fileexplorer);

    }
    public void getfile(View view)
    {
        FileexplorerActivity fileExplorer = new FileexplorerActivity();
        fileExplorer.getfile(view);
    }

}
