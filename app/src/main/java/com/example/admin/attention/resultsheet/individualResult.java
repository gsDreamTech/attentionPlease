package com.example.admin.attention.resultsheet;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class individualResult extends AppCompatActivity {

    private DatabaseReference mData;
    private List<String> listData;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_individual_result);
        Log.i("usndata",getIntent().getExtras().getString("usn"));
        try {
            mData = FirebaseDatabase.getInstance().getReference().child("Colleges")
                    .child(MainActivity.topicsSubscribed.getString("CollegeCode", "")).child("results").child("2018")
                    .child("1").child("CSE").child("data").child(getIntent().getExtras().getString("usn"));
            mData.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {

                    GenericTypeIndicator<List<String>> gs = new GenericTypeIndicator<List<String>>() {
                    };
                    listData = dataSnapshot.getValue(gs);

                    init();
//                    for (int i = 0; i < listData.size(); i = i + 4) {
//
//                        int i=0;

//                        LinearLayout row_result = new LinearLayout(getApplicationContext());
//                        row_result.setOrientation(LinearLayout.HORIZONTAL);
//
//
//                        TextView tv_subject = new TextView(getApplicationContext());
//                        tv_subject.setText(listData.get(i));
//                        row_result.addView(tv_subject);
////
//                        TextView tv_attendance = new TextView(getApplicationContext());
//                        tv_attendance.setText(listData.get(i + 1));
//                        row_result.addView(tv_attendance);
//
//                        TextView tv_cie = new TextView(getApplicationContext());
//                        tv_cie.setText(listData.get(i + 2));
//                        row_result.addView(tv_cie);
//
//                        TextView tv_grade = new TextView(getApplicationContext());
//                        tv_grade.setText(listData.get(i + 3));
//                        row_result.addView(tv_grade);
//
//
//                    }
//

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });


            TextView usn = findViewById(R.id.usntv);
            TextView name = findViewById(R.id.nametv);
            TextView cgpa = findViewById(R.id.cgpatv);

        }catch(Exception e){
            Log.i("pop up error",e.getMessage());
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }
    void init()
    {

        try {
            Log.i("heyyy", listData.get(0));
        }
        catch(Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
        }

    }
}
