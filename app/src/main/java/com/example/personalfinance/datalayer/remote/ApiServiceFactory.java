package com.example.personalfinance.datalayer.remote;

import android.os.Build;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;

import com.example.personalfinance.datalayer.remote.models.Error;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.Arrays;
import java.util.concurrent.TimeUnit;

import okhttp3.Credentials;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

public class ApiServiceFactory {

    public static class AuthenticateInterceptor implements Interceptor{
        private String token;

        public AuthenticateInterceptor(String token){
            this.token = Credentials.basic(username, password);
        }

        @NonNull
        @Override
        public Response intercept(@NonNull Chain chain) throws IOException {
            Request request = chain.request();
            if (token != null)
                 return chain.proceed(request.newBuilder().header("Authorization", "Bearer " + token).build());
            return chain.proceed(request);
        }
    }

    public static HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
    static{
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
    }

    private static final long TIME_INTERVAL_BETWEEN_REQUEST = 1000l;
    private static final int MAX_REQUEST_COUNT = 5;
    private static final long TIME_OUT_INTERVAL = 30;
    private static final String username = "kiev";
    private static final String password = "trung";
    private static final String domain = "http://10.0.2.2:8080/api/v1/";
    private static String token = null;

    private static final Gson gson = new GsonBuilder()
            .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
            .create();

    private static final OkHttpClient okHttpClient = new OkHttpClient.Builder()
            .addInterceptor(loggingInterceptor)
            .addInterceptor(new AuthenticateInterceptor(token))
            .connectTimeout(TIME_OUT_INTERVAL, TimeUnit.SECONDS)
            .readTimeout(TIME_OUT_INTERVAL, TimeUnit.SECONDS)
            .writeTimeout(TIME_OUT_INTERVAL, TimeUnit.SECONDS)
            .build();

    private static final Retrofit retrofit = new Retrofit
            .Builder()
            .baseUrl(domain)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create(gson))
            .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
            .build();

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static Error toError(String body){
        Object[] objects = Arrays.stream(body.split(","))
                .flatMap(s -> Arrays.stream(s.split("[{}\"]")))
                .flatMap(s2 -> Arrays.stream(s2.split(":", 1)))
                .filter(s3 -> s3.equals("\n") || s3.equals("") || s3.equals(":") ? false : true)
                .filter(s4 -> s4.equals("status") || s4.equals("msg") || s4.equals("timeStamp") ? false : true)
                .toArray();

        return new Error(objects[0].toString(), objects[1].toString(), objects[2].toString());
    }

    public static WalletService getWalletService(){
        return retrofit.create(WalletService.class);
    }

    public static UserService getUserService(){
        return retrofit.create(UserService.class);
    }
}
