package com.example.admin.attention.SeatAllotment;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.dd.processbutton.iml.ActionProcessButton;
import com.example.admin.attention.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.List;
import java.util.Map;

public class seatAllotment extends AppCompatActivity {

    private TableLayout tl;
    private TableRow tr;
    private DatabaseReference mSeat;

    private TextInputEditText usnText,branchtext;
    private RecyclerView recyclerView;
    private List<Map<String,String>> list;
    private ProgressDialog pd;
    private SharedPreferences timeShared;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_seta_allotment);


        timeShared=this.getSharedPreferences("com.example.lenovo.seatallotment", Context.MODE_PRIVATE);
        Button submit = findViewById(R.id.button);
        usnText = findViewById(R.id.usnInput);
        //tl = findViewById(R.id.table_head);
        pd= new ProgressDialog(this);
        pd.setTitle("Fetching data!");
        pd.setMessage("Please wait..");

        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //pd.show();
                if( usnText.getText().toString().equals("") )
                {
                    Toast.makeText(getApplicationContext(),"Please enter a valid data...",Toast.LENGTH_LONG).show();
                    pd.dismiss();
                }
                else {

                    try{
                        mSeat= FirebaseDatabase.getInstance().getReference().child("Colleges").child("C-1297").child("Seat")
//                                .child(branchtext.getText().toString().toLowerCase().replace(" ",""))
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
    void display(){
//        tl.removeAllViews();
//        TableRow r=findViewById(R.id.tableheadrow);
//        tl.addView(r);
//        for(int row=0;row<list.size();row++)
//        {
//            TableRow trr = new TableRow(this);
//            if(row%2==0)
//                trr.setBackgroundColor(Color.argb(255,199,217,226));
//            else
//                trr.setBackgroundColor(Color.argb(255,233,243,249));
//            for(int col=0;col<list.get(row).size();col++)
//            {
//                TextView tv= new TextView(this);
//                tv.setText(list.get(row).get(col));
//                TableRow.LayoutParams params = new TableRow.LayoutParams(TableRow.LayoutParams.MATCH_PARENT, TableRow.LayoutParams.MATCH_PARENT,1.0f);
//                tv.setLayoutParams(params);
//                tv.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);
//                trr.addView(tv);
//            }
//            tl.addView(trr);
//        }
    }

}
