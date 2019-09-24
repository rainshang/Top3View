/*
 * Copyright (c) 2019.
 * Created by Ethan at 7/2/19 1:49 PM
 */

package com.xyx.top3view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.LayerDrawable;
import android.os.AsyncTask;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;

import java.io.IOException;
import java.net.URL;


public class Top3View extends ConstraintLayout {

    private final static float SHADOW_WIDTH = 15;
    private final static float SIDE_MID_BOTTOM_MARGIN = 15;
    private final static float RECT_CORNER_RADIUS = 13;
    private final static float SIDE_RECT_WIDTH = 95;
    private final static float SIDE_RECT_HEIGHT = 115;
    private final static float SIDE_CIRCLE_BIG_DIAMETER = 100 + SHADOW_WIDTH * 2;
    private final static float SIDE_CIRCLE_SMALL_DIAMETER = 30;
    private final static float SIDE_CIRCLE_BIG_MARGIN_BOTTOM = 60 - SHADOW_WIDTH;
    private final static float SIDE_CIRCLE_BIG_PADDING = 5 + SHADOW_WIDTH;
    private final static float SIDE_CIRCLE_SMALL_MARGIN_BOTTOM = 145;
    private final static float MID_RECT_WIDTH = 110;
    private final static float MID_RECT_HEIGHT = 130;
    private final static float MID_CIRCLE_BIG_DIAMETER = 135 + SHADOW_WIDTH * 2;
    private final static float MID_CIRCLE_SMALL_DIAMETER = 35;
    private final static float MID_CIRCLE_BIG_MARGIN_BOTTOM = 80 - SHADOW_WIDTH;
    private final static float MID_CIRCLE_BIG_PADDING = 6 + SHADOW_WIDTH;
    private final static float MID_CIRCLE_SMALL_MARGIN_BOTTOM = 195;
    private final static float WIDTH = SIDE_CIRCLE_BIG_DIAMETER + SIDE_RECT_WIDTH + MID_RECT_WIDTH; //(SIDE_CIRCLE_BIG_DIAMETER / 2 + SIDE_RECT_WIDTH / 2) * 2 + MID_RECT_WIDTH

    private final static int DEFAULT_LEFT_COLOR = Color.parseColor("#0dacbb");
    private final static int DEFAULT_RIGHT_COLOR = Color.parseColor("#0dbbad");
    private final static int DEFAULT_MID_GRADIENT_COLOR_START = Color.parseColor("#029bbf");
    private final static int DEFAULT_MID_GRADIENT_COLOR_END = Color.parseColor("#46ffa9");
    private final static int DEFAULT_SHADOW_COLOR = Color.BLACK;

    private View leftCircleSmall;
    private View leftCircleBig;
    private View leftRect;
    private View midCircleSmall;
    private View midCircleBig;
    private View midRect;
    private View rightCircleSmall;
    private View rightCircleBig;
    private View rightRect;
    private boolean isLayoutSet = false;

    private int mLeftColor;
    private String mLeftImageUrl;
    private String mLeftName;
    private float mLeftCps;
    private int mMidColorStart;
    private int mMidColorEnd;
    private String mMidImageUrl;
    private String mMidName;
    private float mMidCps;
    private int mRightColor;
    private String mRightImageUrl;
    private String mRightName;
    private float mRightCps;
    private int mShadowColor;

    private ImageView top3_left_image;
    private TextView top3_left_name;
    private TextView top3_left_cps;
    private ImageView top3_mid_image;
    private TextView top3_mid_name;
    private TextView top3_mid_cps;
    private ImageView top3_right_image;
    private TextView top3_right_name;
    private TextView top3_right_cps;

    private GradientDrawable leftRectBg, midRectBg, rightRectBg;
    private GradientDrawable[] leftCircleBigBgs, midCircleBigBgs, rightCircleBigBgs;

    public Top3View(Context context) {
        super(context);
        init(null, 0);
    }

