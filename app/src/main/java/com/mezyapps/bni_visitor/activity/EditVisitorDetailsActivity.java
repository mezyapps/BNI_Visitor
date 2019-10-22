package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.AddVisitorFragment;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.LunchDcModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVisitorDetailsActivity extends AppCompatActivity {

    private AutoCompleteTextView textMobileNumber, textName, textEmail, textCategory, textLocation, textPersonName, textFollowUpDateTime, textDescription;
    private String MobileNumber, Name, Email, Category, Location, ChapterName, Source, PersonName,FollowUpDateTime, LaunchDc, Description;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<String> contactName = new ArrayList<>();
    private ArrayList<String> mobile_No_list = new ArrayList<>();
    private ImageView iv_back;
    private Button btn_save;
    private LinearLayout ll_person_name, ll_follow_up_date_time;
    private ArrayAdapter<String> adapterNameMobile;
    private ArrayAdapter<String> adapterName;
    private String followUpDateTimeShow, followDate="", followTime="", status = "0",time="";
    private RadioGroup radioGroupStatus;
    private Spinner SpinnerSource, SpinnerChapter, SpinnerLaunchDc;
    private RadioButton rbFollow_UP, rbMember, rbNot_Interested;

    private VisitorListStatusModel visitorListStatusModel;

    //Chapter List Spinner
    private ArrayList<ChapterListModel> chapterListModelArrayList = new ArrayList<>();
    private ArrayList<String> chapterStringArrayList = new ArrayList<>();
    //Chapter Launch Dc
    private ArrayList<LunchDcModel> lunchDcModelArrayList = new ArrayList<>();
    private ArrayList<String> launchDcStringArrayList = new ArrayList<>();
    String visitor_id;
    private Boolean isChapterSelect = false, isSourceSelect = false, isLaunchDcSelect = false;
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitor_details);
        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(EditVisitorDetailsActivity.this);
        textMobileNumber = findViewById(R.id.textMobileNumber);
        textName = findViewById(R.id.textName);
        iv_back = findViewById(R.id.iv_back);
        textEmail = findViewById(R.id.textEmail);
        textCategory = findViewById(R.id.textCategory);
        textLocation = findViewById(R.id.textLocation);
        SpinnerSource = findViewById(R.id.SpinnerSource);
        SpinnerChapter = findViewById(R.id.SpinnerChapter);
        textPersonName = findViewById(R.id.textPersonName);
        textFollowUpDateTime = findViewById(R.id.textFollowUpDateTime);
        textDescription = findViewById(R.id.textDescription);
        SpinnerLaunchDc = findViewById(R.id.SpinnerLaunchDc);
        ll_person_name = findViewById(R.id.ll_person_name);
        btn_save = findViewById(R.id.btn_save);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        ll_follow_up_date_time = findViewById(R.id.ll_follow_up_date_time);
        rbFollow_UP = findViewById(R.id.rbFollow_UP);
        rbMember = findViewById(R.id.rbMember);
        rbNot_Interested = findViewById(R.id.rbNot_Interested);

        textName.setThreshold(1);
        textMobileNumber.setThreshold(1);

        String dateStrSend = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        String currentDateandTime = sdf.format(new Date());

        Date date = null;
        try {
            date = sdf.parse(currentDateandTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        calendar.add(Calendar.HOUR, 1);
        time = String.valueOf(calendar.getTime());
        StringTokenizer stringTokenizer = new StringTokenizer(time, " ");
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        stringTokenizer.nextToken();
        time = stringTokenizer.nextToken();

        followUpDateTimeShow = dateStrSend + " " + time;
        textFollowUpDateTime.setText(followUpDateTimeShow);


        Bundle bundle = getIntent().getExtras();
        visitorListStatusModel = bundle.getParcelable("VISITOR");
        visitor_id = visitorListStatusModel.getVisitor_id();
        status = visitorListStatusModel.getStatus();
        textName.setText(visitorListStatusModel.getName());
        textMobileNumber.setText(visitorListStatusModel.getMobile_no());
        textEmail.setText(visitorListStatusModel.getEmail_id());
        textCategory.setText(visitorListStatusModel.getCategory());
        textLocation.setText(visitorListStatusModel.getLocation());
        ChapterName = visitorListStatusModel.getChapter_name();
        Source = visitorListStatusModel.getSource();
        LaunchDc = visitorListStatusModel.getLaunch_dc();
        textDescription.setText(visitorListStatusModel.getDescription());

        if (status.equalsIgnoreCase("0")) {
            rbFollow_UP.setChecked(true);
        } else if (status.equalsIgnoreCase("1")) {
            rbMember.setChecked(true);
        } else {
            rbNot_Interested.setChecked(true);
        }

        textMobileNumber.setCursorVisible(false);
        textMobileNumber.setCursorVisible(false);
        textEmail.setCursorVisible(false);
        textEmail.setCursorVisible(false);
        textFollowUpDateTime.setFocusable(false);
        textFollowUpDateTime.setCursorVisible(false);

    }


    private void events() {
        AccessContactList accessContactList = new AccessContactList();
        accessContactList.execute("");


        addAutoComplete();
        if (NetworkUtils.isNetworkAvailable(EditVisitorDetailsActivity.this)) {
            chapterList();
            launchDcList();
        } else {
            NetworkUtils.isNetworkNotAvailable(EditVisitorDetailsActivity.this);
        }

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (NetworkUtils.isNetworkAvailable(EditVisitorDetailsActivity.this)) {
                        callAddVisitor();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(EditVisitorDetailsActivity.this);
                    }
                }
            }
        });
        SpinnerSource.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSourceSelect = true;
                return false;
            }
        });
        SpinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {

                if (isSourceSelect) {
                    Source = parent.getItemAtPosition(position).toString();
                    if (Source.equalsIgnoreCase("BNI Member") || Source.equalsIgnoreCase("Non BNI Member")) {
                        ll_person_name.setVisibility(View.VISIBLE);
                    } else {
                        ll_person_name.setVisibility(View.GONE);
                    }
                    isSourceSelect = false;
                } else {
                   // Toast.makeText(EditVisitorDetailsActivity.this, Source, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerChapter.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isChapterSelect = true;
                return false;
            }
        });

        SpinnerChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if (isChapterSelect) {
                    ChapterName = parent.getItemAtPosition(position).toString();
                    isChapterSelect = false;
                } else {
                    //Toast.makeText(EditVisitorDetailsActivity.this, ChapterName, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SpinnerLaunchDc.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isLaunchDcSelect = true;
                return false;
            }
        });


        SpinnerLaunchDc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                if (isLaunchDcSelect) {
                    LaunchDc = parent.getItemAtPosition(position).toString();
                    isLaunchDcSelect = false;
                } else {
                   // Toast.makeText(EditVisitorDetailsActivity.this, LaunchDc, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        radioGroupStatus.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                RadioButton radioStatusButton = findViewById(checkedId);
                String statusName = radioStatusButton.getText().toString().trim();
                if (statusName.equalsIgnoreCase("Follow Up")) {
                    status = "0";
                    ll_follow_up_date_time.setVisibility(View.VISIBLE);
                    textFollowUpDateTime.setText(followUpDateTimeShow);
                } else if (statusName.equalsIgnoreCase("Member")) {
                    status = "1";
                    ll_follow_up_date_time.setVisibility(View.GONE);
                    textFollowUpDateTime.setText("");
                } else {
                    status = "2";
                    ll_follow_up_date_time.setVisibility(View.GONE);
                    textFollowUpDateTime.setText("");
                }
            }
        });
        textFollowUpDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
            }
        });
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        datePickerDialog= new DatePickerDialog(EditVisitorDetailsActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);



                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(calendar.getTime());
                        followDate=dateString;
                        timePicker();
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
       int  mHour = c.get(Calendar.HOUR_OF_DAY);
       int  mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay,int minute) {

                        String hour= String.valueOf(hourOfDay);
                        String minutes= String.valueOf(minute);
                        String minutes1="",hour1 ="";
                        if(hour.length()==1)
                        {
                            hour1="0"+hour;
                        }
                        else
                        {
                            hour1=hour;
                        }
                        if (minutes.length()==1)
                        {
                            minutes1="0"+minutes;
                        }
                        else
                        {
                            minutes1=minutes;
                        }

                        followTime= hour1+":"+minutes1;
                        textFollowUpDateTime.setText(followDate+" "+followTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }

    private void addAutoComplete() {
        adapterName = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, contactName);
        textName.setAdapter(adapterName);

        adapterNameMobile = new ArrayAdapter<String>(this, android.R.layout.select_dialog_item, mobile_No_list);
        textMobileNumber.setAdapter(adapterNameMobile);

        ArrayList<String> sourceArrayList = new ArrayList<>();
        sourceArrayList.add("BNI Website");
        sourceArrayList.add("Facebook");
        sourceArrayList.add("Instagram");
        sourceArrayList.add("Self");
        sourceArrayList.add("BNI Member");
        sourceArrayList.add("Non BNI Member");
        ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorDetailsActivity.this, android.R.layout.simple_spinner_item, sourceArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSource.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

    }

    private void callAddVisitor() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.editVisitor(visitor_id,Name, MobileNumber, Email, Category, Location, ChapterName, Source, PersonName, status, FollowUpDateTime, LaunchDc, Description);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {
                                Toast.makeText(EditVisitorDetailsActivity.this, "Visitor Edit Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            }  else {
                                Toast.makeText(EditVisitorDetailsActivity.this, "Visitor Not Edit", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(EditVisitorDetailsActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }


            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                showProgressDialog.dismissDialog();
            }
        });
    }

    private boolean validation() {
        MobileNumber = textMobileNumber.getText().toString().trim();
        Name = textName.getText().toString().trim();
        Email = textEmail.getText().toString().trim();
        Category = textCategory.getText().toString().trim();
        Location = textLocation.getText().toString().trim();
        //ChapterName = textChapterName.getText().toString().trim();
        // Source = textSource.getText().toString().trim();
        PersonName = textPersonName.getText().toString().trim();
        FollowUpDateTime = textFollowUpDateTime.getText().toString().trim();
        //LaunchDc = textLaunchDc.getText().toString().trim();
        Description = textDescription.getText().toString().trim();

        if (Name.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (MobileNumber.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Email.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Email", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Category.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Location.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (ChapterName.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Chapter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Source.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Source Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (status.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Status", Toast.LENGTH_SHORT).show();
            return false;
        } else if (LaunchDc.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Launch Dc", Toast.LENGTH_SHORT).show();
            return false;
        } else if (Description.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Description", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getContactDetails() {
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor.moveToNext()) {
            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            contactName.add(name);
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            if (phoneCursor.moveToNext()) {
                String mobile_number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
                mobile_No_list.add(mobile_number);
            }

        }

    }

    public class AccessContactList extends AsyncTask<String, String, String> {

        String msg = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {

        }

        @Override
        protected void onPostExecute(String message) {

        }

        @SuppressLint("SetTextI18n")
        @Override
        protected String doInBackground(String... strings) {

            try {
                getContactDetails();
            } catch (Exception ex) {
                isSuccess = false;
                msg = ex.getMessage();
            }

            return msg;
        }
    }

    private void chapterList() {
        Call<SuccessModel> call = apiInterface.chapterList();
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        chapterListModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {
                                chapterListModelArrayList = successModule.getChapterListModelArrayList();
                                if (chapterListModelArrayList.size() != 0) {
                                    chapterListModelArrayList = successModule.getChapterListModelArrayList();
                                    for (ChapterListModel chapterListModel : chapterListModelArrayList) {
                                        chapterStringArrayList.add(chapterListModel.getChapter_name());
                                    }
                                    ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorDetailsActivity.this, android.R.layout.simple_spinner_item, chapterStringArrayList);
                                    arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                    SpinnerChapter.setAdapter(arrayAdapter);
                                    arrayAdapter.notifyDataSetChanged();
                                }
                            }
                        } else {
                            Toast.makeText(EditVisitorDetailsActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
            }
        });
    }

    private void launchDcList() {
        Call<SuccessModel> call = apiInterface.launchDcList();
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        lunchDcModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                lunchDcModelArrayList = successModule.getLunchDcModelArrayList();
                                for (LunchDcModel lunchDcModel : lunchDcModelArrayList) {
                                    launchDcStringArrayList.add(lunchDcModel.getLaunch_dc_name());
                                }
                                ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorDetailsActivity.this, android.R.layout.simple_spinner_item, launchDcStringArrayList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerLaunchDc.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(EditVisitorDetailsActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
            }
        });
    }
}
