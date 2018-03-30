package com.example.admin.attention.notificationHistory;

import android.content.Context;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.attention.NewsFeed.rowNewsFeed;
import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.stone.vega.library.VegaLayoutManager;

import java.util.List;

public class NotificationHistory extends AppCompatActivity {

    private RecyclerView mNotiHistory;

    private static LinearLayout layout;
    private static LinearLayout.LayoutParams params;
    private DatabaseReference mNotiDatabase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification_history);

        mNotiDatabase= FirebaseDatabase.getInstance().getReference().child("Colleges")
                .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("notifications");
        mNotiDatabase.keepSynced(true);
        mNotiHistory=findViewById(R.id.newsList);
        mNotiHistory.setHasFixedSize(true);
        mNotiHistory.setItemAnimator(new DefaultItemAnimator());



        VegaLayoutManager vg=new VegaLayoutManager();
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mNotiHistory.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                Log.i("state"," "+newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        mNotiHistory.setLayoutManager(vg);




        FirebaseRecyclerAdapter<rowNewsFeed,NewsViewHolder> firebaseAdapter=new FirebaseRecyclerAdapter<rowNewsFeed, NewsViewHolder>(
                rowNewsFeed.class,
                R.layout.news_feed_layout,
                NewsViewHolder.class,
                mNotiDatabase
        ) {
            @Override
            protected void populateViewHolder(final NewsViewHolder viewHolder, final rowNewsFeed model, final int position) {
                getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        try {
                            //Log.i("topics list",dataSnapshot.child("topics").getValue().toString());
                            layout = viewHolder.mView.findViewById(R.id.linearLayoutNewsFeed);
                            params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                    ViewGroup.LayoutParams.WRAP_CONTENT);
                            GenericTypeIndicator<List<String>> gs = new GenericTypeIndicator<List<String>>() {
                            };
                            List<String> list = dataSnapshot.child("topics").getValue(gs);
                            for (int i = 0; i < list.size(); i++)
                                if ( MainActivity.topicsSubscribed.getBoolean(list.get(i), false)) {

                                    viewHolder.setNewsTitle(model.getTitle());
                                    viewHolder.setNewsInfo(model.getOne_line_desc());

                                    viewHolder.setNewsImage(model.getThumb_image(), getApplicationContext());
                                    if (i < 5) {
                                        MainActivity.topicsSubscribed.edit().putString(String.valueOf(i), model.getThumb_image()).apply();
                                        i = i + 1;
                                    }
                                    layout.setOnClickListener(new View.OnClickListener() {
                                        @Override
                                        public void onClick(View view) {
                                            Log.i("title",model.getTitle());
                                            Log.i("titleId", getRef(position).getKey());

//                                            rowNewsData.edit().putString("notiid", getRef(position).getKey()).apply();
//                                            rowNewsData.edit().putString("title", model.getTitle()).apply();
//                                            rowNewsData.edit().putString("oneline", model.getOne_line_desc()).apply();
//                                            rowNewsData.edit().putString("detail", model.getDetail_desc()).apply();
//                                            rowNewsData.edit().putString("links", model.getLinks()).apply();
//                                            rowNewsData.edit().putString("image", model.getImage()).apply();
//                                            rowNewsData.edit().putString("thumbimage", model.getThumb_image()).apply();

//                                            ScrollView sl=findViewById(R.id.sview);
//                                            sl.setScrollY(0);
//                                            sl.setVisibility(View.VISIBLE);
//                                            tt.setVisibility(View.GONE);
//
//                                            aTitle.setEnabled(false);
//                                            aOneLine.setEnabled(false);
//                                            aDetail.setEnabled(false);
//                                            aLink.setEnabled(false);
//
//                                            aTitle.setText(model.getTitle());
//                                            aOneLine.setText(model.getOne_line_desc());
//                                            aDetail.setText(model.getDetail_desc());
//                                            aLink.setText(model.getLinks());
//                                            final ImageView imageView=findViewById(R.id.imageViewBottomSheet);
//                                            if(model.getThumb_image().equals("default") || model.getThumb_image().equals(""))
//                                                Picasso.with(getApplicationContext()).load(R.drawable.noti1).into(imageView);
//                                            else {
//                                                Picasso.with(getApplicationContext()).load(model.getThumb_image()).placeholder(R.drawable.noti1)
//                                                        .networkPolicy(NetworkPolicy.OFFLINE).fit().into(imageView, new Callback() {
//                                                    @Override
//                                                    public void onSuccess() {
//
//                                                    }
//
//                                                    @Override
//                                                    public void onError() {
//                                                        Picasso.with(getApplicationContext()).load(model.getThumb_image())
//                                                                .placeholder(R.drawable.noti1).fit().into(imageView);
//                                                    }
//                                                });
//                                            }


                                            //===uncoment xml file in bottom sheet and comment in content_news_feed.xml===
//                                            bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                                        }
                                    });
                                    break;
                                }
                                else {
                                    //                            viewHolder.mView.setVisibility(View.GONE);
                                    //                            viewHolder.mView.setSystemUiVisibility(View.GONE);
                                    viewHolder.Layout_hide();

                                }
                        }catch(Exception e)
                        {
                            Log.i("error in populate",e.getMessage());
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mNotiHistory.setAdapter(firebaseAdapter);




    }


    public static class NewsViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public NewsViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setNewsTitle(String newsTitle)
        {
            TextView newsTitleView=mView.findViewById(R.id.newsTitle);
            newsTitleView.setText(newsTitle);

        }
        public void setNewsInfo(String newsInfo)
        {
            TextView newsInfoView=mView.findViewById(R.id.newsDesdription);
            newsInfoView.setText(newsInfo);

        }


        public void setNewsImage(final String thumb_image, final Context c)
        {

            final ImageView userImageView=mView.findViewById(R.id.newsImage);
            if(thumb_image.equals("default") || thumb_image.equals(""))
            {
                Glide.with(c).load(R.drawable.notific).into(userImageView);
            }
            else{
                Glide.with(c).load(thumb_image).into(userImageView);
            }
        }

        private void Layout_hide() {
            params.height = 0;
            //itemView.setLayoutParams(params); //This One.
            layout.setLayoutParams(params);   //Or This one.

        }
    }
}
