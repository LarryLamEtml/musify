package musify.lamho.musify;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.Window;
import android.widget.EditText;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        getWindow().requestFeature(Window.FEATURE_ACTION_BAR);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().hide();
        playlistClick(findViewById(R.id.btnPlaylist));
    }
    public void searchClick(View view)
    {
        View playlist = findViewById(R.id.btnPlaylist);
        View explorer = findViewById(R.id.btnExplorer);
       // final EditText edittext = (EditText)view;
        //Récupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.VISIBLE);
        settings.setVisibility(View.GONE);

        view.setBackgroundResource(R.drawable.searchblue);
        playlist.setBackgroundResource(R.drawable.playlist);
        explorer.setBackgroundResource(R.drawable.folder);

        //Récupère les contenus
        View content_explorer = findViewById(R.id.content_explorer);
        View content_playlist = findViewById(R.id.content_playlist);
        View content_search = findViewById(R.id.content_search);

        //Affiche le contenu correspondant et cache tout les autres
        content_explorer.setVisibility(View.GONE);
        content_playlist.setVisibility(View.GONE);
        content_search.setVisibility(View.VISIBLE);
    }
    public void playlistClick(View view)
    {
        View explorer = findViewById(R.id.btnExplorer);
        View search = findViewById(R.id.btnSearch);

        view.setBackgroundResource(R.drawable.playlistblue);
        explorer.setBackgroundResource(R.drawable.folder);
        search.setBackgroundResource(R.drawable.search);


        //Ràcupère l'input et le bouton paramètre
        View input_search = findViewById(R.id.input_search);
        View settings = findViewById(R.id.btnParameter);

        //affiche l'input et cache le bouton des paramètres
        input_search.setVisibility(View.GONE);
        settings.setVisibility(View.VISIBLE);

        //Récupère les contenus
        View content_explorer = findViewById(R.id.content_explorer);
        View content_playlist = findViewById(R.id.content_playlist);
        View content_search = findViewById(R.id.content_search);

        //Affiche le contenu correspondant et cache tout les autres
        content_explorer.setVisibility(View.GONE);
        content_playlist.setVisibility(View.VISIBLE);
        content_search.setVisibility(View.GONE);

        //Efface le contenu de l'input
        ((EditText)input_search).setText("");

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

        //Récupère les contenus
        View content_explorer = findViewById(R.id.content_explorer);
        View content_playlist = findViewById(R.id.content_playlist);
        View content_search = findViewById(R.id.content_search);

        //Affiche le contenu correspondant et cache tout les autres
        content_explorer.setVisibility(View.VISIBLE);
        content_playlist.setVisibility(View.GONE);
        content_search.setVisibility(View.GONE);

        //Efface le contenu de l'input
        ((EditText)input_search).setText("");
    }

    public void search(View view)
    {
        String research = ((EditText)view).getText().toString();
    }
}
