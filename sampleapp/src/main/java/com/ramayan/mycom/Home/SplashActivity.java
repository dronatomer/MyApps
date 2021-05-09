package com.ramayan.mycom.Home;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.ramayan.mycom.Model.ServerData;
import com.ramayan.mycom.R;
import com.ramayan.mycom.Utils.AppConstants;


import java.io.Serializable;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;


public class SplashActivity extends Activity {
    public static final String TAG = SplashActivity.class.getSimpleName();
    private Dialog progressBar;
    private ImageView iv_background;
    private ServerData serverData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN);
        ServerData serverData = null;
        openFilePicker();
    }



    private void openFileThread(final ServerData serverData) {
        Thread thread = new Thread() {
            @Override
            public void run() {
                super.run();
                Intent intent = null;
                intent = new Intent(SplashActivity.this, MainActivity.class);
                intent.putExtra(AppConstants.ListID, (Serializable) serverData);
                intent.setFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
                try {
                    Thread.sleep(1 * 1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    startActivity(intent);
                    //overridePendingTransition(R.anim.zoom_in, R.anim.zoom_exit);
                    SplashActivity.this.finish();
                }

            }
        };
        thread.start();
        //  setAnimation();
    }




    private void openFilePicker() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().getDecorView().setSystemUiVisibility(
                    View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                            | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);

        }

        setContentView(R.layout.activity_splash);
        iv_background = findViewById(R.id.iv_background);
        RequestOptions requestOption = new RequestOptions().placeholder(R.drawable.box).error(R.drawable.box).diskCacheStrategy(DiskCacheStrategy.ALL);
        Glide.with(this).load(R.drawable.splash).apply(requestOption).into(iv_background);
        getKeysApi();
    }

    private void getKeysApi() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(AppConstants.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .client(new OkHttpClient())
                .build();
        HomePage homePage = retrofit.create(HomePage.class);
        Call<ServerData> call = homePage.onDataReceived();
        call.enqueue(new Callback<ServerData>() {
            @Override
            public void onResponse(Call<ServerData> call, Response<ServerData> response) {
                 ServerData serverData = response.body();
                openFileThread(serverData);
            }

            @Override
            public void onFailure(Call<ServerData> call, Throwable t) {
                Toast.makeText(SplashActivity.this, t.getMessage() + "" + t.getCause(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void setAnimation() {
        Animation fadeOut = new AlphaAnimation(0, 2);
        fadeOut.setDuration(1000);
        AnimationSet animation = new AnimationSet(true);
        //animation.addAnimation();
        animation.addAnimation(fadeOut);
        // findViewById(R.id.iv_background).setAnimation(animation);
    }

    //    @Override
//    public void dialogLeftButtonPressed() {
//
//    }
//
//    @Override
//    public void dialogRightButtonPressed() {
//
//    }
//
//    @Override
//    public void dialogOKButtonPressed() {
//        finish();
//    }
//
//    @Override
//    public void dialogOKPressed() {
//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            requestPermissions(new String[]{Manifest.permission.READ_PHONE_STATE}, REQUEST_PHONECALL_PERMISSION);
//        }
//    }
    interface HomePage {
        @GET(AppConstants.SHOUTCAST)
        Call<ServerData> onDataReceived();
    }

}