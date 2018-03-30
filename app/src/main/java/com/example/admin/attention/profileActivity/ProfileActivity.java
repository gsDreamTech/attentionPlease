package com.example.admin.attention.profileActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.annotation.RequiresApi;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.admin.attention.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Callback;
import com.squareup.picasso.NetworkPolicy;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class ProfileActivity extends AppCompatActivity {
    FirebaseUser mCurrentUser;
    DatabaseReference mUserDatabase;
    private ImageView mImage;
    private TextView mName,mEmail,mPhone,mUSN,mCollege,mBranch,mTopics;
    private static final int GALLERY_PICK=1;
    private StorageReference mImageStorage;
    private ProgressDialog pd;
    private FloatingActionButton profileFloat;
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_profile);
        getSupportActionBar().setTitle("Settings");
        profileFloat=findViewById(R.id.floatingActionButtonSettingsImage);
        pd=new ProgressDialog(this);
        pd.setTitle("Saving Profile Picture!");
        pd.setMessage("Processing ...");
        pd.setCanceledOnTouchOutside(false);
        mImageStorage= FirebaseStorage.getInstance().getReference();

        mImage=findViewById(R.id.settingsImage);
        mName=findViewById(R.id.settingsDiplayName);
        mEmail=findViewById(R.id.textViewEmail);
        mPhone=findViewById(R.id.textViewPhone);
        //mAddress=findViewById(R.id.textViewAddress);
        mCollege=findViewById(R.id.textViewCollege);
        mBranch=findViewById(R.id.textViewBranch);
        mUSN=findViewById(R.id.textViewUSN);
//        mTopics=findViewById(R.id.textViewTopics);
        final AlphaAnimation buttonAnimation=new AlphaAnimation(1f,0.5f);


        profileFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mImage.startAnimation(buttonAnimation);
                Intent galleryIntent=new Intent();
                galleryIntent.setType("image/*");
                galleryIntent.setAction(Intent.ACTION_GET_CONTENT);
                startActivityForResult(Intent.createChooser(galleryIntent,"SELECT IMAGE"),GALLERY_PICK);


            }
        });
        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
        mUserDatabase=FirebaseDatabase.getInstance().getReference().child("users").child(mCurrentUser.getUid());
        mUserDatabase.keepSynced(true);
        mUserDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(final DataSnapshot dataSnapshot) {

               try{
                   mName.setText(dataSnapshot.child("username").getValue().toString());
                   mEmail.setText(dataSnapshot.child("email").getValue().toString());
                   mCollege.setText(dataSnapshot.child("college").getValue().toString());
                   mBranch.setText(dataSnapshot.child("branch").getValue().toString());
                   mUSN.setText(dataSnapshot.child("usn").getValue().toString());
                   mPhone.setText(dataSnapshot.child("phone").getValue().toString());


//                   if(dataSnapshot.hasChild("topics"))
//                   {
//                       Map<String,Map<String,String>> map= (Map<String ,Map< String,String >>)dataSnapshot.child("topics").getValue();
//                       List<String> list = new ArrayList<>(map.keySet());
//                       StringBuilder topicsString= new StringBuilder();
//                       for(int i=0;i<list.size();i++)
//                       {
//                           topicsString = new StringBuilder("" + topicsString + map.get(list.get(i)).get("title") + "\n");
//                       }
//                       Log.i("topics", topicsString.toString());
//                       mTopics.setText(topicsString.toString());
//                   }

                   if(dataSnapshot.child("image").getValue().toString().equals("default") || dataSnapshot.child("image").getValue().toString().equals(""))
                       Picasso.with(getApplicationContext()).load(R.drawable.people3).into(mImage);
                   else
                   {
                       Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString())
                               .networkPolicy(NetworkPolicy.OFFLINE).placeholder(R.drawable.loading1).into(mImage, new Callback() {
                           @Override
                           public void onSuccess() {

                           }

                           @Override
                           public void onError() {
                               Picasso.with(getApplicationContext()).load(dataSnapshot.child("image").getValue().toString())
                                       .placeholder(R.drawable.loading1).into(mImage);
                           }
                       });
                   }
               }
               catch (Exception e){
                   e.printStackTrace();
                   Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
               }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

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
                Uri resultUri=result.getUri();
                pd.show();
                final String current_user_id=FirebaseAuth.getInstance().getCurrentUser().getUid();
                final File thumb_filePath=new File(resultUri.getPath());
                Bitmap thumb_bitmap =new Compressor(this)
                        .setMaxWidth(400)
                        .setMaxHeight(240)
                        .setQuality(75)
                        .compressToBitmap(thumb_filePath);

                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                final byte[] thumb_byte=baos.toByteArray();

                StorageReference filepath=mImageStorage.child("profile_images").child(current_user_id+".jpg");
                final StorageReference thumb_filepath=mImageStorage.child("profile_images").child("thumbs").child(current_user_id+".jpg");

                filepath.putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                        if(task.isSuccessful()){

                            final String downloadUrl=task.getResult().getDownloadUrl().toString();
                            UploadTask uploadTask=thumb_filepath.putBytes(thumb_byte);
                            uploadTask.addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                @Override
                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> thumb_task) {

                                    String thumb_downloadUrl=thumb_task.getResult().getDownloadUrl().toString();
                                    if(thumb_task.isSuccessful())
                                    {
                                        Map<String, Object> updateHashmap=new HashMap<>();
                                        updateHashmap.put("image",downloadUrl);
                                        updateHashmap.put("thumb_image",thumb_downloadUrl);
                                        FirebaseDatabase.getInstance().getReference().child("users").child(current_user_id).updateChildren(updateHashmap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                pd.dismiss();
                                                Toast.makeText(getApplicationContext(),"Updated Sucessfully.", Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                    else
                                    {
                                        pd.dismiss();
                                        Toast.makeText(getApplicationContext(),"Error in uploading thumbnail.", Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                            //FirebaseDatabase.getInstance().getReference().child("USERS LIST").

                        }
                        else{
                            pd.dismiss();
                            Toast.makeText(getApplicationContext(),"Error in uploading.", Toast.LENGTH_LONG).show();

                        }
                    }
                });

            }
            else if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE)
            {
                Exception error=result.getError();
            }
        }
    }
}
