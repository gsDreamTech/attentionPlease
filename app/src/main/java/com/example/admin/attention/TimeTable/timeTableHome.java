package com.example.admin.attention.TimeTable;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Typeface;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.webkit.WebChromeClient;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Spinner;
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

import org.w3c.dom.Text;

import java.util.List;

public class timeTableHome extends AppCompatActivity implements View.OnClickListener {

    private DatabaseReference mTimeTable;
    private List<List<String>> list;
    private LinearLayout rowLayout,timeLinear,dayslayout;
    private SharedPreferences timeSharedPreference;
    Button button[];
    @SuppressLint("NewApi")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_time_table_home);
        timeSharedPreference=this.getSharedPreferences("com.example.admin.attention.TimeTable", Context.MODE_PRIVATE);


        timeLinear=findViewById(R.id.timeLinearLayout);
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


        LayoutTransition lt=new LayoutTransition();
        lt.enableTransitionType(LayoutTransition.CHANGING);
        lt.setDuration(500);

        dayslayout=findViewById(R.id.dayslayout);
        dayslayout.setLayoutTransition(lt);


        button=new Button[7];
        button[0]=findViewById(R.id.buttonMonday);
        button[1]=findViewById(R.id.buttonTuesday);
        button[2]=findViewById(R.id.buttonWednesday);
        button[3]=findViewById(R.id.buttonThursday);
        button[4]=findViewById(R.id.buttonFriday);
        button[5]=findViewById(R.id.buttonSaturday);
        button[6]=findViewById(R.id.buttonSunday);
        for(int i=0;i<7;i++)
            button[i].setOnClickListener(this);


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
            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_SHORT).show();
        }



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
                    setbutton(timeSharedPreference.getInt("day",1));
                    init(timeSharedPreference.getInt("day",1));

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


    @SuppressLint("NewApi")
    void init(int choice){
        timeLinear.removeAllViews();
        try {
            for (int i = 0; i < list.size(); i++) {

                LinearLayout.LayoutParams layoutparams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
                layoutparams.setMargins(15,15,0,0);
                layoutparams.gravity = Gravity.CENTER_VERTICAL;

                LinearLayout.LayoutParams  params= new LinearLayout.LayoutParams(200, LinearLayout.LayoutParams.WRAP_CONTENT);
                params.setMargins(0,30,0,30);

                rowLayout = new LinearLayout(this);
                rowLayout.setOrientation(LinearLayout.HORIZONTAL);
                rowLayout.setBackgroundResource(R.drawable.time_table_back_ground);
                rowLayout.setLayoutParams(layoutparams);


                TextView time = new TextView(this);
                time.setText(list.get(i).get(0));
                time.setLayoutParams(params);
                time.setTextSize(26);
                time.setTypeface(null, Typeface.BOLD);
                time.setPadding(15,0,0,0);


                TextView sub = new TextView(this);
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


    @Override
    public void onClick(View view) {
        switch( view.getId() ){
            case R.id.buttonMonday:
                if(timeSharedPreference.getInt("day",0)==1)
                    break;
                setbutton(1);
                timeSharedPreference.edit().putInt("day",1).apply();
                fetch();
                break;
            case R.id.buttonTuesday:
                if(timeSharedPreference.getInt("day",0)==2)
                    break;
                setbutton(2);
                timeSharedPreference.edit().putInt("day",2).apply();
                fetch();
                break;
            case R.id.buttonWednesday:
                if(timeSharedPreference.getInt("day",0)==3)
                    break;
                setbutton(3);
                timeSharedPreference.edit().putInt("day",3).apply();
                fetch();
                break;
            case R.id.buttonThursday:
                if(timeSharedPreference.getInt("day",0)==4)
                    break;
                setbutton(4);
                timeSharedPreference.edit().putInt("day",4).apply();
                fetch();
                break;
            case R.id.buttonFriday:
                if(timeSharedPreference.getInt("day",0)==5)
                    break;
                setbutton(5);
                timeSharedPreference.edit().putInt("day",5).apply();
                fetch();
                break;
            case R.id.buttonSaturday:
                if(timeSharedPreference.getInt("day",0)==6)
                    break;
                setbutton(6);
                timeSharedPreference.edit().putInt("day",6).apply();
                fetch();
                break;
            case R.id.buttonSunday:
                if(timeSharedPreference.getInt("day",0)==7)
                    break;
                setbutton(7);
                timeSharedPreference.edit().putInt("day",7).apply();
                fetch();
                break;
        }
    }

    void setbutton(int  choice)
    {
        for(int i=0;i<7;i++)
        {
            if(i==choice-1)
                button[i].setBackgroundResource(R.drawable.days_selected_background);
            else
                button[i].setBackgroundResource(R.drawable.days_background);
        }
    }
}



//================older code====================

//
//
//package com.example.admin.attention.TimeTable;
//
//        import android.annotation.SuppressLint;
//        import android.graphics.Color;
//        import android.support.v7.app.AppCompatActivity;
//        import android.os.Bundle;
//        import android.util.Log;
//        import android.view.Gravity;
//        import android.view.MotionEvent;
//        import android.view.View;
//        import android.widget.AdapterView;
//        import android.widget.ArrayAdapter;
//        import android.widget.RelativeLayout;
//        import android.widget.Spinner;
//        import android.widget.TableRow;
//        import android.widget.TextView;
//        import android.widget.Toast;
//
//        import com.example.admin.attention.R;
//        import com.example.admin.attention.main.MainActivity;
//        import com.google.firebase.database.DataSnapshot;
//        import com.google.firebase.database.DatabaseError;
//        import com.google.firebase.database.DatabaseReference;
//        import com.google.firebase.database.FirebaseDatabase;
//        import com.google.firebase.database.GenericTypeIndicator;
//        import com.google.firebase.database.ValueEventListener;
//
//        import java.util.List;
//
//public class timeTableHome extends AppCompatActivity {
//
//    private DatabaseReference mTimeTable;
//    private List<List<String>> list;
//    private TableRow rowTable[];
//    private RelativeLayout relativeLayout;
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_time_table_home);
//        rowTable=new TableRow[7];
//        relativeLayout=findViewById(R.id.timeRelative);
//        String branch[]={"CSE", "MECH", "EEE", "EC", "IS", "IT", "ARCHI", "BT"};
//        String section[]={"A","B","C","D","E","F","G","H","I","J","K","L","M"};
//        String sem[]={"1","2","3","4"};
//        Spinner semSpinner=findViewById(R.id.spinnerYear);
//        Spinner branchSpinner=findViewById(R.id.spinnerBranch);
//        Spinner sectionSpinner=findViewById(R.id.spinnerSection);
//        ArrayAdapter<String> semesterAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,sem);
//        ArrayAdapter<String> branchAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,branch);
//        ArrayAdapter<String> sectionAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,section);
//        semSpinner.setAdapter(semesterAdapter);
//        branchSpinner.setAdapter(branchAdapter);
//        sectionSpinner.setAdapter(sectionAdapter);
//
//
//        branchSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                MainActivity.topicsSubscribed.edit().putString("branch",adapterView.getSelectedItem().toString()).apply();
//                fetch();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        semSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                MainActivity.topicsSubscribed.edit().putString("semester",adapterView.getSelectedItem().toString()).apply();
//                fetch();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        sectionSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
//                MainActivity.topicsSubscribed.edit().putString("section",adapterView.getSelectedItem().toString()).apply();
//                fetch();
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//        try {
//            sectionSpinner.setSelection(sectionAdapter.getPosition(MainActivity.topicsSubscribed.getString("section", "")));
//            semSpinner.setSelection(semesterAdapter.getPosition(MainActivity.topicsSubscribed.getString("semester", "")));
//            branchSpinner.setSelection(branchAdapter.getPosition(MainActivity.topicsSubscribed.getString("branch", "")));
//            fetch();
//        }catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
//
//
//        final ZoomLinearLayout zoomLinearLayout =findViewById(R.id.timeRelative);
//        zoomLinearLayout.setOnTouchListener(new View.OnTouchListener() {
//            @Override
//            public boolean onTouch(View v, MotionEvent event) {
//                zoomLinearLayout.init(timeTableHome.this);
//                return false;
//            }
//        });
//    }
//
//    void fetch()
//    {
//        try {
//            mTimeTable = FirebaseDatabase.getInstance().getReference().child("Colleges").child(MainActivity.topicsSubscribed.getString("CollegeCode", ""))
//                    .child("timetables").child(MainActivity.topicsSubscribed.getString("semester", ""))
//                    .child(MainActivity.topicsSubscribed.getString("branch", "")).child(MainActivity.topicsSubscribed.getString("section", ""));
//            mTimeTable.keepSynced(true);
//
//            mTimeTable.addValueEventListener(new ValueEventListener() {
//                @SuppressLint("NewApi")
//                @Override
//                public void onDataChange(DataSnapshot dataSnapshot) {
//                    GenericTypeIndicator<List<List<String>>> genericTypeIndicator = new GenericTypeIndicator<List<List<String>>>() {
//                    };
//                    list = dataSnapshot.getValue(genericTypeIndicator);
//                    init();
//
//                }
//
//                @Override
//                public void onCancelled(DatabaseError databaseError) {
//
//                }
//            });
//        }catch (Exception e)
//        {
//            Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
//        }
//    }
//
//    @SuppressLint("ResourceAsColor")
//    void init()
//    {
//
//
//        //tableLayout=findViewById(R.id.rowTableLayout);
//        for(int i=0;i<7;i++)
//        {
//            //rowTable[i]=new TableRow(getApplicationContext());//findViewById(R.id.timeTableRow);
//
//            //rowTable[i].setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f));
//
//        }
//        rowTable[0]=findViewById(R.id.timeTableRow);
//        rowTable[1]=findViewById(R.id.monTableRow);
//        rowTable[2]=findViewById(R.id.tueTableRow);
//        rowTable[3]=findViewById(R.id.wedTableRow);
//        rowTable[4]=findViewById(R.id.thuTableRow);
//        rowTable[5]=findViewById(R.id.friTableRow);
//        rowTable[6]=findViewById(R.id.satTableRow);
//        for(int row=0;row<7;row++)
//            rowTable[row].removeAllViews();
//        if(list==null)
//        {
//            Toast.makeText(getApplicationContext(),"Time Table is not available for this section...",Toast.LENGTH_SHORT).show();
//            return;
//        }
//        for(int col=0;col<list.size();col++)
//        {
//            for(int row=0;row<list.get(col).size()-1;row++)
//            {
//
//                Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
//                TextView tv=new TextView(this);
////                tv.setWidth(TableRow.LayoutParams.MATCH_PARENT);
////                tv.setHeight(TableRow.LayoutParams.MATCH_PARENT);
//                TableRow.LayoutParams params=new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f);
//                params.setMargins(0,0,2,2);
//                tv.setLayoutParams(params);
//                //tv.setTextAppearance(R.style.tablecells);
//                tv.setGravity(Gravity.CENTER_VERTICAL);
//                tv.setTextSize(20f);
//
//                tv.setGravity(Gravity.CENTER_HORIZONTAL|Gravity.CENTER_VERTICAL);
//
//
//                if(row==0)
//                {
//                    Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
//                    tv.setText(list.get(col).get(row).concat("-").concat(list.get(col).get(row+1)));
//                    rowTable[row].addView(tv);
//                    row++;
//                    tv.setBackgroundColor(Color.argb(255,91,199,250));
//                    tv.setTextColor(Color.WHITE);
//
//                }
//                else{
//                    Log.i("row",col+"  ,  "+row+"  ,  "+list.get(col).get(row));
//                    tv.setText(list.get(col).get(row));
//                    rowTable[row-1].addView(tv);
//
//                    //tv.setBackgroundColor(Color.WHITE);
//                    tv.setTextColor(Color.BLACK);
//                }
//            }
//        }
//
//        for(int row=0;row<7;row++);
//        //tableLayout.addView(rowTable[row]);
//
//    }
//
//
//
//}

