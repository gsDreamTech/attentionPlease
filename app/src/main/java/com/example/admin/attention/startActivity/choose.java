package com.example.admin.attention.startActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.constraint.ConstraintLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.RelativeLayout;
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

public class choose extends AppCompatActivity {
    private Button fab,fab1;
    private ConstraintLayout rv;
    private FirebaseAuth mAuth;
    private ProgressDialog pd;
    private DatabaseReference mUsersData,mRootRef;
    private FirebaseUser mCurrentUser;
    private View dialogView;
    private Dialog dialog;
    private AlphaAnimation ap;






    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_choose);

        ap=new AlphaAnimation(1f,0.5f);

        pd=new ProgressDialog(this);

        pd.setCanceledOnTouchOutside(false);

        mAuth = FirebaseAuth.getInstance();
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();



        rv=findViewById(R.id.layoutChoose);
        fab =  findViewById(R.id.buttonLogIn);
        fab.setAnimation(ap);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //rv.setVisibility(View.GONE);
                //showDiag();
                startActivity(new Intent(choose.this, alogin.class));

            }
        });

        fab1 =  findViewById(R.id.buttonNewUser);
        fab1.setAnimation(ap);
        fab1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent intent=new Intent(choose.this, NewUser.class);
                startActivity(intent);

            }
        });
    }

    private void showDiag() {

//        dialogView = View.inflate(this,R.layout.login,null);
//
//        dialog = new Dialog(this,R.style.MyAlertDialogStyle);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);
//        dialog.getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
//
//        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
//        dialog.setContentView(dialogView);
//        //dialog.setCanceledOnTouchOutside(false);
//
//
//        dialog.setOnShowListener(new DialogInterface.OnShowListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public void onShow(DialogInterface dialogInterface) {
//                revealShow(dialogView, true, null, R.id.login_form);
//            }
//        });
//
//        dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
//            @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//            @Override
//            public boolean onKey(DialogInterface dialogInterface, int i, KeyEvent keyEvent) {
//                if (i == KeyEvent.KEYCODE_BACK){
//
//                    revealShow(dialogView, false, dialog, R.id.login_form);
//                    return true;
//                }
//
//                return false;
//            }
//        });
//
//            pd.setTitle("Loging in");
//            pd.setMessage("Wait for a while...");
//            FloatingActionButton imageView = dialogView.findViewById(R.id.backDialogImage);
//            imageView.setOnClickListener(new View.OnClickListener() {
//                @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
//                @Override
//                public void onClick(View v) {
//
//                    revealShow(dialogView, false, dialog, R.id.login_form);
//                }
//            });
//            Button loginButton=dialogView.findViewById(R.id.buttonLLogin);
//
//            loginButton.setOnClickListener(new View.OnClickListener() {
//                @Override
//                public void onClick(View view) {
//                    Log.i("dialog","login clicked");
//                    AutoCompleteTextView emailAuto=dialogView.findViewById(R.id.editTextLoginEmail);
//                    AutoCompleteTextView passwordAuto=dialogView.findViewById(R.id.editTextLoginPassword);
//                    String email=emailAuto.getText().toString();
//                    String password=passwordAuto.getText().toString();
//                    if(email.isEmpty())
//                        Snackbar.make(dialogView,"Invalid Email...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
//                    else if(password.isEmpty() || password.length()<6)
//                        Snackbar.make(dialogView,"Invalid Password...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
//                    else
//                    {
//                        pd.show();
//                        signIn(email,password, dialogView);
//                    }
//                }
//            });
//
//
//
//
//
//
//        dialog.getWindow().setGravity(Gravity.BOTTOM);
//        dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));
//
//        dialog.show();
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
                    Intent intent=new Intent(dialog.getContext(), MainActivity.class);
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
                        Toast.makeText(dialog.getContext(), "Authentication failed.",
                                Toast.LENGTH_SHORT).show();
                }

                    // ...
                }
            });
    }



    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    private void revealShow(View dialogView, boolean b, final Dialog dialog, int dialoglayout) {

        final View view = dialogView.findViewById(dialoglayout);

        int w = view.getWidth();
        int h = view.getHeight();

        int endRadius = (int) Math.hypot(w, h);

        int cx = (int) (fab.getX() + (fab.getWidth()/2));
        int cy = (int) (fab.getY())+ fab.getHeight() + 56;


        if(b){
            Animator revealAnimator = ViewAnimationUtils.createCircularReveal(view, cx,cy, 0, endRadius);

            view.setVisibility(View.VISIBLE);
            revealAnimator.setDuration(1000);
            revealAnimator.start();

        } else {

            Animator anim =
                    ViewAnimationUtils.createCircularReveal(view, cx, cy, endRadius, 0);

            anim.addListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    super.onAnimationEnd(animation);
                    dialog.dismiss();
                    view.setVisibility(View.INVISIBLE);
                    rv.setVisibility(View.VISIBLE);

                }
            });
            anim.setDuration(1000);
            anim.start();
        }

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
    }
}
