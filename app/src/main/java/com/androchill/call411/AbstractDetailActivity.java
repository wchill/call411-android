/**
 * Copyright 2014 Rahul Parsani
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.androchill.call411;

import android.app.LoaderManager;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TableLayout;
import android.widget.TextView;

import com.androchill.call411.utils.Phone;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;
import com.squareup.picasso.Transformation;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

public abstract class AbstractDetailActivity extends ActionBarActivity implements Target, LoaderManager.LoaderCallbacks<List<Phone>> {

    public ImageView hero;
    public Bitmap photo;
    public View container;
    public Phone phone;

    public Transformation transformation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        hero = (ImageView) findViewById(R.id.photo);
        container = findViewById(R.id.container);

        phone = getIntent().getParcelableExtra("phone");

        transformation = new Transformation() {

            @Override public Bitmap transform(Bitmap source) {
                int targetWidth = container.getWidth();

                double aspectRatio = (double) source.getHeight() / (double) source.getWidth();
                int targetHeight = (int) (targetWidth * aspectRatio);
                Log.d("Transform", Integer.toString(targetWidth));
                Bitmap result = Bitmap.createScaledBitmap(source, targetWidth, targetHeight, false);
                if (result != source) {
                    // Same bitmap is returned if sizes are the same
                    source.recycle();
                }
                return result;
            }

            @Override public String key() {
                return "transformation" + " desiredWidth";
            }
        };

        hero.post(new Runnable() {

            @Override
            public void run() {
                Picasso.with(AbstractDetailActivity.this)
                        .load(phone.getImageUrl())
                        .placeholder(R.drawable.loading_placeholder)
                        .error(android.R.drawable.stat_notify_error)
                        .tag("PhoneViewAdapter")
                        .transform(transformation)
                        .into(hero);
            }
        });

        //photo = setupPhoto();

        //colorize(photo);

        setupText();

        postCreate();

        setupEnterAnimation();
        getLoaderManager().initLoader(2, null, this);
    }

    public abstract void postCreate();

    @Override
    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
        photo = bitmap;
        hero.setImageBitmap(photo);
        colorize(photo);
    }

    @Override
    public void onBitmapFailed(Drawable errorDrawable) {
        hero.setImageDrawable(errorDrawable);
    }

    @Override
    public void onPrepareLoad(Drawable placeHolderDrawable) {
        hero.setImageDrawable(placeHolderDrawable);
    }

    @Override
    public void onBackPressed() {
        setupExitAnimation();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                onBackPressed();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupText() {
        TableLayout tablelayout = (TableLayout) findViewById(R.id.table);
        tablelayout.setColumnShrinkable(1,true);
        TextView titleView = (TextView) findViewById(R.id.title);
        TextView manufacturerView = (TextView) findViewById(R.id.detail_manufacturer_text);
        TextView systemView = (TextView) findViewById(R.id.detail_system_text);
        TextView processorView = (TextView) findViewById(R.id.detail_processor_text);
        TextView ramView = (TextView) findViewById(R.id.detail_ram_text);
        TextView screenSizeView = (TextView) findViewById(R.id.detail_screen_size_text);
        TextView screenResolutionView = (TextView) findViewById(R.id.detail_screen_resolution_text);
        TextView batteryCapacityView = (TextView) findViewById(R.id.detail_battery_capacity_text);
        TextView talkTimeView = (TextView) findViewById(R.id.detail_talk_time_text);
        TextView cameraMegapixelsView = (TextView) findViewById(R.id.detail_camera_megapixels_text);
        TextView priceView = (TextView) findViewById(R.id.detail_price_text);
        TextView weightView = (TextView) findViewById(R.id.detail_weight_text);
        TextView storageView = (TextView) findViewById(R.id.detail_storage_options_text);
        TextView dimensionsView = (TextView) findViewById(R.id.detail_dimensions_text);
        TextView carrierView = (TextView) findViewById(R.id.detail_carrier_text);
        TextView networkFrequenciesView = (TextView) findViewById(R.id.detail_network_frequencies_text);

        titleView.setText(phone.getModelNumber());
        manufacturerView.setText(phone.getManufacturer());
        systemView.setText(phone.getSystem());
        processorView.setText(phone.getProcessor());
        if(phone.getRam() < 0) {
            ramView.setText("No data available");
        } else {
            ramView.setText(Integer.toString(phone.getRam()) + " MB");
        }
        if(phone.getScreenSize() < 0) {
            screenSizeView.setText("No data available");
        } else {
            String scText = new BigDecimal(phone.getScreenSize()).setScale(2, RoundingMode.HALF_EVEN).toString();
            screenSizeView.setText(scText + " inches");
        }
        screenResolutionView.setText(phone.getScreenResolution());
        if(phone.getBatteryCapacity() < 0) {
            batteryCapacityView.setText("No data available");
        } else {
            batteryCapacityView.setText(Integer.toString(phone.getBatteryCapacity()) + " mAh");
        }
        if(phone.getTalkTime() < 0) {
            talkTimeView.setText("No data available");
        } else {
            talkTimeView.setText(Integer.toString((int)phone.getTalkTime()) + " minutes");
        }
        if(phone.getCameraMegapixels() < 0) {
            cameraMegapixelsView.setText("No data available");
        } else {
            String mpText = new BigDecimal(phone.getCameraMegapixels()).setScale(2, RoundingMode.HALF_EVEN).toString();
            cameraMegapixelsView.setText(mpText + " megapixels");
        }
        if(phone.getPrice() < 0) {
            priceView.setText("No data available");
        } else {
            priceView.setText("$" + Integer.toString(phone.getPrice()));
        }
        if(phone.getWeight() < 0) {
            weightView.setText("No data available");
        } else {
            weightView.setText(Double.toString(phone.getWeight()) + " oz");
        }
        storageView.setText(phone.getStorageOptions());
        dimensionsView.setText(phone.getDimensions());
        carrierView.setText(phone.getCarrier());
        networkFrequenciesView.setText(phone.getNetworkFrequencies());
    }

    private void colorize(Bitmap photo) {
        Palette palette = Palette.generate(photo);
        applyPalette(palette);
    }

    public void applyPalette(Palette palette) {
        Resources res = getResources();

        container.setBackgroundColor(palette.getDarkMutedColor(res.getColor(R.color.default_dark_muted)));

        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setTextColor(palette.getVibrantColor(res.getColor(R.color.default_vibrant)));
    }

    public abstract void setupEnterAnimation();

    public abstract void setupExitAnimation();
}
