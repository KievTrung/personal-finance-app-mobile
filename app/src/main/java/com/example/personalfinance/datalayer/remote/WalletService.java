package com.example.personalfinance.datalayer.remote;

import com.example.personalfinance.datalayer.remote.models.UserRemote;
import com.example.personalfinance.datalayer.remote.models.WalletRemote;

import java.util.List;
import java.util.Map;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.PATCH;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface WalletService {
    String base = "users/{user-id}/";

    @GET(base + "wallets")
    Single<List<WalletRemote>> getWallets(@Path("user-id") Integer userId);

    @POST(base + "wallet")
    Single<WalletRemote> addWallet(@Path("user-id") Integer userId, @Body WalletRemote walletRemote);

    @PUT(base + "wallets/{wallet-title}/title")
    Single<Integer> updateWallet(@Path("user-id") Integer userId, @Path("wallet-title") String oldTitle, @Query("new") String newTitle);

    @PUT(base + "wallets/{wallet-title}/amount")
    Single<Integer> updateAmount(@Path("user-id") Integer userId, @Path("wallet-title") String title, @Query("new") Double amount);

    @PUT(base + "wallets/{wallet-title}/description")
    Single<Integer> updateDescription(@Path("user-id") Integer userId, @Path("wallet-title") String title, @Query("new") String description);

    @DELETE(base + "wallets/{wallet-title}")
    Single<Integer> deleteWallet(@Path("user-id") Integer userId, @Path("wallet-title") String title);
}
