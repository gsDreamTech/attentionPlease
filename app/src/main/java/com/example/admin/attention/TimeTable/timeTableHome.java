package com.example.admin.attention.TimeTable;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TableRow;
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

public class timeTableHome extends AppCompatActivity {

    private DatabaseReference mTimeTable;
    private List<List<String>> list;
    private TableRow rowTable[];
    private RelativeLayout relativeLayout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_home);
        rowTable=new TableRow[7];
        relativeLayout=findViewById(R.id.timeRelative);
        String branch[]={"CSE", "MECH", "EEE", "EC", "IS", "IT", "ARCHI", "BT"};
        String section[]={"A","B","C","D","E","F","G","H","I","J","K","L","M"};
        String sem[]={"1","2","3","4"};
        Spinner semSpinner=findViewById(R.id.spinnerYear);
        Spinner branchSpinner=findViewById(R.id.spinnerBranch);
        Spinner sectionSpinner=findViewById(R.id.spinnerSection);
        ArrayAdapter<String> semesterAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,sem);
        ArrayAdapter<String> branchAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,branch);
        ArrayAdapter<String> sectionAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,section);
        semSpinner.setAdapter(semesterAdapter);
        branchSpinner.setAdapter(branchAdapter);
        sectionSpinner.setAdapter(sectionAdapter);


        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.topicsSubscribed.edit().putString("branch",adapterView.getSelectedItem().toString()).apply();
                fetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.topicsSubscribed.edit().putString("semester",adapterView.getSelectedItem().toString()).apply();
                fetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                MainActivity.topicsSubscribed.edit().putString("section",adapterView.getSelectedItem().toString()).apply();
                fetch();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        try {
            sectionSpinner.setSelection(sectionAdapter.getPosition(MainActivity.topicsSubscribed.getString("section", "")));
            semSpinner.setSelection(semesterAdapter.getPosition(MainActivity.topicsSubscribed.getString("semester", "")));
            branchSpinner.setSelection(branchAdapter.getPosition(MainActivity.topicsSubscribed.getString("branch", "")));
            fetch();
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }


        final ZoomLinearLayout zoomLinearLayout =findViewById(R.id.timeRelative);
        zoomLinearLayout.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                zoomLinearLayout.init(timeTableHome.this);
                return false;
            }
        });
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
                    init();

                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
        }catch (Exception e)
        {
            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
        }
    }

    @SuppressLint("ResourceAsColor")
    void init()
    {


        //tableLayout=findViewById(R.id.rowTableLayout);
        for(int i=0;i<7;i++)
        {
            //rowTable[i]=new TableRow(getApplicationContext());//findViewById(R.id.timeTableRow);

            //rowTable[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));

        }
        rowTable[0]=findViewById(R.id.timeTableRow);
        rowTable[1]=findViewById(R.id.monTableRow);
        rowTable[2]=findViewById(R.id.tueTableRow);
        rowTable[3]=findViewById(R.id.wedTableRow);
        rowTable[4]=findViewById(R.id.thuTableRow);
        rowTable[5]=findViewById(R.id.friTableRow);
        rowTable[6]=findViewById(R.id.satTableRow);
        for(int row=0;row<7;row++)
            rowTable[row].removeAllViews();
        if(list==null)
        {
            Toast.makeText(getApplicationContext(),"Time Table is not available for this section...",Toast.LENGTH_SHORT).show();
            return;
        }
        for(int col=0;col<list.size();col++)
        {
            for(int row=0;row<list.get(col).size()-1;row++)
            {

                Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
                TextView tv=new TextView(this);
//                tv.setWidth(TableRow.LayoutParams.MATCH_PARENT);
//                tv.setHeight(TableRow.LayoutParams.MATCH_PARENT);
                TableRow.LayoutParams params=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f);
                params.setMargins(0,0,2,2);
                tv.setLayoutParams(params);
                //tv.setTextAppearance(R.style.tablecells);
                tv.setGravity(Gravity.CENTER_VERTICAL);
                tv.setTextSize(20f);

                tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);


                if(row==0)
                {
                    Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
                    tv.setText(list.get(col).get(row).concat("-").concat(list.get(col).get(row+1)));
                    rowTable[row].addView(tv);
                    row++;
                    tv.setBackgroundColor(Color.argb(255,91,199,250));
                    tv.setTextColor(Color.WHITE);

                }
                else{
                    Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
                    tv.setText(list.get(col).get(row));
                    rowTable[row-1].addView(tv);

                    //tv.setBackgroundColor(Color.WHITE);
                    tv.setTextColor(Color.BLACK);
                }
            }
        }

        for(int row=0;row<7;row++);
            //tableLayout.addView(rowTable[row]);

    }



}
