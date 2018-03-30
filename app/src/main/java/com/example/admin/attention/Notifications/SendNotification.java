package com.example.admin.attention.Notifications;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.icu.text.DateFormat;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.admin.attention.NewsFeed.Newsfeed;
import com.example.admin.attention.R;
import com.example.admin.attention.Result.result;
import com.example.admin.attention.SeatAllotment.seatAllotment;
import com.example.admin.attention.TimeTable.timeTableHome;
import com.example.admin.attention.main.MainActivity;
import com.example.admin.attention.startActivity.choose;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import id.zelory.compressor.Compressor;

public class SendNotification extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {



    private ProgressDialog pd;
    public static DatabaseReference mDatabaseRef;
    private StorageReference
            mImageStorage;
    private FirebaseUser mCurrentUser;
    private FirebaseAuth mAuth;
    public static SharedPreferences topicsSubscribed;
    private String topics_subscribed[],topics_description[];
    private ImageButton mImage;
    private EditText mTitle,mOneLine,mDetail,mDead,mLinks,mTopics,mCollege;
    private static final int GALLERY_PICK=1;
    private boolean flag=false;
    private Uri resultUri=null;
    private String notificationId;
    private Button  logoutNavigationButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_send_notification);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);






        ActivityCompat.requestPermissions(SendNotification.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},1);

        topicsSubscribed=this.getSharedPreferences("com.example.admin.attentionplease", Context.MODE_PRIVATE);
        FirebaseMessaging.getInstance().subscribeToTopic("global");


        pd=new ProgressDialog(this);
        pd.setTitle("Sending notification!");
        pd.setMessage("Please wait...");
        pd.setCanceledOnTouchOutside(false);

        logoutNavigationButton=findViewById(R.id.logout_navigation_button);
        logoutNavigationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                pd.setTitle("Logging out");
                pd.setMessage("Please wait for a while...");
                pd.show();
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
                pd.dismiss();
                Intent intent=new Intent(SendNotification.this, MainActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            }
        });
        //mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        mAuth=FirebaseAuth.getInstance();
        mDatabaseRef= FirebaseDatabase.getInstance().getReference().child("users");
        mDatabaseRef.keepSynced(true);
        mImageStorage= FirebaseStorage.getInstance().getReference();
        /*final EditText topic=findViewById(R.id.textViewtopic);
        final EditText message=findViewById(R.id.textViewMessage);
        Button button=findViewById(R.id.butto);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                FirebaseSendMessage fsm=new FirebaseSendMessage();
                fsm.execute(topic.getText().toString(),message.getText().toString());
            }
        });*/







        mTitle=findViewById(R.id.editTextTitle);
        mOneLine=findViewById(R.id.editTextOneLine);
        mDetail=findViewById(R.id.editTextDetail);
        mDead=findViewById(R.id.editTextDead);
        mLinks=findViewById(R.id.editTextLinks);
        mTopics=findViewById(R.id.editTextTopics);
        mImage=findViewById(R.id.notiImage);
        final AlphaAnimation buttonAnimation=new AlphaAnimation(1f,0.5f);

        mImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImage.startAnimation(buttonAnimation);
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);

            }
        });


        Button button=findViewById(R.id.buttonSendNotification);
        button.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onClick(View view) {


                if(resultUri!=null )
                {
                    Log.i("image","true");
                    pd.show();
                    uploadImage();
                }
                else{
                    mDatabaseRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            pd.show();
                            notificationId= DateFormat.getDateTimeInstance().format(new Date()).replace(".","-");
                            Log.i("ID noti",notificationId);

                            Map<String, Object> updateHashmap=new HashMap<>();
                            updateHashmap.put("title",mTitle.getText().toString());
                            updateHashmap.put("one_line_desc",mOneLine.getText().toString());
                            updateHashmap.put("topics",mTopics.getText().toString());
                            updateHashmap.put("detail_desc",mDetail.getText().toString());
                            updateHashmap.put("ccode",dataSnapshot.child("ccode").getValue());
                            updateHashmap.put("links",mLinks.getText().toString());
                            updateHashmap.put("image","default");
                            updateHashmap.put("thumb_image",mLinks.getText().toString());
                            FirebaseDatabase.getInstance().getReference().child("Notifications").child(notificationId).updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    pd.dismiss();
                                    FirebaseSendMessage fsm=new FirebaseSendMessage();
                                    fsm.execute(mTopics.getText().toString(),mTitle.getText().toString());
                                    Toast.makeText(getApplicationContext(),"Updated Sucessfully.", Toast.LENGTH_LONG).show();
                                }
                            }).addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    pd.dismiss();
                                    Toast.makeText(getApplicationContext(),"Error in sending notification.", Toast.LENGTH_LONG).show();
                                }
                            });
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        });


    }


    public void uploadImage(){

        final String current_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
        final File thumb_filePath=new File(resultUri.getPath());
        Bitmap thumb_bitmap =new Compressor(this)
                .setMaxWidth(200)
                .setMaxHeight(200)
                .setQuality(75)
                .compressToBitmap(thumb_filePath);

        ByteArrayOutputStream baos=new ByteArrayOutputStream();
        thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
        final byte[] thumb_byte=baos.toByteArray();

        StorageReference filepath=mImageStorage.child("notification_images").child(notificationId+".jpg");
        final StorageReference thumb_filepath=mImageStorage.child("notification_images").child("thumbs").child(notificationId+".jpg");

        filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                if(task.isSuccessful()){
                    final String downloadUrl=task.getResult().getDownloadUrl().toString();
                    UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                    uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {
                            final String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                            if(thumb_task.isSuccessful())
                            {
                                mDatabaseRef.child(mAuth.getUid()).addValueEventListener(new ValueEventListener() {
                                    @SuppressLint("NewApi")
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        notificationId= DateFormat.getDateTimeInstance().format(new Date()).replace(".","-");
                                        Log.i("ID noti",notificationId);
                                        FirebaseSendMessage fsm=new FirebaseSendMessage();
                                        fsm.execute(mTopics.getText().toString(),mTitle.getText().toString());
                                        Map<String, Object> updateHashmap=new HashMap<>();
                                        updateHashmap.put("image",downloadUrl);
                                        updateHashmap.put("thumb_image",thumb_downloadUrl);
                                        updateHashmap.put("title",mTitle.getText().toString());
                                        updateHashmap.put("one_line_desc",mOneLine.getText().toString());
                                        updateHashmap.put("topics",mTopics.getText().toString());
                                        updateHashmap.put("detail_desc",mDetail.getText().toString());
                                        updateHashmap.put("ccode",dataSnapshot.child("ccode").getValue());
                                        updateHashmap.put("links",mLinks.getText().toString());
                                        FirebaseDatabase.getInstance().getReference().child("Notifications").child(notificationId).updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(),"Updated Sucessfully.", Toast.LENGTH_LONG).show();

                                            }
                                        });
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }
                            else {
                                pd.dismiss();

                                Toast.makeText(getApplicationContext(),"Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                    //FirebaseDatabase.getInstance().getReference().child("USERS LIST").
                }
                else {
                    pd.dismiss();
                    Toast.makeText(getApplicationContext(),"Error in uploading.", Toast.LENGTH_LONG).show();
                }
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==GALLERY_PICK && resultCode==RESULT_OK)
        {
            Uri imageUri=data.getData();
            CropImage.activity(imageUri)
                    .setAspectRatio(1,1)
                    .start(this);


        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                resultUri=result.getUri();
                Glide.with(this).load(resultUri).into(mImage);
                flag=true;
            }
            else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error=result.getError();
            }
        }
    }



    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }



    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();



        if (id == R.id.notification_id) {



        } else if (id == R.id.timetable_id) {

            startActivity(new Intent(SendNotification.this,timeTableHome.class));
            finish();

        } else if (id == R.id.settings_id) {


        } else if (id == R.id.results_id) {

            startActivity(new Intent(SendNotification.this,result.class));
            finish();
        } else if (id == R.id.seatallotment_id) {

            startActivity(new Intent(SendNotification.this,seatAllotment.class));
            finish();
        } else if (id == R.id.newsfeed_id) {

            startActivity(new Intent(SendNotification.this,Newsfeed.class));
            finish();
        } else if ( id == R.id.logout_navigation_button){
            pd.setTitle("Logging out");
            pd.setMessage("Please wait for a while...");
            pd.show();
            mAuth.signOut();
            Toast.makeText(getApplicationContext(),"Signed out Sucessfully",Toast.LENGTH_LONG).show();
            pd.dismiss();
            Intent intent=new Intent(SendNotification.this, MainActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
            finish();
        }


        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }
}
