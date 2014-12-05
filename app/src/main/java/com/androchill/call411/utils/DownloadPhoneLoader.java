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

public class DownloadPhoneLoader extends AsyncTaskLoader<Phone> {

    public static final String GET_PHONE_URL = "http://intense.io:8000/api/v1/getPhone/";
    private boolean dataIsReady = false;
    private Phone data = null;
    private String model_number;

    public DownloadPhoneLoader(Context context, String model_number) {
        super(context);
        this.model_number = model_number;
    }

    @Override
    public Phone loadInBackground() {
        Log.d("Download phones", "Downloading started");
        DefaultHttpClient client = new DefaultHttpClient();
        HttpGet httpGet = new HttpGet(GET_PHONE_URL + model_number);
        try {
            HttpResponse execute = client.execute(httpGet);
            InputStream content = execute.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String s;
            StringBuilder sb = new StringBuilder();
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            Log.d("Download phones", "Downloading complete");
            data = PhoneJSONParser.parse(sb.toString());
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
    public void deliverResult(Phone results) {
        if(isReset()) {
            data = null;
            dataIsReady = false;
            return;
        }
        super.deliverResult(results);
    }
}
