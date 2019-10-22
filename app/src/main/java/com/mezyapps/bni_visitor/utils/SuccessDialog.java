package com.mezyapps.bni_visitor.utils;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.ColorDrawable;
import android.os.Handler;
import android.view.View;
import android.view.Window;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.mezyapps.bni_visitor.R;


public class SuccessDialog {
    private Context mContext;
    private Dialog dialog;
    private TextView textSuccess,textOk;
    Handler handler;


    public SuccessDialog(Context mContext) {
        this.mContext = mContext;
        this.dialog = new Dialog(mContext);
        this.dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.dialog.setContentView(R.layout.dialog_success_message);
        this.textOk=dialog.findViewById(R.id.textOk);
        this.textSuccess =dialog.findViewById(R.id.textSuccess);
        this.dialog.setCancelable(false);
        this.dialog.getWindow().setBackgroundDrawable(new ColorDrawable(android.graphics.Color.TRANSPARENT));

    }
    public void  showDialog(final String messasge) {
        dialog.show();
        textSuccess.setText(messasge);
        Window window = dialog.getWindow();
        window.setLayout(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);


        textOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

    }


}
