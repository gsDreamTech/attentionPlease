package com.example.admin.attention.startActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.Toast;


import com.bumptech.glide.Glide;
import com.example.admin.attention.R;
import com.example.admin.attention.TopicSubscription.SubscribeTopics;
import com.example.admin.attention.main.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import id.zelory.compressor.Compressor;

public class NewUser extends AppCompatActivity {
    private FirebaseAuth mAuth;
    private FirebaseUser mCurrentUser;
    private ProgressDialog pd;
    public static DatabaseReference mUsersData;
    private DatabaseReference mRootRef;
    private Uri resultUri=null;
    private CircleImageView circleImageView;
    ArrayAdapter<String> stateArrayAdapter;
    ArrayAdapter<String> cityAarrayAdapter;
    ArrayAdapter<String> collegeArrayAdapter;
    ArrayAdapter<String> branchArrayAdapter;
    ArrayAdapter<String> yearArrayAdapter;
    ArrayAdapter<String> degreeArrayAdapter;
    ArrayAdapter<String> semArrayAdapter;
    ArrayAdapter<String> secArrayAdapter;
    private static final int GALLERY_PICK=1;
    private StorageReference mStorage;
    private String json=null,CollegeCode="";
    String[] s={},code={};
    private FloatingActionButton profileFloat;
    private List<String> li;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup);
        profileFloat=findViewById(R.id.floatingActionButtonProfileImage);
        loadJSONFromAsset();
        pd = new ProgressDialog(this);
        pd.setTitle("Creating Account !");
        pd.setMessage("Wait for a while...");
        pd.setCanceledOnTouchOutside(false);
        mAuth = FirebaseAuth.getInstance();
        mRootRef=FirebaseDatabase.getInstance().getReference();
        mStorage=FirebaseStorage.getInstance().getReference();
        final AutoCompleteTextView yourName = findViewById(R.id.editTextUserName);
        final AutoCompleteTextView emailId = findViewById(R.id.editTextEmail);
        final AutoCompleteTextView passwordId = findViewById(R.id.editTextPassword);
        final AutoCompleteTextView phoneNumber = findViewById(R.id.editTextPhone);
        final AutoCompleteTextView usn = findViewById(R.id.editTextUsn);
        final AutoCompleteTextView state = findViewById(R.id.editTextState);
        final AutoCompleteTextView city = findViewById(R.id.editTextCity);
        final AutoCompleteTextView college = findViewById(R.id.editTextCollege);
        final AutoCompleteTextView branch = findViewById(R.id.editTextBranch);
        final Spinner year = findViewById(R.id.Year);
        final Spinner degree = findViewById(R.id.Degree);
        final Spinner sem = findViewById(R.id.Sem);
        final Spinner sec = findViewById(R.id.Sec);
        circleImageView=findViewById(R.id.profileImage);
        profileFloat.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent=new Intent();
                intent.setAction(Intent.ACTION_GET_CONTENT);
                intent.setType("image/*");
                startActivityForResult(Intent.createChooser(intent,"Select Image"),GALLERY_PICK);
            }
        });


        List<String> Year=Arrays.asList("Year","1st Year","2nd Year","3rd Year","4th Year");
        List<String> Degree=Arrays.asList("Degree","BE","MTECH","MSC","MBA","MCA");
        List<String> Sem=Arrays.asList("Semester","1","2","3","4","5","6","7","8");
        List<String> Sec=Arrays.asList("Section","A","B","C","D","E","F","G");


