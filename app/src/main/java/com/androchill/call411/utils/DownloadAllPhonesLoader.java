package com.androchill.call411.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;

public class DownloadAllPhonesLoader extends AsyncTaskLoader<List<Phone>> {

    public static final String ALL_PHONES_URL = "http://intense.io:8000/api/v1/allPhones";
    private boolean dataIsReady = false;
    private List<Phone> data = null;

    public DownloadAllPhonesLoader(Context context) {
        super(context);
    }

    @Override
    public List<Phone> loadInBackground() {
        Log.d("Download phones", "Downloading started");
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(ALL_PHONES_URL);
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String s = "";
            String response = "";
            while((s = br.readLine()) != null) {
                response += s;
            }
            Log.d("Download phones", "Downloading complete");
            data = PhoneJSONParser.parseArray(response);
            dataIsReady = true;
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
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
