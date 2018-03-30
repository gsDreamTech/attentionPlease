package com.example.admin.attention.main;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.view.PagerAdapter;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.Notifications.SendNotification;
import com.example.admin.attention.Result.chooseresultdata;
import com.example.admin.attention.Result.result;
import com.example.admin.attention.SeatAllotment.seatAllotment;
import com.example.admin.attention.R;
import com.example.admin.attention.TimeTable.timeTableHome;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.parent_activity.parent;
import com.example.admin.attention.profileActivity.ProfileActivity;
import com.example.admin.attention.resultsheet.result_layout;
import com.example.admin.attention.startActivity.choose;
import com.example.admin.attention.subadmin.SubAdmin;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
//import com.special.ResideMenu.ResideMenu;
import com.tmall.ultraviewpager.UltraViewPager;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,
        AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener  {


    private FirebaseUser mUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    public static SharedPreferences topicsSubscribed;
    public static DatabaseReference mDatabaseRef;
    private PagerAdapter adapter;


    private Button results_button;

    private UltraViewPager.Orientation gravity_indicator;

    private List<String> listUser;
    private  Map<String,Map<String,String>> mapUser;
    private Button  logoutNavigationButton;
    //private ResideMenu resideMenu;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);



        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);





        results_button=findViewById(R.id.Results);
        results_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(MainActivity.this,result_layout.class);
                startActivity(intent);
            }
        });















        topicsSubscribed=this.getSharedPreferences("com.example.admin.attentionplease", Context.MODE_PRIVATE);






        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth= FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);

        pd.setCanceledOnTouchOutside(false);



        logoutNavigationButton=findViewById(R.id.logout_navigation_button);
        logoutNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setTitle("Logging out");
                pd.setMessage("Please wait for a while...");
                pd.show();
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
                pd.dismiss();
                Intent intent=new Intent(MainActivity.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });




        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseRef.keepSynced(true);

        CardView sendNoti=findViewById(R.id.sendNotificationCard);
        sendNoti.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, timeTableHome.class));
            }
        });

        CardView NotiList=findViewById(R.id.notificationListCard);
        NotiList.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, Newsfeed.class));
            }
        });
        CardView subTop=findViewById(R.id.subscribeTopicsCard);
        subTop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
            }
        });
        final CardView seat=findViewById(R.id.seatAllotmentCard);
        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, seatAllotment.class));
            }
        });

        CardView res=findViewById(R.id.resultcard);
        res.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, chooseresultdata.class));
            }
        });

        CardView subadmin=findViewById(R.id.subadminCard);
        subadmin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, SubAdmin.class));
            }
        });

        CardView parent_card=findViewById(R.id.ParentCard);
        parent_card.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, parent.class));
            }
        });

//        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
//        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//        adapter = new UltraPagerAdapter(true);
//        ultraViewPager.setAdapter(adapter);
//        ultraViewPager.setMultiScreen(0.6f);
//        ultraViewPager.setItemRatio(1.0f);
////                ultraViewPager.setRatio(2.0f);
////                ultraViewPager.setMaxHeight(800);
//        ultraViewPager.setAutoMeasureHeight(true);
//        gravity_indicator = UltraViewPager.Orientation.HORIZONTAL;
//        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
        //initUI();



//        =======  reside menu===================
        // attach to current activity;
//        resideMenu = new ResideMenu(this);
//        resideMenu.setBackground(R.drawable.menu_background);
//        resideMenu.attachToActivity(this);
//
//        ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
//            @Override
//            public void openMenu() {
//                Toast.makeText(getApplicationContext(), "Menu is opened!", Toast.LENGTH_SHORT).show();
//            }
//
//            @Override
//            public void closeMenu() {
//                Toast.makeText(getApplicationContext(), "Menu is closed!", Toast.LENGTH_SHORT).show();
//            }
//        };
//        resideMenu.setMenuListener(menuListener);
//        resideMenu.setScaleValue(0.7f);
//        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
//        // create menu items;
//        String titles[] = { "Home", "Profile", "Calendar", "Settings" };
//        int icon[] = { R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_calendar, R.drawable.icon_settings };
//
//        for (int i = 0; i < titles.length; i++){
//            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
//            item.setOnClickListener(this);
//            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
//        }

        //=====end reside menu======

    }



