package com.androchill.call411.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.androchill.call411.R;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.util.List;

public class DownloadAllPhonesLoader extends AsyncTaskLoader<List<Phone>> {

    //public static final String ALL_PHONES_URL = "http://intense.io:8000/api/v1/allPhones";
    private boolean dataIsReady = false;
    private List<Phone> data = null;

    public DownloadAllPhonesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Phone> loadInBackground() {
        Log.d("Download phones", "Downloading started");
        //HttpURLConnection urlConnection = null;
        try {
//            URL url = new URL(ALL_PHONES_URL);
//            urlConnection = (HttpURLConnection) url.openConnection();
//            urlConnection.setUseCaches(true);
//            InputStream in = new BufferedInputStream(urlConnection.getInputStream());

            Resources res = getContext().getResources();
            InputStream in = res.openRawResource(R.raw.allphones);

            BufferedReader br = new BufferedReader(new InputStreamReader(in));
            String s;
            StringBuilder sb = new StringBuilder();
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            Log.d("Download phones", "Downloading complete");
            data = PhoneJSONParser.parseArray(sb.toString());
            dataIsReady = true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return data;
    }

    @Override
    protected void onStartLoading() {
        if(dataIsReady) {
            deliverResult(data);
        } else {
            forceLoad();
        }
    }

    @Override
    public void deliverResult(List<Phone> results) {
        if(isReset()) {
            if(results != null) results.clear();
            data = null;
            dataIsReady = false;
            return;
        }
        super.deliverResult(results);
    }
}
