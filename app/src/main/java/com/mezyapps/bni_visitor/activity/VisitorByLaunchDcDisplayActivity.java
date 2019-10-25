package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.VisitorByChapterListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorByLaunchDcListAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorByChapterModel;
import com.mezyapps.bni_visitor.model.VisitorByLaunchDcModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorByLaunchDcDisplayActivity extends AppCompatActivity {

    private ImageView iv_back;
    private String launch_dc_id, status;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorByLaunchDcModel> visitorByLaunchDcModelArrayList = new ArrayList<>();
    private VisitorByLaunchDcListAdapter visitorByLaunchDcListAdapter;
    private RecyclerView recycler_view_visitor_launch_dc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_launch_dc_dispaly);
        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(VisitorByLaunchDcDisplayActivity.this);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_visitor_launch_dc = findViewById(R.id.recycler_view_visitor_launch_dc);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VisitorByLaunchDcDisplayActivity.this);
        recycler_view_visitor_launch_dc.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launch_dc_id = bundle.getString("launch_dc_id");
            status = bundle.getString("status");
        }

        if (NetworkUtils.isNetworkAvailable(VisitorByLaunchDcDisplayActivity.this)) {
            callVisitorListChapter();
        } else {
            NetworkUtils.isNetworkNotAvailable(VisitorByLaunchDcDisplayActivity.this);
        }
    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    private void callVisitorListChapter() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.visitorListLaunchDc(launch_dc_id, status);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorByLaunchDcModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorByLaunchDcModelArrayList = successModule.getVisitorByLaunchDcModelArrayList();
                                Collections.reverse(visitorByLaunchDcModelArrayList);
                                if (visitorByLaunchDcModelArrayList.size() != 0) {
                                    visitorByLaunchDcListAdapter = new VisitorByLaunchDcListAdapter(VisitorByLaunchDcDisplayActivity.this, visitorByLaunchDcModelArrayList);
                                    recycler_view_visitor_launch_dc.setAdapter(visitorByLaunchDcListAdapter);
                                    visitorByLaunchDcListAdapter.notifyDataSetChanged();
                                } else {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorByLaunchDcListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                                visitorByLaunchDcListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorByLaunchDcDisplayActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
