package com.teamtreehouse.albumcover;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.res.ColorStateList;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.graphics.Palette;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageButton;
import android.widget.ImageView;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;


public class AlbumDetailActivity extends Activity {

    public static final String EXTRA_ALBUM_ART_RESID = "EXTRA_ALBUM_ART_RESID";

    @Bind(R.id.album_art) ImageView albumArtView;
    @Bind(R.id.fab) ImageButton fab;
    @Bind(R.id.title_panel) ViewGroup titlePanel;
    @Bind(R.id.track_panel) ViewGroup trackPanel;
    @Bind(R.id.detail_container) ViewGroup detailContainer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_album_detail);
        ButterKnife.bind(this);
        populate();
    }




    private void animate() {
        // value of 1 for setScale means no scale is applied. -> we want it to end at one.
        /*fab.setScaleX(0);
        fab.setScaleY(0);
        fab.animate().scaleX(1).scaleY(1).start();*/


        //by creating object animators and putting them together through a set
        // -> you are grouping these two into a single object so to say.
        /*ObjectAnimator scalex = ObjectAnimator.ofFloat(fab, "scaleX", 0, 1);
        ObjectAnimator scaley = ObjectAnimator.ofFloat(fab, "scaleY", 0, 1);
        AnimatorSet scaleFab = new AnimatorSet();
        scaleFab.playTogether(scalex, scaley);*/

        Animator scaleFab = AnimatorInflater.loadAnimator(this, R.animator.scale);
        scaleFab.setTarget(fab);

        int titleStartValue = titlePanel.getTop();
        int titleEndValue = titlePanel.getBottom();
        ObjectAnimator animatorTitle = ObjectAnimator.ofInt(titlePanel,"bottom",titleStartValue, titleEndValue);
        //accelerate the animator
        animatorTitle.setInterpolator(new AccelerateDecelerateInterpolator());

        int trackStartValue = trackPanel.getTop();
        int trackEndValue = trackPanel.getBottom();
        ObjectAnimator animatorTrack = ObjectAnimator.ofInt(trackPanel,"bottom",trackStartValue,trackEndValue);
        //decelerate the animator
        animatorTrack.setInterpolator(new DecelerateInterpolator());

        //hiding the views initially.
        titlePanel.setBottom(titleStartValue);
        trackPanel.setBottom(titleStartValue);
        fab.setScaleX(0);
        fab.setScaleY(0);

        //delay and duration
        animatorTitle.setDuration(1000);
        animatorTrack.setDuration(1000);
        animatorTitle.setStartDelay(1000);//animator will start after 1 second

        //set a Animator set to play objectAnimators sequentially.
        AnimatorSet set = new AnimatorSet();
        set.playSequentially(animatorTitle, animatorTrack, scaleFab);
        set.start();


    }

    @OnClick(R.id.album_art)
    public void onAlbumArtClick(View view) {
        animate();
    }

    private void populate() {
        int albumArtResId = getIntent().getIntExtra(EXTRA_ALBUM_ART_RESID, R.drawable.mean_something_kinder_than_wolves);
        albumArtView.setImageResource(albumArtResId);

        Bitmap albumBitmap = getReducedBitmap(albumArtResId);
        colorizeFromImage(albumBitmap);
    }

    private Bitmap getReducedBitmap(int albumArtResId) {
        // reduce image size in memory to avoid memory errors
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = false;
        options.inSampleSize = 8;
        return BitmapFactory.decodeResource(getResources(), albumArtResId, options);
    }

    private void colorizeFromImage(Bitmap image) {
        Palette palette = Palette.from(image).generate();

        // set panel colors
        int defaultPanelColor = 0xFF808080;
        int defaultFabColor = 0xFFEEEEEE;
        titlePanel.setBackgroundColor(palette.getDarkVibrantColor(defaultPanelColor));
        trackPanel.setBackgroundColor(palette.getLightMutedColor(defaultPanelColor));

        // set fab colors
        int[][] states = new int[][]{
                new int[]{android.R.attr.state_enabled},
                new int[]{android.R.attr.state_pressed}
        };

        int[] colors = new int[]{
                palette.getVibrantColor(defaultFabColor),
                palette.getLightVibrantColor(defaultFabColor)
        };
        fab.setBackgroundTintList(new ColorStateList(states, colors));
    }
}
