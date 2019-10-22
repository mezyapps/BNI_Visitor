package com.mezyapps.bni_visitor.fragment;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.AddVisitorActivity;
import com.mezyapps.bni_visitor.activity.CommonVisitorActivity;
import com.mezyapps.bni_visitor.activity.MainActivity;
import com.mezyapps.bni_visitor.activity.VisitorByChapterActivity;
import com.mezyapps.bni_visitor.activity.VisitorByDateActivity;
import com.mezyapps.bni_visitor.activity.VisitorBySourceActivity;
import com.mezyapps.bni_visitor.activity.VisitorNotInterestedActivity;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.MyValueFormatter;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class HomeFragment extends Fragment {
    private Context mContext;
    private PieChart pieChart_visitor;
    private PieData pieDataVisitor;
    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    private BottomNavigationView bottom_navigation;
    private RelativeLayout rr_visitor_not_interested, rr_visitor_by_chapter, rr_visitor_by_source, rr_visitor_by_date;
    private String not_interested="0",member="0",follow_up="0";
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    public static boolean isRefresh=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_home, container, false);
        mContext = getActivity();
        find_View_IDs(view);
        events();
        return view;
    }

    private void find_View_IDs(View view) {
        pieChart_visitor = view.findViewById(R.id.pieChart_visitor);
        bottom_navigation = view.findViewById(R.id.bottom_navigation);
        rr_visitor_not_interested = view.findViewById(R.id.rr_visitor_not_interested);
        rr_visitor_by_chapter = view.findViewById(R.id.rr_visitor_by_chapter);
        rr_visitor_by_source = view.findViewById(R.id.rr_visitor_by_source);
        rr_visitor_by_date = view.findViewById(R.id.rr_visitor_by_date);

        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(mContext);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            callPieChartCount();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }

    }

    private void events() {
        bottom_navigation.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {

                switch (menuItem.getItemId()) {
                    case R.id.nav_total_visitor:
                        ((MainActivity) mContext).loadFragment(new LunchFragment());
                        break;
                    case R.id.nav_chapter:
                        ((MainActivity) mContext).loadFragment(new ChapterListFragment());
                        break;
                    case R.id.nav_add_visitor:
                        startActivity(new Intent(mContext,AddVisitorActivity.class));
                        break;

                }

                return true;
            }
        });
        rr_visitor_not_interested.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((MainActivity) mContext).loadFragment(new AddVisitorFragment());
            }
        });
        rr_visitor_by_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, VisitorByChapterActivity.class));
            }
        });
        rr_visitor_by_source.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, VisitorBySourceActivity.class));
            }
        });
        rr_visitor_by_date.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, VisitorByDateActivity.class));
            }
        });

        pieChart_visitor.setOnChartValueSelectedListener( new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                String value = pieChart_visitor.getData().getXVals().get(e.getXIndex());
                if(value.equalsIgnoreCase("Not Interested"))
                {
                    Intent intent=new Intent(mContext,CommonVisitorActivity.class);
                    intent.putExtra("status","2");
                    startActivity(intent);

                }else if(value.equalsIgnoreCase("Follow-up"))
                {
                    Intent intent=new Intent(mContext,CommonVisitorActivity.class);
                    intent.putExtra("status","0");
                    startActivity(intent);
                }
                else
                {
                    Intent intent=new Intent(mContext,CommonVisitorActivity.class);
                    intent.putExtra("status","1");
                    startActivity(intent);
                }

            }

            @Override
            public void onNothingSelected() {

            }
        });

    }

    private PieDataSet getFirstYValue() {
        PieDataSet pieDataSet;
        pieDataSet = new PieDataSet(entries, "");
        pieDataSet.setValueFormatter(new MyValueFormatter());
        pieDataSet.setColors(colors);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setSliceSpace(3);
        pieDataSet.setValueTextSize(12);
        pieDataSet.setValueTextColor(Color.WHITE);

        return pieDataSet;

    }

    private ArrayList<String> getFirstXValue() {

        ArrayList<String> xValues = new ArrayList<>();
        xValues.add("Members");
        xValues.add("Follow-up");
        xValues.add("Not Interested");
        return xValues;
    }

    private void pieChartSet() {

        colors.add(getResources().getColor(R.color.green));
        colors.add(getResources().getColor(R.color.orange));
        colors.add(getResources().getColor(R.color.red));

        float  memberInt=Float.parseFloat(member);
        float followUpInt=Float.parseFloat(follow_up);
        float InterestedInt=Float.parseFloat(not_interested);

        Entry entryGQ = new Entry(Math.round(memberInt), 0);
        Entry entryLQ = new Entry(Math.round(followUpInt), 1);
        Entry entryOR = new Entry(Math.round(InterestedInt), 2);


        entries.add(entryGQ);
        entries.add(entryLQ);
        entries.add(entryOR);


        pieDataVisitor = new PieData(getFirstXValue(), getFirstYValue());
        pieChart_visitor.setData(pieDataVisitor);
        pieChart_visitor.setHoleRadius(15);
        pieChart_visitor.setDescription("");
        pieChart_visitor.invalidate();
        Legend legendFirstChart = pieChart_visitor.getLegend();
        legendFirstChart.setTextSize(12);
        legendFirstChart.setTextColor(getResources().getColor(R.color.app_color));

    }
    private void callPieChartCount() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.pieChartCount();
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
                            if (code.equalsIgnoreCase("1"))
                            {
                                colors.clear();
                                entries.clear();
                                not_interested=successModule.getNot_interested();
                                member=successModule.getMember();
                                follow_up=successModule.getFollow_up();
                                pieChartSet();
                            } else {
                                showProgressDialog.dismissDialog();
                            }
                        } else {
                            Toast.makeText(mContext, "Response Null", Toast.LENGTH_SHORT).show();
                            showProgressDialog.dismissDialog();
                        }
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    showProgressDialog.dismissDialog();
                }
            }

            @Override
            public void onFailure(Call<SuccessModel> call, Throwable t) {
                showProgressDialog.dismissDialog();
            }
        });
    }
    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh)
        {
            isRefresh=false;
            callPieChartCount();
        }
    }
}
