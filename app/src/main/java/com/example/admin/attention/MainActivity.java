package com.example.admin.attention;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.support.v4.view.PagerAdapter;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.CardView;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.FrameLayout;
import android.widget.Toast;

import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.TimeTable.timeTableHome;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.profileActivity.ProfileActivity;
import com.example.admin.attention.startActivity.choose;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.special.ResideMenu.ResideMenu;
import com.special.ResideMenu.ResideMenuItem;
import com.tmall.ultraviewpager.UltraViewPager;
import com.tmall.ultraviewpager.transformer.UltraDepthScaleTransformer;
import com.yalantis.contextmenu.lib.ContextMenuDialogFragment;
import com.yalantis.contextmenu.lib.MenuObject;
import com.yalantis.contextmenu.lib.MenuParams;
import com.yalantis.contextmenu.lib.interfaces.OnMenuItemClickListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;


public class MainActivity extends AppCompatActivity implements OnMenuItemClickListener, AdapterView.OnItemSelectedListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {

    private FirebaseUser mUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    public static SharedPreferences topicsSubscribed;
    public static DatabaseReference mDatabaseRef;
    private PagerAdapter adapter;

    private UltraViewPager.Orientation gravity_indicator;

    private List<String> listUser;
    private  Map<String,Map<String,String>> mapUser;
    private ResideMenu resideMenu;
    private ContextMenuDialogFragment mMenuDialogFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        topicsSubscribed=this.getSharedPreferences("com.example.admin.attentionplease", Context.MODE_PRIVATE);
//        Button bt=findViewById(R.id.testing);
//        bt.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Map<String, Object> updateHashmap=new HashMap<>();
//                updateHashmap.put("title","sdg");
//                updateHashmap.put("one_line_desc","dgav");
////                updateHashmap.put("topics",mTopics.getText().toString());
//                updateHashmap.put("detail_desc","gdafajafs");
//                updateHashmap.put("ccode","C-1297");
//                updateHashmap.put("links","");
//                updateHashmap.put("image","default");
//                updateHashmap.put("thumb_image","");
//                FirebaseDatabase.getInstance().getReference().child("Colleges").child("C-1297").child("notifications").push().setValue(updateHashmap);
//            }
//        });


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
        resideMenu = new ResideMenu(this);
        resideMenu.setBackground(R.drawable.menu_background);
        resideMenu.attachToActivity(this);

        ResideMenu.OnMenuListener menuListener = new ResideMenu.OnMenuListener() {
            @Override
            public void openMenu() {
                Toast.makeText(getApplicationContext(), "Menu is opened!", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void closeMenu() {
                Toast.makeText(getApplicationContext(), "Menu is closed!", Toast.LENGTH_SHORT).show();
            }
        };
        resideMenu.setMenuListener(menuListener);
        resideMenu.setScaleValue(0.7f);
        resideMenu.setSwipeDirectionDisable(ResideMenu.DIRECTION_RIGHT);
        // create menu items;
        String titles[] = { "Home", "Profile", "Calendar", "Settings" };
        int icon[] = { R.drawable.icon_home, R.drawable.icon_profile, R.drawable.icon_calendar, R.drawable.icon_settings };

        for (int i = 0; i < titles.length; i++){
            ResideMenuItem item = new ResideMenuItem(this, icon[i], titles[i]);
            item.setOnClickListener(this);
            resideMenu.addMenuItem(item,  ResideMenu.DIRECTION_LEFT); // or  ResideMenu.DIRECTION_RIGHT
        }

        //=====end reside menu======

        //====== context menu =======
        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icon_home);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.drawable.icon_profile);

        List<MenuObject> menuObjects = new ArrayList<>();
        menuObjects.add(close);
        menuObjects.add(send);

        MenuParams menuParams = new MenuParams();
        menuParams.setActionBarSize((int) getResources().getDimension(R.dimen.nav_header_height));
        menuParams.setMenuObjects(getMenuObjects());
        menuParams.setClosableOutside(true);
        // set other settings to meet your needs
        mMenuDialogFragment = ContextMenuDialogFragment.newInstance(menuParams);
        mMenuDialogFragment.setItemClickListener(this);


    }


    private List<MenuObject> getMenuObjects() {
        // You can use any [resource, bitmap, drawable, color] as image:
        // item.setResource(...)
        // item.setBitmap(...)
        // item.setDrawable(...)
        // item.setColor(...)
        // You can set image ScaleType:
        // item.setScaleType(ScaleType.FIT_XY)
        // You can use any [resource, drawable, color] as background:
        // item.setBgResource(...)
        // item.setBgDrawable(...)
        // item.setBgColor(...)
        // You can use any [color] as text color:
        // item.setTextColor(...)
        // You can set any [color] as divider color:
        // item.setDividerColor(...)

        List<MenuObject> menuObjects = new ArrayList<>();

        MenuObject close = new MenuObject();
        close.setResource(R.drawable.icon_profile);

        MenuObject send = new MenuObject("Send message");
        send.setResource(R.drawable.icon_home);

        MenuObject like = new MenuObject("Like profile");
        Bitmap b = BitmapFactory.decodeResource(getResources(), R.drawable.icon_home);
        like.setBitmap(b);

        MenuObject addFr = new MenuObject("Add to friends");
        BitmapDrawable bd = new BitmapDrawable(getResources(),
                BitmapFactory.decodeResource(getResources(), R.drawable.icon_home));
        addFr.setDrawable(bd);

        MenuObject addFav = new MenuObject("Add to favorites");
        addFav.setResource(R.drawable.icon_home);

        MenuObject block = new MenuObject("Block user");
        block.setResource(R.drawable.icon_home);

        menuObjects.add(close);
        menuObjects.add(send);
        menuObjects.add(like);
        menuObjects.add(addFr);
        menuObjects.add(addFav);
        menuObjects.add(block);
        return menuObjects;
    }
    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        return resideMenu.dispatchTouchEvent(ev);
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


//    private void defaultUltraViewPager(){
//        UltraViewPager ultraViewPager = (UltraViewPager)findViewById(R.id.ultra_viewpager);
//        ultraViewPager.setScrollMode(UltraViewPager.ScrollMode.HORIZONTAL);
//        //initialize UltraPagerAdapter，and add child view to UltraViewPager
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

    @Override
    public void onClick(View view) {

    }

    @Override
    public void onMenuItemClick(View clickedView, int position) {

    }
}
