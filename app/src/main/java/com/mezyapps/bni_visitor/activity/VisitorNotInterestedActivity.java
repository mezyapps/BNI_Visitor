package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.VisitorListCommonAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorNotInterestedActivity extends AppCompatActivity {
    private ImageView iv_back;
    private RecyclerView recycler_view_follow_up;
    private String status="2";
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList = new ArrayList<>();
    private VisitorListCommonAdapter visitorListCommonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_not_interested);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(VisitorNotInterestedActivity.this);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_follow_up = findViewById(R.id.recycler_view_follow_up);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VisitorNotInterestedActivity.this);
        recycler_view_follow_up.setLayoutManager(linearLayoutManager);



        if (NetworkUtils.isNetworkAvailable(VisitorNotInterestedActivity.this)) {
            callVisitorList();
        } else {
            NetworkUtils.isNetworkNotAvailable(VisitorNotInterestedActivity.this);
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

    private void callVisitorList() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.visitorListStatus(status);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorListStatusModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorListStatusModelArrayList = successModule.getVisitorListStatusModelArrayList();
                                if (visitorListStatusModelArrayList.size() != 0) {
                                    Collections.reverse(visitorListStatusModelArrayList);
                                    visitorListCommonAdapter = new VisitorListCommonAdapter(VisitorNotInterestedActivity.this, visitorListStatusModelArrayList);
                                    recycler_view_follow_up.setAdapter(visitorListCommonAdapter);
                                    visitorListCommonAdapter.notifyDataSetChanged();
                                } else {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorListCommonAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                                visitorListCommonAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorNotInterestedActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.isRefresh=true;
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        callVisitorList();
    }
}

