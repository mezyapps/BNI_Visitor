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
import com.mezyapps.bni_visitor.adapter.ChapterListAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorByChapterAdapter;
import com.mezyapps.bni_visitor.adapter.VisitorBySourceAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.HomeFragment;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class VisitorByChapterActivity extends AppCompatActivity {

    private ImageView iv_back;
    private RecyclerView recycler_view_chapter;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private ArrayList<ChapterListModel> chapterListModelArrayList=new ArrayList<>();
    private VisitorByChapterAdapter visitorByChapterAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visitor_by_chapter);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        showProgressDialog=new ShowProgressDialog(VisitorByChapterActivity.this);
        iv_back=findViewById(R.id.iv_back);
        recycler_view_chapter=findViewById(R.id.recycler_view_chapter);

        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(VisitorByChapterActivity.this);
        recycler_view_chapter.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(VisitorByChapterActivity.this)) {
            chapterList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(VisitorByChapterActivity.this);
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
    private void chapterList() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.chapterList();
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        chapterListModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                chapterListModelArrayList=successModule.getChapterListModelArrayList();
                                if(chapterListModelArrayList.size()!=0) {
                                    visitorByChapterAdapter=new VisitorByChapterAdapter(VisitorByChapterActivity.this,chapterListModelArrayList);
                                    recycler_view_chapter.setAdapter(visitorByChapterAdapter);
                                    visitorByChapterAdapter.notifyDataSetChanged();
                                }
                                else
                                {
                                    //text_view_empty.setVisibility(View.VISIBLE);
                                    visitorByChapterAdapter.notifyDataSetChanged();
                                }
                            } else {
                                //text_view_empty.setVisibility(View.VISIBLE);
                                visitorByChapterAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(VisitorByChapterActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
        chapterList();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        HomeFragment.isRefresh=true;
    }
}
