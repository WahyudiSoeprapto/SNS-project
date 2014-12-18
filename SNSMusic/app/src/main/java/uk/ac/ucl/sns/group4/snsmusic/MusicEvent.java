package uk.ac.ucl.sns.group4.snsmusic;

import android.support.v4.app.FragmentManager;
import android.support.v4.app.ListFragment;
import android.support.v7.app.ActionBarActivity;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;


public class MusicEvent extends ActionBarActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_music_event);

        FragmentManager fm = getSupportFragmentManager();
        Fragment fragment_detail = fm.findFragmentById(R.id.container_detail);
        Fragment fragment_list = fm.findFragmentById(R.id.container_list);
        if (fragment_detail == null) {
            fm.beginTransaction()
                    .add(R.id.container_detail, new PlaceholderFragmentDetail())
                    .commit();

        }
        if (fragment_list == null){
            fm.beginTransaction()
                    .add(R.id.container_list, new PlaceholderFragmentList())
                    .commit();
        }
    }

    /**
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_music_event, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    **/

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragmentDetail extends Fragment {

        public PlaceholderFragmentDetail() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_music_detail, container, false);
            TextView name = (TextView) rootView.findViewById(R.id.detail_title_text);
            name.setText("Artist name:'cher'");
            return rootView;
        }
    }
    public static class PlaceholderFragmentList extends ListFragment {
        private ArrayList<Artist> mArtist;

        public PlaceholderFragmentList() {
        }


        public void onCreate(Bundle savedInstanceState) {
            super.onCreate(savedInstanceState);
            mArtist = DataGenerator.get(getActivity()).getArtist();
            ArtisAdapter adapter=new ArtisAdapter(mArtist);
            setListAdapter(adapter);

        }

        @Override public void onActivityCreated(Bundle savedInstanceState) {
            super.onActivityCreated(savedInstanceState);
            getListView().setDivider(null);

        }
        private class ArtisAdapter extends ArrayAdapter<Artist> {
            public ArtisAdapter(ArrayList<Artist> mArtist) {
                super(getActivity(), android.R.layout.simple_list_item_1, mArtist);
            }


            @Override
            public View getView(int position, View convertView, ViewGroup container) {
                if (null == convertView) {
                    convertView = getActivity().getLayoutInflater().inflate(R.layout.fragment_music_list, null);
                }

                Artist a = mArtist.get(position);
                TextView name = (TextView) convertView.findViewById(R.id.list_title_text);
                name.setText(a.getName());
                TextView mbid = (TextView) convertView.findViewById(R.id.list_subtitle_text);
                mbid.setText(a.getMbid());

                return convertView;
            }

        }

    }
}
