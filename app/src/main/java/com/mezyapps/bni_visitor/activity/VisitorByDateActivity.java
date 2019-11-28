package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.AllVisitorListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorDateFilterAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.AddVisitorFragment;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorDateFilterModel;
import com.mezyapps.bni_visitor.model.VisitorListAllModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorByDateActivity extends AppCompatActivity {

    private ImageView iv_back,iv_custom_calender,iv_search,iv_back_search;
    private LinearLayout linear_layout_custom_day,linear_layout_today_date;
    private String currentDate,currentDateSend;
    private boolean isStartDate;
    private TextView textDateStart, textDateEnd,textDateStartCustom, textDateEndCustom,text_today_date;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorListAllModel> visitorListAllModelArrayList=new ArrayList<>();
    private AllVisitorListAdapter allVisitorListAdapter;
    private ArrayList<VisitorDateFilterModel> visitorDateFilterModelArrayList=new ArrayList<>();
    private VisitorDateFilterAdapter visitorDateFilterAdapter;
    private RecyclerView recycler_view_all_visitor;
    private RelativeLayout rr_toolbar,rr_toolbar_search;
    private EditText edit_search;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_date);

        find_View_IDs();
        events();
    }

    private void find_View_IDs()
    {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(VisitorByDateActivity.this);
        iv_back=findViewById(R.id.iv_back);
        iv_custom_calender=findViewById(R.id.iv_custom_calender);
        linear_layout_custom_day=findViewById(R.id.linear_layout_custom_day);
        linear_layout_today_date=findViewById(R.id.linear_layout_today_date);
        textDateStart=findViewById(R.id.textDateStart);
        textDateEnd=findViewById(R.id.textDateEnd);
        text_today_date=findViewById(R.id.text_today_date);
        iv_search=findViewById(R.id.iv_search);
        recycler_view_all_visitor=findViewById(R.id.recycler_view_all_visitor);
        rr_toolbar = findViewById(R.id.rr_toolbar);
        rr_toolbar_search = findViewById(R.id.rr_toolbar_search);
        iv_back_search = findViewById(R.id.iv_back_search);
        edit_search = findViewById(R.id.edit_search);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitorByDateActivity.this);
        recycler_view_all_visitor.setLayoutManager(linearLayoutManager);


        currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());
        currentDateSend = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
        textDateEnd.setText(currentDate);
        textDateStart.setText(currentDate);

        if (NetworkUtils.isNetworkAvailable(VisitorByDateActivity.this)) {
            callSingleDateFilter(currentDateSend);
        }
        else {
            NetworkUtils.isNetworkNotAvailable(VisitorByDateActivity.this);
        }


    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        iv_custom_calender.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                customDateDialog();
            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });

        iv_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rr_toolbar.setVisibility(View.GONE);
                rr_toolbar_search.setVisibility(View.VISIBLE);
            }
        });

        iv_back_search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                rr_toolbar_search.setVisibility(View.GONE);
                rr_toolbar.setVisibility(View.VISIBLE);
                edit_search.setText("");
            }
        });

        edit_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                visitorDateFilterAdapter.getFilter().filter(edit_search.getText().toString().trim());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

    }


    /*private void callAllVisitorList() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.visitorAllList();
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorListAllModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorListAllModelArrayList=successModule.getVisitorListAllModelArrayList();
                                Collections.reverse(visitorListAllModelArrayList);
                                if(visitorListAllModelArrayList.size()!=0) {
                                    allVisitorListAdapter=new AllVisitorListAdapter(VisitorByDateActivity.this,visitorListAllModelArrayList);
                                    recycler_view_all_visitor.setAdapter(allVisitorListAdapter);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(VisitorByDateActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    }*/

    private void callSingleDateFilter(String date) {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.singleDateFilter(date);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorListAllModelArrayList.clear();
                        visitorDateFilterModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorDateFilterModelArrayList=successModule.getVisitorDateFilterModelArrayList();
                                if(visitorDateFilterModelArrayList.size()!=0) {
                                    Collections.reverse(visitorDateFilterModelArrayList);
                                    visitorDateFilterAdapter=new VisitorDateFilterAdapter(VisitorByDateActivity.this,visitorDateFilterModelArrayList);
                                    recycler_view_all_visitor.setAdapter(visitorDateFilterAdapter);
                                    visitorDateFilterAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorDateFilterAdapter.notifyDataSetChanged();
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                allVisitorListAdapter=new AllVisitorListAdapter(VisitorByDateActivity.this,visitorListAllModelArrayList);
                                recycler_view_all_visitor.setAdapter(allVisitorListAdapter);
                                allVisitorListAdapter.notifyDataSetChanged();
                                // text_view_empty.setVisibility(View.VISIBLE);
                                visitorDateFilterAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorByDateActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    private void callTwoDateFilter(String startDateSend, String endDateSend) {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.twoDateFilter(startDateSend,endDateSend);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorListAllModelArrayList.clear();
                        visitorDateFilterModelArrayList.clear();

                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorDateFilterModelArrayList=successModule.getVisitorDateFilterModelArrayList();
                                if(visitorDateFilterModelArrayList.size()!=0) {
                                   // Collections.reverse(visitorDateFilterModelArrayList);
                                    visitorDateFilterAdapter=new VisitorDateFilterAdapter(VisitorByDateActivity.this,visitorDateFilterModelArrayList);
                                    recycler_view_all_visitor.setAdapter(visitorDateFilterAdapter);
                                    visitorDateFilterAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorDateFilterAdapter.notifyDataSetChanged();
                                    allVisitorListAdapter.notifyDataSetChanged();

                                }
                            } else {
                                allVisitorListAdapter=new AllVisitorListAdapter(VisitorByDateActivity.this,visitorListAllModelArrayList);
                                recycler_view_all_visitor.setAdapter(allVisitorListAdapter);
                                allVisitorListAdapter.notifyDataSetChanged();
                                visitorDateFilterAdapter.notifyDataSetChanged();
                                // text_view_empty.setVisibility(View.VISIBLE);
                            }
                        } else {
                            Toast.makeText(VisitorByDateActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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


    //Custom Date Dialog
    private void customDateDialog() {
        final Dialog customDateDialog= new Dialog(VisitorByDateActivity.this);
        customDateDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        customDateDialog.setContentView(R.layout.custom_date);

        TextView text_today = customDateDialog.findViewById(R.id.text_today);
        TextView text_yesterday = customDateDialog.findViewById(R.id.text_yesterday);
        TextView text_this_week = customDateDialog.findViewById(R.id.text_this_week);
        TextView text_this_month = customDateDialog.findViewById(R.id.text_this_month);
        TextView text_last_month = customDateDialog.findViewById(R.id.text_last_month);
        final TextView text_custom = customDateDialog.findViewById(R.id.text_custom);


        customDateDialog.setCancelable(true);
        customDateDialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        customDateDialog.show();

        Window window = customDateDialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );


        //Events Custom Date
        text_today.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.VISIBLE);
                linear_layout_custom_day.setVisibility(View.GONE);
                customDateDialog.dismiss();
                currentDate = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault()).format(new Date());

                String SendDate= new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());
                text_today_date.setText(currentDate);
                callSingleDateFilter(SendDate);
                //   Toast.makeText(SalesReportActivity.this, currentDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_yesterday.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.VISIBLE);
                linear_layout_custom_day.setVisibility(View.GONE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DATE, -1);
                String yesterday=dateFormat.format(cal.getTime());
                text_today_date.setText(yesterday);


                DateFormat sendDateFormat = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.add(Calendar.DATE, -1);
                String yesterdaySend=sendDateFormat.format(calSend.getTime());
                text_today_date.setText(yesterday);


                callSingleDateFilter(yesterdaySend);
                // Toast.makeText(SalesReportActivity.this, yesterday, Toast.LENGTH_SHORT).show();
            }
        });
        text_this_week.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();

                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String startDate=dateFormat.format(cal.getTime());
                cal.add(Calendar.DATE, 6);
                String endDate = dateFormat.format(cal.getTime());

                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DAY_OF_WEEK, Calendar.MONDAY);
                String startDateSend=dateFormatSend.format(calSend.getTime());
                calSend.add(Calendar.DATE, 6);
                String endDateSend = dateFormatSend.format(calSend.getTime());

                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);
                callTwoDateFilter(startDateSend,endDateSend);

                // Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }

        });
        text_this_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DAY_OF_MONTH, 1);
                String startDate=dateFormat.format(cal.getTime());
                cal.add(Calendar.MONTH, 1);
                cal.add(Calendar.DATE, -1);
                String endDate = dateFormat.format(cal.getTime());

                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DAY_OF_MONTH, 1);
                String startDateSend=dateFormatSend.format(calSend.getTime());
                calSend.add(Calendar.MONTH, 1);
                calSend.add(Calendar.DATE, -1);
                String endDateSend = dateFormatSend.format(calSend.getTime());

                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);
                callTwoDateFilter(startDateSend,endDateSend);
                //Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_last_month.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
                Calendar cal = Calendar.getInstance();
                cal.set(Calendar.DATE, 1);
                cal.add(Calendar.DAY_OF_MONTH, -1);
                Date lastDateOfPreviousMonth = cal.getTime();
                String endDate=dateFormat.format(lastDateOfPreviousMonth);
                cal.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonth = cal.getTime();
                String startDate=dateFormat.format(firstDateOfPreviousMonth);


                DateFormat dateFormatSend = new SimpleDateFormat("yyyy-MM-dd");
                Calendar calSend = Calendar.getInstance();
                calSend.set(Calendar.DATE, 1);
                calSend.add(Calendar.DAY_OF_MONTH, -1);
                Date lastDateOfPreviousMonthSend = calSend.getTime();
                String endDateSend=dateFormatSend.format(lastDateOfPreviousMonthSend);
                calSend.set(Calendar.DATE, 1);
                Date firstDateOfPreviousMonthSend = calSend.getTime();
                String startDateSend=dateFormatSend.format(firstDateOfPreviousMonthSend);


                textDateStart.setText(startDate);
                textDateEnd.setText(endDate);
                callTwoDateFilter(startDateSend,endDateSend);
                //Toast.makeText(SalesReportActivity.this, startDate+" "+endDate, Toast.LENGTH_SHORT).show();
            }
        });
        text_custom.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                linear_layout_today_date.setVisibility(View.GONE);
                linear_layout_custom_day.setVisibility(View.VISIBLE);
                customDateDialog.dismiss();
                final Dialog customDateDialogDate= new Dialog(VisitorByDateActivity.this);
                customDateDialogDate.requestWindowFeature(Window.FEATURE_NO_TITLE);
                customDateDialogDate.setContentView(R.layout.custom_date_calendar);

                textDateStartCustom = customDateDialogDate.findViewById(R.id.textDateStart);
                textDateEndCustom = customDateDialogDate.findViewById(R.id.textDateEnd);
                TextView textAll = customDateDialogDate.findViewById(R.id.textAll);

                currentDate = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault()).format(new Date());

                textDateStartCustom.setText(currentDate);
                textDateEndCustom.setText(currentDate);

                customDateDialogDate.setCancelable(false);
                customDateDialogDate.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

                customDateDialogDate.show();
                Window window = customDateDialogDate.getWindow();
                window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT );

                textDateStartCustom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStartDate=true;
                        customDatePickerDialog();
                    }
                });
                textDateEndCustom.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        isStartDate=false;
                        customDatePickerDialog();
                    }
                });

                textAll.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        customDateDialogDate.dismiss();
                        String startDate=textDateStartCustom.getText().toString();
                        String endDate=textDateEndCustom.getText().toString();
                        textDateStart.setText(startDate);
                        textDateEnd.setText(endDate);

                        callTwoDateFilter(startDate,endDate);
                    }
                });
            }
        });
    }



    private void customDatePickerDialog() {
        Calendar calendar = Calendar.getInstance();
        int mYear = calendar.get(Calendar.YEAR);
        int mMonth = calendar.get(Calendar.MONTH);
        int mDay = calendar.get(Calendar.DAY_OF_MONTH);

        DatePickerDialog datePickerDialog = new DatePickerDialog(VisitorByDateActivity.this,
                new DatePickerDialog.OnDateSetListener() {

                    @Override
                    public void onDateSet(DatePicker view, int year,
                                          int monthOfYear, int dayOfMonth) {
                        Calendar calendar = Calendar.getInstance();
                        calendar.set(year, monthOfYear, dayOfMonth);

                        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
                        String dateString = format.format(calendar.getTime());

                        if(isStartDate) {
                            textDateStartCustom.setText(dateString);
                        }
                        else {
                            textDateEndCustom.setText(dateString);
                        }
                    }
                }, mYear, mMonth, mDay);
        datePickerDialog.show();

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callSingleDateFilter(currentDateSend);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.isRefresh=true;
    }
}