//    @Override
//    public boolean dispatchTouchEvent(MotionEvent ev) {
//        return resideMenu.dispatchTouchEvent(ev);
//    }


    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

//    private void defaultUltraViewPager(){
//        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
//        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//        //initialize UltraPagerAdapterï¼Œand add child view to UltraViewPager
//        PagerAdapter adapter = new UltraPagerAdapter(false);
//        ultraViewPager.setAdapter(adapter);
//
//        //initialize built-in indicator
//        ultraViewPager.initIndicator();
//        //set style of indicators
//        ultraViewPager.getIndicator()
//                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
//                .setFocusColor(Color.GREEN)
//                .setNormalColor(Color.WHITE)
//                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
//        //set the alignment
//        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
//        //construct built-in indicator, and add it to  UltraViewPager
//        ultraViewPager.getIndicator().build();
//
//        //set an infinite loop
//        ultraViewPager.setInfiniteLoop(true);
//        //enable auto-scroll mode
//        ultraViewPager.setAutoScroll(2000);
//
//    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            startActivity(new Intent(MainActivity.this, choose.class));
            finish();
        } else {
            FirebaseMessaging.getInstance().subscribeToTopic(topicsSubscribed.getString("CollegeCode",""));
            mDatabaseRef.child(mAuth.getCurrentUser().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if(dataSnapshot.hasChild("ccode")){
                        FirebaseMessaging.getInstance().subscribeToTopic(dataSnapshot.child("ccode").getValue().toString());
                    }
                    if (dataSnapshot.hasChild("topics")) {


                        mapUser = (Map<String, Map<String, String>>) dataSnapshot.child("topics").getValue();
                        Set<String> sUser = mapUser.keySet();
                        listUser = new ArrayList<>(sUser);
                        Log.i("list",listUser.get(0));


                        try{
                            for (int i = 0; i < listUser.size(); i++) {
                                //FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic"+i));
                                FirebaseMessaging.getInstance().subscribeToTopic(mapUser.get(listUser.get(i)).get("title"));
                                //Log.i("topics" + i, mapUser.get(listUser.get(i)).get("title"));
                            }
                        }catch(Exception e)
                        {
                            Log.i("error",e.getMessage());
                        }


                    } else {

                        // to automatically entering into topics if user is not subscribed to any topics
//                        startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
                        Toast.makeText(MainActivity.this, "Please subscribe atleast one topic...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    // to automatically entering into topics if user is not subscribed to any topics
  //                  startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
                }
            });
        }
    }







    @Override
    public void onClick(View view) {

    }


    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.notification_id) {



        } else if (id == R.id.timetable_id) {

            startActivity(new Intent(MainActivity.this,timeTableHome.class));
            //finish();

        } else if (id == R.id.settings_id) {


        } else if (id == R.id.results_id) {

            startActivity(new Intent(MainActivity.this,result.class));
            //finish();
        } else if (id == R.id.seatallotment_id) {

            startActivity(new Intent(MainActivity.this,seatAllotment.class));
            //finish();
        } else if (id == R.id.newsfeed_id) {

            startActivity(new Intent(MainActivity.this,Newsfeed.class));
            //finish();
        } else if ( id == R.id.logout_navigation_button){
            pd.setTitle("Logging out");
            pd.setMessage("Please wait for a while...");
            pd.show();
            FirebaseMessaging.getInstance().unsubscribeFromTopic(topicsSubscribed.getString("CollegeCode",""));
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
            pd.dismiss();
            Intent intent=new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            //finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> adapterView) {

    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {

    }
}
