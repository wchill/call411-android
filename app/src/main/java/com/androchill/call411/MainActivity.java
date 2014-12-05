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
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.net.http.HttpResponseCache;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.androchill.call411.ui.Utils;
import com.androchill.call411.utils.DownloadAllPhonesLoader;
import com.androchill.call411.utils.Phone;
import com.androchill.call411.utils.PhoneViewAdapter;
import com.androchill.call411.utils.SlideInOutRightItemAnimator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import org.lucasr.twowayview.ItemClickSupport;
import org.lucasr.twowayview.ItemClickSupport.OnItemClickListener;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MainActivity extends ActionBarActivity implements LoaderManager.LoaderCallbacks<List<Phone>> {

    // Bitmaps will only be decoded once and stored in this cache
    public static SparseArray<Bitmap> sPhotoCache = new SparseArray<Bitmap>(4);
    private RecyclerView mRecyclerView;
    private PhoneViewAdapter mAdapter;
    private LinearLayoutManager mLayoutManager;
    private View lastViewed = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getSupportActionBar().setHomeButtonEnabled(false);
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
        /*
        // Used to get the dimensions of the image views to load scaled down bitmaps
        final View parent = findViewById(R.id.parent);
        parent.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                Utils.removeOnGlobalLayoutListenerCompat(parent, this);
                setImageBitmap((ImageView) findViewById(R.id.card_photo_1).findViewById(R.id.photo), R.drawable.photo1);
                setImageBitmap((ImageView) findViewById(R.id.card_photo_2).findViewById(R.id.photo), R.drawable.photo2);
                setImageBitmap((ImageView) findViewById(R.id.card_photo_3).findViewById(R.id.photo), R.drawable.photo3);
                setImageBitmap((ImageView) findViewById(R.id.card_photo_4).findViewById(R.id.photo), R.drawable.photo4);
            }
        });
        */
        enableHttpResponseCache();
        getLoaderManager().initLoader(1, null, this);
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
        int[] screenLocation = new int[2];
        view.getLocationOnScreen(screenLocation);
        intent.
                putExtra("left", screenLocation[0]).
                putExtra("top", screenLocation[1]).
                putExtra("width", view.getWidth()).
                putExtra("height", view.getHeight());

        startActivity(intent);

        // Override transitions: we don't want the normal window animation in addition to our
        // custom one
        overridePendingTransition(0, 0);

        // The detail activity handles the enter and exit animations. Both animations involve a
        // ghost view animating into its final or initial position respectively. Since the detail
        // activity starts translucent, the clicked view needs to be invisible in order for the
        // animation to look correct.
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
