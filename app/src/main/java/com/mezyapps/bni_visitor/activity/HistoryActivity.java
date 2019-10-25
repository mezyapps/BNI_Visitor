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
import com.mezyapps.bni_visitor.adapter.VisitorHistoryAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorListCommonAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorHistoryModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HistoryActivity extends AppCompatActivity {

    private RecyclerView recycler_view_all_history;
    public static ApiInterface apiInterface;
    private ImageView iv_back;
    private String visitor_id;
    private ShowProgressDialog showProgressDialog;
    private ArrayList<VisitorHistoryModel> visitorHistoryModelArrayList=new ArrayList<>();
    private VisitorHistoryAdapter visitorHistoryAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        find_View_IDS();
        events();
    }

    private void find_View_IDS() {
        recycler_view_all_history=findViewById(R.id.recycler_view_all_history);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        iv_back = findViewById(R.id.iv_back);
        showProgressDialog=new ShowProgressDialog(HistoryActivity.this);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null) {
            visitor_id = bundle.getString("VISITOR");
        }
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(HistoryActivity.this);
        recycler_view_all_history.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(HistoryActivity.this)) {
            callVisitorHistoryList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(HistoryActivity.this);
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
    private void callVisitorHistoryList() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.visitorHistory(visitor_id);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorHistoryModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorHistoryModelArrayList=successModule.getVisitorHistoryModelArrayList();
                                if(visitorHistoryModelArrayList.size()!=0) {
                                    visitorHistoryAdapter=new VisitorHistoryAdapter(HistoryActivity.this,visitorHistoryModelArrayList);
                                    recycler_view_all_history.setAdapter(visitorHistoryAdapter);
                                    visitorHistoryAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorHistoryAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                                visitorHistoryAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(HistoryActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
