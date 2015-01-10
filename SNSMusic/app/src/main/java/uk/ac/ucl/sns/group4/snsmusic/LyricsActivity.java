package uk.ac.ucl.sns.group4.snsmusic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.ucl.sns.group4.snsmusic.fetch.LyricsParser;
import uk.ac.ucl.sns.group4.snsmusic.model.Track;


/**
 * Created by oljas on 05/01/15.
 */
public class LyricsActivity extends Activity {
    public static String TRACK_OBJECT = "uk.ac.ucl.sns.group4.snsmusic.LyricsMainActivity.TRACK_OBJECT";
    public static String TRACK_BITMAP = "uk.ac.ucl.sns.group4.snsmusic.LyricsMainActivity.TRACK_BITMAP";
    private Track track;
    private ImageView musicImage,backImage;
    private TextView artist;
    private TextView title;
    private TextView songLyrics;
    private TextView link;
    private String lyricsBody = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        //getting a  Track object from intent
        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra(LyricsActivity.TRACK_OBJECT);
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra(LyricsActivity.TRACK_BITMAP);
        // Initializing UI objects
        musicImage = (ImageView) findViewById(R.id.lyrics_image);
        artist = (TextView) findViewById(R.id.lyrics_artist);
        title = (TextView) findViewById(R.id.lyrics_songname);
        songLyrics = (TextView) findViewById(R.id.lyrics_song_content);
        //set hyperlink to www.azlyrics.com
        link = (TextView) findViewById(R.id.hyperlink);
        link.setMovementMethod(LinkMovementMethod.getInstance());

        //setting the image of album or artist, artist name, song name  and songLyrics
        musicImage.setImageBitmap(bitmap);
        if(track != null) {
            artist.setText(track.getArtistName());
            title.setText(track.getTrackName());
        }
        // geting the value from Bundle
        if(savedInstanceState != null) {
            lyricsBody = savedInstanceState.getString("LYRICS");
        }
        //Starts MyTask task in order to get lyrics
        if(lyricsBody != null) {
            songLyrics.setText(lyricsBody);
        } else {
            new MyTask().execute();
        }

        //new code --> ASK ABZAL
        backImage = (ImageView) findViewById(R.id.lyrics_back_Image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }


    class MyTask extends AsyncTask<Void, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(Void... params) {
            String lyrics = "Error";
            if (track != null) {
                lyrics = LyricsParser.getLyricsOfSong(track.getArtistName() + " " + track.getTrackName());
            }

            return lyrics;
        }
        //post lyrics
        @Override
        protected void onPostExecute(String lyrics) {
            super.onPostExecute(lyrics);
            lyricsBody = lyrics;
            songLyrics.setText(lyrics);
        }
    }
    //saving lyricsBody content to Bundle
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(lyricsBody != null) {
            outState.putString("LYRICS", lyricsBody);
        }
    }
}
