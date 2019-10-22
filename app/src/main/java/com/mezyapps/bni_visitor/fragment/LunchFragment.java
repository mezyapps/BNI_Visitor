package com.mezyapps.bni_visitor.fragment;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.adapter.ChapterListAdapter;
import com.mezyapps.bni_visitor.adapter.LunchDcAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.LunchDcModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LunchFragment extends Fragment {
    private Context mContext;
    private FloatingActionButton fab_add_lunch_dc;
    private Dialog dialogAddChapter;
    public static ApiInterface apiInterface;
    private ShowProgressDialog showProgressDialog;
    private TextInputEditText edit_chapter;
    private RecyclerView recycler_view_lunch_list;
    private ArrayList<LunchDcModel> lunchDcModelArrayList=new ArrayList<>();
    private LunchDcAdapter lunchDcAdapter;
    private TextView text_view_empty;
    public static boolean isRefresh=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_lunch, container, false);

        mContext=getContext();
        find_View_IDs(view);
        events();

        return  view;
    }

    private void find_View_IDs(View view) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        fab_add_lunch_dc=view.findViewById(R.id.fab_add_lunch_dc);
        recycler_view_lunch_list=view.findViewById(R.id.recycler_view_lunch_list);
        text_view_empty=view.findViewById(R.id.text_view_empty);
        showProgressDialog=new ShowProgressDialog(mContext);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        recycler_view_lunch_list.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            launchDcList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    private void events() {
        fab_add_lunch_dc.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddChapterDialog();
            }
        });
    }

    private void openAddChapterDialog() {
        dialogAddChapter = new Dialog(mContext);
        dialogAddChapter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddChapter.setContentView(R.layout.dialog_launch_dc_add);
        edit_chapter = dialogAddChapter.findViewById(R.id.edit_chapter);
        ImageView iv_close_dialog = dialogAddChapter.findViewById(R.id.iv_close_dialog);
        Button btn_add = dialogAddChapter.findViewById(R.id.btn_add);
        TextView text_title = dialogAddChapter.findViewById(R.id.text_title);
        text_title.setText("Launch DC ");
        dialogAddChapter.setCancelable(false);
        dialogAddChapter.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

        dialogAddChapter.show();

        Window window = dialogAddChapter.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);

        iv_close_dialog.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialogAddChapter.dismiss();
            }
        });
        btn_add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String launch_dc=edit_chapter.getText().toString().trim().toUpperCase();
                if(launch_dc.equalsIgnoreCase(""))
                {
                    Toast.makeText(mContext, "Please Enter launch DC Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        addLaunchDC(launch_dc);
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });
    }

    private void addLaunchDC(String launch_dc) {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.addLaunchDp(launch_dc);
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
                                Toast.makeText(mContext, "Launch Dc Add Successfully.", Toast.LENGTH_SHORT).show();
                                dialogAddChapter.dismiss();
                                launchDcList();
                            } else if(code.equalsIgnoreCase("2")) {
                                Toast.makeText(mContext, "Launch Already Exits..", Toast.LENGTH_SHORT).show();
                                edit_chapter.setText("");
                            }else if(code.equalsIgnoreCase("3")) {
                                Log.d("PARAMETER_MISSING","Parameter missing..");
                                edit_chapter.setText("");
                            }
                            else
                            {
                                Toast.makeText(mContext, "Launch DC Not Add", Toast.LENGTH_SHORT).show();
                                edit_chapter.setText("");
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
                                    lunchDcAdapter=new LunchDcAdapter(mContext,lunchDcModelArrayList);
                                    recycler_view_lunch_list.setAdapter(lunchDcAdapter);
                                    text_view_empty.setVisibility(View.GONE);
                                    lunchDcAdapter.notifyDataSetChanged();

                                }
                                else
                                {
                                    text_view_empty.setVisibility(View.VISIBLE);
                                    lunchDcAdapter.notifyDataSetChanged();
                                }
                            } else {
                                text_view_empty.setVisibility(View.VISIBLE);
                                lunchDcAdapter.notifyDataSetChanged();
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
            launchDcList();
        }
    }
}
