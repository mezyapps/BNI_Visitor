package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;

import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;

import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.ScrollView;
import android.widget.Spinner;

import android.widget.TimePicker;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.ContactDetailsAdapter;
import com.mezyapps.bni_visitor.adapter.ContactListAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.AddVisitorFragment;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.ContactDetailsModel;
import com.mezyapps.bni_visitor.model.ContactListModel;
import com.mezyapps.bni_visitor.model.LunchDcModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.ContactListInterface;
import com.mezyapps.bni_visitor.utils.ErrorDialog;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;
import com.mezyapps.bni_visitor.utils.SuccessDialog;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;
import java.util.StringTokenizer;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AddVisitorActivity extends AppCompatActivity implements ContactListInterface {

    private AutoCompleteTextView textMobileNumber, textName, textEmail, textCategory, textLocation, textPersonName,
            textCurrentDateTime, textFollowUpDateTime, textDescription;
    private String MobileNumber, Name, Email, Category, Location, ChapterName, Source, PersonName, CurrentDateTime, FollowUpDateTime, LaunchDc, Description;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ImageView iv_back, iv_contact;
    private Button btn_save;
    private LinearLayout ll_person_name, ll_follow_up_date_time;
    private ArrayAdapter<String> adapterNameMobile;
    private ArrayAdapter<String> adapterName;
    private String showDate, sendDate, time, followUpDateTimeShow, followUpDateTimeSend, status = "0", followDate = "", followTime = "";
    private RadioGroup radioGroupStatus;
    private Spinner SpinnerSource, SpinnerChapter, SpinnerLaunchDc;
    private ScrollView scroll_add_visitor;

    //Chapter List Spinner
    private ArrayList<ChapterListModel> chapterListModelArrayList = new ArrayList<>();
    private ArrayList<String> chapterStringArrayList = new ArrayList<>();
    //Chapter Launch Dc
    private ArrayList<LunchDcModel> lunchDcModelArrayList = new ArrayList<>();
    private ArrayList<String> launchDcStringArrayList = new ArrayList<>();
    private DatePickerDialog datePickerDialog;
    private TimePickerDialog timePickerDialog;
    private SuccessDialog successDialog;
    private ErrorDialog errorDialog;
    private ArrayList<ContactListModel> contactListModelArrayList = new ArrayList<>();
    //Contact Dialog
    private Dialog contactDialog;
    private EditText edt_search;
    private RecyclerView recyclerView_contact;
    private ContactListAdapter contactListAdapter;
    private static final String[] PROJECTION = new String[] {
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_visitor);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(AddVisitorActivity.this);

        textMobileNumber = findViewById(R.id.textMobileNumber);
        textName = findViewById(R.id.textName);
        iv_back = findViewById(R.id.iv_back);
        textEmail = findViewById(R.id.textEmail);
        textCategory = findViewById(R.id.textCategory);
        textLocation = findViewById(R.id.textLocation);
        SpinnerChapter = findViewById(R.id.SpinnerChapter);
        SpinnerSource = findViewById(R.id.SpinnerSource);
        textPersonName = findViewById(R.id.textPersonName);
        textCurrentDateTime = findViewById(R.id.textCurrentDateTime);
        textFollowUpDateTime = findViewById(R.id.textFollowUpDateTime);
        textDescription = findViewById(R.id.textDescription);
        SpinnerLaunchDc = findViewById(R.id.SpinnerLaunchDc);
        ll_person_name = findViewById(R.id.ll_person_name);
        btn_save = findViewById(R.id.btn_save);
        iv_contact = findViewById(R.id.iv_contact);
        scroll_add_visitor = findViewById(R.id.scroll_add_visitor);
        radioGroupStatus = findViewById(R.id.radioGroupStatus);
        ll_follow_up_date_time = findViewById(R.id.ll_follow_up_date_time);
        successDialog = new SuccessDialog(AddVisitorActivity.this);
        errorDialog = new ErrorDialog(AddVisitorActivity.this);


        //Contact Dialog
        contactDialog = new Dialog(AddVisitorActivity.this);
        contactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactDialog.setCancelable(true);
        contactDialog.setContentView(R.layout.dialog_conatct_dialog);
        recyclerView_contact = contactDialog.findViewById(R.id.recyclerView_contact);
        edt_search = contactDialog.findViewById(R.id.edt_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(AddVisitorActivity.this);
        recyclerView_contact.setLayoutManager(linearLayoutManager);


        showDate = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss", Locale.getDefault()).format(new Date());
        sendDate = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.getDefault()).format(new Date());
        String dateStr = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
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

        followUpDateTimeShow = dateStr + " " + time;
        followUpDateTimeSend = dateStr + " " + time;
        textCurrentDateTime.setText(showDate);
        textFollowUpDateTime.setText(followUpDateTimeShow);
        textFollowUpDateTime.setFocusable(false);
        textCurrentDateTime.setFocusable(false);
        textFollowUpDateTime.setCursorVisible(false);
        textCurrentDateTime.setCursorVisible(false);

    }


    private void events() {

        addAutoComplete();

        if (NetworkUtils.isNetworkAvailable(AddVisitorActivity.this)) {
            chapterList();
            launchDcList();
        } else {
            NetworkUtils.isNetworkNotAvailable(AddVisitorActivity.this);
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
                    if (NetworkUtils.isNetworkAvailable(AddVisitorActivity.this)) {
                        callAddVisitor();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(AddVisitorActivity.this);
                    }
                }
            }
        });

        SpinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                Source = parent.getItemAtPosition(position).toString();
                if (Source.equalsIgnoreCase("BNI Member") || Source.equalsIgnoreCase("Non BNI Member")) {
                    ll_person_name.setVisibility(View.VISIBLE);
                } else {
                    ll_person_name.setVisibility(View.GONE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SpinnerChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                try {
                    int chapterId = Integer.parseInt(chapterListModelArrayList.get(position).getChapter_id());
                    ChapterName = String.valueOf(chapterId);

                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });


        SpinnerLaunchDc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                try {
                    int launchDcID = Integer.parseInt(lunchDcModelArrayList.get(position).getLaunch_dc_id());
                    LaunchDc = String.valueOf(launchDcID);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        textFollowUpDateTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePicker();
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

        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
                AccessContactList accessContactList = new AccessContactList();
                accessContactList.execute("");
            }
        });

        edt_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setFocusableInTouchMode(true);
            }
        });


        edt_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try {
                    edt_search.setFocusableInTouchMode(true);
                    contactListAdapter.getFilter().filter(edt_search.getText().toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }

    public class AccessContactList extends AsyncTask<String, String, String> {

        String msg = "";
        boolean isSuccess = false;

        @Override
        protected void onPreExecute() {
            contactDialog.show();
            showProgressDialog.showDialog();
        }

        @Override
        protected void onPostExecute(String message) {
            showProgressDialog.dismissDialog();
            if (isSuccess) {
                setAdapter();
            }
        }

        @Override
        protected String doInBackground(String... strings) {

            try {
                getContactDetails();
                isSuccess = true;
            } catch (Exception ex) {
                isSuccess = false;
                msg = ex.getMessage();
            }

            return msg;
        }
    }

    private void setAdapter() {
        showProgressDialog.dismissDialog();
        contactListAdapter = new ContactListAdapter(AddVisitorActivity.this, contactListModelArrayList, this);
        Window window = contactDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        recyclerView_contact.setAdapter(contactListAdapter);
        contactListAdapter.notifyDataSetChanged();
    }

    private void datePicker() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);


        datePickerDialog = new DatePickerDialog(AddVisitorActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("dd-MM-yyyy");
                        String dateString = format.format(calendar.getTime());
                        followDate = dateString;
                        timePicker();
                    }

                }, mYear, mMonth, mDay);
        datePickerDialog.show();
    }

    private void timePicker() {
        // Get Current Time
        final Calendar c = Calendar.getInstance();
        int mHour = c.get(Calendar.HOUR_OF_DAY);
        int mMinute = c.get(Calendar.MINUTE);

        // Launch Time Picker Dialog
        TimePickerDialog timePickerDialog = new TimePickerDialog(this,
                new TimePickerDialog.OnTimeSetListener() {

                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {

                        String hour = String.valueOf(hourOfDay);
                        String minutes = String.valueOf(minute);
                        String minutes1 = "", hour1 = "";
                        if (hour.length() == 1) {
                            hour1 = "0" + hour;
                        } else {
                            hour1 = hour;
                        }
                        if (minutes.length() == 1) {
                            minutes1 = "0" + minutes;
                        } else {
                            minutes1 = minutes;
                        }

                        followTime = hour1 + ":" + minutes1;
                        textFollowUpDateTime.setText(followDate + " " + followTime);
                    }
                }, mHour, mMinute, true);
        timePickerDialog.show();
    }


    private void addAutoComplete() {

        ArrayList<String> sourceArrayList = new ArrayList<>();
        sourceArrayList.add("BNI Website");
        sourceArrayList.add("Facebook");
        sourceArrayList.add("Instagram");
        sourceArrayList.add("Self");
        sourceArrayList.add("BNI Member");
        sourceArrayList.add("Non BNI Member");
        ArrayAdapter arrayAdapter = new ArrayAdapter(AddVisitorActivity.this, android.R.layout.simple_spinner_item, sourceArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSource.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();

    }

    private void callAddVisitor() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.addVisitor(Name, MobileNumber, Email, Category, Location, ChapterName, Source, PersonName, status, CurrentDateTime, FollowUpDateTime, LaunchDc, Description);
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
                                textMobileNumber.setText("");
                                textName.setText("");
                                textEmail.setText("");
                                textCategory.setText("");
                                textLocation.setText("");
                                textPersonName.setText("");
                                textDescription.setText("");
                                textName.setSelection(0);
                                textName.requestFocus();
                                scroll_add_visitor.pageScroll(View.FOCUS_UP);
                                successDialog.showDialog("Visitor Added Successfully");
                            } else if (code.equalsIgnoreCase("2")) {
                                errorDialog.showDialog("Visitor Already Added");
                            } else {
                                errorDialog.showDialog("Visitor Not Add");
                            }

                        } else {
                            Toast.makeText(AddVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
        //Source = SpinnerSource.getText().toString().trim();
        PersonName = textPersonName.getText().toString().trim();
        CurrentDateTime = textCurrentDateTime.getText().toString().trim();
        FollowUpDateTime = textFollowUpDateTime.getText().toString().trim();
        //LaunchDc = textLaunchDc.getText().toString().trim();
        Description = textDescription.getText().toString().trim();

        if (Name.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (MobileNumber.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
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
        }

        return true;
    }

    private void getContactDetails() {
     /*   contactListModelArrayList.clear();
        ContentResolver resolver = getContentResolver();
        Cursor cursor = resolver.query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        while (cursor!=null&&cursor.moveToNext()) {

            String id = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts._ID));
            String name = cursor.getString(cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
            String mobile_number = "";
            Cursor phoneCursor = resolver.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, null,
                    ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = ?", new String[]{id}, null);
            if (phoneCursor!=null&&phoneCursor.moveToNext()) {
                mobile_number = phoneCursor.getString(phoneCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));

            }
            if(!name.equalsIgnoreCase("")&&!mobile_number.equalsIgnoreCase("")) {
                contactListModelArrayList.add(new ContactListModel(name, mobile_number));
            }
            phoneCursor.close();
        }
        cursor.close();*/

        contactListModelArrayList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while ((cursor!=null)&&(cursor.moveToNext())) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    if(!name.equalsIgnoreCase("")&& !number.equalsIgnoreCase("")) {
                        contactListModelArrayList.add(new ContactListModel(name, number));
                    }
                }
            } finally {
                cursor.close();
            }
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
                                for (ChapterListModel chapterListModel : chapterListModelArrayList) {
                                    chapterStringArrayList.add(chapterListModel.getChapter_name());
                                }
                                ArrayAdapter arrayAdapter = new ArrayAdapter(AddVisitorActivity.this, android.R.layout.simple_spinner_item, chapterStringArrayList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerChapter.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(AddVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
                                ArrayAdapter arrayAdapter = new ArrayAdapter(AddVisitorActivity.this, android.R.layout.simple_spinner_item, launchDcStringArrayList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerLaunchDc.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(AddVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.isRefresh = true;
    }

    @Override
    public void selectContact(String name, String number) {
        contactDialog.dismiss();
        textName.setText(name);
        textMobileNumber.setText(number);

    }
}
