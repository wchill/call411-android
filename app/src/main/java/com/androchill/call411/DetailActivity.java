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

import android.content.Loader;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.androchill.call411.ui.AnimatorListener;
import com.androchill.call411.ui.SpotlightView;
import com.androchill.call411.ui.Utils;
import com.androchill.call411.utils.DownloadAllPhonesLoader;
import com.androchill.call411.utils.DownloadSuggestedPhoneLoader;
import com.androchill.call411.utils.Phone;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewHelper;
import com.nineoldandroids.view.ViewPropertyAnimator;

import java.util.List;

public class DetailActivity extends AbstractDetailActivity {

    private static final Interpolator sDecelerator = new DecelerateInterpolator();
    private static final Interpolator sAccelerator = new AccelerateInterpolator();

    // Used for the circular reveal of the map
    private SpotlightView spotlight;

    // Used for the circular reveal of the map. On entry, animatedHero is scaled and
    // translated to the correct position and then crossfades to hero which has no tint.
    // The exit animation runs all of this in reverse.
    private ImageView animatedHero;

    private View infoContainer;
    private float maskScale = 0;

    @Override
    public void postCreate() {
        // Hide the back button until the entry animation is complete
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        spotlight = (SpotlightView) findViewById(R.id.spotlight);
        infoContainer = findViewById(R.id.info_container);

        animatedHero = (ImageView) findViewById(R.id.animated_photo);

        // Setup the alpha values here for Gingerbread support
        ViewHelper.setAlpha(infoContainer, 0);
        ViewHelper.setAlpha(container, 0);
    }

    @Override
    public void setupEnterAnimation() {
        spotlight.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {
                runEnterAnimation();

                Utils.removeOnGlobalLayoutListenerCompat(spotlight, this);
            }
        });
    }

    /**
     * The enter animation scales the hero in from its previous thumbnail
     * size/location. In parallel, the container of the activity is fading in.
     * When the picture is in place, we crossfade in the actual hero image.
     */
    public void runEnterAnimation() {
        // Retrieve the data we need for the picture to display and
        // the thumbnail to animate it from
        Bundle bundle = getIntent().getExtras();
        if(bundle.getInt("top", -1) != -1) {
            final int thumbnailTop = bundle.getInt("top");
            final int thumbnailLeft = bundle.getInt("left");
            final int thumbnailWidth = bundle.getInt("width");
            final int thumbnailHeight = bundle.getInt("height");

            // Scale factors to make the large version the same size as the thumbnail
            float mWidthScale = (float) thumbnailWidth / animatedHero.getWidth();
            float mHeightScale = (float) thumbnailHeight / animatedHero.getHeight();

            // Set starting values for properties we're going to animate. These
            // values scale and position the full size version down to the thumbnail
            // size/location, from which we'll animate it back up
            ViewHelper.setPivotX(animatedHero, 0);
            ViewHelper.setPivotY(animatedHero, 0);
            ViewHelper.setScaleX(animatedHero, mWidthScale);
            ViewHelper.setScaleY(animatedHero, mHeightScale);
            ViewHelper.setTranslationX(animatedHero, thumbnailLeft);
            ViewHelper.setTranslationY(animatedHero, thumbnailTop);
        }
        // Animate scale and translation to go from thumbnail to full size
        ViewPropertyAnimator.animate(animatedHero).
                scaleX(1).scaleY(1).
                translationX(0).translationY(0).
                setInterpolator(sDecelerator).
                setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        ViewPropertyAnimator.animate(animatedHero).alpha(0);
                        ViewPropertyAnimator.animate(infoContainer).alpha(1);

                        // The back button can be shown
                        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                    }
                });
        // Animate in the container with the background and text
        ViewPropertyAnimator.animate(container).alpha(1);
    }

    @Override
    public void setupExitAnimation() {
        ViewPropertyAnimator.animate(animatedHero).alpha(1);
        ViewPropertyAnimator.animate(infoContainer).alpha(0).setListener(new AnimatorListener() {
            @Override
            public void onAnimationEnd(Animator animation) {
                runExitAnimation();
            }
        });
    }

    /**
     * The exit animation is basically a reverse of the enter animation
     */
    public void runExitAnimation() {
        Bundle bundle = getIntent().getExtras();
        if(bundle.getInt("top", -1) == -1) finish();
        final int thumbnailTop = bundle.getInt("top");
        final int thumbnailLeft = bundle.getInt("left");
        final int thumbnailWidth = bundle.getInt("width");
        final int thumbnailHeight = bundle.getInt("height");

        float mWidthScale = (float) thumbnailWidth / animatedHero.getWidth();
        float mHeightScale = (float) thumbnailHeight / animatedHero.getHeight();

        ViewHelper.setPivotX(animatedHero, 0);
        ViewHelper.setPivotY(animatedHero, 0);
        ViewHelper.setScaleX(animatedHero, 1);
        ViewHelper.setScaleY(animatedHero, 1);
        ViewHelper.setTranslationX(animatedHero, 0);
        ViewHelper.setTranslationY(animatedHero, 0);

        ViewPropertyAnimator.animate(animatedHero).
                scaleX(mWidthScale).scaleY(mHeightScale).
                translationX(thumbnailLeft).translationY(thumbnailTop).
                setInterpolator(sAccelerator).
                setListener(new AnimatorListener() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        finish();
                    }
                });
        ViewPropertyAnimator.animate(container).alpha(0);

        // Hide the back button during the exit animation
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
    }

    @Override
    public void finish() {
        super.finish();

        // Override transitions: we don't want the normal window animation in addition to our
        // custom one
        overridePendingTransition(0, 0);
    }

    @Override
    public Loader<List<Phone>> onCreateLoader(int id, Bundle args) {
        return new DownloadSuggestedPhoneLoader(this, phone);
    }

    @Override
    public void onLoadFinished(Loader<List<Phone>> loader, List<Phone> data) {
        if(data == null) {
            Log.d("Download phones", "Null result");
            Toast.makeText(this, "Failed to load phone suggestions!", Toast.LENGTH_SHORT).show();
        } else {
            Log.d("Download phones", "Load finished");
            Log.d("Similar phones", data.toString());
            TextView suggested = (TextView) findViewById(R.id.suggested_phones);
            if(data.size() == 0) {
                suggested.setText("No phone suggestions");
            } else {
                StringBuilder sb = new StringBuilder();
                for(int i = 0; i < 10 && i < data.size(); i++) {
                    sb.append(data.get(i).getModelNumber() + "\n");
                }
                suggested.setText(sb.toString());
            }
        }
    }

    @Override
    public void onLoaderReset(Loader<List<Phone>> loader) {

    }
}
