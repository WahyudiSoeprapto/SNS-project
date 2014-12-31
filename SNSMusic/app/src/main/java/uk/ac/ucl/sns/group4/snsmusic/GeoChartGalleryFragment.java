package uk.ac.ucl.sns.group4.snsmusic;

import android.app.SearchManager;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;

import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.SearchView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.ViewFlipper;

import org.apache.commons.logging.Log;

import java.util.ArrayList;

import java.util.Random;

import static uk.ac.ucl.sns.group4.snsmusic.R.color.purple;

/**
 * Fragment to hold GeoChartTrack data
 * Displayed as Pager Fragment
 * Created by andi on 20/12/2014.
 */

public class GeoChartGalleryFragment extends Fragment {
    GridView mGridView;
    Spinner countrySpinner,metroSpinner;
    ArrayList<Track> mItems;
    ArrayList<Metro> mMetro;
    int page =1;
    boolean isLoading = false;
    ImageDownloader imageThread;
    String location = "London";
    String country = "United Kingdom";
    int countryInitial,locationInitial;


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
        //setHasOptionsMenu(true);


        // download initial data track for London, United Kingdom and all available Metro
        new SearchTask().execute(country,location,page + "");
        new MetroTask().execute();

        // Prepare handler thread to download image data
        imageThread = new ImageDownloader(new Handler());
        imageThread.setListener(new ImageDownloader.Listener<ImageView>() {
            public void onThumbnailDownloaded(ImageView imageView, Bitmap eventImage) {
                if (isVisible()) {
                    imageView.setImageBitmap(eventImage);
                }
            }
        });
        imageThread.start();
        imageThread.getLooper();



}

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_gallery, container, false);



        // Setup Track Gallery
        mGridView = (GridView)v.findViewById(R.id.gridView);
        mGridView.setEmptyView(v.findViewById(R.id.event_preview_viewFlipper));
        setupAdapter();
        ViewFlipper viewFlipper = (ViewFlipper) v.findViewById(R.id.event_preview_viewFlipper);
        viewFlipper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mItems = null;
                new SearchTask().execute(country,location,page + "");
                new MetroTask().execute();
            }
        });

        // If track click (not yet implemented) - andi
        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> gridView, View view, int pos,
                                    long id) {
                Track item = mItems.get(pos);
                try {
                Intent intent = new Intent(Intent.ACTION_SEARCH);
                intent.setPackage("com.google.android.youtube");
                intent.putExtra("query", item.getArtistName()+" "+item.getTrackName());
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                } catch (ActivityNotFoundException e){
                    String url = "https://www.youtube.com/results?search_query="+item.getArtistName().replace(" ","+")+"+"+item.getTrackName().replace(" ","+");
                    Intent intent = new Intent(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);

                }


            }
        });


        countrySpinner = (Spinner) v.findViewById(R.id.gallery_detail1_spinner);
        metroSpinner = (Spinner) v.findViewById(R.id.gallery_detail2_spinner);


        setupCountrySpinner();

        return v;
    }

    @Override
    public void onResume() {
        super.onResume();

    }

    @Override
    public void onStart() {
        super.onStart();
    }

    // shutdown thread
    @Override
    public void onDestroy() {
        super.onDestroy();
        imageThread.quit();

    }


    // shutdown thread
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        imageThread.clearQueue();
    }



    // track gallery adapter
    void setupAdapter() {
        if (getActivity() == null || mGridView == null) return;

        if (mItems != null) {

            // create adapter
            mGridView.setAdapter(new ItemAdapter(mItems));



            // to get more data if user already reach end of chart
            mGridView.setOnScrollListener(new AbsListView.OnScrollListener() {
                private int lastFirstVisibleItem = 0;
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





                }

            });

        } else {
            mGridView.setAdapter(null);
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
                    if (!(isLoading || countryInitial == position)) {
                        countryInitial = position;
                        setupMetroSpinner(position,0);
                        country = mMetro.get(position).getCountry();
                        location = mMetro.get(position).getLocation().get(0);
                        page = 1;
                        mItems = null;
                        setupAdapter();
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
                        mItems = null;
                        setupAdapter();
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
        protected void onPostExecute(ArrayList<Track> items){
            isLoading = false;
            if (mItems == null){
                mItems = items;
                setupAdapter();
            }
            else if (page == 1) {
                mItems = items;
                ((ItemAdapter) mGridView.getAdapter()).notifyDataSetChanged();
            }
            else {
                mItems.addAll(items);
                ((ItemAdapter)mGridView.getAdapter()).notifyDataSetChanged();
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
        }
    }





    // Adapter to be used by Chart Gallery
    private class ItemAdapter extends ArrayAdapter<Track> {
        public ItemAdapter(ArrayList<Track> items) {
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
                imageThread.queueImage(imageView, track.getArtistName());
            } else {
                imageThread.queueImage(imageView, track.getImageUrl());
            }
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

        public SpinnerAdapter(ArrayList <String> items )  {
            super(getActivity(), 0, items);
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getActivity().getLayoutInflater()
                        .inflate(R.layout.spinner_list, parent, false);
            }
            String item = getItem(position);
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
            TextView itemTextView = (TextView)convertView.findViewById(R.id.spinner_textView);
            itemTextView.setBackgroundResource(R.color.purple);
            itemTextView.setTextColor(getActivity().getResources().getColor(android.R.color.white));
            itemTextView.setText(item);
            return convertView;
        }
    }




}
