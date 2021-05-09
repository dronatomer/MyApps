package com.ramayan.mycom.Home;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.ramayan.mycom.R;


public class NotificationActivity extends AppCompatActivity {

    ImageView iv_back;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_notification);
        overridePendingTransition(R.anim.right_in_transition, R.anim.left_out_transition);
        init();
    }

    private void init() {
        iv_back = findViewById(R.id.iv_back);
        iv_back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
                overridePendingTransition(0, R.anim.right_to_left_animation);
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(0, R.anim.right_to_left_animation);
    }
}