    public Top3View(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    public Top3View(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(attrs, defStyle);
    }

    private void init(AttributeSet attrs, int defStyle) {
        initAttr(attrs, defStyle);
        initLayoutViews(LayoutInflater.from(getContext()).inflate(R.layout.view_top3, this));
        initGradientBg();
        initViews();

        set1stImage(mMidImageUrl);
        set1stName(mMidName);
        set1stCps(mMidCps);

        set2ndImage(mLeftImageUrl);
        set2ndName(mLeftName);
        set2ndCps(mLeftCps);

        set3rdImage(mRightImageUrl);
        set3rdName(mRightName);
        set3rdCps(mRightCps);
    }

    private void initAttr(AttributeSet attrs, int defStyle) {
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.Top3View, defStyle, 0);

        mLeftColor = a.getColor(R.styleable.Top3View_color2nd, DEFAULT_LEFT_COLOR);
        mLeftImageUrl = a.getString(R.styleable.Top3View_avatarUrl2nd);
        mLeftName = a.getString(R.styleable.Top3View_name2nd);
        mLeftCps = a.getFloat(R.styleable.Top3View_cps2nd, -1);

        mMidColorStart = a.getColor(R.styleable.Top3View_startColor1st, DEFAULT_MID_GRADIENT_COLOR_START);
        mMidColorEnd = a.getColor(R.styleable.Top3View_endColor1st, DEFAULT_MID_GRADIENT_COLOR_END);
        mMidImageUrl = a.getString(R.styleable.Top3View_avatarUrl1st);
        mMidName = a.getString(R.styleable.Top3View_name1st);
        mMidCps = a.getFloat(R.styleable.Top3View_cps1st, -1);

        mRightColor = a.getColor(R.styleable.Top3View_color3rd, DEFAULT_RIGHT_COLOR);
        mRightImageUrl = a.getString(R.styleable.Top3View_avatarUrl3rd);
        mRightName = a.getString(R.styleable.Top3View_name3rd);
        mRightCps = a.getFloat(R.styleable.Top3View_cps3rd, -1);

        mShadowColor = a.getColor(R.styleable.Top3View_shadowColor, DEFAULT_SHADOW_COLOR);
        a.recycle();
    }

    private void initLayoutViews(View contentView) {
        leftCircleSmall = contentView.findViewById(R.id.top3_left_rank);
        leftCircleBig = contentView.findViewById(R.id.top3_left_circle_big);
        leftRect = contentView.findViewById(R.id.top3_left_rect);
        midCircleSmall = contentView.findViewById(R.id.top3_mid_rank);
        midCircleBig = contentView.findViewById(R.id.top3_mid_circle_big);
        midRect = contentView.findViewById(R.id.top3_mid_rect);
        rightCircleSmall = contentView.findViewById(R.id.top3_right_rank);
        rightCircleBig = contentView.findViewById(R.id.top3_right_circle_big);
        rightRect = contentView.findViewById(R.id.top3_right_rect);
    }

