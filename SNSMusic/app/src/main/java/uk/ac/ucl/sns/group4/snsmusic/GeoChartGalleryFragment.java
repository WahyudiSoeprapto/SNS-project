package uk.ac.ucl.sns.group4.snsmusic;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import android.support.v4.app.FragmentManager;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.ViewFlipper;

import java.util.ArrayList;

import java.util.Random;

import uk.ac.ucl.sns.group4.snsmusic.fetch.AppLocationService;
import uk.ac.ucl.sns.group4.snsmusic.fetch.DownloadImage;
import uk.ac.ucl.sns.group4.snsmusic.fetch.GeoChartTrack;
import uk.ac.ucl.sns.group4.snsmusic.fetch.GeoMetro;
import uk.ac.ucl.sns.group4.snsmusic.fetch.LocationMetro;
import uk.ac.ucl.sns.group4.snsmusic.fetch.TrackYoutube;
import uk.ac.ucl.sns.group4.snsmusic.model.Metro;
import uk.ac.ucl.sns.group4.snsmusic.model.Track;

/**
 * Fragment to hold GeoChartTrack data
 * Displayed as Pager Fragment
 * Created by andi on 20/12/2014.
 */

public class GeoChartGalleryFragment extends Fragment {
    GridView mGridView;
    Spinner countrySpinner,metroSpinner;
    TextView retryTextView;
    ArrayList<Track> mTracks;
    ArrayList<Metro> mMetro;
    int page =1;
    boolean isLoading = false;
    DownloadImage downloadImage;
    String location = "Bristol";
    String country = "United Kingdom";
    int countryInitial,locationInitial;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);

        new LocationTask().execute();



        // Prepare handler thread to download image data

        downloadImage = new DownloadImage(new Handler());
        downloadImage.start();
        downloadImage.getLooper();


}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);



        // Setup Track Gallery
        mGridView = (GridView)v.findViewById(R.id.gridView);
        mGridView.setEmptyView(v.findViewById(R.id.gallery_loading_viewFlipper));

        retryTextView = (TextView) v.findViewById(R.id.retry_textView);
        retryTextView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!isLoading) {
                    mTracks = null;
                    new SearchTask().execute(country, location, page + "");
                    new MetroTask().execute();
                }
            }
        });


        setupGridAdapter();


        // If track click (not yet implemented) - andi
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> gridView, View view, int pos,
                                    long id) {
            Track track = mTracks.get(pos);
            Bitmap bitmap = null;
            if (track.getImageUrl() == null) {
                bitmap = downloadImage.getBitmap(track.getArtistName());
            } else {
                bitmap = downloadImage.getBitmap(track.getImageUrl());
            }


            FragmentManager fm = getActivity()
                    .getSupportFragmentManager();

            TrackFragment trackDialog = TrackFragment
                    .newInstance(track, bitmap);
            trackDialog.show(fm,TrackFragment.TRACK_ITEM);








            }
        });


        countrySpinner = (Spinner) v.findViewById(R.id.gallery_detail1_spinner);
        metroSpinner = (Spinner) v.findViewById(R.id.gallery_detail2_spinner);


        setupCountrySpinner();

        return v;
    }


    // shutdown thread
    @Override
    public void onDestroy() {
        super.onDestroy();
        downloadImage.quit();

    }


    // shutdown thread
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        downloadImage.clearQueue();
    }



    // track gallery adapter
    void setupGridAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mTracks != null) {

            // create adapter
            mGridView.setAdapter(new GridAdapter(mTracks));
            if (mGridView.getLastVisiblePosition() > 0){
                for (int i = mGridView.getLastVisiblePosition() + 1 ; i < mGridView.getLastVisiblePosition() + 11; i++ ){
                    cacheNextImage(i);
                }

            }



            // to get more data if user already reach end of chart
            mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int currentFirstVisibleItem;
                private int currentVisibleItemCount;
                private int currentScrollState;
                private int currentTotalItemCount;

                @Override
                public void onScrollStateChanged(AbsListView view, int scrollState) {
                    this.currentScrollState = scrollState;
                    this.isScrollCompleted();
                }

                @Override
                public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
                    this.currentFirstVisibleItem = firstVisibleItem;
                    this.currentVisibleItemCount = visibleItemCount;
                    this.currentTotalItemCount = totalItemCount;


                }

                private void isScrollCompleted() {
                    // check if user scroll until the end and if adapter finished loading download next page
                    if (this.currentVisibleItemCount > 0 && (this.currentFirstVisibleItem+this.currentVisibleItemCount) > (this.currentTotalItemCount -1) && this.currentScrollState == SCROLL_STATE_IDLE) {

                        if(!isLoading){
                            page ++;

                            new SearchTask().execute(country,location,page+"");

                        }
                    }

                    int startCacheItem = this.currentFirstVisibleItem - 10;
                    if (startCacheItem < 0){
                        startCacheItem = 0;
                    }

                    int endCacheItem = this.currentFirstVisibleItem + this.currentVisibleItemCount + 10;
                    if (endCacheItem > this.currentTotalItemCount){
                        endCacheItem = this.currentTotalItemCount;
                    }

                    for (int i = startCacheItem ; i < this.currentFirstVisibleItem; i++ ){
                        cacheNextImage(i);
                    }

                    for (int i = this.currentFirstVisibleItem+this.currentVisibleItemCount ; i < endCacheItem; i++ ){
                        cacheNextImage(i);
                    }




                }

            });

        } else {
            mGridView.setAdapter(null);
        }
    }


    void cacheNextImage(int i){
        if (mTracks.get(i).getImageUrl() == null){
            downloadImage.cacheImage(mTracks.get(i).getArtistName());
        } else {
            downloadImage.cacheImage(mTracks.get(i).getImageUrl());
        }

    }


    // spinner to select country
    void setupCountrySpinner() {
        if (getActivity() == null || countrySpinner == null | metroSpinner == null) return;

        if (mMetro != null ) {
            if (mMetro.size() ==0)
            {
                return;
            }

            //get country array from Metro object
            ArrayList<String> countries = new ArrayList<String>();
            for (int i=0;i<mMetro.size();i++){
                countries.add(mMetro.get(i).getCountry());
            }

            // add adapter
            countrySpinner.setAdapter(new SpinnerAdapter(countries));

            countryInitial = countries.indexOf(country);
            locationInitial = mMetro.get(countryInitial).getLocation().indexOf(location);
            countrySpinner.setSelection(countries.indexOf(country));

            // if country selected choose first metro and refresh gallery
            countrySpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {


                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if (!(countryInitial == position)) {
                        countryInitial = position;
                        setupMetroSpinner(position,0);
                        country = mMetro.get(position).getCountry();
                        location = mMetro.get(position).getLocation().get(0);
                        page = 1;
                        mTracks = null;
                        setupGridAdapter();
                        new SearchTask().execute(country, location, page + "");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });


            setupMetroSpinner(countryInitial,locationInitial);

        }

        else {
            countrySpinner.setAdapter(null);
        }


    }

    // spinner to select metro or location
    void setupMetroSpinner(final int countryPos, int metroPos){
        if (getActivity() == null || countrySpinner == null | metroSpinner == null) return;

        if (mMetro != null) {
            // add adapter for city selection
            metroSpinner.setAdapter(new SpinnerAdapter(mMetro.get(countryPos).getLocation()));
            metroSpinner.setSelection(metroPos);
            metroSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {

                // if metro selected refresh gallery
                @Override
                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                    if(!(isLoading || locationInitial == position)){
                        locationInitial = position;
                        location = mMetro.get(countrySpinner.getSelectedItemPosition()).getLocation().get(position);
                        page = 1;
                        mTracks = null;
                        setupGridAdapter();
                        new SearchTask().execute(country,location,page + "");
                    }
                }

                @Override
                public void onNothingSelected(AdapterView<?> parent) {

                }
            });

        }

        else {
            metroSpinner.setAdapter(null);
        }
    }




    // AsyncTask to query GeoChart data
    private class SearchTask extends AsyncTask<String,Void,ArrayList<Track>> {
        @Override
        protected ArrayList<Track> doInBackground(String... params) {
            isLoading = true;
            int page = Integer.valueOf(params[2]);
            String country = params [0];
            String location = params [1];
            return new GeoChartTrack().getChart(country,location,page);

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            if (retryTextView != null) {
                retryTextView.setVisibility(View.GONE);
            }
            if (countrySpinner !=null){
                countrySpinner.setEnabled(false);
                metroSpinner.setEnabled(false);
            }
        }

        @Override
        protected void onPostExecute(ArrayList<Track> items){
            isLoading = false;
            if (mTracks == null){
                mTracks = items;
                setupGridAdapter();
                if (items.size() == 0){
                    retryTextView.setVisibility(View.VISIBLE);
                }
            }
            else if (page == 1) {
                mTracks = items;
                ((GridAdapter) mGridView.getAdapter()).notifyDataSetChanged();
                if (items.size() == 0){
                    retryTextView.setVisibility(View.VISIBLE);
                }
            }
            else {
                mTracks.addAll(items);
                ((GridAdapter)mGridView.getAdapter()).notifyDataSetChanged();
            }
            if (countrySpinner !=null){
                countrySpinner.setEnabled(true);
                metroSpinner.setEnabled(true);
            }

        }
    }

    // AsyncTask to query Metro data
    private class MetroTask extends AsyncTask<String,Void,ArrayList<Metro>> {
        @Override
        protected ArrayList<Metro> doInBackground(String... params) {
            return new GeoMetro().getMetro();

        }

        @Override
        protected void onPostExecute(ArrayList<Metro> items){
            mMetro = items;
            setupCountrySpinner();
            ((MainActivity) getActivity()).Loading(true);
        }
    }

    private class LocationTask extends AsyncTask<String,Void,ArrayList<String>> {
        Location gpsLocation;
        @Override
        protected void onPreExecute() {
            AppLocationService appLocationService = new AppLocationService(getActivity().getBaseContext());
            gpsLocation = appLocationService.getLocation(LocationManager.GPS_PROVIDER);
        }

        @Override
        protected ArrayList<String> doInBackground(String... params) {
            ArrayList<String> gpsLocationMetro = new ArrayList<String>();

            if (gpsLocation != null) {
                double latitude = gpsLocation.getLatitude();
                double longitude = gpsLocation.getLongitude();
                gpsLocationMetro = ((LocationMetro) new LocationMetro()).getAddressFromLocation(latitude,longitude,getActivity().getBaseContext());
            }

            return gpsLocationMetro;
        }



        @Override
        protected void onPostExecute(ArrayList<String> strings) {
            if (strings.size()>0){
                country = strings.get(0);
                location = strings.get(1);
            }
            // download initial data track for London, United Kingdom and all available Metro
            new MetroTask().execute();
            new SearchTask().execute(country,location,page + "");



        }


    }



    // Adapter to be used by Chart Gallery
    private class GridAdapter extends ArrayAdapter<Track> {
        public GridAdapter(ArrayList<Track> items) {
            super(getActivity(), 0, items);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.gallery_item, parent, false);
            }
            Track track = getItem(position);

            ImageView imageView = (ImageView)convertView
                    .findViewById(R.id.item_imageView);
            imageView.setImageResource(R.drawable.placeholder);
            if (track.getImageUrl() == null) {
                imageView.setTag(track.getArtistName());
            } else {
                imageView.setTag(track.getImageUrl());
            }
            downloadImage.showImage(imageView);
            TextView detail1TextView = (TextView)convertView.findViewById(R.id.detail_1_textView);
            detail1TextView.setText(track.getTrackName());
            ImageView detail1ImageView = (ImageView)convertView
                    .findViewById(R.id.icon_detail_1_imageView);
            detail1ImageView.setImageResource(R.drawable.icon_list);
            TextView detail2TextView = (TextView)convertView.findViewById(R.id.detail_2_textView);
            detail2TextView.setText(track.getArtistName());
            ImageView detail2ImageView = (ImageView)convertView
                    .findViewById(R.id.icon_detail_2_imageView);
            detail2ImageView.setImageResource(R.drawable.icon_artist);
            TextView numberTextView = (TextView)convertView.findViewById(R.id.item_number_textView);
            numberTextView.setText((position+1)+"");
            ViewFlipper viewFlipper = (ViewFlipper) convertView.findViewById(R.id.item_preview_viewFlipper);
            Random mRandom = new Random();
            viewFlipper.setDisplayedChild(mRandom.nextInt(2));

            return convertView;
        }
    }

    // Adapter to be used by Country and City selection

    private class SpinnerAdapter extends ArrayAdapter<String> {
            private boolean isCountry = false;

        public SpinnerAdapter(ArrayList <String> items )  {
            super(getActivity(), 0, items);
            if (items.get(0).equals("-country")){
                isCountry = true;
            }

        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.spinner_list, parent, false);
            }
            String item = getItem(position);
            item = allGeo(item);
            TextView itemTextView = (TextView)convertView.findViewById(R.id.spinner_textView);
            itemTextView.setText(item);
            return convertView;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.spinner_list, parent, false);
            }
            String item = getItem(position);
            item = allGeo(item);
            TextView itemTextView = (TextView)convertView.findViewById(R.id.spinner_textView);
            itemTextView.setBackgroundResource(R.color.purple);
            itemTextView.setTextColor(getActivity().getResources().getColor(android.R.color.white));
            itemTextView.setText(item);
            ImageView itemImageView = (ImageView) convertView.findViewById(R.id.spinner_imageView);
            if (isCountry) {
                itemImageView.setImageResource(R.drawable.ic_country);
            } else {
                itemImageView.setImageResource(R.drawable.ic_city);
            }
            return convertView;
        }

        String allGeo(String item){
            if (item.equals("-city")) {
                item = (String) getText(R.string.all_cities);
            }
            if (item.equals("-country")) {
                item = (String) getText(R.string.all_countries);
            }
            return item;
        }
    }




}
