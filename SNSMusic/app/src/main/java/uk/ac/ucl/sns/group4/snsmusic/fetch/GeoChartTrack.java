package uk.ac.ucl.sns.group4.snsmusic.fetch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ucl.sns.group4.snsmusic.model.Track;

/**
 *  To parse JSON Data of Geo Track query from Last FM
 *
 *  Originally part of ParsingGeoMetroTrackChart by Wahyudi
 *  but separated for further reuse - Andi
 *
 *
 */

// Prepare data container
public class GeoChartTrack {
    ArrayList<Track> mItems;
    Track item = null;

    public ArrayList<Track> getChart (String country, String location, int page) {
        mItems = new ArrayList<Track>();
        // URL for fetch GeoChartTrack Data
        String fetchUrl =
                "http://ws.audioscrobbler.com/2.0/?method=geo.getmetrotrackchart&country="+country.replace(" ","+")+"&metro="+location.replace(" ","+");
        if (country.equals("-country")){
               fetchUrl = "http://ws.audioscrobbler.com/2.0/?method=chart.gettoptracks";
        } else if (location.equals("-city")){
               fetchUrl =  "http://ws.audioscrobbler.com/2.0/?method=geo.gettoptracks&country="+country.replace(" ","+");;
        }
        // Fetch and Parse data to Track object
        try{
            JSONObject responseObj = new JSONObject(new DownloadData().getUrl(fetchUrl, 72, page));
            JSONObject topTracksObj;
            if (country.equals("-country")){
                topTracksObj = responseObj.getJSONObject("tracks");
            } else {
                topTracksObj = responseObj.getJSONObject("toptracks");
            }

            JSONArray tracks = topTracksObj.getJSONArray("track");
            for (int i = 0; i < tracks.length(); i++) {
                    item = new Track();
                    JSONObject track = tracks.getJSONObject(i);
                    item.setTrackName(track.getString("name"));
                    item.setTrackUrl(track.getString("url"));
                    JSONObject artistObj = track.getJSONObject("artist");
                    item.setArtistName(artistObj.getString("name"));
                    item.setArtistUrl(artistObj.getString("url"));
                    // choose maximum extra large image return null if no image data (no image will be handled by Image Downloader
                    try {
                        JSONArray imageUrls = track.getJSONArray("image");
                        for (int j = 0; j < imageUrls.length(); j++) {
                            JSONObject imageObj = imageUrls.getJSONObject(j);
                            item.setImageUrl(imageObj.getString("#text"));
                            if (imageObj.getString("size").equals("extralarge")) {
                                break;
                            }
                        }
                    } catch (Exception e) {}
                    mItems.add(item);
                 }
            }  catch (JSONException e) {
                    Log.e("SNS","Fail to parse GeoChartTrack "+location,e);
            }  catch (IOException e) {
                    Log.e("SNS", "Fail to fetch GeoChartTrack " + location, e);}

            return mItems;
        }

}
