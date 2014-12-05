package com.androchill.call411.utils;

public class PhoneBuilder {
    private String model_number;
    private int ram = -1;
    private String processor;
    private String manufacturer;
    private String system;
    private double screen_size = -1;
    private String screen_resolution;
    private int battery_capacity = -1;
    private double talk_time = -1;
    private double camera_megapixels = -1;
    private int price = -1;
    private double weight = -1;
    private String storage_options;
    private String dimensions;
    private String carrier;
    private String network_frequencies;
    private String image = null;

    public PhoneBuilder setModelNumber(String model_number) {
        this.model_number = model_number;
        return this;
    }

    public PhoneBuilder setRam(int ram) {
        this.ram = ram;
        return this;
    }

    public PhoneBuilder setProcessor(String processor) {
        this.processor = processor;
        return this;
    }

    public PhoneBuilder setManufacturer(String manufacturer) {
        this.manufacturer = manufacturer;
        return this;
    }

    public PhoneBuilder setSystem(String system) {
        this.system = system;
        return this;
    }

    public PhoneBuilder setScreenSize(double screen_size) {
        this.screen_size = screen_size;
        return this;
    }

    public PhoneBuilder setScreenResolution(String screen_resolution) {
        this.screen_resolution = screen_resolution;
        return this;
    }

    public PhoneBuilder setBatteryCapacity(int battery_capacity) {
        this.battery_capacity = battery_capacity;
        return this;
    }

    public PhoneBuilder setTalkTime(double talk_time) {
        this.talk_time = talk_time;
        return this;
    }

    public PhoneBuilder setCameraMegapixels(double camera_megapixels) {
        this.camera_megapixels = camera_megapixels;
        return this;
    }

    public PhoneBuilder setPrice(int price) {
        this.price = price;
        return this;
    }

    public PhoneBuilder setWeight(double weight) {
        this.weight = weight;
        return this;
    }

    public PhoneBuilder setStorageOptions(String storage_options) {
        this.storage_options = storage_options;
        return this;
    }

    public PhoneBuilder setDimensions(String dimensions) {
        this.dimensions = dimensions;
        return this;
    }

    public PhoneBuilder setCarrier(String carrier) {
        this.carrier = carrier;
        return this;
    }

    public PhoneBuilder setNetworkFrequencies(String network_frequencies) {
        this.network_frequencies = network_frequencies;
        return this;
    }

    public PhoneBuilder setImage(String image) {
        this.image = image;
        return this;
    }

    public Phone createPhone() {
        return new Phone(model_number, ram, processor, manufacturer, system, screen_size,
                         screen_resolution, battery_capacity, talk_time, camera_megapixels,
                         price, weight, storage_options, dimensions, carrier, network_frequencies,
                         image);
    }
}