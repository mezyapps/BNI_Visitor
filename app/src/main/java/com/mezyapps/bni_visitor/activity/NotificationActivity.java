package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.AllVisitorListAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorListAllModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class NotificationActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recycler_view_notification;
    private LinearLayout ll_notification_not_found;
    private String visitor_id;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorListAllModel> visitorListAllModelArrayList=new ArrayList<>();
    private AllVisitorListAdapter allVisitorListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);

        find_View_Ids();
        events();
    }

    private void find_View_Ids() {
        iv_back=findViewById(R.id.iv_back);
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(NotificationActivity.this);
        recycler_view_notification=findViewById(R.id.recycler_view_notification);
        ll_notification_not_found=findViewById(R.id.ll_notification_not_found);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(NotificationActivity.this);
        recycler_view_notification.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            visitor_id = bundle.getString("ID");
            if(!visitor_id.equalsIgnoreCase(""))
            {
                //Toast.makeText(this, visitor_id, Toast.LENGTH_SHORT).show();
                ll_notification_not_found.setVisibility(View.GONE);
                recycler_view_notification.setVisibility(View.VISIBLE);
                if (NetworkUtils.isNetworkAvailable(NotificationActivity.this)) {
                    callVisitorNotification();
                } else {
                    NetworkUtils.isNetworkNotAvailable(NotificationActivity.this);
                }
            }
            else
            {
                ll_notification_not_found.setVisibility(View.VISIBLE);
                recycler_view_notification.setVisibility(View.GONE);
            }
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
    private void callVisitorNotification() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.visitorById(visitor_id);
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

                                visitorListAllModelArrayList=successModule.getVisitorListIdModelArrayList();
                                if(visitorListAllModelArrayList.size()!=0) {
                                    allVisitorListAdapter=new AllVisitorListAdapter(NotificationActivity.this,visitorListAllModelArrayList);
                                    recycler_view_notification.setAdapter(allVisitorListAdapter);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    ll_notification_not_found.setVisibility(View.VISIBLE);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                ll_notification_not_found.setVisibility(View.VISIBLE);
                                allVisitorListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(NotificationActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
