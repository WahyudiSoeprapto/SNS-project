package uk.ac.ucl.sns.group4.snsmusic;

import android.os.Build;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.PagerTabStrip;
import android.support.v4.view.ViewPager;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

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

        if (savedInstanceState != null) {
            RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash_screen);
            splash.setVisibility(View.GONE);
        }


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
        mSlidingTabLayout.setViewPager(mViewPager);
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



    public void Loading(Boolean loaded){
        if (loaded) {
            final RelativeLayout splash = (RelativeLayout) findViewById(R.id.splash_screen);
            new CountDownTimer(2000, 2000) {

                public void onTick(long millisUntilFinished) {
                }

                public void onFinish() {
                    splash.setVisibility(View.GONE);
                }

            }.start();


        }
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
