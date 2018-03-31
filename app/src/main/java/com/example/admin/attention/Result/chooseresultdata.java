package com.example.admin.attention.Result;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class chooseresultdata extends AppCompatActivity {

    private Spinner resSpinner,resSemS,resBranchS;
    private CheckBox resCheck;
    private EditText resUsn;
    private DatabaseReference resData;
    private Map<String,String> resTitle;
    private Button resQuery;
    public static List<String> rowHeading,colHeading;
    public static Map<String,List<String>> cellValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chooseresultdata);
        Log.i("ccode",MainActivity.topicsSubscribed.getString("CollegeCode",""));


        String[] semester={"1","2","3","4","5","6","7","8"};
        String[] branches={"CSE","MECH","IS","EEE","EC"};

        resSpinner=findViewById(R.id.resultTitleSpinner);
        resCheck=findViewById(R.id.resultCheckbox);
        resUsn=findViewById(R.id.resultUsnEditText);
        resSemS=findViewById(R.id.resultSemSpinner);
        resBranchS=findViewById(R.id.resultBranchSpinner);
        ArrayAdapter<String> semAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,semester);
        resSemS.setAdapter(semAdapter);
        ArrayAdapter<String> branchesAdapter=new ArrayAdapter<String>(this,android.R.layout.simple_spinner_dropdown_item,branches);
        resBranchS.setAdapter(branchesAdapter);
        resQuery=findViewById(R.id.resultButton);
        resQuery.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            try {
                Log.i("cicked1",resSpinner.getSelectedItem().toString());


                resData=FirebaseDatabase.getInstance().getReference().child("Colleges")
                        .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("results")
                        .child(resSpinner.getSelectedItem().toString()).child(resSemS.getSelectedItem().toString())
                        .child(resBranchS.getSelectedItem().toString());
                resData.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        Log.i("map  error",dataSnapshot.getValue().toString());



                        GenericTypeIndicator<List<String>> gs = new GenericTypeIndicator<List<String>>() {
                        };
                        colHeading = dataSnapshot.child("headings").getValue(gs);


                        Map<String, List<String>> mapUser = (Map<String, List<String>>) dataSnapshot.child("data").getValue();
                        rowHeading = new ArrayList<>(mapUser.keySet());


                        cellValue = (Map<String,List<String>>)dataSnapshot.child("data").getValue();
                        //Log.i("datas",cellValue.get(rowHeading.get(0)).get(0));
                        startActivity(new Intent(chooseresultdata.this,result.class));
                        finish();
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


//                resData.child("results").child(resSpinner.getSelectedItem().toString()).child(resSemS.getSelectedItem().toString())
//                        .child(resBranchS.getSelectedItem().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
//                    @Override
//                    public void onDataChange(DataSnapshot dataSnapshot) {
//                        GenericTypeIndicator<List<String>> gs = new GenericTypeIndicator<List<String>>() {
//                        };
//                        colHeading = dataSnapshot.child("headings").getValue(gs);
//                        Log.i("map  error",dataSnapshot.getValue().toString());
//                        try {
//                            Map<String, List<String>> mapUser = (Map<String, List<String>>) dataSnapshot.child("data").getValue();
//                            rowHeading = new ArrayList<>(mapUser.keySet());
//                        }catch (Exception e)
//                        {
//                            Log.i("map  error",e.getMessage());
//                        }
//
//                        GenericTypeIndicator<List<List<String>>> gs1 = new GenericTypeIndicator<List<List<String>>>() {
//                        };
//                        cellValue = dataSnapshot.child("data").getValue(gs1);
//                    }
//
//                    @Override
//                    public void onCancelled(DatabaseError databaseError) {
//
//                    }
//                });
            }catch (Exception e)
            {
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
            }
        });





        resData=FirebaseDatabase.getInstance().getReference().child("Colleges")
                .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("results");

        resData.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
//                GenericTypeIndicator<List<String>> gs=new GenericTypeIndicator<List<String>>(){};
                resTitle= (Map<String, String>) dataSnapshot.child("result_years").getValue();
                List<String> list= new ArrayList<>(resTitle.values());

                Log.i("title", String.valueOf(list.get(0)));
                String[] data=new String[list.size()];
                for(int i=0;i<list.size();i++)
                {
                    data[i]=list.get(i).split("-")[0];
                }
                ArrayAdapter<String> titleAdapter=new ArrayAdapter<String>(getApplicationContext(),android.R.layout.simple_spinner_dropdown_item,data );
                resSpinner.setSelection(1);
                resSpinner.setAdapter(titleAdapter);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        resCheck.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if(compoundButton.isChecked())
                {
                    resUsn.setVisibility(View.VISIBLE);
                }
                else
                {
                    resUsn.setVisibility(View.GONE);
                }
            }
        });








    }
}
