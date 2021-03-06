package uk.ac.ucl.sns.group4.snsmusic.fetch;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;

import uk.ac.ucl.sns.group4.snsmusic.model.Metro;

/**
 * Query valid Country and Location on Last FM
 * Created by andi on 24/12/2014.
 */
public class GeoMetro {

    ArrayList<Metro> mItems;
    Metro item = null;
    String country ="";
    String oldCountry ="";
    String location ="";
    ArrayList<String> metros;


    public ArrayList<Metro> getMetro () {
        mItems = new ArrayList<Metro>();
        metros = new ArrayList<String>();

        // URL for fetch GeoChartTrack Data
        String fetchUrl =
                "http://ws.audioscrobbler.com/2.0/?method=geo.getmetros";
        // Fetch and Parse data to Metro object
        try{
            JSONObject responseObj = new JSONObject(new DownloadData().getUrl(fetchUrl));
            JSONObject metroObj = responseObj.getJSONObject("metros");
            JSONArray metro = metroObj.getJSONArray("metro");
            for (int i = 0; i < metro.length(); i++) {
                JSONObject locationObj = metro.getJSONObject(i);
                location = locationObj.getString("name");
                country = locationObj.getString("country");
                if (i == 0){
                    oldCountry = country;
                }

                if (country.equals(oldCountry)) {
                    metros.add(location);
                }
                else {
                    item = new Metro();
                    item.setCountry(oldCountry);
                    Collections.sort(metros);
                    metros.add(0,"-city");
                    item.setLocation(metros);
                    mItems.add(item);
                    oldCountry = country;
                    metros = new ArrayList<String>();
                    metros.add(location);
                }
                if (i == metro.length()-1){
                    item = new Metro();
                    item.setCountry(oldCountry);
                    Collections.sort(metros);
                    item.setLocation(metros);
                    mItems.add(item);
                }
            }
            item = new Metro();
            item.setCountry("-country");
            metros = new ArrayList<String>();
            metros.add("-city");
            item.setLocation(metros);
            mItems.add(0,item);
        }  catch (JSONException e) {
            Log.e("SNS", "Fail to parse GeoMetro " + location, e);
        }  catch (IOException e) {
            Log.e("SNS", "Fail to fetch GeoMetro " + location, e);}

        return mItems;
    }

}
