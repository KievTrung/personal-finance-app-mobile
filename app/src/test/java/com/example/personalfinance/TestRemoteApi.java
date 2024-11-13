package com.example.personalfinance;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Language;
import com.example.personalfinance.datalayer.remote.ApiServiceFactory;
import com.example.personalfinance.datalayer.remote.UserService;
import com.example.personalfinance.datalayer.remote.WalletService;
import com.example.personalfinance.datalayer.remote.models.UserRemote;
import com.example.personalfinance.datalayer.remote.models.WalletRemote;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.junit.Before;
import org.junit.Test;

import java.time.LocalDateTime;
import java.util.List;

import io.reactivex.rxjava3.core.Single;
import io.reactivex.rxjava3.observers.TestObserver;
import okhttp3.ResponseBody;
import retrofit2.HttpException;

public class TestRemoteApi {
    private static final String TAG = "TestRemoteApi";
    UserService userService;
    WalletService walletService;

    @Before
    public void setUp(){
        userService = ApiServiceFactory.getUserService();
        walletService = ApiServiceFactory.getWalletService();
    }

    @Test
    public void testPostUser() throws InterruptedException {
        UserRemote userRemote = new UserRemote();
        userRemote.setUserName("trung");
        userRemote.setPassword("123");
        userRemote.setEmail("string2");
        userRemote.setCurrency(Currency.usd);
        userRemote.setLanguage(Language.usa);
        Single<UserRemote> user = userService.postUser(userRemote);

        TestObserver<UserRemote> testObserver = user.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(userRemote1 -> {
            System.out.println(userRemote1);
            return userRemote1.getUserName().equals(userRemote.getUserName());
        });
    }

    @Test
    public void testGetUser() throws InterruptedException {
        Single<UserRemote> userRemote = userService.getUser(1);
        TestObserver<UserRemote> testObserver = userRemote.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(userRemote1 -> {
            System.out.println(userRemote1);
            return userRemote1.getUserName().equals("hine");
        });
    }

    @Test
    public void testGetWallets() throws InterruptedException {
        Single<List<WalletRemote>> single = walletService.getWallets(1);

        TestObserver<List<WalletRemote>> testObserver = single.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(walletRemotes -> {
            System.out.println(walletRemotes);
            return walletRemotes.size() != 0;
        });
    }

    @Test
    public void testPostWallet() throws InterruptedException {
        WalletRemote walletRemote = new WalletRemote();
        walletRemote.setWallet_amount(1000d);
        walletRemote.setWallet_title("string3");
        walletRemote.setWallet_description("hello");

        Single<WalletRemote> single = walletService.addWallet(1, walletRemote);
        TestObserver<WalletRemote> testObserver = single.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(walletRemote1 -> {
            System.out.println(walletRemote1);
            return walletRemote1.getWallet_title().equals(walletRemote.getWallet_title());
        });
    }

    @Test
    public void testPutWallet() throws InterruptedException {
        String newTitle = "newWallet";
        Single<Integer> single = walletService.updateWallet(1, "string3", newTitle);
        TestObserver<Integer> testObserver = single.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(row -> row == 1);

        Single<Integer> single2 = walletService.updateAmount(1, newTitle, 2000d);
        Single<Integer> single3 = walletService.updateDescription(1, newTitle, "bitch");

        TestObserver<Integer> testObserver2 = Single.concat(single2, single3).toObservable().test();

        testObserver2.await();
        testObserver2.assertNoErrors();
        testObserver2.assertValueAt(0, row -> row == 1);
        testObserver2.assertValueAt(1, row -> row == 1);
    }

    @Test
    public void testDeleteWallet() throws InterruptedException {
        Single<Integer> single = walletService.deleteWallet(1, "bitch");

        TestObserver<Integer> testObserver = single.test();

        testObserver.await();
        testObserver.assertNoErrors();
        testObserver.assertValue(row -> row == 1);
    }

    @Test
    public void testErrorAddWallet() throws InterruptedException {
        WalletRemote walletRemote = new WalletRemote();
        walletRemote.setWallet_amount(-1000d);
        walletRemote.setWallet_title("string");
        walletRemote.setWallet_description("hello");

        Single<WalletRemote> single = walletService.addWallet(3, walletRemote);
        TestObserver<WalletRemote> testObserver = single.test();

        testObserver.await();
        testObserver.assertError(throwable -> {
            if (throwable instanceof HttpException) {
                HttpException httpException = (HttpException) throwable;
                ResponseBody responseBody = httpException.response().errorBody();
                System.out.println(ApiServiceFactory.toError(responseBody.string()));
                return httpException.code() == 400;
            }
            return false;
        });
        testObserver.assertNoValues();
    }
}