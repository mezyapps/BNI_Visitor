package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.fragment.ChapterListFragment;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditChapterActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextInputEditText edit_chapter;
    private Button btn_edit;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private String chapter,chapter_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_chapter);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        iv_back=findViewById(R.id.iv_back);
        edit_chapter=findViewById(R.id.edit_chapter);
        btn_edit=findViewById(R.id.btn_edit);
        showProgressDialog=new ShowProgressDialog(EditChapterActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            chapter_id = bundle.getString("chapter_id");
            chapter = bundle.getString("chapter_name");
            edit_chapter.setText(chapter);
        }

    }

    private void events() {
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                chapter=edit_chapter.getText().toString().trim();
                if(chapter.equalsIgnoreCase(""))
                {
                    Toast.makeText(EditChapterActivity.this, "Please Enter Chapter Name", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (NetworkUtils.isNetworkAvailable(EditChapterActivity.this)) {
                        editChapter();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(EditChapterActivity.this);
                    }
                }

            }
        });
    }

    private void editChapter() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.editChapter(chapter_id,chapter);
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
                                Toast.makeText(EditChapterActivity.this, "Chapter Edit Successfully.", Toast.LENGTH_SHORT).show();
                                ChapterListFragment.isRefresh=true;
                                onBackPressed();
                            } else if(code.equalsIgnoreCase("2")) {
                                Toast.makeText(EditChapterActivity.this, "Chapter Already Exits..", Toast.LENGTH_SHORT).show();
                                ChapterListFragment.isRefresh=true;
                                onBackPressed();
                            }else if(code.equalsIgnoreCase("3")) {
                                Log.d("PARAMETER_MISSING","Parameter missing..");
                                edit_chapter.setText("");
                            }
                            else
                            {
                                Toast.makeText(EditChapterActivity.this, "Chapter Not Edit", Toast.LENGTH_SHORT).show();
                                edit_chapter.setText("");
                            }
                        } else {
                            Toast.makeText(EditChapterActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
