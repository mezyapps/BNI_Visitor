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
import com.mezyapps.bni_visitor.fragment.LunchFragment;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class EditLaunchDcActivity extends AppCompatActivity {

    private ImageView iv_back;
    private TextInputEditText edit_launch_dc;
    private Button btn_edit;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private String launch_dc_id,launch_dc_name;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_launch_dc);
        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        iv_back=findViewById(R.id.iv_back);
        edit_launch_dc=findViewById(R.id.edit_launch_dc);
        btn_edit=findViewById(R.id.btn_edit);
        showProgressDialog=new ShowProgressDialog(EditLaunchDcActivity.this);

        Bundle bundle = getIntent().getExtras();
        if (bundle != null) {
            launch_dc_id = bundle.getString("launch_dc_id");
            launch_dc_name = bundle.getString("launch_dc_name");
            edit_launch_dc.setText(launch_dc_name);
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
                launch_dc_name=edit_launch_dc.getText().toString().trim();
                if(launch_dc_name.equalsIgnoreCase(""))
                {
                    Toast.makeText(EditLaunchDcActivity.this, "Please Enter Chapter Name", Toast.LENGTH_SHORT).show();
                }
                else
                {

                    if (NetworkUtils.isNetworkAvailable(EditLaunchDcActivity.this)) {
                        editChapter();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(EditLaunchDcActivity.this);
                    }
                }

            }
        });
    }

    private void editChapter() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.editLaunchDc(launch_dc_id,launch_dc_name);
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
                                Toast.makeText(EditLaunchDcActivity.this, "Launch DC Edit Successfully.", Toast.LENGTH_SHORT).show();
                                LunchFragment.isRefresh=true;
                                onBackPressed();
                            } else if(code.equalsIgnoreCase("2")) {
                                Toast.makeText(EditLaunchDcActivity.this, "Launch DC Already Exits..", Toast.LENGTH_SHORT).show();
                                LunchFragment.isRefresh=true;
                                onBackPressed();
                            }else if(code.equalsIgnoreCase("3")) {
                                Log.d("PARAMETER_MISSING","Parameter missing..");
                                edit_launch_dc.setText("");
                            }
                            else
                            {
                                Toast.makeText(EditLaunchDcActivity.this, "Launch DC Not Edit", Toast.LENGTH_SHORT).show();
                                edit_launch_dc.setText("");
                            }
                        } else {
                            Toast.makeText(EditLaunchDcActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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
