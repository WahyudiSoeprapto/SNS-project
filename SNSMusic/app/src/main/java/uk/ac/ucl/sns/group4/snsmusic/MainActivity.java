package uk.ac.ucl.sns.group4.snsmusic;

import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;

import com.example.android.common.view.SlidingTabLayout;

/* Main Activity
 *
 * for containing multiple fragment of SNS Music apps
 * right now only Chart Fragment finished
 *
 */


public class MainActivity extends FragmentActivity  {

    SectionsPagerAdapter mSectionsPagerAdapter;
    SlidingTabLayout mSlidingTabLayout;

    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //getActionBar().setDisplayShowHomeEnabled(false);

        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getSupportFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);
        mViewPager.setOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int i, float v, int i2) {

            }

            @Override
            public void onPageSelected(int i) {

            }

            @Override
            public void onPageScrollStateChanged(int i) {

            }
        });

        mSlidingTabLayout = (SlidingTabLayout) findViewById(R.id.sliding_tabs);
        //mSlidingTabLayout.setCustomTabView(R.layout.custom_tab_title, R.id.tabtext);
        mSlidingTabLayout.setViewPager(mViewPager);
        //PagerTabStrip tabStrip = (PagerTabStrip) findViewById(R.id.pager_tab_strip);
        //tabStrip.setTextSize(TypedValue.COMPLEX_UNIT_DIP,20);

        mSlidingTabLayout.setCustomTabColorizer(new SlidingTabLayout.TabColorizer() {

            @Override
            public int getIndicatorColor(int position) {
                return getResources().getColor(android.R.color.white);
            }

            @Override
            public int getDividerColor(int position) {
                return getResources().getColor(android.R.color.white);
            }
        });


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP)
            getWindow().setStatusBarColor(getResources().getColor(R.color.purple));

    }

    // Pager Fragment in Main Activity created by Android Studio

    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            // getItem is called to instantiate the fragment for the given page.
            // Return a PlaceholderFragment (defined as a static inner class below).
           Fragment fragment = null;

            switch (position) {
                case 0:
                    fragment = new GeoChartGalleryFragment();
                    break;
                case 1:
                    fragment = new GeoEventFragment();
                    break;
            }


            return fragment;
        }



        @Override
        public int getCount() {
            // Show 1 total pages.
            return 2;
        }

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position) {
                case 0:
                    return getString(R.string.tab_chart);
                case 1:
                    return getString(R.string.tab_event);

            }
            return null;
        }
    }
}
