package com.example.admin.attention.TimeTable;

import android.annotation.SuppressLint;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

public class saturdayFragment extends Fragment {

    View view;
    private DatabaseReference mTimeTable;
    private List<List<String>> list;
    private LinearLayout rowLayout,timeLinear,dayslayout;
    private SharedPreferences timeSharedPreference;


    public saturdayFragment() {

    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.saturday_tt,container,false);
        timeLinear=view.findViewById(R.id.timeLinearLayout);
        try {
//            sectionSpinner.setSelection(sectionAdapter.getPosition(MainActivity.topicsSubscribed.getString("section", "")));
//            semSpinner.setSelection(semesterAdapter.getPosition(MainActivity.topicsSubscribed.getString("semester", "")));
//            branchSpinner.setSelection(branchAdapter.getPosition(MainActivity.topicsSubscribed.getString("branch", "")));
            fetch();

        }catch (Exception e)
        {
            Toast.makeText(getContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        return view;


    }

    void fetch()
    {
        try {
            mTimeTable = FirebaseDatabase.getInstance().getReference().child("Colleges").child(MainActivity.topicsSubscribed.getString("CollegeCode", ""))
                    .child("timetables").child(MainActivity.topicsSubscribed.getString("semester", ""))
                    .child(MainActivity.topicsSubscribed.getString("branch", "")).child(MainActivity.topicsSubscribed.getString("section", ""));
            mTimeTable.keepSynced(true);

            mTimeTable.addValueEventListener(new ValueEventListener() {
                @SuppressLint("NewApi")
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    GenericTypeIndicator<List<List<String>>> genericTypeIndicator = new GenericTypeIndicator<List<List<String>>>() {
                    };
                    list = dataSnapshot.getValue(genericTypeIndicator);
//                    setbutton(timeSharedPreference.getInt("day",1));
                    init(6);

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {
            Toast.makeText(getContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }


    @SuppressLint("NewApi")
    void init(int choice){
        //timeLinear.removeAllViews();
        try {
            for (int i = 0; i < list.size(); i++) {

                LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutparams.setMargins(15,15,15,0);
                layoutparams.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams  params= new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,30,0,30);

                rowLayout = new LinearLayout(getContext());
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setBackgroundResource(R.drawable.time_table_back_ground);
                rowLayout.setLayoutParams(layoutparams);


                TextView time = new TextView(getContext());
                time.setText(list.get(i).get(0));
                time.setLayoutParams(params);
                time.setTextSize(26);
                time.setTypeface(null, Typeface.BOLD);
                time.setPadding(15,0,0,0);


                TextView sub = new TextView(getContext());
                sub.setText(list.get(i).get(choice + 1));
                sub.setTextSize(26);
                sub.setLayoutParams(params);
                sub.setTypeface(null, Typeface.BOLD);
                sub.setTextAlignment(TextView.TEXT_ALIGNMENT_CENTER);


                rowLayout.addView(time);
                rowLayout.addView(sub);
                timeLinear.addView(rowLayout);


            }
        }catch(Exception e)
        {
            Log.i("error in row logic ",e.getMessage());
        }




    }

}
