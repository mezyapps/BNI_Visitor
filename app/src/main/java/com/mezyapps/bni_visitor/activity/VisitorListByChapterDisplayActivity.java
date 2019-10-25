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
import com.mezyapps.bni_visitor.adapter.VisitorByChapterAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorByChapterListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorBySourceAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.model.VisitorByChapterModel;
import com.mezyapps.bni_visitor.model.VisitorBySourceModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorListByChapterDisplayActivity extends AppCompatActivity {

    private ImageView iv_back;
    private String chapter,status;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<VisitorByChapterModel> visitorByChapterModelArrayList = new ArrayList<>();
    private VisitorByChapterListAdapter visitorByChapterListAdapter;
    private RecyclerView recycler_view_visitor_chapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_list_by_chapter_display);
        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog = new ShowProgressDialog(VisitorListByChapterDisplayActivity.this);
        iv_back = findViewById(R.id.iv_back);
        recycler_view_visitor_chapter = findViewById(R.id.recycler_view_visitor_chapter);

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(VisitorListByChapterDisplayActivity.this);
        recycler_view_visitor_chapter.setLayoutManager(linearLayoutManager);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chapter = bundle.getString("chapter");
            status = bundle.getString("status");
        }

        if (NetworkUtils.isNetworkAvailable(VisitorListByChapterDisplayActivity.this)) {
            callVisitorListChapter();
        } else {
            NetworkUtils.isNetworkNotAvailable(VisitorListByChapterDisplayActivity.this);
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
        Call<SuccessModel> call = apiInterface.visitorListChapter(chapter,status);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        visitorByChapterModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                visitorByChapterModelArrayList = successModule.getVisitorByChapterModelArrayList();
                                Collections.reverse(visitorByChapterModelArrayList);
                                if (visitorByChapterModelArrayList.size() != 0) {
                                    visitorByChapterListAdapter=new VisitorByChapterListAdapter(VisitorListByChapterDisplayActivity.this, visitorByChapterModelArrayList);
                                    recycler_view_visitor_chapter.setAdapter(visitorByChapterListAdapter);
                                    visitorByChapterListAdapter.notifyDataSetChanged();
                                } else {
                                    // text_view_empty.setVisibility(View.VISIBLE);
                                    visitorByChapterListAdapter.notifyDataSetChanged();
                                }
                            } else {
                                // text_view_empty.setVisibility(View.VISIBLE);
                                visitorByChapterListAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorListByChapterDisplayActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
        callVisitorListChapter();
    }
}
