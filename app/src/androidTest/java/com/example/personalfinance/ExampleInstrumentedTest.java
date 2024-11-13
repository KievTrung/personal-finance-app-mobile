package com.example.personalfinance;

import android.content.Context;
import android.database.sqlite.SQLiteConstraintException;

//import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.*;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.TransactDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.entities.Wallet;
import com.example.personalfinance.datalayer.local.relationships.WalletWithTransacts;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Instrumented test, which will execute on an Android device.
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private WalletDao walletDao;
    private TransactDao transactDao;
    private AppLocalDatabase db;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = AppLocalDatabase.getInstance(context);
        transactDao = db.getTransactDao();
        walletDao = db.getWalletDao();
    }

    @After
    public void closeDb(){
        AppLocalDatabase.closeDb();
    }

    @Test(expected = SQLiteConstraintException.class)
    public void testPrimaryKeyWalletException(){
//        Wallet wallet = new Wallet();
//        wallet.setWallet_title("testWallet");
//        wallet.setWallet_amount(1000l);

//        walletDao.insertWallet(wallet);

//        Wallet wallet2 = new Wallet();
//        wallet2.setWallet_title("testWallet");
//        wallet2.setWallet_amount(1000l);

//        walletDao.insertWallet(wallet2);
    }

//    @Test
//    public void insertWalletWithTransactAndRead(){
//        //insert wallet
//        Wallet wallet = new Wallet();
//        wallet.setWallet_title("testWallet");
////        wallet.setWallet_amount(1000l);
//        walletDao.insertWallet(wallet);
//
//        //insert transact with wallet
//        Transact transact = new Transact();
//        transact.setWallet_title(wallet.getWallet_title());
//        transact.setTran_title("tran1");
//        transact.setDate_time(LocalDateTime.now());
////        transact.setTran_amount(1000l);
//        transactDao.insertTransact(transact);
//
//        //Query wallet with transact
////        List<WalletWithTransacts> walletWithTransacts = walletDao.getWalletWithTransactList();
//
//        //verify result
////        assertEquals("testWallet", walletWithTransacts.get(0).wallet.getWallet_title());
////        assertEquals("tran1", walletWithTransacts.get(0).transactList.get(0).getTran_title());
//    }

    @Test
    public void insertAndDeleteTransact(){
        //insert transact with wallet
        Transact transact = new Transact();
        transact.setWallet_title("hello");
        transact.setTran_title("tran1");
        transact.setDate_time(LocalDateTime.now());
        transact.setTran_amount(1000l);
//        long id = transactDao.insertTransact(transact);

        // delete transact
//        transactDao.deleteTransactById(id);

        //verify deleted
//        Transact deletedTransact = transactDao.getTransactById(id);
//        assertNull(deletedTransact);
    }
}