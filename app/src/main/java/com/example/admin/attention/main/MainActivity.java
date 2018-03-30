package com.example.admin.attention.main;

import android.app.Dialog;
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
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.Notifications.SendNotification;
import com.example.admin.attention.Result.chooseresultdata;
import com.example.admin.attention.Result.result;
import com.example.admin.attention.SeatAllotment.seatAllotment;
import com.example.admin.attention.R;
import com.example.admin.attention.TimeTable.timeTableHome;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.admin.admin;
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
import com.google.firebase.database.GenericTypeIndicator;
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

    private DatabaseReference mSeat;
    private List<Map<String,String>> list;
    private SharedPreferences timeShared;
    private EditText usnText;

    private Dialog dgp,dgpp;




    private FirebaseUser mUser;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    public static SharedPreferences topicsSubscribed;
    public static DatabaseReference mDatabaseRef;
    private PagerAdapter adapter;


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

        CardView adminview=findViewById(R.id.admincard);
        adminview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this, admin.class));
            }
        });










        View view=View.inflate(getApplicationContext(),R.layout.seat_allotment_usn,null);
        dgp=new Dialog(this);
        dgp.setContentView(view);





        timeShared=this.getSharedPreferences("com.example.lenovo.seatallotment", Context.MODE_PRIVATE);
        Button submit = view.findViewById(R.id.button);
        usnText = view.findViewById(R.id.usnInput);
        //tl = findViewById(R.id.table_head);
        pd= new ProgressDialog(this);
        pd.setTitle("Fetching data!");
        pd.setMessage("Please wait..");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.show();
                if( usnText.getText().toString().equals("") )
                {
                    Toast.makeText(getApplicationContext(),"Please enter a valid data...",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                else {

                    try{
                        mSeat= FirebaseDatabase.getInstance().getReference().child("Colleges")
                                .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("Seat")
                                .child(usnText.getText().toString().toLowerCase().replace(" ",""));
                        mSeat.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                try{
                                    GenericTypeIndicator<List<Map<String,String>>> Generic = new GenericTypeIndicator<List<Map<String,String>>>(){};
                                    list = dataSnapshot.getValue(Generic);
                                    //Log.i("Data",list.get(timeShared.getInt("subnum),0).get(0));
                                    if(list==null||list.size()==0)
                                    {
                                        Toast.makeText(getApplicationContext(),"USN not found",Toast.LENGTH_LONG).show();
                                        popuperror();
                                        pd.dismiss();
                                    }
                                    else{
                                        popupdisplay();
                                        //display();
                                        pd.dismiss();
                                    }
                                }catch (Exception e)
                                {
                                    Log.i("err",e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {
                                pd.dismiss();
                            }
                        });
                    }catch (Exception e)
                    {
                        Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                        pd.dismiss();
                    }
                }

            }
        });







        final CardView seat=findViewById(R.id.seatAllotmentCard);
        seat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dgp.show();//startActivity(new Intent(MainActivity.this, seatAllotment.class));
            }
        });









    }




    void popuperror(){
        View view= View.inflate(this,R.layout.error_layout,null);
        dgpp=new Dialog(this);
        dgpp.setContentView(view);
        Button bt=view.findViewById(R.id.gobackButton);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dgpp.dismiss();
            }
        });


        dgpp.show();
    }


    void popupdisplay(){
        View view= View.inflate(this,R.layout.pop_sub_layout,null);
        Dialog dg=new Dialog(this);
        dg.setContentView(view);
        final TextView tvblock= view.findViewById(R.id.tvblock);
        final TextView tvroom= view.findViewById(R.id.tvroom);
        final TextView tvseat= view.findViewById(R.id.tvseat);
        final TextView tvtime= view.findViewById(R.id.tvtime);
        final TextView tvsub= view.findViewById(R.id.tvsubject);
        Button button=view.findViewById(R.id.button);

        tvsub.setText( list.get(timeShared.getInt("subnum",0)).get("language"));
        tvtime.setText( list.get(timeShared.getInt("subnum",0)).get("time"));
        tvseat.setText( list.get(timeShared.getInt("subnum",0)).get("seat"));
        tvroom.setText( list.get(timeShared.getInt("subnum",0)).get("room"));
        tvblock.setText( list.get(timeShared.getInt("subnum",0)).get("block"));



        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
//                Toast.makeText(getApplicationContext(),"working..",Toast.LENGTH_LONG).show();
                int num=timeShared.getInt("subnum",0);
                if(list.size()==num+1)
                {
                    num=0;
                }
                else
                {
                    num++;
                }
                timeShared.edit().putInt("subnum",num).apply();
                tvsub.setText( list.get(timeShared.getInt("subnum",0)).get("language"));
                tvtime.setText( list.get(timeShared.getInt("subnum",0)).get("time"));
                tvseat.setText( list.get(timeShared.getInt("subnum",0)).get("seat"));
                tvroom.setText( list.get(timeShared.getInt("subnum",0)).get("room"));
                tvblock.setText( list.get(timeShared.getInt("subnum",0)).get("block"));

            }
        });
        dg.show();
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

            startActivity(new Intent(MainActivity.this,ProfileActivity.class));

        } else if (id == R.id.timetable_id) {

            startActivity(new Intent(MainActivity.this,timeTableHome.class));
            //finish();

        } else if (id == R.id.settings_id) {


        } else if (id == R.id.results_id) {

            startActivity(new Intent(MainActivity.this,result.class));
            //finish();
        } else if (id == R.id.seatallotment_id) {

            dgp.show();//startActivity(new Intent(MainActivity.this,seatAllotment.class));
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
