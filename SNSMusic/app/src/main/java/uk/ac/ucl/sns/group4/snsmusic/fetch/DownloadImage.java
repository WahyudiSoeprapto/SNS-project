package uk.ac.ucl.sns.group4.snsmusic.fetch;


import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import uk.ac.ucl.sns.group4.snsmusic.R;

/**
 * Created by andi on 07/01/2015.
 */
public class DownloadImage extends HandlerThread {
    Handler downloadHandler;
    Handler showHandler;
    private static final int SHOW_IMAGE = 1;
    private static final int CACHE_IMAGE = 0;
    private LruCache<String,Bitmap> mCache;



    public DownloadImage(Handler handler) {
        super("Album Image");
        showHandler = handler;
        mCache = new LruCache<String,Bitmap>(20 * 1024 * 1024){
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

        };
    }

    @Override
    protected void onLooperPrepared() {
        downloadHandler = new Handler(){

            @Override
            public void handleMessage(Message message) {
                if (message.what == SHOW_IMAGE){
                    final ImageView imageView = (ImageView)message.obj;
                    final Bitmap bitmap = getImage((String)imageView.getTag());
                    if (bitmap == null){
                        return;
                    }

                    boolean post = showHandler.post(new Runnable() {
                        public void run() {
                            Animation animation = AnimationUtils.loadAnimation(imageView.getContext(), R.anim.abc_fade_in);
                            imageView.setImageBitmap(bitmap);
                            imageView.startAnimation(animation);
                        }
                    });
                } else if (message.what == CACHE_IMAGE){
                    final Bitmap bitmap = getImage((String) message.obj);
                }
            }
        };
    }


    public void showImage(ImageView imageView){
        downloadHandler.obtainMessage(SHOW_IMAGE,imageView).sendToTarget();
        Log.i("id", "get"+imageView.getTag());
    }

    public void cacheImage(String url){
        downloadHandler.obtainMessage(CACHE_IMAGE,url).sendToTarget();
        Log.i("id", "cache"+url);
    }


    private Bitmap getImage(String url){
        Bitmap bitmap = null;
        try {
             String urlFetch = url;

            if (urlFetch == null)
                return bitmap;

            if (mCache.get(urlFetch) != null) {
                bitmap = mCache.get(urlFetch);
                Log.i("id", "Bitmap cached");
            } else {
                if (!urlFetch.contains("http:")) {
                    try {
                        JSONObject reFetchObj = new JSONObject(new DownloadData().getUrl("http://ws.audioscrobbler.com/2.0/?method=artist.getinfo&artist=" + url.replace(" ", "%20")));
                        JSONObject reArtistObj = reFetchObj.getJSONObject("artist");
                        JSONArray imageUrls = reArtistObj.getJSONArray("image");
                        for (int j = 0; j < imageUrls.length(); j++) {
                            JSONObject imageObj = imageUrls.getJSONObject(j);
                            urlFetch = imageObj.getString("#text");
                            if (imageObj.getString("size").equals("extralarge")) {
                                break;
                            }
                        }
                    } catch (JSONException ee) {
                    } catch (IOException ee) {
                    }

                }
                if (urlFetch == null){
                    bitmap = BitmapFactory.decodeResource(Resources.getSystem(), R.drawable.placeholder);
                } else {
                    byte[] bitmapBytes = new DownloadData().getUrlBytes(urlFetch);
                    bitmap = BitmapFactory
                            .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                }

                if (bitmap == null) {
                    return bitmap;
                }

                Log.i("id", "Bitmap created"+url);
                mCache.put(url, bitmap);
            }

        } catch (IOException ioe) {
            Log.e("id", "Error downloading image", ioe);
        }
        return bitmap;
    }

    public void clearQueue() {
        downloadHandler.removeMessages(SHOW_IMAGE);
        downloadHandler.removeMessages(CACHE_IMAGE);

    }

    public Bitmap getBitmap (String url) {
        return mCache.get(url);
    }



}
