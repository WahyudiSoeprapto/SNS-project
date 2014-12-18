package uk.ac.ucl.sns.group4.snsmusic;

import android.content.Context;

import java.util.ArrayList;

/**
 * Created by andi on 18/12/2014.
 */
public class DataGenerator {

    private ArrayList<Artist> mArtist;

    private static DataGenerator sDataGenerator;
    private Context mAppContext;

    private DataGenerator (Context appContext) {
        mAppContext = appContext;
        mArtist = new ArrayList<Artist>();
        Artist a = new Artist();
        a.setName("Cher");
        a.setMbid("bfcc6d75-a6a5-4bc6-8282-47aec8531818");
        mArtist.add(a);
        Artist b = new Artist();
        b.setName("Cheryl Cole");
        b.setMbid("0e418756-bc62-40be-9dac-396ff77eaedb");
        mArtist.add(b);
        Artist c = new Artist();
        c.setName("Cher b");
        c.setMbid("bfcc6d75-a6a5-4bc6-8282-47aec8531818");
        mArtist.add(c);
        Artist d = new Artist();
        d.setName("Cheryl Cole b");
        d.setMbid("0e418756-bc62-40be-9dac-396ff77eaedb");
        mArtist.add(d);



    }

    public static DataGenerator get(Context c) {
        if (sDataGenerator == null) {
            sDataGenerator = new DataGenerator(c.getApplicationContext());
        }
        return sDataGenerator;
    }

    public ArrayList<Artist> getArtist() {
        return mArtist;
    }



}
