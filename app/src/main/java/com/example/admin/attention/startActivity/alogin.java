package com.example.admin.attention.startActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.admin.attention.R;
import com.example.admin.attention.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class alogin extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private DatabaseReference mUsersData,mRootRef;
    private FirebaseUser mCurrentUser;
    private AlphaAnimation ap;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        ap=new AlphaAnimation(1f,0.5f);

        pd=new ProgressDialog(this);

        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mRootRef= FirebaseDatabase.getInstance().getReference();
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        Button loginButton=findViewById(R.id.buttonLLogin);

        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.i("dialog","login clicked");
                EditText emailAuto=findViewById(R.id.editTextLoginEmail);
                EditText passwordAuto=findViewById(R.id.editTextLoginPassword);
                String email=emailAuto.getText().toString();
                String password=passwordAuto.getText().toString();
                if(email.isEmpty())
                    Snackbar.make(findViewById(R.id.buttonLLogin),"Invalid Email...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                else if(password.isEmpty() || password.length()<6)
                    Snackbar.make(findViewById(R.id.buttonLLogin),"Invalid Password...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                else
                {
                    pd.show();
                    signIn(email,password, findViewById(R.id.buttonLLogin));
                }
            }
        });
    }

    public void signIn(String email, String password, final View view)
    {
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener( this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information


                            mUsersData= FirebaseDatabase.getInstance().getReference().child("users").child(task.getResult().getUser().getUid());
                            mUsersData.keepSynced(true);
                            mUsersData.addValueEventListener(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    MainActivity.topicsSubscribed.edit().putString("CollegeCode",dataSnapshot.child("ccode").getValue().toString()).apply();
                                    MainActivity.topicsSubscribed.edit().putString("semester",dataSnapshot.child("year").getValue().toString()).apply();
                                    MainActivity.topicsSubscribed.edit().putString("branch",dataSnapshot.child("branch").getValue().toString()).apply();
                                    MainActivity.topicsSubscribed.edit().putString("section",dataSnapshot.child("sec").getValue().toString()).apply();
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

                            Log.d("LogIn", "signInWithEmail:success");
                            pd.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                            Intent intent=new Intent(alogin.this, MainActivity.class);
                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK| Intent.FLAG_ACTIVITY_CLEAR_TASK);
                            startActivity(intent);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LogIn", "signInWithEmail:failure", task.getException());
                            pd.dismiss();
                            if(task.getException().getMessage().contains("The password is invalid or the user does not have a password.")  ||
                                    task.getException().getMessage().contains("There is no user record corresponding to this identifier. The user may have been deleted."))
                                Snackbar.make(view,"Email or Password is incorrect...", Snackbar.LENGTH_LONG).setAction("Action",null).show();
                            else
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                        }

                        // ...
                    }
                });
    }

}
