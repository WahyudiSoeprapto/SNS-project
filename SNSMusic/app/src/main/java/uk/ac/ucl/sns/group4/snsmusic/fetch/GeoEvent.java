package uk.ac.ucl.sns.group4.snsmusic.fetch;

import android.support.v4.app.Fragment;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;

import uk.ac.ucl.sns.group4.snsmusic.model.Event;

/**
 * To parse JSON Data of Geo Event query from Last FM
 * Created by wahyudi on 03/01/2014.
 */
public class GeoEvent {


    ArrayList<Event> mItems;
    Event item = null;

    public ArrayList<Event> getEvent (double longitude,double latitude, int page) {
        mItems = new ArrayList<Event>();
        // URL for fetch GeoEvent Data
        String fetchUrl =
                "http://ws.audioscrobbler.com/2.0/?method=geo.getevents&long="+longitude+"&latt="+latitude;
        // Fetch and Parse data to Event object
        try{
            JSONObject responseObj = new JSONObject(new DownloadData().getUrl(fetchUrl, 20, page));
            JSONObject eventsObj = responseObj.getJSONObject("events");
            JSONArray events = eventsObj.getJSONArray("event");
            for (int i = 0; i < events.length(); i++) {
                item = new Event();
                JSONObject event = events.getJSONObject(i);
                item.setEventTitle(event.getString("title"));
                item.setEventUrl(event.getString("url"));
                item.setEventWebsite(event.getString("website"));
                item.setEventDateAndTime(event.getString("startDate"));
                item.setEventDescription(event.getString("description"));
                // choose maximum extra large image return null if no image data (no image will be handled by Image Downloader
                try {
                    JSONArray imageEvents = event.getJSONArray("image");
                    for (int j = 0; j < imageEvents.length(); j++) {
                        JSONObject imageEventObj = imageEvents.getJSONObject(j);
                        item.setEventImage(imageEventObj.getString("#text"));
                        if (imageEventObj.getString("size").equals("extralarge")) {
                            break;
                        }
                    }
                } catch (Exception e) {}
                JSONObject artistsObj = event.getJSONObject("artists");
                item.setMainArtist(artistsObj.getString("headliner"));
                JSONObject venueObj = event.getJSONObject("venue");
                item.setVenueName(venueObj.getString("name"));
                JSONObject locationObj = venueObj.getJSONObject("location");
                JSONObject geoPointLocationObj = locationObj.getJSONObject("geo:point");
                item.setVenueLongitude(geoPointLocationObj.getString("geo:lat"));
                item.setVenueLatitude(geoPointLocationObj.getString("geo:long"));
                item.setVenueCity(locationObj.getString("city"));
                item.setVenueCountry(locationObj.getString("country"));
                item.setVenueStreet(locationObj.getString("street"));
                item.setVenuePostCode(locationObj.getString("postalcode"));
                item.setVenueUrl(venueObj.getString("url"));
                item.setVenueWebsite(venueObj.getString("website"));
                item.setVenuePhone(venueObj.getString("phonenumber"));
                // choose maximum extra large image return null if no image data (no image will be handled by Image Downloader
                try {
                    JSONArray imageLocations = venueObj.getJSONArray("image");
                    for (int j = 0; j < imageLocations.length(); j++) {
                        JSONObject imageLocationObj = imageLocations.getJSONObject(j);
                        item.setVenueImage(imageLocationObj.getString("#text"));
                        if (imageLocationObj.getString("size").equals("extralarge")) {
                            break;
                        }
                    }
                } catch (Exception e) {}
                mItems.add(item);
            }
        }  catch (JSONException e) {
            Log.e("SNS", "Fail to parse GeoEvent " + longitude+","+latitude, e);
        }  catch (IOException e) {
            Log.e("SNS", "Fail to fetch GeoEvent " + longitude+","+latitude, e);}

        return mItems;
    }
}


