package com.mezyapps.bni_visitor.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.gson.Gson;
import com.mezyapps.bni_visitor.R;
import com.mezyapps.bni_visitor.api_common.ApiClient;
import com.mezyapps.bni_visitor.api_common.ApiInterface;
import com.mezyapps.bni_visitor.model.AdminLoginModel;
import com.mezyapps.bni_visitor.model.SuccessModel;
import com.mezyapps.bni_visitor.utils.NetworkUtils;
import com.mezyapps.bni_visitor.utils.SharedLoginUtils;
import com.mezyapps.bni_visitor.utils.ShowProgressDialog;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class LoginActivity extends AppCompatActivity {

    private TextInputEditText edit_username,edit_password;
    private Button btn_login;
    private ShowProgressDialog showProgressDialog;
    public static ApiInterface apiInterface;
    private String username,password;
    private ArrayList<AdminLoginModel>  adminLoginModelArrayList=new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        find_View_IDs();
        events();
    }

    private void find_View_IDs() {
        apiInterface = ApiClient.getClient().create(ApiInterface.class);
        edit_username=findViewById(R.id.edit_username);
        edit_password=findViewById(R.id.edit_password);
        btn_login=findViewById(R.id.btn_login);
        showProgressDialog=new ShowProgressDialog(LoginActivity.this);
    }

    private void events() {
        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(validation())
                {
                    if (NetworkUtils.isNetworkAvailable(LoginActivity.this)) {
                        callLogin();
                    }
                    else {
                        NetworkUtils.isNetworkNotAvailable(LoginActivity.this);
                    }
                }
            }
        });
    }

    private void callLogin() {
        showProgressDialog.showDialog();
        Call<SuccessModel> call = apiInterface.login(username,password);
        call.enqueue(new Callback<SuccessModel>() {
            @Override
            public void onResponse(Call<SuccessModel> call, Response<SuccessModel> response) {
                showProgressDialog.dismissDialog();
                String str_response = new Gson().toJson(response.body());
                Log.d("Response >>", str_response);

                try {
                    if (response.isSuccessful()) {
                        SuccessModel successModule = response.body();
                        adminLoginModelArrayList.clear();
                        String message = null, code = null;
                        if (successModule != null) {
                            message = successModule.getMessage();
                            code = successModule.getCode();
                            if (code.equalsIgnoreCase("1")) {

                                adminLoginModelArrayList=successModule.getAdminLoginModelArrayList();

                                if(adminLoginModelArrayList.size()!=0) {
                                    SharedLoginUtils.putLoginSharedUtils(LoginActivity.this);
                                    String user_id=adminLoginModelArrayList.get(0).getAdmin_id();
                                    SharedLoginUtils.addUserId(LoginActivity.this,user_id);
                                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                    startActivity(intent);
                                    finish();
                                }
                            } else {
                                Toast.makeText(LoginActivity.this, "User Not Registered", Toast.LENGTH_SHORT).show();
                            }


                        } else {
                            Toast.makeText(LoginActivity.this, "Response Null", Toast.LENGTH_SHORT).show();
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

    private boolean validation() {
        username=edit_username.getText().toString().trim();
        password=edit_password.getText().toString().trim();

        if (username.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Username", Toast.LENGTH_SHORT).show();
            return false;
        }
        if (password.equalsIgnoreCase("")) {
            Toast.makeText(this, "Enter Password", Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }
}
