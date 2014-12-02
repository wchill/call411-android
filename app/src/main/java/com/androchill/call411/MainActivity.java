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

import android.annotation.TargetApi;
import android.app.ActivityOptions;
import android.app.LoaderManager;
import android.content.Intent;
import android.content.Loader;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.SparseArray;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
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
                //showPhoto(child, p);
                Toast.makeText(MainActivity.this, "Item clicked: " + p.getModelNumber(), Toast.LENGTH_SHORT).show();
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
        getLoaderManager().initLoader(1, null, this);
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
     * Loads drawables into the given image view efficiently. Uses the method described
     * <a href="http://developer.android.com/training/displaying-bitmaps/load-bitmap.html">here.</a>
     *
     * @param imageView
     * @param resId     Resource identifier of the drawable to load from memory
     */
    private void setImageBitmap(ImageView imageView, int resId) {
        Bitmap bitmap = Utils.decodeSampledBitmapFromResource(getResources(),
                resId, imageView.getMeasuredWidth(), imageView.getMeasuredHeight());
        sPhotoCache.put(resId, bitmap);
        imageView.setImageBitmap(bitmap);
    }

    /**
     * When the user clicks a thumbnail, bundle up information about it and launch the
     * details activity.
     */
    public void showPhoto(View view, Phone phone) {
        Intent intent = new Intent();
        intent.setClass(this, DetailActivity.class);

        // Interesting data to pass across are the thumbnail location, the map parameters,
        // the picture title & description, and the key to retrieve the bitmap from the cache
        intent.putExtra("title", phone.getModelNumber())
              .putExtra("description", phone.toString())
              .putExtra("photo", R.drawable.photo1);
//        if (Utils.hasLollipop()) {
//            startActivityLollipop(view, intent);
//        } else {
            startActivityGingerBread(view, intent);
//        }
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void startActivityLollipop(View view, Intent intent) {
        intent.setClass(this, DetailActivityL.class);
        ImageView hero = (ImageView) ((View) view.getParent()).findViewById(R.id.photo);
        ((ViewGroup) hero.getParent()).setTransitionGroup(false);

        sPhotoCache.put(intent.getIntExtra("photo", -1),
                ((BitmapDrawable) hero.getDrawable()).getBitmap());

        ActivityOptions options =
                ActivityOptions.makeSceneTransitionAnimation(this, hero, "photo_hero");
        startActivity(intent, options.toBundle());
    }

    private void startActivityGingerBread(View view, Intent intent) {
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
        if(data == null) {
            Log.d("Download phones", "Null result");
        } else {
            Log.d("Download phones", "Load finished");
            mAdapter.setDataset(data);
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Phone>> loader) {

    }
}
