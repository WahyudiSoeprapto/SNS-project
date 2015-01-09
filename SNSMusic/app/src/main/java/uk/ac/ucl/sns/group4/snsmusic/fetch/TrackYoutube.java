package uk.ac.ucl.sns.group4.snsmusic.fetch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by andi on 06/01/2015.
 */
public class TrackYoutube {
    String videoId = "";


    public String getID(String queryTrack){
        // URL for fetch TrackYoutube Data
        String url = "https://www.googleapis.com/youtube/v3/search?part=snippet&type=video&key=AIzaSyATIgvNMH-ruHBdCLZNp2UdL5hhpfy7nSE&q="+queryTrack;

        // Fetch and Parse data to TrackYoutube object
        try{
            JSONObject searchResult = new JSONObject(new String(new DownloadData().getUrlBytes(url)));
            JSONArray videos = searchResult.getJSONArray("items");
            JSONObject videoTrack = videos.getJSONObject(0);
            JSONObject videoTrackId = videoTrack.getJSONObject("id");
            videoId = videoTrackId.getString("videoId");

        }  catch (JSONException e) {
            Log.e("SNS", "Fail to parse TrackYoutube " + queryTrack, e);
        }  catch (IOException e) {
            Log.e("SNS", "Fail to fetch TrackYoutube " + queryTrack, e);}

        return videoId;
    }





}
