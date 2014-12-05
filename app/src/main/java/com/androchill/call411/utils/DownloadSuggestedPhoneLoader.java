package com.androchill.call411.utils;

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.util.Log;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Collections;
import java.util.List;

public class DownloadSuggestedPhoneLoader extends AsyncTaskLoader<List<Phone>> {

    public static final String GET_PHONE_URL = "http://intense.io:8000/api/v1/similarPhones/";
    private boolean dataIsReady = false;
    private List<Phone> data = null;
    private Phone template;

    public DownloadSuggestedPhoneLoader(Context context, Phone template) {
        super(context);
        this.template = template;
    }

    @Override
    public List<Phone> loadInBackground() {
        Log.d("Download phones", "Downloading started");
        DefaultHttpClient client = new DefaultHttpClient();
        HttpPost httpPost = new HttpPost(GET_PHONE_URL);
        try {
            JSONObject postdata = new JSONObject();
            int ram = template.getRam();
            if(ram > 0) {
                JSONArray ramRange = new JSONArray();
                ramRange.put(0, (int) (ram * 0.5 + 1));
                ramRange.put(1, (int) (ram * 1.5));
                postdata.put("ram", ramRange);
            }
            double screen_size = template.getScreenSize();
            if(screen_size > 0) {
                JSONArray sizeRange = new JSONArray();
                sizeRange.put(0, screen_size - 0.5);
                sizeRange.put(1, screen_size + 0.5);
                postdata.put("screen_size", sizeRange);
            }
            int price = template.getPrice();
            if(price > 0) {
                JSONArray priceRange = new JSONArray();
                priceRange.put(0, price - 300);
                priceRange.put(1, price + 300);
                postdata.put("price", priceRange);
            }
            StringEntity entity = new StringEntity(postdata.toString(), HTTP.UTF_8);
            entity.setContentType("application/json");
            httpPost.setEntity(entity);
            HttpResponse execute = client.execute(httpPost);
            InputStream content = execute.getEntity().getContent();
            BufferedReader br = new BufferedReader(new InputStreamReader(content));
            String s;
            StringBuilder sb = new StringBuilder();
            while((s = br.readLine()) != null) {
                sb.append(s);
            }
            Log.d("Download phones", "Downloading complete");
            data = PhoneJSONParser.parseArray(sb.toString());
            Collections.shuffle(data);
            dataIsReady = true;
            return data;
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
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
            data = null;
            dataIsReady = false;
            return;
        }
        super.deliverResult(results);
    }
}
