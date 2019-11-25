package com.mezyapps.bni_visitor.fragment;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
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
import com.mezyapps.bni_visitor.activity.LoginActivity;
import com.mezyapps.bni_visitor.activity.MainActivity;
import com.mezyapps.bni_visitor.adapter.ChapterListAdapter;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.interfaceUtils.ChapterSelectInterface;
import com.mezyapps.bni_visitor.model.ChapterListModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.SharedLoginUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;
import java.util.Collections;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


public class ChapterListFragment extends Fragment  implements ChapterSelectInterface {
     private Context mContext;
     private FloatingActionButton fab_add_chapter;
     private Dialog dialogAddChapter;
     public static ApiInterface apiInterface;
     private ShowProgressDialog showProgressDialog;
     private  TextInputEditText edit_chapter;
     private RecyclerView recycler_view_chapter_list;
     private ArrayList<ChapterListModel> chapterListModelArrayList=new ArrayList<>();
     private ChapterListAdapter chapterListAdapter;
     private TextView text_view_empty;
     public static Boolean isRefresh=false;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view=inflater.inflate(R.layout.fragment_chapter_list, container, false);
        mContext=getActivity();

        find_View_IDs(view);
        events();
        return view;
    }

    private void find_View_IDs(View view) {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        fab_add_chapter=view.findViewById(R.id.fab_add_chapter);
        recycler_view_chapter_list=view.findViewById(R.id.recycler_view_chapter_list);
        text_view_empty=view.findViewById(R.id.text_view_empty);
        showProgressDialog=new ShowProgressDialog(mContext);


        LinearLayoutManager linearLayoutManager=new LinearLayoutManager(mContext);
        recycler_view_chapter_list.setLayoutManager(linearLayoutManager);

        if (NetworkUtils.isNetworkAvailable(mContext)) {
            chapterList();
        }
        else {
            NetworkUtils.isNetworkNotAvailable(mContext);
        }
    }

    private void events() {
        fab_add_chapter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openAddChapterDialog();
            }
        });
    }

    private void openAddChapterDialog() {
        dialogAddChapter = new Dialog(mContext);
        dialogAddChapter.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialogAddChapter.setContentView(R.layout.dialog_add);
        edit_chapter = dialogAddChapter.findViewById(R.id.edit_chapter);
        ImageView iv_close_dialog = dialogAddChapter.findViewById(R.id.iv_close_dialog);
        Button btn_add = dialogAddChapter.findViewById(R.id.btn_add);
        TextView text_title = dialogAddChapter.findViewById(R.id.text_title);
        text_title.setText("Chapter");
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
                String chapter_name=edit_chapter.getText().toString().trim().toUpperCase();
                if(chapter_name.equalsIgnoreCase(""))
                {
                    Toast.makeText(mContext, "Please Enter Chapter Name", Toast.LENGTH_SHORT).show();
                }
                else
                {
                    if (NetworkUtils.isNetworkAvailable(mContext)) {
                        addChapter(chapter_name);
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(mContext);
                    }
                }
            }
        });
    }

    private void addChapter(String chapter_name) {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.addChapterName(chapter_name);
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
                                Toast.makeText(mContext, "Chapter Add Successfully.", Toast.LENGTH_SHORT).show();
                                dialogAddChapter.dismiss();
                                chapterList();
                            } else if(code.equalsIgnoreCase("2")) {
                                Toast.makeText(mContext, "Chapter Already Exits..", Toast.LENGTH_SHORT).show();
                                edit_chapter.setText("");
                            }else if(code.equalsIgnoreCase("3")) {
                                Log.d("PARAMETER_MISSING","Parameter missing..");
                                edit_chapter.setText("");
                            }
                            else
                            {
                                Toast.makeText(mContext, "Chapter Not Add", Toast.LENGTH_SHORT).show();
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
                                   Collections.reverse(chapterListModelArrayList);
                                    chapterListAdapter=new ChapterListAdapter(mContext,chapterListModelArrayList);
                                    recycler_view_chapter_list.setAdapter(chapterListAdapter);
                                    chapterListAdapter.notifyDataSetChanged();

                                }
                                else
                                {
                                   // text_view_empty.setVisibility(View.VISIBLE);
                                    chapterListAdapter.notifyDataSetChanged();
                                }
                            } else {
                               // text_view_empty.setVisibility(View.VISIBLE);
                                chapterListAdapter.notifyDataSetChanged();
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
    public void selectChapter(ChapterListModel chapterListModel) {
        String chapter=chapterListModel.getChapter_name();
        Toast.makeText(mContext, chapter, Toast.LENGTH_SHORT).show();

    }

    @Override
    public void onResume() {
        super.onResume();
        if (isRefresh)
        {
            isRefresh=false;
            chapterList();
        }
    }
}
