package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.Dialog;
import android.content.ContentResolver;
import android.database.Cursor;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.Window;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.ContactListAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.ContactListModel;
import com.mezyapps.bni_visitor.model.LunchDcModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.ContactListInterface;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditVisitorActivity extends AppCompatActivity implements ContactListInterface {

    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ImageView iv_back, iv_contact;
    private String visitor_id, visitor_name, visitor_mobile, visitor_email, visitor_category, visitor_location, visitor_chapter, visitor_description, LaunchDc, visitor_source;
    private AutoCompleteTextView textName, textMobileNumber, textEmail, textCategory, textLocation, textDescription;
    //Contact Dialog
    private ArrayList<ContactListModel> contactListModelArrayList = new ArrayList<>();
    private Dialog contactDialog;
    private EditText edt_search;
    private RecyclerView recyclerView_contact;
    private ContactListAdapter contactListAdapter;
    private static final String[] PROJECTION = new String[]{
            ContactsContract.CommonDataKinds.Phone.CONTACT_ID,
            ContactsContract.Contacts.DISPLAY_NAME,
            ContactsContract.CommonDataKinds.Phone.NUMBER};

    //Chapter List Spinner
    private ArrayList<ChapterListModel> chapterListModelArrayList = new ArrayList<>();
    private ArrayList<String> chapterStringArrayList = new ArrayList<>();
    //Chapter Launch Dc
    private ArrayList<LunchDcModel> lunchDcModelArrayList = new ArrayList<>();
    private ArrayList<String> launchDcStringArrayList = new ArrayList<>();
    ArrayList<String> sourceArrayList = new ArrayList<>();
    private Spinner SpinnerSource, SpinnerChapter, SpinnerLaunchDc;
    private Button btn_delete,btn_update;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_visitor);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(EditVisitorActivity.this);
        iv_back = findViewById(R.id.iv_back);
        textName = findViewById(R.id.textName);
        textMobileNumber = findViewById(R.id.textMobileNumber);
        textEmail = findViewById(R.id.textEmail);
        SpinnerChapter = findViewById(R.id.SpinnerChapter);
        SpinnerSource = findViewById(R.id.SpinnerSource);
        SpinnerLaunchDc = findViewById(R.id.SpinnerLaunchDc);
        btn_delete = findViewById(R.id.btn_delete);
        btn_update = findViewById(R.id.btn_update);
        textCategory = findViewById(R.id.textCategory);
        textLocation = findViewById(R.id.textLocation);
        textDescription = findViewById(R.id.textDescription);
        iv_contact = findViewById(R.id.iv_contact);

        //Contact Dialog
        contactDialog = new Dialog(EditVisitorActivity.this);
        contactDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        contactDialog.setCancelable(true);
        contactDialog.setContentView(R.layout.dialog_conatct_dialog);
        recyclerView_contact = contactDialog.findViewById(R.id.recyclerView_contact);
        edt_search = contactDialog.findViewById(R.id.edt_search);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(EditVisitorActivity.this);
        recyclerView_contact.setLayoutManager(linearLayoutManager);


            Bundle bundle = getIntent().getExtras();
            if (bundle != null) {
                visitor_id = bundle.getString("VISITOR_ID");
                visitor_name = bundle.getString("VISITOR_NAME");
                visitor_mobile = bundle.getString("VISITOR_MOBILE");
                visitor_email = bundle.getString("VISITOR_EMAIL");
                visitor_category = bundle.getString("VISITOR_CATEGORY");
                visitor_location = bundle.getString("VISITOR_LOCATION");
                visitor_chapter = bundle.getString("VISITOR_CHAPTER");
                visitor_source = bundle.getString("VISITOR_SOURCE");
            LaunchDc = bundle.getString("VISITOR_LAUNCH_DC");
            visitor_description = bundle.getString("VISITOR_DESCRIPTION");

            textName.setText(visitor_name);
            textMobileNumber.setText(visitor_mobile);
            textEmail.setText(visitor_email);
            textCategory.setText(visitor_category);
            textLocation.setText(visitor_location);
            textDescription.setText(visitor_description);
            chapterStringArrayList.add(visitor_chapter);
            launchDcStringArrayList.add(LaunchDc);
            sourceArrayList.add(visitor_source);
        }

    }

    private void events() {
        getSourceList();
        if (NetworkUtils.isNetworkAvailable(EditVisitorActivity.this)) {
            chapterList();
            launchDcList();
        } else {
            NetworkUtils.isNetworkNotAvailable(EditVisitorActivity.this);
        }
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        iv_contact.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                edt_search.setText("");
                EditVisitorActivity.AccessContactList accessContactList = new EditVisitorActivity.AccessContactList();
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
        SpinnerSource.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                    visitor_source = parent.getItemAtPosition(position).toString();

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        SpinnerChapter.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                visitor_chapter = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        SpinnerLaunchDc.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long l) {
                LaunchDc = parent.getItemAtPosition(position).toString();
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        btn_update.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validation()) {
                    if (NetworkUtils.isNetworkAvailable(EditVisitorActivity.this)) {
                        callUpdateVisitor();
                    } else {
                        NetworkUtils.isNetworkNotAvailable(EditVisitorActivity.this);
                    }
                }
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (NetworkUtils.isNetworkAvailable(EditVisitorActivity.this)) {
                    callDeleteVisitor();
                } else {
                    NetworkUtils.isNetworkNotAvailable(EditVisitorActivity.this);
                }
            }
        });
    }

    private boolean validation() {
        visitor_mobile = textMobileNumber.getText().toString().trim();
        visitor_name = textName.getText().toString().trim();
        visitor_email = textEmail.getText().toString().trim();
        visitor_category = textCategory.getText().toString().trim();
        visitor_location = textLocation.getText().toString().trim();
        visitor_description = textDescription.getText().toString().trim();

        if (visitor_name.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (visitor_mobile.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        }else if (visitor_mobile.length()<10) {
            Toast.makeText(this, "Enter  Valid Mobile Number", Toast.LENGTH_SHORT).show();
            return false;
        } else if (visitor_category.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Category", Toast.LENGTH_SHORT).show();
            return false;
        } else if (visitor_location.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Location", Toast.LENGTH_SHORT).show();
            return false;
        } else if (visitor_chapter.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Chapter Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (visitor_source.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Source Name", Toast.LENGTH_SHORT).show();
            return false;
        } else if (LaunchDc.equalsIgnoreCase("")) {
            Toast.makeText(this, "Select Launch Dc", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true;
    }

    private void getSourceList() {
        if (!visitor_source.equals("BNI Website")) {
            sourceArrayList.add("BNI Website");
        }
        if (!visitor_source.equals("Facebook")) {
            sourceArrayList.add("Facebook");
        }
        if (!visitor_source.equals("Instagram")) {
            sourceArrayList.add("Instagram");
        }
        if (!visitor_source.equals("Self")) {
            sourceArrayList.add("Self");
        }
        if (!visitor_source.equals("BNI Member")) {
            sourceArrayList.add("BNI Member");
        }
        if (!visitor_source.equals("Non BNI Member")) {
            sourceArrayList.add("Non BNI Member");
        }
        ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorActivity.this, android.R.layout.simple_spinner_item, sourceArrayList);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        SpinnerSource.setAdapter(arrayAdapter);
        arrayAdapter.notifyDataSetChanged();
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
        contactListAdapter = new ContactListAdapter(EditVisitorActivity.this, contactListModelArrayList, this);
        Window window = contactDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
        recyclerView_contact.setAdapter(contactListAdapter);
        contactListAdapter.notifyDataSetChanged();
    }

    private void getContactDetails() {
        contactListModelArrayList.clear();
        ContentResolver cr = getContentResolver();
        Cursor cursor = cr.query(ContactsContract.CommonDataKinds.Phone.CONTENT_URI, PROJECTION, null, null, null);
        if (cursor != null) {
            try {
                final int nameIndex = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
                final int numberIndex = cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER);

                String name, number;
                while ((cursor != null) && (cursor.moveToNext())) {
                    name = cursor.getString(nameIndex);
                    number = cursor.getString(numberIndex);
                    if (!name.equalsIgnoreCase("") && !number.equalsIgnoreCase("")) {
                        contactListModelArrayList.add(new ContactListModel(name, number));
                    }
                }
            } finally {
                cursor.close();
            }
        }
    }

    @Override
    public void selectContact(String name, String number) {
        contactDialog.dismiss();
        textName.setText(name);
        textMobileNumber.setText(number);
        textEmail.setSelection(0);
        textEmail.requestFocus();
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
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            chapterListModelArrayList.clear();
                            if (code.equalsIgnoreCase("1")) {
                                chapterListModelArrayList = successModule.getChapterListModelArrayList();
                                for (ChapterListModel chapterListModel : chapterListModelArrayList) {
                                    String chapter_name = chapterListModel.getChapter_name();
                                    if (!chapter_name.equals(visitor_chapter)) {
                                        chapterStringArrayList.add(chapter_name);
                                    }
                                }
                                ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorActivity.this, android.R.layout.simple_spinner_item, chapterStringArrayList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerChapter.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();

                            }
                        } else {
                            Toast.makeText(EditVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
                            lunchDcModelArrayList.clear();
                            if (code.equalsIgnoreCase("1")) {
                                lunchDcModelArrayList = successModule.getLunchDcModelArrayList();
                                for (LunchDcModel lunchDcModel : lunchDcModelArrayList) {
                                    String launch_dc = lunchDcModel.getLaunch_dc_name();
                                    if (!launch_dc.equals(LaunchDc)) {
                                        launchDcStringArrayList.add(launch_dc);
                                    }
                                }
                                ArrayAdapter arrayAdapter = new ArrayAdapter(EditVisitorActivity.this, android.R.layout.simple_spinner_item, launchDcStringArrayList);
                                arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                                SpinnerLaunchDc.setAdapter(arrayAdapter);
                                arrayAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(EditVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    private void callDeleteVisitor() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.deleteVisitor(visitor_id);
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
                                Toast.makeText(EditVisitorActivity.this, "Visitor Delete Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(EditVisitorActivity.this, "Visitor Not Delete", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(EditVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    private void callUpdateVisitor() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.updateVisitor(visitor_id, visitor_name, visitor_mobile, visitor_email, visitor_category,visitor_location,visitor_chapter,visitor_source,LaunchDc,visitor_description);
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
                                Toast.makeText(EditVisitorActivity.this, "Visitor Update Successfully", Toast.LENGTH_SHORT).show();
                                onBackPressed();
                            } else {
                                Toast.makeText(EditVisitorActivity.this, "Visitor Not Update", Toast.LENGTH_SHORT).show();
                            }

                        } else {
                            Toast.makeText(EditVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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


}
