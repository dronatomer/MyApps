package com.ramayan.mycom.Home;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.ads.Ad;
import com.facebook.ads.AdError;
import com.facebook.ads.AdSize;
import com.facebook.ads.AudienceNetworkAds;
import com.facebook.ads.InterstitialAdListener;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.reward.RewardItem;
import com.google.android.gms.ads.reward.RewardedVideoAd;
import com.google.android.gms.ads.reward.RewardedVideoAdListener;
import com.ramayan.mycom.Adaptors.HomeAdapter;
import com.ramayan.mycom.Adaptors.HorizontalMainAdapter;
import com.ramayan.mycom.Adaptors.MainSliderAdapter;
import com.ramayan.mycom.Controller.YTApplication;
import com.ramayan.mycom.Model.MediaMetaData;
import com.ramayan.mycom.Model.ServerData;
import com.ramayan.mycom.Model.Videos;
import com.ramayan.mycom.Model.VideosModel;
import com.ramayan.mycom.R;
import com.ramayan.mycom.Utils.ApiClient;
import com.ramayan.mycom.Utils.ApiInterface;
import com.ramayan.mycom.Utils.AppConstants;
import com.ramayan.mycom.Utils.CustomProgressDialog;
import com.ramayan.mycom.Utils.GlideImageLoadingService;
import com.ramayan.mycom.Utils.MovableFloatingActionButton;
import com.ramayan.mycom.Utils.SharedPreferenceHelper;
import com.ramayan.mycom.Utils.UtilityClass;
import com.ramayan.mycom.interfaces.OnItemSelected;
import com.ramayan.mycom.player.player.PlaybackStatus;
import com.ramayan.mycom.player.player.RadioManager;
import com.ramayan.mycom.player.player.RadioService;
import com.ramayan.mycom.player.sampleapp.SampleActivity;
import com.google.android.material.navigation.NavigationView;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import ss.com.bannerslider.Slider;
import ss.com.bannerslider.event.OnSlideClickListener;

public class MainActivity extends AppCompatActivity implements OnItemSelected, NavigationView.OnNavigationItemSelectedListener, View.OnClickListener, com.dailymotion.android.interfaces.OnRadioSelected , RewardedVideoAdListener {

