package com.mezyapps.bni_visitor.fragment;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.activity.AddVisitorActivity;
import com.mezyapps.bni_visitor.activity.CommonVisitorActivity;
import com.mezyapps.bni_visitor.adapter.AllVisitorListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorListCommonAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorListAllModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.net.Inet4Address;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class AddVisitorFragment extends Fragment {
    private Context mContext;
    private RecyclerView recycler_view_visitor_list;
    private FloatingActionButton fab_add_visitor;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorListAllModel> visitorListAllModelArrayList=new ArrayList<>();
    private AllVisitorListAdapter allVisitorListAdapter;
    public static boolean isRefresh=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view=inflater.inflate(R.layout.fragment_add_visitor, container, false);
        mContext=getActivity();
        find_View_IDs(view);
        events();
        return view;
    }

    private void find_View_IDs(View view) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(mContext);
        recycler_view_visitor_list=view.findViewById(R.id.recycler_view_visitor_list);
        fab_add_visitor=view.findViewById(R.id.fab_add_visitor);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        recycler_view_visitor_list.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            callAllVisitorList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    private void events() {
        fab_add_visitor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(mContext, AddVisitorActivity.class));
            }
        });
    }


    private void callAllVisitorList() {
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
                                    allVisitorListAdapter=new AllVisitorListAdapter(mContext,visitorListAllModelArrayList);
                                    recycler_view_visitor_list.setAdapter(allVisitorListAdapter);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    allVisitorListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                                allVisitorListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(mContext, "Response Null", Toast.LENGTH_SHORT).show();
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
    public void onResume() {
        super.onResume();
        if (isRefresh)
        {
            isRefresh=false;
            callAllVisitorList();
        }
    }
}
