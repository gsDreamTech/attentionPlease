package com.example.admin.attention.TopicSubscription;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;

import com.example.admin.attention.ProgressGenerator;
import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class   SubscribeTopics extends AppCompatActivity implements ProgressGenerator.OnCompleteListener{
    private ArrayList<Row> rows;
    private DatabaseReference mUserRef;
    private ListView listView;
    private ProgressDialog pd;
    private FirebaseAuth mAuth;
    private  Row row = null;
    private DatabaseReference mTopicRef;
    private List<String> list,listUser;
    private  Map<String,Map<String,String>> mapUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_subscribe_topics);


        final ProgressGenerator progressGenerator = new ProgressGenerator(this);
        final ActionProcessButton bt = findViewById(R.id.buttonSubscribe);
        bt.setMode(ActionProcessButton.Mode.ENDLESS);
        bt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                progressGenerator.start(bt);
                bt.setEnabled(false);
            }
        });


        mTopicRef=FirebaseDatabase.getInstance().getReference().child("Colleges")
                .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("topics");
        mTopicRef.keepSynced(true);

        mAuth=FirebaseAuth.getInstance();

        mUserRef=FirebaseDatabase.getInstance().getReference().child("users").child(mAuth.getUid());
        mUserRef.keepSynced(true);

        listView = findViewById(R.id.listView);

        mTopicRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {
//              condition if the user dont have any topics in there college
                if(!dataSnapshot.exists())
                {
                    Toast.makeText(getApplicationContext(),"Your college dont have any topics",Toast.LENGTH_LONG).show();
                    startActivity(new Intent(SubscribeTopics.this,MainActivity.class));
                    finish();

                }
                else{
                    try {
                        mUserRef.addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot Snapshot) {
                                //==================getting the list of topics subscribed by the user========================
                                if(Snapshot.hasChild("topics")) {
                                    try {
                                        mapUser = (Map<String, Map<String, String>>) Snapshot.child("topics").getValue();
                                        Set<String> sUser = mapUser.keySet();
                                        listUser = new ArrayList<>(sUser);
                                        Log.i("sets", listUser.get(0));
                                    } catch (Exception e) {
                                        Log.i("error user", e.getMessage());
                                    }
                                }
                                //==================getting the total topics provided by the college===========================
                                try {
                                    Map<String, Map<String, String>> map = (Map<String, Map<String, String>>) dataSnapshot.getValue();

                                    Set<String> s = map.keySet();
                                    list = new ArrayList<>(s);
                                    Log.i("set", list.get(0));

                                    rows = new ArrayList<>(30);
                                    for (int i = 0; i < list.size(); i++) {
                                        row = new Row();
                                        row.setTitle(map.get(list.get(i)).get("title"));
                                        row.setSubtitle(map.get(list.get(i)).get("desc"));
                                        //=================condition for already subscribed topic============================
                                        if(Snapshot.hasChild("topics")) {
                                            for (int j = 0; j < listUser.size(); j++) {
                                                if (mapUser.get(listUser.get(j)).get("title").equals(map.get(list.get(i)).get("title"))) {
                                                    row.setChecked(true);
                                                    break;
                                                }
                                            }
                                        }
                                        rows.add(row);
                                    }
                                    listView.setAdapter(new CustomArrayAdapter(SubscribeTopics.this, rows));

                                } catch (Exception e) {
                                    Log.i("error1", e.getMessage());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }catch (Exception e){
                        Log.i("error2",e.getMessage());
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        Button subButton=findViewById(R.id.buttonSubscribe);
        subButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                mUserRef.child("topics").setValue(null).addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {

                    }
                });
                for(int i=0;i<rows.size();i++)
                {
                    FirebaseMessaging.getInstance().unsubscribeFromTopic(rows.get(i).getTitle());
                    if(rows.get(i).isChecked())
                    {
                        HashMap<String,String> map=new HashMap<>();
                        map.put("title",rows.get(i).getTitle());
                        map.put("desc",rows.get(i).getSubtitle());
                        final int j=i;
                        mUserRef.child("topics").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                MainActivity.topicsSubscribed.edit().putBoolean(rows.get(j).getTitle(),true).apply();
                                if(j==rows.size()-1)
                                {
                                    startActivity(new Intent(SubscribeTopics.this,MainActivity.class));
                                    finish();
                                }
                            }
                        });
                    }
                    if(i==rows.size()-1)
                    {
                        startActivity(new Intent(SubscribeTopics.this,MainActivity.class));
                        finish();
                    }
                }

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }

    @Override
    public void onComplete() {
        Toast.makeText(this, "Loading_Complete", Toast.LENGTH_LONG).show();
    }
}