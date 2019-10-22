package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.ChapterListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorListCommonAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorListStatusModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CommonVisitorActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recycler_view_follow_up;
    private String status;
    private TextView  text_title_name;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorListStatusModel> visitorListStatusModelArrayList=new ArrayList<>();
    private VisitorListCommonAdapter visitorListCommonAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_common_visitor);


        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(CommonVisitorActivity.this);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_follow_up = findViewById(R.id.recycler_view_follow_up);
        text_title_name = findViewById(R.id.text_title_name);
        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(CommonVisitorActivity.this);
        recycler_view_follow_up.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if(bundle!=null)
        {
            status=bundle.getString("status");
        }
        if(status.equalsIgnoreCase("0"))
        {
            text_title_name.setText("Follow Up");
        }else if (status.equalsIgnoreCase("1"))
        {
            text_title_name.setText("Member");
        }
        else{
            text_title_name.setText("Not Interested");
        }

        if (NetworkUtils.isNetworkAvailable(CommonVisitorActivity.this)) {
            callVisitorList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(CommonVisitorActivity.this);
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

                                visitorListStatusModelArrayList=successModule.getVisitorListStatusModelArrayList();
                                if(visitorListStatusModelArrayList.size()!=0) {
                                    visitorListCommonAdapter=new VisitorListCommonAdapter(CommonVisitorActivity.this,visitorListStatusModelArrayList);
                                    recycler_view_follow_up.setAdapter(visitorListCommonAdapter);
                                    visitorListCommonAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                   // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorListCommonAdapter.notifyDataSetChanged();
                                }
                            } else {
                               // text_view_empty.setVisibility(View.VISIBLE);
                                visitorListCommonAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(CommonVisitorActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
    protected void onRestart() {
        super.onRestart();
        callVisitorList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.isRefresh=true;
    }
}
