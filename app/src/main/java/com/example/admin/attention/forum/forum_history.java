package com.example.admin.attention.forum;

import android.app.Dialog;
import android.content.Context;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.NewsFeed.rowNewsFeed;
import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ServerValue;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.HashMap;
import java.util.List;

public class forum_history extends AppCompatActivity {


    private DatabaseReference mForumData;
    private RecyclerView mForumLayout;
    private FloatingActionButton postQuestion;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forum_history);

        mForumLayout=findViewById(R.id.forumRecycler);
        postQuestion=findViewById(R.id.fabadd);
        postQuestion.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                View v=View.inflate(forum_history.this,R.layout.forum_question,null);
                final Dialog dg=new Dialog(forum_history.this);
                dg.setContentView(v);
                final EditText tet=v.findViewById(R.id.editTextQuestion);
                Button but=v.findViewById(R.id.postQuesionButton);
                but.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        HashMap<String,String> map=new HashMap<String, String>();
                        map.put("question",tet.getText().toString());
                        map.put("uid", FirebaseAuth.getInstance().getUid());
                        FirebaseDatabase.getInstance().getReference().child("Colleges")
                                .child(MainActivity.topicsSubscribed.getString("CollegeCode",""))
                                .child("forum").push().setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                dg.dismiss();
                            }
                        });
                    }
                });
                dg.show();




            }
        });




        mForumData = FirebaseDatabase.getInstance().getReference().child("Colleges")
                .child(MainActivity.topicsSubscribed.getString("CollegeCode","")).child("forum");
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(this);
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        mForumLayout.setLayoutManager(linearLayoutManager);



        FirebaseRecyclerAdapter<row_forum,forumViewHolder> firebaseAdapter=new FirebaseRecyclerAdapter<row_forum, forumViewHolder>(
                row_forum.class,
                R.layout.row_forum,
                forumViewHolder.class,
                mForumData
        ) {
            @Override
            protected void populateViewHolder(final forumViewHolder viewHolder, final row_forum model, int position) {
                getRef(position).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(dataSnapshot.hasChild("question"))
                        viewHolder.setQuestion(dataSnapshot.child("question").getValue().toString());
                        if(dataSnapshot.hasChild("answer"))
                        viewHolder.setAnswer(dataSnapshot.child("answer").getValue().toString());




                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });


            }
        };
        mForumLayout.setAdapter(firebaseAdapter);






    }



    public static class forumViewHolder extends RecyclerView.ViewHolder{
        View mView;
        public forumViewHolder(View itemView) {
            super(itemView);
            mView=itemView;
        }

        public void setQuestion(String s){
            TextView tv=mView.findViewById(R.id.textViewQuestion);
            tv.setText(s);
        }

        public void setAnswer(String s){
            TextView tv=mView.findViewById(R.id.textViewAnswer);
            tv.setText(s);
        }


    }
}
