package com.example.personalfinance;

import android.content.Context;
import android.util.Log;

import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.work.BackoffPolicy;
import androidx.work.Configuration;
import androidx.work.Constraints;
import androidx.work.OneTimeWorkRequest;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;
import androidx.work.testing.SynchronousExecutor;
import androidx.work.testing.TestDriver;
import androidx.work.testing.WorkManagerTestInitHelper;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.UserDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.User;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.datalayer.local.enums.Language;
import com.example.personalfinance.datalayer.local.repositories.WalletRepository;
import com.example.personalfinance.datalayer.workers.WalletWorker;
import com.example.personalfinance.datalayer.workers.WorkTag;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

@RunWith(AndroidJUnit4.class)
public class WorkerTesting {
    Context context;
    private WalletRepository walletRepository;
    private WalletDao walletDao;
    private UserDao userDao;
    private OneTimeWorkRequest oneTimeWorkRequest;
    private WorkManager workManager;
    private Constraints constraints;
    private TestDriver testDriver;

    @Before
    public void setUp(){
        context = ApplicationProvider.getApplicationContext();

        walletRepository = new WalletRepository(context);
        userDao = AppLocalDatabase.getInstance(context).getUserDao();
        walletDao = AppLocalDatabase.getInstance(context).getWalletDao();

        Configuration config = new Configuration.Builder()
                .setMinimumLoggingLevel(Log.DEBUG)
                .setExecutor(new SynchronousExecutor())
                .build();
        //init work manager
        WorkManagerTestInitHelper.initializeTestWorkManager(ApplicationProvider.getApplicationContext(), config);
    }

    @After
    public void cleanUp(){
        AppLocalDatabase.closeDb();
    }

//    @Test
//    @Ignore
//    public void testSyncManger(){
//        assertEquals(SyncState.not_sync_insert, SyncStateManager.determineSyncState(SyncStateManager.Action.insertTransact, null));
//        assertEquals(SyncState.not_sync_insert, SyncStateManager.determineSyncState(SyncStateManager.Action.update, SyncState.not_sync_insert));
//        assertEquals(SyncState.not_sync_update, SyncStateManager.determineSyncState(SyncStateManager.Action.update, SyncState.not_sync_update));
//        assertEquals(SyncState.not_sync_update, SyncStateManager.determineSyncState(SyncStateManager.Action.update, SyncState.synced));
//        assertEquals(SyncState.not_sync_delete, SyncStateManager.determineSyncState(SyncStateManager.Action.delete, SyncState.synced));
//        assertEquals(SyncState.no_sync_delete, SyncStateManager.determineSyncState(SyncStateManager.Action.delete, SyncState.not_sync_insert));
//        assertEquals(SyncState.no_sync_delete, SyncStateManager.determineSyncState(SyncStateManager.Action.delete, SyncState.not_sync_update));
//    }

    @Test
    public void insertUser(){
        User user = new User();
        user.setUserId(1);
        user.setUserName("hine");
        user.setPassword("123");
        user.setEmail("string1");
        user.setCurrency(Currency.vnd);
        user.setLanguage(Language.vn);

        //insertTransact
//        userDao.addUser(user).test().assertComplete().assertNoErrors();
    }

    @Test
    public void getUser(){
        //verify user
//        Integer id = userDao.getUserId().blockingGet();
//        assertEquals(1l, (long)id);
    }

//    @Test
//    public void checkWalletStateTransition(){
//        WalletModel walletModel1 = new WalletModel();
//        walletModel1.setWallet_title("wallet1");
//        walletModel1.setWallet_amount(1000d);
//        walletModel1.setWallet_description("this is wallet1");
//
//        //insertTransact wallet
//        walletRepository.insertTransact(walletModel1).test().assertNoErrors().assertComplete();
//        //assert the state
//        walletDao.getState(walletModel1.getWallet_title()).test().assertValue(state -> state == SyncState.not_sync_insert);
//
//        //update the wallet
//        walletRepository.updateTitle("wallet1", "newWallet1").test().assertNoErrors().assertComplete();
//        walletDao.getState("newWallet1").test().assertValue(state -> state == SyncState.not_sync_insert);
//        //and change back
//        walletRepository.updateTitle("newWallet1", walletModel1.getWallet_title()).test().assertNoErrors().assertComplete();
//        walletDao.getState(walletModel1.getWallet_title()).test().assertValue(state -> state == SyncState.not_sync_insert);
//        //delete the wallet
//        walletRepository.delete(walletModel1.getWallet_title()).test().assertComplete().assertNoErrors();
//        walletDao.getState(walletModel1.getWallet_title()).test().assertValue(state -> state == SyncState.no_sync_delete);
//        //remove completely
//        walletDao.removeWallet(walletModel1.getWallet_title()).test().assertNoErrors().assertComplete();
//        //add it back in
//        walletRepository.insertTransact(walletModel1).test().assertNoErrors().assertComplete();
//        //delete it
//        walletRepository.delete(walletModel1.getWallet_title()).test().assertComplete().assertNoErrors();
//        walletDao.getState(walletModel1.getWallet_title()).test().assertValue(state -> state == SyncState.no_sync_delete);
//
//    }

