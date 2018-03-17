package com.example.admin.attention;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.SparseIntArray;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Spinner;
import android.widget.Toast;

import com.daimajia.slider.library.SliderLayout;
import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.Notifications.SendNotification;
import com.example.admin.attention.TimeTable.timeTableHome;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.profileActivity.ProfileActivity;
import com.example.admin.attention.startActivity.choose;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.tmall.ultraviewpager.transformer.UltraScaleTransformer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener{

    private FirebaseUser mUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    public static SharedPreferences topicsSubscribed;
    private String topics_subscribed[],topics_description[];
    public static DatabaseReference mDatabaseRef;
    private PagerAdapter adapter;

    private UltraViewPager.Orientation gravity_indicator;

    private List<String> list,listUser;
    private  Map<String,Map<String,String>> mapUser;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topicsSubscribed=this.getSharedPreferences("com.example.admin.attentionplease", Context.MODE_PRIVATE);


        Button but=findViewById(R.id.testing);
        but.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String ar1[]={"one","two","three"};
                List ar= Arrays.asList(ar1);
                FirebaseDatabase.getInstance().getReference().child("examples").push().child("array").setValue(ar).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Toast.makeText(getApplicationContext(),"sucess",Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });

        FirebaseDatabase.getInstance().getReference().child("examples").child("array").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                //String arr[]=dataSnapshot.getValue().toString().split(",");
                GenericTypeIndicator<List<String>> genericTypeIndicator = new GenericTypeIndicator<List<String>>() {};
                List<String> list = dataSnapshot.getValue(genericTypeIndicator);
                Log.i("arraygs",list.get(1));
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


        mUser = FirebaseAuth.getInstance().getCurrentUser();
        mAuth= FirebaseAuth.getInstance();
        pd=new ProgressDialog(this);

        pd.setCanceledOnTouchOutside(false);

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


        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        adapter = new UltraPagerAdapter(true);
        ultraViewPager.setAdapter(adapter);
        ultraViewPager.setMultiScreen(0.6f);
        ultraViewPager.setItemRatio(1.0f);
//                ultraViewPager.setRatio(2.0f);
//                ultraViewPager.setMaxHeight(800);
        ultraViewPager.setAutoMeasureHeight(true);
        gravity_indicator = UltraViewPager.Orientation.HORIZONTAL;
        ultraViewPager.setPageTransformer(false, new UltraDepthScaleTransformer());
        //initUI();


    }




    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

    }



    /**
     *
     */
    private void defaultUltraViewPager(){
        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
        //initialize UltraPagerAdapter，and add child view to UltraViewPager
        PagerAdapter adapter = new UltraPagerAdapter(false);
        ultraViewPager.setAdapter(adapter);

        //initialize built-in indicator
        ultraViewPager.initIndicator();
        //set style of indicators
        ultraViewPager.getIndicator()
                .setOrientation(UltraViewPager.Orientation.HORIZONTAL)
                .setFocusColor(Color.GREEN)
                .setNormalColor(Color.WHITE)
                .setRadius((int)TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        //set the alignment
        ultraViewPager.getIndicator().setGravity(Gravity.CENTER_HORIZONTAL | Gravity.BOTTOM);
        //construct built-in indicator, and add it to  UltraViewPager
        ultraViewPager.getIndicator().build();

        //set an infinite loop
        ultraViewPager.setInfiniteLoop(true);
        //enable auto-scroll mode
        ultraViewPager.setAutoScroll(2000);

    }


    @Override
    protected void onStart() {
        super.onStart();
        if (mUser == null) {
            startActivity(new Intent(MainActivity.this, choose.class));
            finish();
        } else {
            mDatabaseRef.child(mAuth.getCurrentUser().getUid()).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    if (dataSnapshot.hasChild("topics")) {


                        mapUser = (Map<String, Map<String, String>>) dataSnapshot.getValue();
                        Set<String> sUser = mapUser.keySet();
                        listUser = new ArrayList<>(sUser);

                        for (int i = 0; i < listUser.size(); i++) {
                            //FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic"+i));
                            FirebaseMessaging.getInstance().subscribeToTopic(mapUser.get(listUser.get(i)).get("title"));
                            Log.i("topics" + i, mapUser.get(listUser.get(i)).get("title"));
                        }
                        try{
//                            for (int i = 0; i < listUser.size(); i++) {
//                                //FirebaseMessaging.getInstance().subscribeToTopic(getIntent().getExtras().getString("topic"+i));
//                                FirebaseMessaging.getInstance().subscribeToTopic(mapUser.get(listUser.get(i)).get("title"));
//                                Log.i("topics" + i, mapUser.get(listUser.get(i)).get("title"));
//                            }
                        }catch(Exception e)
                        {
                            Log.i("error",e.getMessage());
                        }


                    } else {
                        startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
                        Toast.makeText(MainActivity.this, "Please subscribe atleast one topic...", Toast.LENGTH_LONG).show();
                    }
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {
                    startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
                }
            });
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
            startActivity(new Intent(MainActivity.this, ProfileActivity.class));
        }
        if (id == R.id.action_topics) {
            startActivity(new Intent(MainActivity.this, SubscribeTopics.class));
        }
        if (id == R.id.action_logout) {
            pd.setTitle("Logging out");
            pd.setMessage("Please wait for a while...");
            pd.show();
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
            pd.dismiss();
            Intent intent=new Intent(MainActivity.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();

        }

        return super.onOptionsItemSelected(item);
    }

}
