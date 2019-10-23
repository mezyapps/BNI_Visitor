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

    private AutoCompleteTextView textFollowUpDateTime, textDescription;
    private String FollowUpDateTime, LaunchDc, Description;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ImageView iv_back;
    private Button btn_save;
    private LinearLayout  ll_follow_up_date_time;
    private String followUpDateTimeShow, followDate="", followTime="", status = "0",time="";
    private RadioGroup radioGroupStatus;
    private Spinner  SpinnerLaunchDc;
    private RadioButton rbFollow_UP, rbMember, rbNot_Interested;

    private VisitorListStatusModel visitorListStatusModel;


    //Chapter Launch Dc
    private ArrayList<LunchDcModel> lunchDcModelArrayList = new ArrayList<>();
    private ArrayList<String> launchDcStringArrayList = new ArrayList<>();
    String visitor_id;
    private Boolean isLaunchDcSelect = false;
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

        iv_back = findViewById(R.id.iv_back);
        textFollowUpDateTime = findViewById(R.id.textFollowUpDateTime);
        textDescription = findViewById(R.id.textDescription);
        SpinnerLaunchDc = findViewById(R.id.SpinnerLaunchDc);
        btn_save = findViewById(R.id.btn_save);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        ll_follow_up_date_time = findViewById(R.id.ll_follow_up_date_time);
        rbFollow_UP = findViewById(R.id.rbFollow_UP);
        rbMember = findViewById(R.id.rbMember);
        rbNot_Interested = findViewById(R.id.rbNot_Interested);

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
        LaunchDc = visitorListStatusModel.getLaunch_dc();

        if (status.equalsIgnoreCase("0")) {
            rbFollow_UP.setChecked(true);
        } else if (status.equalsIgnoreCase("1")) {
            rbMember.setChecked(true);
        } else {
            rbNot_Interested.setChecked(true);
        }

        textFollowUpDateTime.setFocusable(false);
        textFollowUpDateTime.setCursorVisible(false);

    }


    private void events() {
        if (NetworkUtils.isNetworkAvailable(EditVisitorDetailsActivity.this)) {
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

    private void callAddVisitor() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.editVisitor(visitor_id, status, FollowUpDateTime, LaunchDc, Description);
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

        FollowUpDateTime = textFollowUpDateTime.getText().toString().trim();
        Description = textDescription.getText().toString().trim();

     if (status.equalsIgnoreCase("")) {
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
