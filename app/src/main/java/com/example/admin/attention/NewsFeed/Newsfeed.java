package com.example.admin.attention.NewsFeed;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.BottomSheetBehavior;
import android.support.design.widget.BottomSheetDialogFragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.attention.MainActivity;
import com.example.admin.attention.R;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.profileActivity.ProfileActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;

public class Newsfeed extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {
    private DatabaseReference mNotiDatabase;
    private RecyclerView mNewsList;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private static LinearLayout layout;
    private static LinearLayout.LayoutParams params;
    public static SharedPreferences rowNewsData;
    private BottomSheetDialogFragment bottomSheetDialogFragment;
    private BottomSheetBehavior mBottomSheetBehavior;
    private int flag=0,i=0;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_newsfeed);
        flag=0;
        i=0;
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        rowNewsData=this.getSharedPreferences("com.example.admin.attentionplease.NewsFeed",Context.MODE_PRIVATE);
        bottomSheetDialogFragment= new bottomSheet();
        mBottomSheetBehavior=new BottomSheetBehavior();
        mBottomSheetBehavior.setState(1);
        pd=new ProgressDialog(this);
        pd.setTitle("Getting news list!");
        pd.setMessage("Please wait...");
        mAuth= FirebaseAuth.getInstance();
        mNotiDatabase= FirebaseDatabase.getInstance().getReference().child("Notifications");
        mNotiDatabase.keepSynced(true);
        mNewsList=findViewById(R.id.newsList);
        mNewsList.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mNewsList.setLayoutManager(linearLayoutManager);


        //-------------------------newsfeed logic-----------------------------

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
                        Log.i("topics list",dataSnapshot.child("topics").getValue().toString());
                        layout =viewHolder.mView.findViewById(R.id.linearLayoutNewsFeed);
                        params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);



                        if(MainActivity.topicsSubscribed.getBoolean(dataSnapshot.child("topics").getValue().toString(),false))
                        {

                            viewHolder.setNewsTitle(model.getTitle());
                            viewHolder.setNewsInfo(model.getOne_line_desc());
                            Picasso p=Picasso.with(getApplicationContext());
                            viewHolder.setNewsImage(model.getThumb_image(),p);
                            if(i<5)
                            {
                                MainActivity.topicsSubscribed.edit().putString(String.valueOf(i),model.getThumb_image()).apply();
                                i=i+1;
                            }
                            viewHolder.mView.setOnClickListener(new View.OnClickListener() {
                                @Override
                                public void onClick(View view) {
                                    rowNewsData.edit().putString("title",model.getTitle()).apply();
                                    rowNewsData.edit().putString("oneline",model.getOne_line_desc()).apply();
                                    rowNewsData.edit().putString("detail",model.getDetail_desc()).apply();
                                    rowNewsData.edit().putString("links",model.getLinks()).apply();
                                    rowNewsData.edit().putString("image",model.getImage()).apply();
                                    rowNewsData.edit().putString("thumbimage",model.getThumb_image()).apply();

                                    bottomSheetDialogFragment.show(getSupportFragmentManager(), bottomSheetDialogFragment.getTag());

                                }
                            });
                        }
                        else
                        {
//                            viewHolder.mView.setVisibility(View.GONE);
//                            viewHolder.mView.setSystemUiVisibility(View.GONE);
                            viewHolder.Layout_hide();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mNewsList.setAdapter(firebaseAdapter);






        //__________________________________________________________







    }


    @Override
    protected void onStart() {
        super.onStart();

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


        public void setNewsImage(final String thumb_image,final Picasso picasso)
        {

            final ImageView userImageView=mView.findViewById(R.id.newsImage);
            if(thumb_image.equals("default") || thumb_image.equals(""))
            {
                picasso.load(R.drawable.noti1).fit().into(userImageView);
            }
            else{
                picasso.load(thumb_image).networkPolicy(NetworkPolicy.OFFLINE)
                        .placeholder(R.drawable.loading1).fit().into(userImageView, new Callback() {
                    @Override
                    public void onSuccess() {
                    }

                    @Override
                    public void onError() {
                        picasso.load(thumb_image).placeholder(R.drawable.loading1).fit().into(userImageView);
                    }
                });
            }
        }

        private void Layout_hide() {
            params.height = 0;
            //itemView.setLayoutParams(params); //This One.
            layout.setLayoutParams(params);   //Or This one.

        }
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_profile) {
            startActivity(new Intent(Newsfeed.this, ProfileActivity.class));
        }
        if (id == R.id.action_topics) {
            startActivity(new Intent(Newsfeed.this, SubscribeTopics.class));
        }
        if (id == R.id.action_logout) {
            pd.setTitle("Logging out");
            pd.setMessage("Please wait for a while...");
            pd.show();
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
            pd.dismiss();
            Intent intent=new Intent(Newsfeed.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }


        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if(id==R.id.nav_newsFeed){

        }
        if(id==R.id.nav_home){
            startActivity(new Intent(Newsfeed.this, MainActivity.class));
        }

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}