    private ApiInterface apiInterface;
    private ApiClient apiClient;
    private RecyclerView recycler_view, rvhorizontal;
    private HomeAdapter adapter;
    private String listIds;
    private List<Videos> videosList;
    private Slider slider;
    private ServerData bannerSliderList;
    private UtilityClass utilityClass;
    private CustomProgressDialog customProgressDialog;
    private RelativeLayout rlMoreItems, rl_notification, rl_share, rl_favorite;

//    private AdView adView;
    private ConstraintLayout middle_layout_id;
    private boolean reverse;
    private int pos = 0;
    private TextView tvMore, tvMoreRadio;
    private RadioManager radioManager;
    private com.facebook.ads.InterstitialAd fb_interstitialAd;
    private String stream_url = "";
    private MovableFloatingActionButton playPauseBtn;
    private ArrayList<MediaMetaData> radioDataList;
    private Intent serviceIntent;
    private LinearLayout ll_layout;
    private LinearLayout adContainer;
    private RewardedVideoAd mRewardedVideoAd;
    private InterstitialAd mInterstitialAd;
    private AdView adView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        overridePendingTransition(R.anim.right_in_transition, R.anim.left_out_transition);
        init();
        AppConstants.adsCounter++;
        getDataApi();
    }

    private void init() {
        fb_interstitialAd = new com.facebook.ads.InterstitialAd(this, getString(R.string.fb_interstitial));
        try {
            MobileAds.initialize(this, getString(R.string.admob_init));

            mInterstitialAd = new InterstitialAd(this);
            mInterstitialAd.setAdUnitId(getString(R.string.admob_interstitial));
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
            adView = findViewById(R.id.adView);
            mRewardedVideoAd = MobileAds.getRewardedVideoAdInstance(this);
            mRewardedVideoAd.setRewardedVideoAdListener(this);
            loadRewardedVideoAd();

            AudienceNetworkAds.initialize(this);
//            adView = new AdView(this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_90);
//            adView = new AdView(this, getString(R.string.fb_banner), AdSize.BANNER_HEIGHT_90);
//            adView = new AdView(this, "IMG_16_9_APP_INSTALL#YOUR_PLACEMENT_ID", AdSize.BANNER_HEIGHT_90);

            // Find the Ad Container
            loadBannerAds();
            shofbAds();

        } catch (Exception e) {
            e.getMessage();
        }

        //read ids
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        NavigationView navigationView = findViewById(R.id.nav_view);
        recycler_view = findViewById(R.id.recycler_view);
        rvhorizontal = findViewById(R.id.rvhorizontal);
        rlMoreItems = findViewById(R.id.rl_more_items);
        slider = findViewById(R.id.banner_slider1);
        rl_notification = findViewById(R.id.rl_notification);
        rl_share = findViewById(R.id.rl_share);
        rl_favorite = findViewById(R.id.rl_favorite);
        tvMore = findViewById(R.id.tv_more);
        middle_layout_id = findViewById(R.id.middle_layout_id);
        playPauseBtn = findViewById(R.id.play_pause_btn);
        tvMoreRadio = findViewById(R.id.tv_more_radio);
        ll_layout = findViewById(R.id.ll_layout);
        ll_layout.setVisibility(View.GONE);
        middle_layout_id.setVisibility(View.GONE);
        slider.setVisibility(View.GONE);
        rlMoreItems.setVisibility(View.GONE);

        //RadioPlayerService
        serviceIntent = new Intent(this, RadioService.class);

        if (!isMyServiceRunning(RadioService.class))
            startService(serviceIntent);

        radioManager = YTApplication.getRadioManager();

        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();
        utilityClass = new UtilityClass(this);
        customProgressDialog = new CustomProgressDialog(this, R.drawable.circularprogressbar);
        navigationView.setNavigationItemSelectedListener(this);
        bannerSliderList = (ServerData) getIntent().getSerializableExtra(AppConstants.ListID);
        listIds = bannerSliderList.getPlist_id();
        reverse = bannerSliderList.isReverse();
        radioDataList = bannerSliderList.getBhakti_json();
        Slider.init(new GlideImageLoadingService(this));
        if (utilityClass.checkInternetConnection())
            setSlider();
        else
            Toast.makeText(this, "No internet connection", Toast.LENGTH_SHORT).show();
        GridLayoutManager gridLayoutManager = new GridLayoutManager(this, 2);
        recycler_view.setLayoutManager(gridLayoutManager);
        rvhorizontal.setLayoutManager(new LinearLayoutManager(MainActivity.this, LinearLayoutManager.HORIZONTAL, false));

        rl_notification.setOnClickListener(this);
        rl_share.setOnClickListener(this);
        rl_favorite.setOnClickListener(this);
        tvMore.setOnClickListener(this);
        tvMoreRadio.setOnClickListener(this);
        //setRadioRecycler
        HorizontalMainAdapter horizotalMainAdapter = new HorizontalMainAdapter(MainActivity.this, radioDataList);
        rvhorizontal.setAdapter(horizotalMainAdapter);


        playPauseBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (radioManager != null) {
                    radioManager.playOrPause(stream_url);
                }
            }
        });

        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
            }

            @Override
            public void onAdFailedToLoad(int errorCode) {
                // Code to be executed when an ad request fails.
            }

            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
