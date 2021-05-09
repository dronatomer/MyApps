package com.ramayan.mycom.Utils;

import android.annotation.SuppressLint;



import java.io.IOException;
import java.util.concurrent.TimeUnit;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiClient {
    //live URL

    private static Retrofit retrofit = null;

    public static Retrofit getClient() {

        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);

        OkHttpClient okHttpClient = new OkHttpClient().newBuilder().readTimeout(150, TimeUnit.SECONDS).
                connectTimeout(150, TimeUnit.SECONDS).addInterceptor(new Interceptor() {
            @SuppressLint("LongLogTag")
            @Override
            public okhttp3.Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();
                Request request = originalRequest.newBuilder()
                        .method(originalRequest.method(), originalRequest.body())
                        .build();
                return chain.proceed(request);
            }
        }).addInterceptor(interceptor).build();

        if (retrofit == null) {
            retrofit = new Retrofit.Builder()
                    .baseUrl(AppConstants.MY_OWNER)
                    .addConverterFactory(GsonConverterFactory.create()).client(okHttpClient)
                    .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                    .build();
        }
        return retrofit;
    }
}
