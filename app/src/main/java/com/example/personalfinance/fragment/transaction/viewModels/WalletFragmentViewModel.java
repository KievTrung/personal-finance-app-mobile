package com.example.personalfinance.fragment.transaction.viewModels;

import android.app.Application;
import android.util.Log;

import androidx.lifecycle.AndroidViewModel;

import com.example.personalfinance.datalayer.repositories.WalletRepository;
import com.example.personalfinance.fragment.transaction.models.WalletModel;

import java.util.ArrayList;
import java.util.List;

import io.reactivex.rxjava3.core.Completable;
import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

public class WalletFragmentViewModel extends AndroidViewModel{
    private static final String TAG = "kiev";
    private List<WalletModel> wallets;
    private CompositeDisposable compositeDisposable;
    private WalletRepository walletRepository;

    public enum WalletAction { create, update, delete }
    public enum ActionState{ complete }

    public static WalletAction walletAction = null;
    public static Integer position = null;
    public static Completable completable = null;
    public static ActionState actionState = null;

    public WalletFragmentViewModel(Application application){
        super(application);
        wallets = new ArrayList<>();
        compositeDisposable = new CompositeDisposable();
        walletRepository = new WalletRepository(getApplication().getApplicationContext());
    }

    public List<WalletModel> getWallets(){
        return wallets;
    }

    public Completable useWallet(int position) {
        Log.d(TAG, "useWallet");
        return walletRepository
                .useWallet(wallets.get(position).getWallet_title()).doOnComplete(() -> updateUseWallet(position));
    }

    public Single<List<WalletModel>> fetchWallets(){
        Log.d(TAG, "fetchWallets");
        return walletRepository.getAllWallet().doOnSuccess(walletModels -> wallets = walletModels);
    }

    private void updateUseWallet(int position){
        for (int i=0; i<wallets.size(); i++)
        {
            WalletModel walletModel = wallets.get(i);
            if (i == position)
                walletModel.setCurrent_use(true);
            else
                walletModel.setCurrent_use(false);
        }
        Log.d(TAG, "updateUseWallet");
    }

    public Completable add(WalletModel walletModel){
        Log.d(TAG, "addWallet");
        return walletRepository.addWallet(walletModel);
    }

    public Completable update(int position, WalletModel newWalletModel){
        WalletModel oldWalletModel = wallets.get(position);
        if (!oldWalletModel.getWallet_title().equals(newWalletModel.getWallet_title())){
            Log.d(TAG, "updateWallet: title");
            return walletRepository.updateTitle(oldWalletModel.getWallet_title(), newWalletModel.getWallet_title());
        }
        if (!oldWalletModel.getWallet_amount().equals(newWalletModel.getWallet_amount())){
            Log.d(TAG, "updateWallet: amount");
            return walletRepository.updateAmount(oldWalletModel.getWallet_title(), newWalletModel.getWallet_amount());
        }
        if (!oldWalletModel.getWallet_description().equals(newWalletModel.getWallet_description())){
            Log.d(TAG, "updateWallet: description");
            return walletRepository.updateDescription(oldWalletModel.getWallet_title(), newWalletModel.getWallet_description());
        }
        return Completable.complete();
    }

    public WalletModel get(int position){
        Log.d(TAG, "getWallet: ");
        Log.d(TAG, "wallet size: " + wallets.size());
        return wallets.get(position);
    }

    public Completable remove(int position){
        Log.d(TAG, "removeWallet");
        return walletRepository.deleteWallet(wallets.get(position).getWallet_title());
    }

    @Override
    protected void onCleared() {
        super.onCleared();
        Log.d(TAG, "onCleared: dispose");
        compositeDisposable.dispose();
    }
}
