package com.example.admin.attention.NewsFeed;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.design.widget.CoordinatorLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.admin.attention.R;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;


public class bottomSheet extends BottomSheetDialogFragment {
    private AutoCompleteTextView aTitle,aOneLine,aDetail,aLink;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);



    }

    private BottomSheetBehavior.BottomSheetCallback mBottomSheetBehaviorCallback = new BottomSheetBehavior.BottomSheetCallback() {


        @SuppressLint("NewApi")
        @Override
        public void onStateChanged(@NonNull View bottomSheet, int newState) {
            if (newState == BottomSheetBehavior.STATE_HIDDEN) {
                dismiss();
            }
            Log.i("state", String.valueOf(newState));
        }


        @Override
        public void onSlide(@NonNull View bottomSheet, float slideOffset) {
            // React to dragging events

        }
    };

    @SuppressLint("RestrictedApi")
    @Override
    public void setupDialog(Dialog dialog, int style) {
        super.setupDialog(dialog, style);
        final View contentView = View.inflate(getContext(), R.layout.activity_news_feed_bottom_sheet, null);
        dialog.setContentView(contentView);



        TextView v=contentView.findViewById(R.id.tv);
        final ImageView imageView=contentView.findViewById(R.id.imageViewBottomSheet);
        aTitle=contentView.findViewById(R.id.editTextTitle);
        aOneLine=contentView.findViewById(R.id.editTextOneLine);
        aDetail=contentView.findViewById(R.id.editTextDetail);
        aLink=contentView.findViewById(R.id.editTextLinks);
        v.setText(Newsfeed.rowNewsData.getString("title",""));


        aTitle.setEnabled(false);
        aOneLine.setEnabled(false);
        aDetail.setEnabled(false);
        aLink.setEnabled(false);




        aTitle.setText(Newsfeed.rowNewsData.getString("title",""));
        aOneLine.setText(Newsfeed.rowNewsData.getString("oneline",""));
        aDetail.setText(Newsfeed.rowNewsData.getString("detail",""));
        aLink.setText(Newsfeed.rowNewsData.getString("links",""));
        if(Newsfeed.rowNewsData.getString("thumbimage","").equals("default") || Newsfeed.rowNewsData.getString("thumbimage","").equals(""))
            Picasso.with(getContext()).load(R.drawable.noti1).into(imageView);
        else {
            Picasso.with(getContext()).load(Newsfeed.rowNewsData.getString("thumbimage", "")).placeholder(R.drawable.noti1)
                    .networkPolicy(NetworkPolicy.OFFLINE).fit().into(imageView, new Callback() {
                @Override
                public void onSuccess() {

                }

                @Override
                public void onError() {
                    Picasso.with(getContext()).load(Newsfeed.rowNewsData.getString("image", ""))
                            .placeholder(R.drawable.noti1).fit().into(imageView);
                }
            });
        }



        CoordinatorLayout.LayoutParams layoutParams =
                (CoordinatorLayout.LayoutParams) ((View) contentView.getParent()).getLayoutParams();
        CoordinatorLayout.Behavior behavior = layoutParams.getBehavior();
        if (behavior != null && behavior instanceof BottomSheetBehavior) {
            ((BottomSheetBehavior) behavior).setBottomSheetCallback(mBottomSheetBehaviorCallback);
        }
    }

}