//                if (flagAds)
                    finish();
                mInterstitialAd.loadAd(new AdRequest.Builder().build());
            }
        });

    }

    @Override
    public void onStart() {
        super.onStart();
        EventBus.getDefault().register(this);
    }

    @Override
    public void onStop() {
        EventBus.getDefault().unregister(this);
        super.onStop();
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    @Subscribe
    public void onEvent(String status) {

        switch (status) {

            case PlaybackStatus.LOADING:
                break;

            case PlaybackStatus.ERROR:

                utilityClass.RadioNotPlaying(this);
                //Toast.makeText(this, R.string.no_stream, Toast.LENGTH_SHORT).show();

                break;

        }
        try {
            playPauseBtn.setImageResource(status.equals(PlaybackStatus.PLAYING)
                    ? R.drawable.icon_pause
                    : R.drawable.icon_play);
            //   this.status = status;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setSlider() {
        slider.setAdapter(new MainSliderAdapter(bannerSliderList.getThumnailList()));
        //        slider.setSelectedSlide(currentPage++, true);
        slider.setOnSlideClickListener(new OnSlideClickListener() {
            @Override
            public void onSlideClick(int position) {
//                pos = position;
//                if (utilityClass.checkInternetConnection()) {
//                    if(radioManager!=null)
//                        radioManager.pause();
//                    AppConstants.adsCounter++;
//                    SharedPreferenceHelper.setSharedPreferenceInt(HomeActivity.this, AppConstants.adsFlag, AppConstants.adsCounter);
//                    Intent intent = new Intent(HomeActivity.this, PlayerActivity.class);
//                    intent.putExtra(AppConstants.PLAY_LIST_IDS, bannerSliderList.get(position).getId());
//                    intent.putExtra("banner", "banner");
//                    startActivity(intent);
//                    }
//                } else {
//                    utilityClass.getAlertDialogWithSingleButtons(HomeActivity.this, getString(R.string.no_internet));
//                }
            }
        });
    }


    private void getDataApi() {
        if (listIds != null) {
            customProgressDialog.show();
            apiInterface = apiClient.getClient().create(ApiInterface.class);
            apiInterface.getvideosList(listIds).subscribeOn(Schedulers.io()).observeOn(AndroidSchedulers.mainThread()).subscribe(new Observer<VideosModel>() {
                @Override
                public void onSubscribe(Disposable d) {

                }

                @Override
                public void onNext(VideosModel videosModel) {
                    if (videosModel != null) {
                        // if (videosModel.getStatusCode() == AppConstant.STATUS) {
                        List<Videos> data = videosModel.getList();
                        if (reverse)
                            Collections.reverse(data);
                        if (data != null) {
                            setView(data);
                        }
//                    }
                    }
                }

                @Override
                public void onError(Throwable e) {
                    Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }

                @Override
                public void onComplete() {
                    customProgressDialog.dismiss();
                }
            });
        } else {
            Toast.makeText(this, "Something went wrong.", Toast.LENGTH_SHORT).show();
        }

    }

    private void setView(List<Videos> data) {
        middle_layout_id.setVisibility(View.VISIBLE);
        rlMoreItems.setVisibility(View.VISIBLE);
        slider.setVisibility(View.VISIBLE);
        ll_layout.setVisibility(View.VISIBLE);
        videosList = data;
        adapter = new HomeAdapter(this, data, this);
        recycler_view.setAdapter(adapter);
        adapter.setOnClickListner(this);
        rvhorizontal.setVisibility(View.VISIBLE);
    }

    @Override
    public void onVideoSelected(Videos video, int position) {
        radioManager.pause();
        if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.adsFlag, 0) > 3) {
            AppConstants.adsCounter = 0;
            SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
            AppConstants.clickFlag = true;
            pos = position;
            showAds();
        } else {
            AppConstants.adsCounter++;
            SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
            loadPlayer(position);
        }
    }

    public void loadPlayer(int position) {
        if (utilityClass.checkInternetConnection()) {
            Intent intent = new Intent(MainActivity.this, SampleActivity.class);
            intent.putExtra(AppConstants.VidId, videosList.get(pgit add osition).getId());
            intent.putExtra(AppConstants.ListID, listIds);
            intent.putExtra(AppConstants.VIDEO_LIST, (Serializable) videosList);
            intent.putExtra(AppConstants.POSITION, position);
            startActivity(intent);
        } else {
            Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem menuItem) {
        // Handle navigation view item clicks here.

        int id = menuItem.getItemId();

        if (id == R.id.nav_home) {

        } else if (id == R.id.nav_more) {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://search?q=pub:Fm+App+World")));
            } catch (ActivityNotFoundException anfe) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Fm+App+World")));
            }
        } else if (id == R.id.nav_rate) {
            rateApp();
        } else if (id == R.id.nav_declaimer) {
            showDialog();
        } else if (id == R.id.nav_share) {
            shareApp();
        } else if (id == R.id.nav_exit) {
            showAds();
            finish();
        }

        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void showAds() {

        if (mRewardedVideoAd != null && mRewardedVideoAd.isLoaded()) {
//            if(radioManager!=null)
//                radioManager.pause();
            mRewardedVideoAd.show();
        } else {
            loadRewardedVideoAd();
            if (mInterstitialAd != null && mInterstitialAd.isLoaded()) {
//                if(radioManager!=null)
//                    radioManager.pause();
                mInterstitialAd.show();
            } else {
               // shofbAds();
                loadInterstitialAd();
            }
        }
    }

    private void shofbAds() {
        // Set listeners for the Interstitial Ad
        fb_interstitialAd.setAdListener(new InterstitialAdListener() {
            @Override
            public void onInterstitialDisplayed(Ad ad) {
                // Interstitial ad displayed callback
                Log.e("", "Interstitial ad displayed.");
            }

            @Override
            public void onInterstitialDismissed(Ad ad) {
                // Interstitial dismissed callback
                Log.e("", "Interstitial ad dismissed.");
            }

            @Override
            public void onError(Ad ad, AdError adError) {
                // Ad error callback
                Log.e("", "Interstitial ad failed to load: " + adError.getErrorMessage());
            }

            @Override
            public void onAdLoaded(Ad ad) {
                // Interstitial ad is loaded and ready to be displayed
                Log.d("", "Interstitial ad is loaded and ready to be displayed!");
                // Show the ad
                fb_interstitialAd.show();
                ///showAdWithDelay();
            }

            @Override
            public void onAdClicked(Ad ad) {
                // Ad clicked callback
                Log.d("", "Interstitial ad clicked!");
            }

            @Override
            public void onLoggingImpression(Ad ad) {
                // Ad impression logged callback
                Log.d("", "Interstitial ad impression logged!");
            }
        });

//         For auto play video ads, it's recommended to load the ad
//         at least 30 seconds before it is shown
        fb_interstitialAd.loadAd();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.rl_notification:
                if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.adsFlag, 0) > 2) {
                    AppConstants.adsCounter = 0;
                    SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
                    showAds();
                } else {
                    AppConstants.adsCounter++;
                    SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
                }
                Intent intent = new Intent(this, NotificationActivity.class);
                startActivity(intent);
                break;
            case R.id.rl_share:
                shareApp();
                break;
            case R.id.rl_favorite:
                if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.adsFlag, 0) > 3) {
                    AppConstants.adsCounter = 0;
                    SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
                    showAds();
                } else {
                    AppConstants.adsCounter++;
                    SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
                }
                Intent intent1 = new Intent(this, FavoriteActivity.class);
                startActivity(intent1);
                break;
            case R.id.tv_more:
                if (utilityClass.checkInternetConnection())
                    showAds();
                else
                    Toast.makeText(this, getString(R.string.no_internet), Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_more_radio:
                try {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=com.hindiradio.fm&hl=en")));
                } catch (ActivityNotFoundException anfe) {
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://play.google.com/store/apps/developer?id=Fm+App+World")));
                }
                break;
        }
    }

    @Override
    protected void onDestroy() {
//        if (fb_interstitialAd != null) {
//            fb_interstitialAd.destroy();
//        }
        if (adView != null) {
            adView.destroy();
        }
        super.onDestroy();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        AppConstants.clickFlag = false;
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
//            if (fb_interstitialAd == null && fb_interstitialAd.isAdLoaded()) {
//                fb_interstitialAd.show();
//            } else {
           // showAds();
//            }
            super.onBackPressed();
        }
        this.overridePendingTransition(0, R.anim.right_to_left_animation);
    }

    @Override
    protected void onResume() {
        super.onResume();
        //  if(AppConstants.player_back){
        AppConstants.player_back = false;
        AppConstants.clickFlag = false;
        if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.adsFlag, 0) > 3) {
            AppConstants.adsCounter = 0;
            SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
            showAds();
        } else {
            AppConstants.adsCounter++;
            SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
        }
        try {
            if (radioManager.isServiceBound() && !radioManager.equals(""))
                EventBus.getDefault().post(radioManager.getStatus());
            if (!radioManager.isServiceBound()) {
                radioManager.bind();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        //  }
        loadBannerAds();
    }

    public void shareApp() {
        try {
            Intent i = new Intent(Intent.ACTION_SEND);
            i.setType("text/plain");
            i.putExtra(Intent.EXTRA_SUBJECT, getString(R.string.app_name));
            String sAux = "\nLet me recommend you this application\n\n";
            sAux = sAux + "https://play.google.com/store/apps/details?id=" + getPackageName() + " \n\n";
            i.putExtra(Intent.EXTRA_TEXT, sAux);
            startActivity(Intent.createChooser(i, "choose one"));
        } catch (Exception e) {
            //e.toString();
        }
    }

    public void rateApp() {
        Uri uri = Uri.parse("market://details?id=" + getPackageName());
        Intent goToMarket = new Intent(Intent.ACTION_VIEW, uri);
        // To count with Play market backstack, After pressing back button,
        // to taken back to our application, we need to add following flags to intent.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            goToMarket.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY |
                    Intent.FLAG_ACTIVITY_NEW_DOCUMENT |
                    Intent.FLAG_ACTIVITY_MULTIPLE_TASK);
        }
        try {
            startActivity(goToMarket);
        } catch (ActivityNotFoundException e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("http://play.google.com/store/apps/details?id=" + getPackageName())));
        }
    }

    public void showDialog() {
        final Dialog dialog = new Dialog(MainActivity.this);
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setCancelable(false);
        dialog.setContentView(R.layout.fragment_dialog);
        TextView foo = dialog.findViewById(R.id.privacy_policy);
        Button ok = dialog.findViewById(R.id.ok);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dialog.dismiss();
            }
        });
        foo.setText(Html.fromHtml(getString(R.string.privacy_policy)));
        dialog.show();
    }

    private void loadBannerAds() {
        AdRequest adRequest = new AdRequest.Builder().build();
        adView.loadAd(adRequest);
    }

    private void loadRewardedVideoAd() {
        mRewardedVideoAd.loadAd(getString(R.string.admob_reward),
                new AdRequest.Builder().build());
    }

    private void loadInterstitialAd() {
        if (mInterstitialAd != null)
            mInterstitialAd.loadAd(new AdRequest.Builder().build());
    }



    @Override
    public void onRadioSelected(View v, MediaMetaData metaData, int position) {
        if (utilityClass.checkInternetConnection()) {
            if (SharedPreferenceHelper.getSharedPreferenceInt(this, AppConstants.adsFlag, 0) > 2) {
                AppConstants.adsCounter = 0;
                SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
                showAds();
            } else {
                radioManager.sendMediaList(metaData);
                radioManager.playOrPause(metaData.getMediaUrl());
                stream_url = metaData.getMediaUrl();
                playPauseBtn.show();
                AppConstants.adsCounter++;
                SharedPreferenceHelper.setSharedPreferenceInt(this, AppConstants.adsFlag, AppConstants.adsCounter);
            }

        } else {
            utilityClass.checkInternetConnection();
        }
    }

    @Override
    public void onRewardedVideoAdLoaded() {

    }

    @Override
    public void onRewardedVideoAdOpened() {

    }

    @Override
    public void onRewardedVideoStarted() {

    }

    @Override
    public void onRewardedVideoAdClosed() {

    }

    @Override
    public void onRewarded(RewardItem rewardItem) {

    }

    @Override
    public void onRewardedVideoAdLeftApplication() {

    }

    @Override
    public void onRewardedVideoAdFailedToLoad(int i) {

    }

    @Override
    public void onRewardedVideoCompleted() {

    }
}
