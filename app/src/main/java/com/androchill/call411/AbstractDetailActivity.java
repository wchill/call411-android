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

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.graphics.Palette;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.androchill.call411.ui.AnimatedPathView;
import com.androchill.call411.ui.AnimatorListener;
import com.nineoldandroids.animation.Animator;
import com.nineoldandroids.view.ViewPropertyAnimator;

public abstract class AbstractDetailActivity extends ActionBarActivity {

    public ImageView hero;
    public Bitmap photo;
    public View container;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        hero = (ImageView) findViewById(R.id.photo);
        container = findViewById(R.id.container);

        photo = setupPhoto(getIntent().getIntExtra("photo", R.drawable.photo1));

        colorize(photo);

        setupText();

        postCreate();

        setupEnterAnimation();
    }

    public abstract void postCreate();

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
        TextView titleView = (TextView) findViewById(R.id.title);
        titleView.setText(getIntent().getStringExtra("title"));

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setText(getIntent().getStringExtra("description"));
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

        TextView descriptionView = (TextView) findViewById(R.id.description);
        descriptionView.setTextColor(palette.getLightVibrantColor(res.getColor(R.color.default_light_vibrant)));
        colorButton(R.id.star_button, palette.getMutedColor(res.getColor(R.color.default_muted)),
                palette.getVibrantColor(res.getColor(R.color.default_vibrant)));

        AnimatedPathView star = (AnimatedPathView) findViewById(R.id.star_container);
        star.setFillColor(palette.getVibrantColor(R.color.default_vibrant));
        star.setStrokeColor(palette.getLightVibrantColor(res.getColor(R.color.default_light_vibrant)));
    }

    public abstract void colorButton(int id, int bgColor, int tintColor);

    private Bitmap setupPhoto(int resource) {
        Bitmap bitmap = MainActivity.sPhotoCache.get(resource);
        hero.setImageBitmap(bitmap);
        return bitmap;
    }

    public void showStar(View view) {
        toggleStarView();
    }

    private void toggleStarView() {
        final AnimatedPathView starContainer = (AnimatedPathView) findViewById(R.id.star_container);

        if (starContainer.getVisibility() == View.INVISIBLE) {
            ViewPropertyAnimator.animate(hero).alpha(0.2f);
            ViewPropertyAnimator.animate(starContainer).alpha(1);
            starContainer.setVisibility(View.VISIBLE);
            starContainer.reveal();
        } else {
            ViewPropertyAnimator.animate(hero).alpha(1);
            ViewPropertyAnimator.animate(starContainer).alpha(0).setListener(new AnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    starContainer.setVisibility(View.INVISIBLE);
                    ViewPropertyAnimator.animate(starContainer).setListener(null);
                }
            });
        }
    }

    public void showInformation(View view) {
        toggleInformationView(view);
    }

    public abstract void toggleInformationView(View view);

    public abstract void setupEnterAnimation();

    public abstract void setupExitAnimation();
}
