package com.example.personalfinance.datalayer.remote;

import com.example.personalfinance.datalayer.remote.models.UserRemote;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

public interface UserService {
    @GET("users/{id}")
    Single<UserRemote> getUser(@Path("id") Integer userId);

    @POST("user")
    Completable postUser(@Body UserRemote userRemote);

    @DELETE("user/{id}")
    Single<Integer> deleteUser(@Path("id") Integer userId);
}
