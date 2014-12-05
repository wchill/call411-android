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

import android.app.ActivityManager;
import android.app.LoaderManager;
import android.content.Context;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.Point;
import android.hardware.Camera;
import android.net.http.HttpResponseCache;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.os.StatFs;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
import android.util.DisplayMetrics;
import android.util.Log;
import android.util.SparseArray;
import android.view.Display;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androchill.call411.ui.Utils;
import com.androchill.call411.utils.DownloadAllPhonesLoader;
import com.androchill.call411.utils.Phone;
import com.androchill.call411.utils.PhoneBuilder;
import com.androchill.call411.utils.PhoneViewAdapter;
import com.androchill.call411.utils.SlideInOutRightItemAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Phone>> {

    // Bitmaps will only be decoded once and stored in this cache
    public static SparseArray<Bitmap> sPhotoCache = new SparseArray<Bitmap>(4);
    private RecyclerView mRecyclerView;
    private PhoneViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View lastViewed = null;
    private ListView mDrawerList;
    private DrawerLayout mDrawerLayout;
    private ActionBarDrawerToggle mDrawerToggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mRecyclerView = (RecyclerView) findViewById(R.id.phone_recycler_view);
        mLayoutManager = new LinearLayoutManager(this);
        mLayoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setItemAnimator(new SlideInOutRightItemAnimator(mRecyclerView));


        // specify an adapter (see also next example)
        /*
        StringBuilder text = new StringBuilder();
        try {
            Resources res = getResources();
            InputStream in_s = res.openRawResource(R.raw.allphones);

            BufferedReader br = new BufferedReader(new InputStreamReader(in_s));
            String line;
            while ((line = br.readLine()) != null) {
                text.append(line);
                text.append('\n');
            }
        }catch (IOException e) {
            e.printStackTrace();
        }
        List<Phone> data = PhoneJSONParser.parseArray(text.toString());
        */

        mAdapter = new PhoneViewAdapter(this, new ArrayList<Phone>());
        mRecyclerView.setAdapter(mAdapter);
        final ItemClickSupport itemClick = ItemClickSupport.addTo(mRecyclerView);

        itemClick.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(RecyclerView parent, View child, int position, long id) {
                PhoneViewAdapter adapter = (PhoneViewAdapter) parent.getAdapter();
                Phone p = adapter.getPhoneAt(position);
                showPhoto(child, p);
            }
        });

        enableHttpResponseCache();
        getLoaderManager().initLoader(1, null, this);
        mDrawerList = (ListView) findViewById(R.id.left_drawer);
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        String[] mOptionNames = new String[] {"All Phones", "My Phone"};
        mDrawerList.setAdapter(new ArrayAdapter<String>(this,
                android.R.layout.simple_list_item_1, mOptionNames));
        mDrawerList.setOnItemClickListener(new ListView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch(position) {
                    case 0:
                        mDrawerLayout.closeDrawers();
                        break;
                    case 1:
                        showPhoto(null, getHardwareSpecs());
                        //Toast.makeText(MainActivity.this, "My Phone", Toast.LENGTH_SHORT).show();
                        break;
                }
            }
        });
        mDrawerToggle = new ActionBarDrawerToggle(
                this,                  /* host Activity */
                mDrawerLayout,         /* DrawerLayout object */
                R.string.app_name,  /* "open drawer" description */
                R.string.app_name  /* "close drawer" description */
        ) {

            /** Called when a drawer has settled in a completely closed state. */
            public void onDrawerClosed(View view) {
                super.onDrawerClosed(view);
            }

            /** Called when a drawer has settled in a completely open state. */
            public void onDrawerOpened(View drawerView) {
                super.onDrawerOpened(drawerView);
            }
        };

        mDrawerLayout.setDrawerListener(mDrawerToggle);
    }

    private Phone getHardwareSpecs() {
        ActivityManager activityManager = (ActivityManager) getSystemService(ACTIVITY_SERVICE);
        PhoneBuilder pb = new PhoneBuilder();
        pb.setModelNumber(Build.MODEL);
        pb.setManufacturer(Build.MANUFACTURER);

        Process p = null;
        String board_platform = "No data available";
        try {
            p = new ProcessBuilder("/system/bin/getprop", "ro.chipname").redirectErrorStream(true).start();
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            String line = "";
            while ((line=br.readLine()) != null){
                board_platform = line;
            }
            p.destroy();
        } catch (IOException e) {
            e.printStackTrace();
            board_platform = "No data available";
        }

        pb.setProcessor(board_platform);
        ActivityManager.MemoryInfo mInfo = new ActivityManager.MemoryInfo();
        activityManager.getMemoryInfo(mInfo);
        pb.setRam((int)(mInfo.totalMem/1048576L));
        pb.setBatteryCapacity(getBatteryCapacity());
        pb.setTalkTime(-1);
        pb.setDimensions("No data available");

        WindowManager mWindowManager = getWindowManager();
        Display mDisplay = mWindowManager.getDefaultDisplay();
        Point mPoint = new Point();
        mDisplay.getSize(mPoint);
        pb.setScreenResolution(mPoint.x + " x " + mPoint.y + " pixels");

        DisplayMetrics mDisplayMetrics = new DisplayMetrics();
        mDisplay.getMetrics(mDisplayMetrics);
        int mDensity = mDisplayMetrics.densityDpi;
        pb.setScreenSize(Math.sqrt(Math.pow(mPoint.x / (double) mDensity, 2)+Math.pow(mPoint.y / (double) mDensity, 2)));

        Camera camera = Camera.open(0);
        android.hardware.Camera.Parameters params = camera.getParameters();
        List sizes = params.getSupportedPictureSizes();
        Camera.Size  result = null;

        ArrayList<Integer> arrayListForWidth = new ArrayList<Integer>();
        ArrayList<Integer> arrayListForHeight = new ArrayList<Integer>();

        for (int i=0;i<sizes.size();i++){
            result = (Camera.Size) sizes.get(i);
            arrayListForWidth.add(result.width);
            arrayListForHeight.add(result.height);
        }
        if(arrayListForWidth.size() != 0 && arrayListForHeight.size() != 0){
            pb.setCameraMegapixels(Collections.max(arrayListForHeight) * Collections.max(arrayListForWidth) / (double) 1000000);
        } else {
            pb.setCameraMegapixels(-1);
        }
        camera.release();

        pb.setPrice(-1);
        pb.setWeight(-1);
        pb.setSystem("Android " + Build.VERSION.RELEASE);

        StatFs stat = new
                StatFs(Environment.getExternalStorageDirectory().getPath());
        StatFs statInternal = new StatFs(Environment.getRootDirectory().getAbsolutePath());
        double storageSize = ((stat.getBlockSizeLong() * stat.getBlockCountLong()) + (statInternal.getBlockSizeLong() * statInternal.getBlockCountLong())) / 1073741824L;
        pb.setStorageOptions(storageSize + " GB");
        TelephonyManager telephonyManager =((TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE));
        String operatorName = telephonyManager.getNetworkOperatorName();
        if(operatorName.length() == 0) operatorName = "No data available";
        pb.setCarrier(operatorName);
        pb.setNetworkFrequencies("No data available");
        pb.setImage(null);
        return pb.createPhone();
    }

    public int getBatteryCapacity() {
        Object mPowerProfile_ = null;

        final String POWER_PROFILE_CLASS = "com.android.internal.os.PowerProfile";

        try {
            mPowerProfile_ = Class.forName(POWER_PROFILE_CLASS)
                    .getConstructor(Context.class).newInstance(this);
        } catch (Exception e) {
            e.printStackTrace();
        }

        try {
            double batteryCapacity = (Double) Class
                    .forName(POWER_PROFILE_CLASS)
                    .getMethod("getAveragePower", java.lang.String.class)
                    .invoke(mPowerProfile_, "battery.capacity");
            return (int) batteryCapacity;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return -1;
    }

    private void enableHttpResponseCache() {
        try {
            long httpCacheSize = 25 * 1024 * 1024; // 10 MiB
            File httpCacheDir = new File(getCacheDir(), "http");
            HttpResponseCache.install(httpCacheDir, httpCacheSize);
        } catch (Exception httpResponseCacheNotAvailable) {
            Log.d("MainActivity", "HTTP response cache is unavailable.");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (!Utils.hasLollipop()) {
            // The activity transition animates the clicked image alpha to zero, reset that value when
            // you come back to this activity
            if(lastViewed != null)
                ViewHelper.setAlpha(lastViewed, 1.0f);
        }

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if (mDrawerToggle.onOptionsItemSelected(item)) {
                    return true;
                }
                Toast.makeText(this, "Button pressed", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * When the user clicks a thumbnail, bundle up information about it and launch the
     * details activity.
     */
    public void showPhoto(View view, Phone phone) {
        Intent intent = new Intent();
        intent.setClass(this, DetailActivity.class);

        intent.putExtra("phone", phone);
        lastViewed = view;
        startPhoneActivity(view, intent);
    }

    private void startPhoneActivity(View view, Intent intent) {
        if(view != null) {
            int[] screenLocation = new int[2];
            view.getLocationOnScreen(screenLocation);
            intent.
                    putExtra("left", screenLocation[0]).
                    putExtra("top", screenLocation[1]).
                    putExtra("width", view.getWidth()).
                    putExtra("height", view.getHeight());
        }

        startActivity(intent);

        // Override transitions: we don't want the normal window animation in addition to our
        // custom one
        overridePendingTransition(0, 0);

        // The detail activity handles the enter and exit animations. Both animations involve a
        // ghost view animating into its final or initial position respectively. Since the detail
        // activity starts translucent, the clicked view needs to be invisible in order for the
        // animation to look correct.
        if(view != null)
            ViewPropertyAnimator.animate(view).alpha(0.0f);
    }

    @Override
    public Loader<List<Phone>> onCreateLoader(int id, Bundle args) {
        Log.d("Download phones", "Creating loader");
        return new DownloadAllPhonesLoader(this);
    }

    @Override
    public void onLoadFinished(Loader<List<Phone>> loader, final List<Phone> data) {
        ProgressBar progress = (ProgressBar) findViewById(R.id.progress);
        progress.setVisibility(View.GONE);
        if(data == null) {
            Log.d("Download phones", "Null result");
            Toast.makeText(this, "Failed to load phone database!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Download phones", "Load finished");
            mAdapter.setDataset(data);
            Toast.makeText(this, "Loaded " + data.size() + " phones", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Phone>> loader) {

    }
}
