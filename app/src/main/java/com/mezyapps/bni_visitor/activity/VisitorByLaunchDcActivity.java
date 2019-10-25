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
import com.mezyapps.bni_visitor.adapter.VisitorByLaunchDcAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.LunchDcModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorByLaunchDcActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recycler_view_launch_dc;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<LunchDcModel> lunchDcModelArrayList=new ArrayList<>();
    private VisitorByLaunchDcAdapter visitorByLaunchDc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_launch_dc);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(VisitorByLaunchDcActivity.this);
        iv_back=findViewById(R.id.iv_back);
        recycler_view_launch_dc=findViewById(R.id.recycler_view_launch_dc);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitorByLaunchDcActivity.this);
        recycler_view_launch_dc.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(VisitorByLaunchDcActivity.this)) {
            launchDcList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(VisitorByLaunchDcActivity.this);
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

    private void launchDcList() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.launchDcList();
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
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

                                lunchDcModelArrayList=successModule.getLunchDcModelArrayList();
                                if(lunchDcModelArrayList.size()!=0) {
                                    visitorByLaunchDc=new VisitorByLaunchDcAdapter(VisitorByLaunchDcActivity.this,lunchDcModelArrayList);
                                    recycler_view_launch_dc.setAdapter(visitorByLaunchDc);
                                    //text_view_empty.setVisibility(View.GONE);
                                    visitorByLaunchDc.notifyDataSetChanged();

                                }
                                else
                                {
                                   // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorByLaunchDc.notifyDataSetChanged();
                                }
                            } else {
                                //text_view_empty.setVisibility(View.VISIBLE);
                                visitorByLaunchDc.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorByLaunchDcActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
}
