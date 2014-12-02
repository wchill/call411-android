package com.androchill.call411.utils;

import android.util.Log;

/**
 * Created by Eric Ahn on 12/2/2014.
 */
public class Phone {
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
    private String model_number;
    private int ram;
    private String processor;
    private String manufacturer;
    private String system;
    private double screen_size;
    private String screen_resolution;
    private int battery_capacity;
    private int talk_time;
    private double camera_megapixels;
    private double price;
    private double weight;
    private String storage_options;
    private String dimensions;
    private String carrier;
    private String network_frequencies;
    private String image;

    public Phone(String model_number, int ram, String processor, String manufacturer, String system,
                 double screen_size, String screen_resolution, int battery_capacity, int talk_time,
                 double camera_megapixels, double price, double weight, String storage_options,
                 String dimensions, String carrier, String network_frequencies, String image) {
        this.model_number = model_number;
        this.ram = ram;
        this.processor = processor;
        this.manufacturer = manufacturer;
        this.system = system;
        this.screen_size = screen_size;
        this.screen_resolution = screen_resolution;
        this.battery_capacity = battery_capacity;
        this.talk_time = talk_time;
        this.camera_megapixels = camera_megapixels;
        this.price = price;
        this.weight = weight;
        this.storage_options = storage_options;
        this.dimensions = dimensions;
        this.carrier = carrier;
        this.network_frequencies = network_frequencies;
        this.image = image;
    }

    public String toString() {
        return this.model_number + " (" + this.ram + "MB, " + this.processor + ", " + this.manufacturer + ")";
    }

    public String getModelNumber() {
        return this.model_number;
    }

    public String getImageUrl() {
        if(this.image == null) {
            Log.d("Phone", "no URL");
            return "http://placehold.it/300";
        } else {
            Log.d("Phone", this.image);
        }
        return this.image;
    }
}
