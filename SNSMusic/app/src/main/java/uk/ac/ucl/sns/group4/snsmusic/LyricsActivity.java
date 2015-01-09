package uk.ac.ucl.sns.group4.snsmusic;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import uk.ac.ucl.sns.group4.snsmusic.fetch.LyricsParser;
import uk.ac.ucl.sns.group4.snsmusic.model.Track;


/**
 * Created by oljas on 01/01/15.
 */
public class LyricsActivity extends Activity {
    public static String TRACK_OBJECT = "uk.ac.ucl.sns.group4.snsmusic.LyricsMainActivity.TRACK_OBJECT";
    private Track track;
    private ImageView musicImage,backImage;
    private TextView artist;
    private TextView title;
    private TextView songLyrics;
    private String lyricsBody = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_lyrics);

        Intent intent = getIntent();
        track = (Track) intent.getSerializableExtra(LyricsActivity.TRACK_OBJECT);
        Bitmap bitmap = (Bitmap) intent.getParcelableExtra("AA");
        musicImage = (ImageView) findViewById(R.id.lyrics_image);
        artist = (TextView) findViewById(R.id.lyrics_artist);
        title = (TextView) findViewById(R.id.lyrics_songname);
        songLyrics = (TextView) findViewById(R.id.lyrics_song_content);
        musicImage.setImageBitmap(bitmap);
        if(track != null) {
            artist.setText(track.getArtistName());
            title.setText(track.getTrackName());
        }
        if(savedInstanceState != null) {
            lyricsBody = savedInstanceState.getString("LYRICS");

        }
        if(lyricsBody != null) {
            songLyrics.setText(lyricsBody);
        } else {
            new MyTask().execute();
        }
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
                lyrics = LyricsParser.getLinkOfSong(track.getArtistName() + " " + track.getTrackName());
            }

            return lyrics;
        }

        @Override
        protected void onPostExecute(String lyrics) {
            super.onPostExecute(lyrics);
            lyricsBody = lyrics;
            songLyrics.setText(lyrics);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        if(lyricsBody != null) {
            outState.putString("LYRICS", lyricsBody);
        }
    }
}
