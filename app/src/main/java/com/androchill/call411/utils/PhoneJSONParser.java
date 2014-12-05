package com.androchill.call411.utils;

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class PhoneJSONParser {

    public static List<Phone> parseArray(String result) {
        Log.d("JSON", result);
        ArrayList<Phone> phones = new ArrayList<Phone>();
        try {
            JSONArray phoneArray = new JSONArray(result);
            for(int i = 0; i < phoneArray.length(); i++) {
                JSONObject phoneObj = phoneArray.getJSONObject(i);
                Phone p = parse(phoneObj);
                if(p == null) throw new NullPointerException();
                phones.add(p);
            }
            return phones;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static Phone parse(String result) {
        try {
            return parse(new JSONObject(result));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    /*
    Parameter	Value
    model_number	The phone's model number
    ram	The phone's RAM capacity (MB)
    processor	The phone's CPU
    manufacturer	The phone's manufacturer
    system	The phone's operating system
    screen_size	The phone's screen size (inches)
    screen_resolution	The phone's screen resolution
    battery_capacity	The phone's battery capacity (mAh)
    talk_time	The phone's talk time (minutes)
    camera_megapixels	The phone's camera resolution (MP)
    price	The phone's price ($)
    weight	The phone's weight (oz)
    storage_options	The phone's storage options
    dimensions	The phone's dimensions
    carrier	The carriers this phone is compatible with
    network_frequencies	The network frequencies this phone supports
    image	URL to an image of the phone
     */
    public static Phone parse(JSONObject phoneObj) {
        PhoneBuilder builder = new PhoneBuilder();
        try {
            builder.setModelNumber(phoneObj.getString("model_number"));
            builder.setRam(phoneObj.optInt("ram", -1));
            builder.setProcessor(phoneObj.optString("processor", "No data available"));
            builder.setManufacturer(phoneObj.optString("manufacturer", "No data available"));
            builder.setSystem(phoneObj.optString("system", "No data available"));
            builder.setScreenSize(phoneObj.optDouble("screen_size", -1));
            builder.setScreenResolution(phoneObj.optString("screen_resolution", "No data available"));
            builder.setBatteryCapacity(phoneObj.optInt("battery_capacity", -1));
            builder.setTalkTime(phoneObj.optDouble("talk_time", -1));
            builder.setCameraMegapixels(phoneObj.optDouble("camera_megapixels", -1));
            builder.setPrice(phoneObj.optInt("price", -1));
            builder.setWeight(phoneObj.optDouble("weight", -1));
            builder.setStorageOptions(phoneObj.optString("storage_options", "No data available"));
            builder.setDimensions(phoneObj.optString("dimensions", "No data available"));
            builder.setCarrier(phoneObj.optString("carrier", "No data available"));
            builder.setNetworkFrequencies(phoneObj.optString("network_frequencies", "No data available"));
            builder.setImage(phoneObj.optString("image", null));
            return builder.createPhone();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
}