    private static long PERIODIC_INTERVAL = 15l;
    private static long PERIODIC_FLEX = 14l;

//    @SuppressLint("CheckResult")
//    @Test
//    public void testWalletWorker() throws ExecutionException, InterruptedException {
//        //insertTransact user
//        insertUser();
//
//        WalletModel walletModel1 = new WalletModel();
//        walletModel1.setWallet_title("wallet1");
//        walletModel1.setWallet_amount(1000d);
//        walletModel1.setWallet_description("this is wallet1");
//
//        //insertTransact wallet
//        walletRepository.insertTransact(walletModel1).test().assertNoErrors().assertComplete();
//
//        //update wallet
//        walletRepository.updateTitle(walletModel1.getWallet_title(), "newWallet").test().assertComplete().assertNoErrors();
//
//        //set constraint for the work
//        constraints = new Constraints.Builder()
//                .setRequiredNetworkType(NetworkType.CONNECTED)
//                .setRequiresBatteryNotLow(true)
//                .build();
//
//        //get test driver
//        testDriver = WorkManagerTestInitHelper.getTestDriver(context);
//
//        //get work manager
//        workManager = WorkManager.getInstance(context);
//
//        assertWorkSucceeded();
//
//        //check that inserted row has been synced
//        walletDao.getState("newWallet").test().assertValue(state -> state == SyncState.synced);
//
//        //update the wallet
//        String newTItle = "new Wallet";
//        walletRepository.updateTitle("newWallet", newTItle).test().assertComplete().assertNoErrors();
//        walletRepository.updateAmount(newTItle, 3000d).test().assertComplete().assertNoErrors();
//        walletRepository.updateDescription(newTItle, "helo bitch").test().assertComplete().assertNoErrors();
//
//        assertWorkSucceeded();
//
//        //check that updated row has been synced
//        walletDao.getState(newTItle).test().assertValue(state -> state == SyncState.synced);
//
//        //delete the wallet
//        walletRepository.delete(newTItle).test().assertComplete().assertNoErrors();
//
//        assertWorkSucceeded();
//
//        //test deleted row
//        walletDao.getState(walletModel1.getWallet_title()).test().assertError(throwable -> true);
//
////        //add back in
////        walletRepository.addWallet(walletModel1).test().assertComplete().assertNoErrors();
////
////        //and delete it
////        walletRepository.deleteWallet(walletModel1.getWallet_title()).test().assertNoErrors().assertComplete();
////
////        assertWorkSucceeded();
////
////        //add back in
////        walletRepository.addWallet(walletModel1).test().assertComplete().assertNoErrors();
////
//    }

    @Test
    @Ignore
    public void assertWorkSucceeded() throws ExecutionException, InterruptedException {
        oneTimeWorkRequest = new OneTimeWorkRequest
                .Builder(WalletWorker.class)
                .setConstraints(constraints)
                .setBackoffCriteria(BackoffPolicy.LINEAR, PeriodicWorkRequest.MIN_BACKOFF_MILLIS, TimeUnit.MILLISECONDS)
                .addTag(WorkTag.persistent.toString())
                .build();
        workManager.enqueue(oneTimeWorkRequest);
        //set constraints are met
        testDriver.setAllConstraintsMet(oneTimeWorkRequest.getId());
        //test syncing
        WorkInfo workInfo = null;
        do
        {
            workInfo = workManager.getWorkInfoById(oneTimeWorkRequest.getId()).get();
        }
        while(!workInfo.getState().isFinished());
        assertEquals(WorkInfo.State.SUCCEEDED, workInfo.getState());
    }
}
