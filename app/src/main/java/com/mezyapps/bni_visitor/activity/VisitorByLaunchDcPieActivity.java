package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.listener.OnChartValueSelectedListener;
import com.github.mikephil.charting.utils.Highlight;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
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

public class VisitorByLaunchDcPieActivity extends AppCompatActivity {

    private PieChart pieChart_visitor;
    private PieData pieDataVisitor;
    ArrayList<Entry> entries = new ArrayList<>();
    ArrayList<Integer> colors = new ArrayList<>();
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ImageView iv_back;
    private TextView text_title_name, textDataNotFound;
    String launch_dc_id, launch_dc_name;
    private String not_interested = "0", member = "0", follow_up = "0";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_launch_dc_pie);
        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        iv_back = findViewById(R.id.iv_back);
        pieChart_visitor = findViewById(R.id.pieChart_visitor);
        text_title_name = findViewById(R.id.text_title_name);
        textDataNotFound = findViewById(R.id.textDataNotFound);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(VisitorByLaunchDcPieActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launch_dc_id = bundle.getString("launch_dc_id");
            launch_dc_name = bundle.getString("launch_dc_name");
            text_title_name.setText(launch_dc_name);
        }

        if (NetworkUtils.isNetworkAvailable(VisitorByLaunchDcPieActivity.this)) {
            callPieChartCount();
        } else {
            NetworkUtils.isNetworkNotAvailable(VisitorByLaunchDcPieActivity.this);
        }

    }

    private void events() {

        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        pieChart_visitor.setOnChartValueSelectedListener(new OnChartValueSelectedListener() {
            @Override
            public void onValueSelected(Entry e, int dataSetIndex, Highlight h) {
                String value = pieChart_visitor.getData().getXVals().get(e.getXIndex());
                if (value.equalsIgnoreCase("Not Interested")) {
                    Intent intent = new Intent(VisitorByLaunchDcPieActivity.this, VisitorByLaunchDcDisplayActivity.class);
                    intent.putExtra("status", "2");
                    intent.putExtra("launch_dc_id", launch_dc_id);
                    startActivity(intent);

                } else if (value.equalsIgnoreCase("Follow-up")) {
                    Intent intent = new Intent(VisitorByLaunchDcPieActivity.this, VisitorByLaunchDcDisplayActivity.class);
                    intent.putExtra("launch_dc_id", launch_dc_id);
                    intent.putExtra("status", "0");
                    startActivity(intent);
                } else {
                    Intent intent = new Intent(VisitorByLaunchDcPieActivity.this, VisitorByLaunchDcDisplayActivity.class);
                    intent.putExtra("launch_dc_id", launch_dc_id);
                    intent.putExtra("status", "1");
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

        colors.add(getResources().getColor(R.color.chapter_member));
        colors.add(getResources().getColor(R.color.chapter_follow_up));
        colors.add(getResources().getColor(R.color.chapter_not_interested));

        float memberInt = Float.parseFloat(member);
        float followUpInt = Float.parseFloat(follow_up);
        float InterestedInt = Float.parseFloat(not_interested);

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
        Call<SuccessModel> call = apiInterface.visitorBYLaunchDcCount(launch_dc_id);
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
                                colors.clear();
                                entries.clear();
                                not_interested = successModule.getNot_interested();
                                member = successModule.getMember();
                                follow_up = successModule.getFollow_up();
                                if (not_interested.equalsIgnoreCase("0") && member.equalsIgnoreCase("0") && follow_up.equalsIgnoreCase("0")) {
                                    textDataNotFound.setVisibility(View.VISIBLE);
                                    pieChart_visitor.setVisibility(View.GONE);
                                } else {
                                    pieChartSet();
                                }
                            } else {
                                showProgressDialog.dismissDialog();
                            }
                        } else {
                            Toast.makeText(VisitorByLaunchDcPieActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        callPieChartCount();
    }
}
