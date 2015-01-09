package uk.ac.ucl.sns.group4.snsmusic;


import android.location.Location;
import android.location.LocationManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;

import uk.ac.ucl.sns.group4.snsmusic.fetch.AppLocationService;
import uk.ac.ucl.sns.group4.snsmusic.fetch.GeoEvent;
import uk.ac.ucl.sns.group4.snsmusic.fetch.LocationMetro;
import uk.ac.ucl.sns.group4.snsmusic.model.Event;


/**
 * Created by andi on 29/12/2014.
 */
public class GeoEventFragment extends Fragment {
    ListView mListView;
    ArrayList<Event> mEvents;
    double longitude = -0.135;
    double latitude = 51.544;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        new EventTask().execute();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_map, container, false);
        mListView = (ListView) v.findViewById(R.id.event_listView);




        return v;
    }


    void setupListAdapter() {
        if (getActivity() == null || mListView == null) return;

        if (mEvents != null) {
            ArrayAdapter<Event> adapter =
                    new ArrayAdapter<Event>(getActivity(),
                            android.R.layout.simple_list_item_1,
                            mEvents);
            mListView.setAdapter(adapter);
        } else {
            mListView.setAdapter(null);
        }
    }


        private class EventTask extends AsyncTask<String,Void,ArrayList<Event>> {
        Location gpsLocation;

        @Override
        protected void onPreExecute() {
            AppLocationService appLocationService = new AppLocationService(getActivity().getBaseContext());
            gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        protected ArrayList<Event> doInBackground(String... params) {
            ArrayList<Event> items = null;

            if (gpsLocation != null) {
                latitude = gpsLocation.getLatitude();
                longitude = gpsLocation.getLongitude();
            }
            items = new GeoEvent().getEvent(longitude,latitude,1);

            return items;
        }


        @Override
        protected void onPostExecute(ArrayList<Event> items) {
            mEvents = items;
            setupListAdapter();

        }


    }

}
