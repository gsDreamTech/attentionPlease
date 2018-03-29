package com.example.admin.attention.resultsheet;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.admin.attention.R;

public class result_layout extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result_layout);
        final TextView tvUsn=findViewById(R.id.usnresults);
        Button submit = findViewById(R.id.but_submitresult);
        submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent(result_layout.this,individualResult.class);
                intent.putExtra("usn",tvUsn.getText().toString());
                startActivity(intent);
            }
        });

    }
}