    private void initGradientBg() {
        // left
        GradientDrawable leftCircleSmallBg = new GradientDrawable();
        leftCircleSmallBg.setShape(GradientDrawable.OVAL);
        leftCircleSmallBg.setColor(mLeftColor);
        leftCircleSmall.setBackground(leftCircleSmallBg);

        GradientDrawable leftCircleBigBgMain = new GradientDrawable();
        leftCircleBigBgMain.setShape(GradientDrawable.OVAL);
        leftCircleBigBgMain.setColor(mLeftColor);
        GradientDrawable leftCircleBigBgShadow = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mShadowColor, Color.TRANSPARENT});
        leftCircleBigBgShadow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        leftCircleBigBgShadow.setShape(GradientDrawable.OVAL);
        leftCircleBigBgs = new GradientDrawable[]{leftCircleBigBgShadow, leftCircleBigBgMain};
        leftCircleBig.setBackground(new LayerDrawable(leftCircleBigBgs));

        leftRectBg = new GradientDrawable();
        leftRectBg.setShape(GradientDrawable.RECTANGLE);
        leftRectBg.setColor(mLeftColor);
        leftRect.setBackground(leftRectBg);

        // mid
        GradientDrawable midCircleSmallBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mMidColorStart, mMidColorEnd});
        midCircleSmallBg.setShape(GradientDrawable.OVAL);
        midCircleSmall.setBackground(midCircleSmallBg);

        GradientDrawable midCircleBigBgMain = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mMidColorStart, mMidColorEnd});
        midCircleBigBgMain.setShape(GradientDrawable.OVAL);
        GradientDrawable midCircleBigBgShadow = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mShadowColor, Color.TRANSPARENT});
        midCircleBigBgShadow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        midCircleBigBgShadow.setShape(GradientDrawable.OVAL);
        midCircleBigBgs = new GradientDrawable[]{midCircleBigBgShadow, midCircleBigBgMain};
        midCircleBig.setBackground(new LayerDrawable(midCircleBigBgs));

        midRectBg = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mMidColorStart, mMidColorEnd});
        midRectBg.setShape(GradientDrawable.RECTANGLE);
        midRect.setBackground(midRectBg);

        // right
        GradientDrawable rightCircleSmallBg = new GradientDrawable();
        rightCircleSmallBg.setShape(GradientDrawable.OVAL);
        rightCircleSmallBg.setColor(mRightColor);
        rightCircleSmall.setBackground(rightCircleSmallBg);

        GradientDrawable rightCircleBigBgMain = new GradientDrawable();
        rightCircleBigBgMain.setShape(GradientDrawable.OVAL);
        rightCircleBigBgMain.setColor(mRightColor);
        GradientDrawable rightCircleBigBgShadow = new GradientDrawable(GradientDrawable.Orientation.TL_BR, new int[]{mShadowColor, Color.TRANSPARENT});
        rightCircleBigBgShadow.setGradientType(GradientDrawable.RADIAL_GRADIENT);
        rightCircleBigBgShadow.setShape(GradientDrawable.OVAL);
        rightCircleBigBgs = new GradientDrawable[]{rightCircleBigBgShadow, rightCircleBigBgMain};
        rightCircleBig.setBackground(new LayerDrawable(rightCircleBigBgs));

        rightRectBg = new GradientDrawable();
        rightRectBg.setShape(GradientDrawable.RECTANGLE);
        rightRectBg.setColor(mRightColor);
        rightRect.setBackground(rightRectBg);
    }

    private void initViews() {
        top3_left_image = leftCircleBig.findViewById(R.id.top3_left_image);
        top3_left_name = leftRect.findViewById(R.id.top3_left_name);
        top3_left_cps = leftRect.findViewById(R.id.top3_left_cps);
        top3_mid_image = midCircleBig.findViewById(R.id.top3_mid_image);
        top3_mid_name = midRect.findViewById(R.id.top3_mid_name);
        top3_mid_cps = midRect.findViewById(R.id.top3_mid_cps);
        top3_right_image = rightCircleBig.findViewById(R.id.top3_right_image);
        top3_right_name = rightRect.findViewById(R.id.top3_right_name);
        top3_right_cps = rightRect.findViewById(R.id.top3_right_cps);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        if (!isLayoutSet) {
            int width = getMeasuredWidth() - getPaddingLeft() - getPaddingRight();

            // left
            ConstraintLayout.LayoutParams layoutParams = (LayoutParams) leftCircleSmall.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * SIDE_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_CIRCLE_SMALL_MARGIN_BOTTOM / WIDTH));

            layoutParams = (LayoutParams) leftCircleBig.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * SIDE_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_CIRCLE_BIG_MARGIN_BOTTOM / WIDTH));
            leftCircleBigBgs[0].setGradientRadius(layoutParams.width / 2);
            leftCircleBigBgs[1].setStroke((int) (width * SHADOW_WIDTH / WIDTH), Color.TRANSPARENT);

            layoutParams = (LayoutParams) leftRect.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_RECT_WIDTH / WIDTH);
            layoutParams.height = (int) (width * SIDE_RECT_HEIGHT / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_MID_BOTTOM_MARGIN / WIDTH));
            leftRectBg.setCornerRadius(width * RECT_CORNER_RADIUS / WIDTH);

            // mid
            layoutParams = (LayoutParams) midCircleSmall.getLayoutParams();
            layoutParams.width = (int) (width * MID_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * MID_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * MID_CIRCLE_SMALL_MARGIN_BOTTOM / WIDTH));

            layoutParams = (LayoutParams) midCircleBig.getLayoutParams();
            layoutParams.width = (int) (width * MID_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * MID_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * MID_CIRCLE_BIG_MARGIN_BOTTOM / WIDTH));
            midCircleBigBgs[0].setGradientRadius(layoutParams.width / 2);
            midCircleBigBgs[1].setStroke((int) (width * SHADOW_WIDTH / WIDTH), Color.TRANSPARENT);

            layoutParams = (LayoutParams) midRect.getLayoutParams();
            layoutParams.width = (int) (width * MID_RECT_WIDTH / WIDTH);
            layoutParams.height = (int) (width * MID_RECT_HEIGHT / WIDTH);
            midRectBg.setCornerRadius(width * RECT_CORNER_RADIUS / WIDTH);

            // right
            layoutParams = (LayoutParams) rightCircleSmall.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * SIDE_CIRCLE_SMALL_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_CIRCLE_SMALL_MARGIN_BOTTOM / WIDTH));

            layoutParams = (LayoutParams) rightCircleBig.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.height = (int) (width * SIDE_CIRCLE_BIG_DIAMETER / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_CIRCLE_BIG_MARGIN_BOTTOM / WIDTH));
            rightCircleBigBgs[0].setGradientRadius(layoutParams.width / 2);
            rightCircleBigBgs[1].setStroke((int) (width * SHADOW_WIDTH / WIDTH), Color.TRANSPARENT);

            layoutParams = (LayoutParams) rightRect.getLayoutParams();
            layoutParams.width = (int) (width * SIDE_RECT_WIDTH / WIDTH);
            layoutParams.height = (int) (width * SIDE_RECT_HEIGHT / WIDTH);
            layoutParams.setMargins(0, 0, 0, (int) (width * SIDE_MID_BOTTOM_MARGIN / WIDTH));
            rightRectBg.setCornerRadius(width * RECT_CORNER_RADIUS / WIDTH);

            // bir circle padding
            int circlePadding = (int) (width * SIDE_CIRCLE_BIG_PADDING / WIDTH);
            leftCircleBig.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);
            rightCircleBig.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);
            circlePadding = (int) (width * MID_CIRCLE_BIG_PADDING / WIDTH);
            midCircleBig.setPadding(circlePadding, circlePadding, circlePadding, circlePadding);

            isLayoutSet = true;
            requestLayout();
        }

    }

    @SuppressLint("StaticFieldLeak")
    public void set1stImage(String url) {
        mMidImageUrl = url;
        // here is a demo. don't load url like this!!
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    return BitmapFactory.decodeStream(new URL(strings[0]).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                top3_mid_image.setImageBitmap(bitmap);
            }
        }.execute(url);
    }

    public void set1stName(String name) {
        mMidName = name;
        top3_mid_name.setText(mMidName);
    }

    public void set1stCps(float cps) {
        mMidCps = cps;
        setCps(top3_mid_cps, mMidCps);
    }

    private void setCps(TextView textView, float cps) {
        textView.setText(cps < 0 ? "--" : String.format("%.1f", cps));
    }

    @SuppressLint("StaticFieldLeak")
    public void set2ndImage(String url) {
        mLeftImageUrl = url;
        // here is a demo. don't load url like this!!
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    return BitmapFactory.decodeStream(new URL(strings[0]).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                top3_left_image.setImageBitmap(bitmap);
            }
        }.execute(url);
    }

    public void set2ndName(String name) {
        mLeftName = name;
        top3_left_name.setText(mLeftName);
    }

    public void set2ndCps(float cps) {
        mLeftCps = cps;
        setCps(top3_left_cps, mLeftCps);
    }

    @SuppressLint("StaticFieldLeak")
    public void set3rdImage(String url) {
        mRightImageUrl = url;
        // here is a demo. don't load url like this!!
        new AsyncTask<String, Void, Bitmap>() {

            @Override
            protected Bitmap doInBackground(String... strings) {
                try {
                    return BitmapFactory.decodeStream(new URL(strings[0]).openStream());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Bitmap bitmap) {
                top3_right_image.setImageBitmap(bitmap);
            }
        }.execute(url);
    }

    public void set3rdName(String name) {
        mRightName = name;
        top3_right_name.setText(mRightName);
    }

    public void set3rdCps(float cps) {
        mRightCps = cps;
        setCps(top3_right_cps, mRightCps);
    }
}
