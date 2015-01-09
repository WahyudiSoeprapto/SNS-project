package uk.ac.ucl.sns.group4.snsmusic;


import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;

import android.support.v4.app.DialogFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;


import java.io.Serializable;


import uk.ac.ucl.sns.group4.snsmusic.fetch.TrackYoutube;
import uk.ac.ucl.sns.group4.snsmusic.model.Track;

/**
 * Created by andi on 08/01/2015.
 */
public class TrackFragment extends DialogFragment {
    public static final String TRACK_ITEM = "uk.ac.ucl.sns.group4.snsmusic.TRACK";
    public static final String TRACK_IMAGE = "uk.ac.ucl.sns.group4.snsmusic.TRACK_IMAGE";
    private Track track;
    private Bitmap bitmap;


    public static TrackFragment newInstance(Track track,Bitmap bitmap) {
        Bundle arguments = new Bundle();
        arguments.putSerializable(TRACK_ITEM, track);
        arguments.putParcelable(TRACK_IMAGE, bitmap);

        TrackFragment fragment = new TrackFragment();
        fragment.setArguments(arguments);

        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        track = (Track) getArguments().getSerializable(TRACK_ITEM);
        bitmap = (Bitmap) getArguments().getParcelable(TRACK_IMAGE);
   }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_track, container, false);
        getDialog().getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        ImageView imageView = (ImageView) v.findViewById(R.id.track_imageView);
        if (bitmap != null){
            imageView.setImageBitmap(bitmap);
        }
        LinearLayout youtubeLayout = (LinearLayout)v.findViewById(R.id.track_youtube_Layout);
        youtubeLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new YoutubeTask().execute(track.getArtistName().replace(" ","+")+"+"+track.getTrackName().replace(" ","+"));
            }
        });

        ImageView youtubeImage = (ImageView) v.findViewById(R.id.track_youtube_Image);
        youtubeImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new YoutubeTask().execute(track.getArtistName().replace(" ","+")+"+"+track.getTrackName().replace(" ","+"));
            }
        });

        TextView youtubeText = (TextView) v.findViewById(R.id.track_youtube_Text);
        youtubeText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new YoutubeTask().execute(track.getArtistName().replace(" ","+")+"+"+track.getTrackName().replace(" ","+"));
            }
        });


        LinearLayout lyricsLayout = (LinearLayout)v.findViewById(R.id.track_lyrics_Layout);
        lyricsLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLyrics();
            }
        });

        ImageView lyricsImage = (ImageView) v.findViewById(R.id.track_lyrics_Image);
        lyricsImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLyrics();

            }
        });

        TextView lyricsText = (TextView) v.findViewById(R.id.track_lyrics_Text);
        lyricsText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getLyrics();
            }
        });

        ImageView backImage = (ImageView) v.findViewById(R.id.track_back_Image);
        backImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dismiss();
            }
        });

        return v;
    }


    // AsyncTask to query Track Youtube data
    private class YoutubeTask extends AsyncTask<String,Void,String> {
        @Override
        protected String doInBackground(String... params) {
            return new TrackYoutube().getID(params[0]);

        }

        @Override
        protected void onPostExecute(String videoId){
            try {
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoId));
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("VIDEO_ID", videoId);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);

            } catch (ActivityNotFoundException e){
                String url = "https://www.youtube.com/watch?v="+videoId;
                Intent intent = new Intent(Intent.ACTION_VIEW);
                intent.setData(Uri.parse(url));
                startActivity(intent);

            }
            getDialog().dismiss();
        }
    }


    private void getLyrics() {
        Intent intent = new Intent();
        intent.setClass(getActivity(), LyricsActivity.class);
        intent.putExtra("AA", bitmap);
        intent.putExtra(LyricsActivity.TRACK_OBJECT, track);
        startActivity(intent);
        getDialog().dismiss();
    }

}
