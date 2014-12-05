package com.androchill.call411.utils;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

public class Phone implements Parcelable {
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
    private double talk_time;
    private double camera_megapixels;
    private int price;
    private double weight;
    private String storage_options;
    private String dimensions;
    private String carrier;
    private String network_frequencies;
    private String image;

    public Phone(String model_number, int ram, String processor, String manufacturer, String system,
                 double screen_size, String screen_resolution, int battery_capacity, double talk_time,
                 double camera_megapixels, int price, double weight, String storage_options,
                 String dimensions, String carrier, String network_frequencies, String image) {
        this.model_number = model_number;
        this.ram = ram;
        if(processor != null && !processor.equals("null"))
            this.processor = processor;
        else
            this.processor = "No data available";
        if(manufacturer != null && !manufacturer.equals("null"))
            this.manufacturer = manufacturer;
        else
            this.manufacturer = "No data available";
        if(system != null && !system.equals("null"))
            this.system = system;
        else
            this.system = "No data available";
        this.screen_size = screen_size;
        if(screen_resolution != null && !screen_resolution.equals("null"))
            this.screen_resolution = screen_resolution;
        else
            this.screen_resolution = "No data available";
        this.battery_capacity = battery_capacity;
        this.talk_time = talk_time;
        this.camera_megapixels = camera_megapixels;
        this.price = price;
        this.weight = weight;
        if(storage_options != null && !storage_options.equals("null"))
            this.storage_options = storage_options;
        else
            this.storage_options = "No data available";
        if(dimensions != null && !dimensions.equals("null"))
            this.dimensions = dimensions;
        else
            this.dimensions = "No data available";
        if(carrier != null && !carrier.equals("null"))
            this.carrier = carrier;
        else
            this.carrier = "No data available";
        if(network_frequencies != null && !network_frequencies.equals("null"))
            this.network_frequencies = network_frequencies;
        else
            this.network_frequencies = "No data available";
        this.image = image;
    }

    public String toString() {
        return this.model_number + " (" + this.ram + "MB, " + this.processor + ", " + this.manufacturer + ")";
    }

    public String getModelNumber() {
        return this.model_number;
    }

    public int getRam() {
        return ram;
    }

    public String getProcessor() {
        return processor;
    }

    public String getManufacturer() {
        return manufacturer;
    }

    public String getSystem() {
        return system;
    }

    public double getScreenSize() {
        return screen_size;
    }

    public String getScreenResolution() {
        return screen_resolution;
    }

    public int getBatteryCapacity() {
        return battery_capacity;
    }

    public double getTalkTime() {
        return talk_time;
    }

    public double getCameraMegapixels() {
        return camera_megapixels;
    }

    public int getPrice() {
        return price;
    }

    public double getWeight() {
        return weight;
    }

    public String getStorageOptions() {
        return storage_options;
    }

    public String getDimensions() {
        return dimensions;
    }

    public String getCarrier() {
        return carrier;
    }

    public String getNetworkFrequencies() {
        return network_frequencies;
    }

    public String getImageUrl() {
        if(this.image == null) {
            Log.d("Phone", "no URL");
            return "http://intense.io:8000/static/images/placeholder.gif";
        } else {
            Log.d("Phone", this.image);
        }
        return this.image;
    }

    public Phone(Parcel in) {
        this.model_number = in.readString();
        this.ram = in.readInt();
        this.processor = in.readString();
        this.manufacturer = in.readString();
        this.system = in.readString();
        this.screen_size = in.readDouble();
        this.screen_resolution = in.readString();
        this.battery_capacity = in.readInt();
        this.talk_time = in.readDouble();
        this.camera_megapixels = in.readDouble();
        this.price = in.readInt();
        this.weight = in.readDouble();
        this.storage_options = in.readString();
        this.dimensions = in.readString();
        this.carrier = in.readString();
        this.network_frequencies = in.readString();
        this.image = in.readString();
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.model_number);
        dest.writeInt(this.ram);
        dest.writeString(this.processor);
        dest.writeString(this.manufacturer);
        dest.writeString(this.system);
        dest.writeDouble(this.screen_size);
        dest.writeString(this.screen_resolution);
        dest.writeInt(this.battery_capacity);
        dest.writeDouble(this.talk_time);
        dest.writeDouble(this.camera_megapixels);
        dest.writeInt(this.price);
        dest.writeDouble(this.weight);
        dest.writeString(this.storage_options);
        dest.writeString(this.dimensions);
        dest.writeString(this.carrier);
        dest.writeString(this.network_frequencies);
        dest.writeString(this.image);
    }

    public static final Parcelable.Creator<Phone> CREATOR = new Parcelable.Creator<Phone>() {

        @Override
        public Phone createFromParcel(Parcel source) {
            return new Phone(source);
        }

        @Override
        public Phone[] newArray(int size) {
            return new Phone[size];
        }
    };
}
