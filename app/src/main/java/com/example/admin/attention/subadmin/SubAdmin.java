package com.example.admin.attention.subadmin;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

public class SubAdmin extends AppCompatActivity {

    private EditText nameText,usnText,descText;
    private Button reqButton;
    private DatabaseReference mData;
    private HashMap<String,String> map;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sub_admin);

        map=new HashMap<>();

        nameText=findViewById(R.id.namesubadmin);
        usnText=findViewById(R.id.usnsubadmin);
        descText=findViewById(R.id.descriptionsubadmin);
        reqButton=findViewById(R.id.sendsubadmin);


        MainActivity.mDatabaseRef.child(FirebaseAuth.getInstance().getUid()).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                nameText.setText(dataSnapshot.child("username").getValue().toString());
                usnText.setText(dataSnapshot.child("usn").getValue().toString());
                map.put("name",dataSnapshot.child("username").getValue().toString());
                map.put("usn",dataSnapshot.child("usn").getValue().toString());
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        try {
            mData = FirebaseDatabase.getInstance().getReference().child("Colleges")
                    .child(MainActivity.topicsSubscribed.getString("CollegeCode", "")).child("adminrequests");
        }
        catch (Exception e)
        {
            Log.i("subadmin error",e.getMessage());
        }
        reqButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(descText.getText().toString().length()<40 || descText.getText().toString().length()>100)
                {
                    Toast.makeText(getApplicationContext(),"Number of characters in the description must be between 40 and 100",Toast.LENGTH_LONG).show();

                }else {

                    map.put("desc",descText.getText().toString());
                    map.put("uid",FirebaseAuth.getInstance().getUid());
                    mData.push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Toast.makeText(getApplicationContext(),"Request sent sucessfully",Toast.LENGTH_LONG).show();
                        }
                    });
                }
            }
        });







    }
}