//        List<String> Branch= Arrays.asList("Computer Science and Engineering",
//                "Mechanical Engineering", "civil Engineering", "Electrical Engineering",
//                "Electronics and Electrical Engineering", "Chemical Engineering", "Telecommunication", "Chemical Engineering",
//                "Industrial Engineering and Management", "Instrumentation", "Information Science and Engineering",
//                "Architecture", "Biotechnology");

        List<String> Branch= Arrays.asList("CSE", "MECH", "CIVIL", "EEE", "EC", "CHEM", "TC",
                "IM", "IT", "IS", "ARCH", "BIOTECH");


        stateArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,getStatesFromJson());
        branchArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Branch);
        yearArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Year);
        degreeArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Degree);
        semArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Sem);
        secArrayAdapter=new ArrayAdapter<>(this,android.R.layout.simple_spinner_dropdown_item,Sec);


        city.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view.isFocused())
                {
                    if(state.getText().toString().equals(""))
                    {
                        state.setFocusable(true);
                        Snackbar.make(view,"First choose the state...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else
                    {
                        cityAarrayAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_dropdown_item, getDistrictsFromJson(state.getText().toString()));
                        city.setAdapter(cityAarrayAdapter);
                    }
                }
            }
        });
        college.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean b) {
                if(view.isFocused())
                {
                    if(state.getText().toString().equals(""))
                    {
                        state.setFocusable(true);
                        Snackbar.make(view,"First choose the state...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else if(city.getText().toString().equals(""))
                    {
                        city.setFocusable(true);
                        Snackbar.make(view,"First choose the district...",Snackbar.LENGTH_LONG).setAction("Action",null).show();
                    }
                    else
                    {
                        try {
                            collegeArrayAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_dropdown_item, getCollegesFromJson(state.getText().toString(),city.getText().toString()));
                            college.setAdapter(collegeArrayAdapter);

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });
        college.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                CollegeCode=code[li.indexOf(adapterView.getItemAtPosition(i).toString())];
                Log.i("College",CollegeCode);
            }
        });
