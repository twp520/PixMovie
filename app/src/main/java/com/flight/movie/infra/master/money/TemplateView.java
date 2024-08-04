// Copyright 2019 Google LLC
//
// Licensed under the Apache License, Version 2.0 (the "License");
// you may not use this file except in compliance with the License.
// You may obtain a copy of the License at
//
//      http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing, software
// distributed under the License is distributed on an "AS IS" BASIS,
// WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// See the License for the specific language governing permissions and
// limitations under the License.

package com.flight.movie.infra.master.money;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.Nullable;

import com.flight.movie.infra.master.R;
import com.google.android.gms.ads.nativead.MediaView;
import com.google.android.gms.ads.nativead.NativeAd;
import com.google.android.gms.ads.nativead.NativeAdView;

/**
 * Base class for a template view. *
 */
public class TemplateView extends FrameLayout {

    private int templateType;
    private NativeAd nativeAd;
    private NativeAdView nativeAdView;
    private TextView primaryView;
    private TextView secondaryView;
    private RatingBar ratingBar;
    private TextView tertiaryView;
    private ImageView iconView;
    private MediaView mediaView;
    private Button callToActionView;
    private Button callToActionViewA;
    private Button callToActionViewB;

    private boolean canClickAd = true;

    private static final String MEDIUM_TEMPLATE = "medium_template";
    private static final String SMALL_TEMPLATE = "small_template";

    public TemplateView(Context context) {
        super(context);
    }

    public TemplateView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        initView(context, attrs);
    }

    public TemplateView(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initView(context, attrs);
    }

    public NativeAdView getNativeAdView() {
        return nativeAdView;
    }

    private boolean adHasOnlyStore(NativeAd nativeAd) {
        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        return !TextUtils.isEmpty(store) && TextUtils.isEmpty(advertiser);
    }

    public void setNativeAd(NativeAd nativeAd) {
        this.nativeAd = nativeAd;

        String store = nativeAd.getStore();
        String advertiser = nativeAd.getAdvertiser();
        String headline = nativeAd.getHeadline();
        String body = nativeAd.getBody();
        String cta = nativeAd.getCallToAction();
        Double starRating = nativeAd.getStarRating();
        NativeAd.Image icon = nativeAd.getIcon();

        String secondaryText;

        nativeAdView.setCallToActionView(callToActionView);
        nativeAdView.setHeadlineView(primaryView);
        nativeAdView.setMediaView(mediaView);
        secondaryView.setVisibility(VISIBLE);
        if (adHasOnlyStore(nativeAd)) {
            nativeAdView.setStoreView(secondaryView);
            secondaryText = store;
        } else if (!TextUtils.isEmpty(advertiser)) {
            nativeAdView.setAdvertiserView(secondaryView);
            secondaryText = advertiser;
        } else {
            secondaryText = "";
        }

        primaryView.setText(headline);
        callToActionView.setText(cta);

        //  Set the secondary view to be the star rating if available.
        if (starRating != null && starRating > 0) {
            secondaryView.setVisibility(GONE);
            ratingBar.setVisibility(VISIBLE);
            ratingBar.setRating(starRating.floatValue());
            nativeAdView.setStarRatingView(ratingBar);
        } else {
            secondaryView.setText(secondaryText);
            secondaryView.setVisibility(VISIBLE);
            ratingBar.setVisibility(GONE);
        }

        if (icon != null) {
            iconView.setVisibility(VISIBLE);
            iconView.setImageDrawable(icon.getDrawable());
        } else {
            iconView.setVisibility(GONE);
        }

        if (tertiaryView != null) {
            tertiaryView.setText(body);
            nativeAdView.setBodyView(tertiaryView);
        }

        nativeAdView.setNativeAd(nativeAd);
    }


    public void destroyNativeAd() {
        nativeAd.destroy();
    }

    public String getTemplateTypeName() {
        if (templateType == R.layout.ad_medium_view) {
            return MEDIUM_TEMPLATE;
        } else if (templateType == R.layout.ad_small_view) {
            return SMALL_TEMPLATE;
        }
        return "";
    }

    private void initView(Context context, AttributeSet attributeSet) {

        TypedArray attributes =
                context.getTheme().obtainStyledAttributes(attributeSet, R.styleable.TemplateView, 0, 0);

        try {
            templateType =
                    attributes.getResourceId(
                            R.styleable.TemplateView_gnt_template_type, R.layout.ad_medium_view);
        } finally {
            attributes.recycle();
        }
        LayoutInflater inflater =
                (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(templateType, this);
    }

    @Override
    public void onFinishInflate() {
        super.onFinishInflate();
        nativeAdView = findViewById(R.id.native_ad_view);
        primaryView = findViewById(R.id.primary);
        secondaryView = findViewById(R.id.secondary);
        tertiaryView = findViewById(R.id.body);

        ratingBar = findViewById(R.id.rating_bar);
        ratingBar.setEnabled(false);
        iconView = findViewById(R.id.icon);
        mediaView = findViewById(R.id.media_view);

        if (getTemplateTypeName().equals(MEDIUM_TEMPLATE)) {
            callToActionViewA = findViewById(R.id.cta_a);
            callToActionViewB = findViewById(R.id.cta);
            boolean isB = InstallManager.INSTANCE.getRunB();
            if (isB) {
                callToActionViewB.setVisibility(VISIBLE);
                callToActionViewA.setVisibility(GONE);
                callToActionView = callToActionViewB;
            } else {
                callToActionViewB.setVisibility(GONE);
                callToActionViewA.setVisibility(VISIBLE);
                callToActionView = callToActionViewA;
            }
        } else {
            callToActionView = findViewById(R.id.cta);
        }
    }

    public void setCanClickAd(boolean canClickAd) {
        this.canClickAd = canClickAd;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        return !canClickAd;
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (canClickAd) {
            return super.onTouchEvent(event);
        }
        return true;
    }
}
