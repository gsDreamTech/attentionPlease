package com.example.admin.attention.Result;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.example.admin.attention.R;

import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Spinner;
import android.widget.TextView;

public class result extends AppCompatActivity {

    private MainFragment mainFragment;
    private EditText searchField;
    private Spinner moodFilter, genderFilter, itemsPerPage;
    public ImageButton previousButton, nextButton;
    public EditText pageNumberField;
    public TextView tablePaginationDetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reslut);

        mainFragment = new MainFragment();

        View tableControls  = findViewById(R.id.table_controls);
        searchField = findViewById(R.id.query_string);
        moodFilter = findViewById(R.id.mood_spinner);
        genderFilter = findViewById(R.id.gender_spinner);
        itemsPerPage = findViewById(R.id.items_per_page_spinner);

        previousButton = findViewById(R.id.previous_button);
        nextButton = findViewById(R.id.next_button);
        pageNumberField = findViewById(R.id.page_number_text);
        tablePaginationDetails = findViewById(R.id.table_details);

        if(mainFragment.isPaginationEnabled()) {
            tableControls.setVisibility(View.VISIBLE);
            searchField.addTextChangedListener(onSearchTextChange);
            moodFilter.setOnItemSelectedListener(onMoodSelectedListener);
            genderFilter.setOnItemSelectedListener(onGenderSelectedListener);
            itemsPerPage.setOnItemSelectedListener(onItemsPerPageSelectedListener);

            previousButton.setOnClickListener(onPreviousPageButtonClicked);
            nextButton.setOnClickListener(onNextPageButtonClicked);
            pageNumberField.addTextChangedListener(onPageTextChanged);
        } else {
            tableControls.setVisibility(View.GONE);
        }

        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().add(R.id.activity_container,
                    mainFragment, mainFragment.getClass().getSimpleName()).commit();
        }

    }

    @Override
    protected void onDestroy() {
        mainFragment.onDestroy();
        super.onDestroy();
    }

    private TextWatcher onSearchTextChange = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            try {
                mainFragment.filterTable(String.valueOf(s));
            }catch(Exception e){}
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

    private AdapterView.OnItemSelectedListener onMoodSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            mainFragment.filterTableForMood(parent.getItemAtPosition(position).toString());
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener onGenderSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            String filter = "";
            switch (parent.getItemAtPosition(position).toString()) {
                case "":
                    filter = "";
                    break;
                case "Male":
                    filter = "boy";
                    break;
                case "Female":
                    filter = "girl";
                    break;
            }

            mainFragment.filterTableForGender(filter);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private AdapterView.OnItemSelectedListener onItemsPerPageSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
            int itemsPerPage;
            switch (parent.getItemAtPosition(position).toString()) {
                case "All":
                    itemsPerPage = 0;
                    break;
                default:
                    itemsPerPage = Integer.valueOf(parent.getItemAtPosition(position).toString());
            }

            mainFragment.setTableItemsPerPage(itemsPerPage);
        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private View.OnClickListener onPreviousPageButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainFragment.previousTablePage();
        }
    };

    private View.OnClickListener onNextPageButtonClicked = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            mainFragment.nextTablePage();
        }
    };

    private TextWatcher onPageTextChanged = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            int page;
            if (TextUtils.isEmpty(s)) {
                page = 1;
            } else {
                page = Integer.valueOf(String.valueOf(s));
            }

            mainFragment.goToTablePage(page);
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };
}