//        state.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                cityAarrayAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_dropdown_item, getDistrictsFromJson(adapterView.getItemAtPosition(i).toString()));
//                city.setAdapter(cityAarrayAdapter);
//            }
//        });
//        city.setOnItemClickListener(new AdapterView.OnItemClickListener() {
//            @Override
//            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
//                try {
//                    collegeArrayAdapter=new ArrayAdapter<>(view.getContext(),android.R.layout.simple_spinner_dropdown_item, getCollegesFromJson(state.getText().toString(),adapterView.getItemAtPosition(i).toString()));
//                    college.setAdapter(collegeArrayAdapter);
//
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }
//            }
//        });
        city.setAdapter(cityAarrayAdapter);
        state.setAdapter(stateArrayAdapter);
        branch.setAdapter(branchArrayAdapter);
        college.setAdapter(collegeArrayAdapter);
        year.setAdapter(yearArrayAdapter);
        degree.setAdapter(degreeArrayAdapter);
        sem.setAdapter(semArrayAdapter);
        sec.setAdapter(secArrayAdapter);



        Button newUserButton = findViewById(R.id.buttonCreateAccount);
        newUserButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String emailS = emailId.getText().toString();
                final String yourNameS = yourName.getText().toString();
                final String phoneS=phoneNumber.getText().toString();
                final String usnS=usn.getText().toString();
                final String stateS=state.getText().toString();
                final String cityS=city.getText().toString();
                final String collegeS=college.getText().toString();
                final String branchS=branch.getText().toString();
                final String yearS=year.getSelectedItem().toString();
                final String degreeS=degree.getSelectedItem().toString();
                final String semS=sem.getSelectedItem().toString();
                final String secS=sec.getSelectedItem().toString();
                final String passwordS = passwordId.getText().toString();

                if (emailS.isEmpty())
                    Snackbar.make(view, "Invalid Email...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if (passwordS.isEmpty() || passwordS.length() < 6)
                    Snackbar.make(view, "Invalid Password...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(phoneS.length()<10)
                    Snackbar.make(view, "Invalid Phone Number...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(yearS.equals("Year"))
                    Snackbar.make(view, "Please select the current year...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(degreeS.equals("Degree"))
                    Snackbar.make(view, "Please select the degree...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(semS.equals("Semester"))
                    Snackbar.make(view, "Please select the current semester...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else if(secS.equals("Section"))
                    Snackbar.make(view, "Please select your section...", Snackbar.LENGTH_LONG).setAction("Action", null).show();
                else {
                    pd.show();
                    createAccount(view,yourNameS,emailS,passwordS,phoneS,usnS,stateS,cityS,collegeS,branchS,yearS,degreeS,semS,secS);
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
                    .setAspectRatio(5,4)
                    .start(this);
        }
        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            if(resultCode==RESULT_OK)
            {
                resultUri=result.getUri();
                Glide.with(this).load(resultUri).into(circleImageView);
            }
        }
    }

    public void createAccount(final View view, final String...paramas) {
        if (mAuth == null)
            mAuth = FirebaseAuth.getInstance();

        mAuth.createUserWithEmailAndPassword(paramas[1], paramas[2])
                .addOnCompleteListener(NewUser.this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            MainActivity.topicsSubscribed.edit().putString("CollegeCode",CollegeCode).apply();
                            MainActivity.topicsSubscribed.edit().putString("semester",paramas[11]).apply();
                            MainActivity.topicsSubscribed.edit().putString("branch",paramas[8]).apply();
                            MainActivity.topicsSubscribed.edit().putString("section",paramas[12]).apply();
                            final HashMap<String, String> userMap = new HashMap<>();
                            if(resultUri==null)
                            {
                                userMap.put("image","default");
                                userMap.put("thumb_image","default");

                                mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();
                                mUsersData=mRootRef.child("users").child(mCurrentUser.getUid());
                                mUsersData.keepSynced(true);

                                userMap.put("username",paramas[0]);
                                userMap.put("email",paramas[1]);
                                userMap.put("phone",paramas[3]);
                                userMap.put("usn",paramas[4]);
                                userMap.put("state",paramas[5]);
                                userMap.put("district",paramas[6]);
                                userMap.put("college",paramas[7]);
                                userMap.put("branch",paramas[8]);
                                userMap.put("year",paramas[9]);
                                userMap.put("degree",paramas[10]);
                                userMap.put("sem",paramas[11]);
                                userMap.put("sec",paramas[12]);
                                userMap.put("ccode",CollegeCode);
                                Log.i("ccc",CollegeCode);
                                //MainActivity.topicsSubscribed.edit().putString("CollegeCode",CollegeCode).apply();
                                mUsersData.setValue(userMap);
                                pd.dismiss();

                                Intent intent = new Intent(NewUser.this, SubscribeTopics.class);
                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                startActivity(intent);
                                finish();
                            }
                            else
                            {
                                final String current_user_id=mAuth.getCurrentUser().getUid();
                                final File thumb_filePath=new File(resultUri.getPath());
                                Bitmap thumb_bitmap =new Compressor(getApplicationContext())
                                        .setMaxWidth(200)
                                        .setMaxHeight(200)
                                        .setQuality(75)
                                        .compressToBitmap(thumb_filePath);

                                ByteArrayOutputStream baos=new ByteArrayOutputStream();
                                thumb_bitmap.compress(Bitmap.CompressFormat.JPEG,100,baos);
                                final byte[] thumb_byte=baos.toByteArray();
                                mStorage.child("profile").child(current_user_id+".jpg").putFile(resultUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onComplete(@NonNull final Task<UploadTask.TaskSnapshot> task) {
                                        if(task.isSuccessful())
                                        {
                                            mStorage.child("profile").child("thumbs").child(current_user_id+".jpg").putBytes(thumb_byte).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                                                @Override
                                                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task1) {
                                                    if(task1.isSuccessful())
                                                    {
                                                        userMap.put("image",task.getResult().getDownloadUrl().toString());
                                                        userMap.put("thumb_image",task1.getResult().getDownloadUrl().toString());
                                                        mCurrentUser=FirebaseAuth.getInstance().getCurrentUser();

                                                        mUsersData=mRootRef.child("users").child(mCurrentUser.getUid());
                                                        mUsersData.keepSynced(true);
                                                        userMap.put("username",paramas[0]);
                                                        userMap.put("email",paramas[1]);
                                                        userMap.put("phone",paramas[3]);
                                                        userMap.put("usn",paramas[4]);
                                                        userMap.put("state",paramas[5]);
                                                        userMap.put("district",paramas[6]);
                                                        userMap.put("college",paramas[7]);
                                                        userMap.put("branch",paramas[8]);
                                                        userMap.put("year",paramas[9]);
                                                        userMap.put("degree",paramas[10]);
                                                        userMap.put("sem",paramas[11]);
                                                        userMap.put("sec",paramas[12]);
                                                        userMap.put("ccode",CollegeCode);
                                                        Log.i("ccc",CollegeCode);
                                                        //MainActivity.topicsSubscribed.edit().putString("CollegeCode",CollegeCode).apply();

                                                        mUsersData.setValue(userMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                            @Override
                                                            public void onComplete(@NonNull Task<Void> task) {
                                                                pd.dismiss();
                                                                Intent intent = new Intent(NewUser.this, SubscribeTopics.class);
                                                                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                                                startActivity(intent);
                                                                finish();
                                                                finish();
                                                            }
                                                        }).addOnFailureListener(new OnFailureListener() {
                                                            @Override
                                                            public void onFailure(@NonNull Exception e) {
                                                                pd.dismiss();
                                                                Toast.makeText(getApplicationContext(), e.getMessage(),
                                                                        Toast.LENGTH_SHORT).show();
                                                            }
                                                        });

                                                    }
                                                }
                                            });
                                        }
                                    }
                                });
                            }

                        } else {
                            // If sign in fails, display a message to the user.
                            pd.dismiss();

                            Log.w("NewUser", "createUserWithEmail:failure", task.getException());
                            if (task.getException().getMessage().contains("The email address is already in use by another account"))
                                Snackbar.make(view, "The email address is already in use...", Snackbar.LENGTH_LONG)
                                        .setAction("Action", null).show();
                            else
                                Toast.makeText(getApplicationContext(), "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
                        }
                    }
                });

    }

    public void loadJSONFromAsset(){
        try{
            InputStream is=getAssets().open("state_dist_collegeWITHCODE.json");
            int size=is.available();
            byte[] buffer=new byte[size];
            is.read(buffer);
            is.close();
            json=new String(buffer,"UTF-8");
        }catch(IOException e){
            e.printStackTrace();
        }
    }

    public   String[] getCollegesFromJson(String state, String city) throws JSONException {
        JSONArray mCityArray;
        String[] arr={};
        try {
            JSONObject obj=new JSONObject(json);
            if(!obj.has(state))
            {
                Snackbar.make(findViewById(R.id.editTextCollege), "Please choose the STATE from the dropdown sugessetions...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return s;
            }
            JSONObject mStateObject=obj.getJSONObject(state);
            if(!mStateObject.has(city))
            {
                Snackbar.make(findViewById(R.id.editTextCollege), "Please choose the DISTRICT from the dropdown sugessetions...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return s;
            }
            mCityArray=mStateObject.getJSONArray(city);
            arr=new String[mCityArray.length()];
            code=new String[mCityArray.length()];
            for(int i=0;i<mCityArray.length();i++){
                //Log.i(mCityArray.getJSONArray(i).getString(0),mCityArray.getJSONArray(i).getString(1));
                arr[i]=mCityArray.getJSONArray(i).getString(1);
                code[i]=mCityArray.getJSONArray(i).getString(0);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        li=Arrays.asList(arr);
        return arr;
    }

    public String[] getDistrictsFromJson(String state){
        JSONObject mStateObject=null;
        String[] s={};
        int j=0;
        try {
            JSONObject obj=new JSONObject(json);
            if(!obj.has(state))
            {
                Snackbar.make(findViewById(R.id.editTextCollege), "Please choose the STATE from the dropdown sugessetions...", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
                return s;
            }
            mStateObject=obj.getJSONObject(state);
            s=new String[mStateObject.length()];


        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> i=mStateObject.keys();
        while(i.hasNext()){
            s[j++]=i.next();
        }
        return s;
    }

    public String[] getStatesFromJson(){
        JSONObject obj=null;
        String[] s={};
        int j=0;
        try {
            obj=new JSONObject(json);
            s=new String[obj.length()];
        } catch (JSONException e) {
            e.printStackTrace();
        }
        Iterator<String> i=obj.keys();
        while(i.hasNext()){
            s[j++]=i.next();
        }
        return s;
    }



}
