package uk.ac.ucl.sns.group4.snsmusic;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.support.v4.util.LruCache;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by andi on 21/12/2014.
 * Created using guidance from big nerd ranch
 *
 */
public class ImageDownloader<Token> extends HandlerThread {


    private static final String TAG = "ThumbnailDownloader";

    Handler mHandler;
    Map<Token, String> requestMap = Collections.synchronizedMap(new HashMap<Token, String>());
    private static final int MESSAGE_DOWNLOAD = 0;
    Handler mResponseHandler;
    Listener<Token> mListener;
    private LruCache<String,Bitmap> mCache;

    public ImageDownloader(Handler responseHandler) {
        super(TAG);
        mResponseHandler = responseHandler;
        mCache = new LruCache<String,Bitmap>(20 * 1024 * 1024){
            protected int sizeOf(String key, Bitmap value) {
                return value.getByteCount();
            }

        };

    }

    public interface Listener<Token> {
        void onThumbnailDownloaded(Token token, Bitmap thumbnail);
    }

    public void setListener(Listener<Token> listener) {
        mListener = listener;
    }


    @Override
    protected void onLooperPrepared() {
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                if (msg.what == MESSAGE_DOWNLOAD) {
                    @SuppressWarnings("unchecked")
                    Token token = (Token) msg.obj;
                    Log.i(TAG, "Got a request for url: " + requestMap.get(token));
                    handleRequest(token);
                }
            }

        };
    }

    public void queueImage(Token token, String url) {
        Log.i(TAG, "Got a URL: " + url);
        requestMap.put(token, url);

        mHandler
                .obtainMessage(MESSAGE_DOWNLOAD, token)
                .sendToTarget();
    }

    private void handleRequest(final Token token) {
        final Bitmap bitmap;
        try {
            final String url = requestMap.get(token);
            String urlFetch = url;


            if (urlFetch == null)
                return;

            if (mCache.get(urlFetch) != null) {
                bitmap = mCache.get(urlFetch);
                Log.i(TAG, "Bitmap cached");
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
                    bitmap = BitmapFactory.decodeResource(Resources.getSystem(),R.drawable.placeholder);
                } else {
                    byte[] bitmapBytes = new DownloadData().getUrlBytes(urlFetch);
                    bitmap = BitmapFactory
                            .decodeByteArray(bitmapBytes, 0, bitmapBytes.length);
                }

                Log.i(TAG, "Bitmap created"+url);
                if (bitmap == null) {
                    return;
                }


                mCache.put(url, bitmap);
            }
            mResponseHandler.post(new Runnable() {
                public void run() {
                    if (requestMap.get(token) != url)
                        return;

                    requestMap.remove(token);
                    mListener.onThumbnailDownloaded(token, bitmap);
                }
            });
        } catch (IOException ioe) {
            Log.e(TAG, "Error downloading image", ioe);
        }
    }

    public void clearQueue() {
        mHandler.removeMessages(MESSAGE_DOWNLOAD);
        requestMap.clear();
    }


}
