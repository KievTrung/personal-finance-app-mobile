package com.example.personalfinance;

import android.content.Context;

//import androidx.test.core.app.ApplicationProvider;
import androidx.test.platform.app.InstrumentationRegistry;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.example.personalfinance.datalayer.local.daos.AppLocalDatabase;
import com.example.personalfinance.datalayer.local.daos.TokenDao;
import com.example.personalfinance.datalayer.local.daos.TransactDao;
import com.example.personalfinance.datalayer.local.daos.WalletDao;
import com.example.personalfinance.datalayer.local.entities.auxiliry.Token;
import com.example.personalfinance.datalayer.local.entities.Transact;
import com.example.personalfinance.datalayer.local.repositories.WalletRepository;
import com.example.personalfinance.fragment.transaction.wallet.WalletModel;

import java.time.LocalDateTime;

@RunWith(AndroidJUnit4.class)
public class ExampleInstrumentedTest {
    private TokenDao tokenDao;
    private WalletRepository walletRepository;
    private WalletDao walletDao;
    private TransactDao transactDao;
    private AppLocalDatabase db;

    @Before
    public void createDb(){
        Context context = InstrumentationRegistry.getInstrumentation().getTargetContext();
        db = AppLocalDatabase.getInstance(context);
        transactDao = db.getTransactDao();
        walletRepository = new WalletRepository(context);
        walletDao = db.getWalletDao();
        tokenDao = db.getTokenDao();
    }

    @After
    public void closeDb(){
        AppLocalDatabase.closeDb();
    }

    @Test
    public void testTokenDao(){
        String token = "123";

        tokenDao.setToken(new Token(token)).test().assertNoErrors().assertComplete();
        tokenDao.getToken().test().assertValue(s -> s.equals("123"));
    }

    @Test//(expected = SQLiteConstraintException.class)
    public void testPrimaryKeyWalletException(){
//        WalletModel wallet1 = new WalletModel();
//        wallet1.setWallet_title("testWallet");
//        wallet1.setWallet_amount(1000d);

//        walletRepository.insert(wallet1).test().assertComplete();

//        Wallet wallet2 = new Wallet();
//        wallet2.setWallet_title("testWallet");
//        wallet2.setWallet_amount(1000d);
        WalletModel wallet2 = new WalletModel();
        wallet2.setWallet_title("testWallet");
        wallet2.setWallet_amount(1000d);

//        walletRepository.insert(wallet2).test().assertError(throwable -> true);
    }

//    @Test
//    public void insertWalletWithTransactAndRead(){
//        //insertTransact wallet
//        Wallet wallet = new Wallet();
//        wallet.setWallet_title("testWallet");
////        wallet.setWallet_amount(1000l);
//        walletRepository.insertWallet(wallet);
//
//        //insertTransact transact with wallet
//        Transact transact = new Transact();
//        transact.setWallet_title(wallet.getWallet_title());
//        transact.setTran_title("tran1");
//        transact.setDate_time(LocalDateTime.now());
////        transact.setTran_amount(1000l);
//        transactDao.insertTransact(transact);
//
//        //Query wallet with transact
////        List<WalletWithTransacts> walletWithTransacts = walletRepository.getWalletWithTransactList();
//
//        //verify result
////        assertEquals("testWallet", walletWithTransacts.get(0).wallet.getWallet_title());
////        assertEquals("tran1", walletWithTransacts.get(0).transactList.get(0).getTran_title());
//    }

    @Test
    public void insertAndDeleteTransact(){
        //insertTransact transact with wallet
//        Transact transact = new Transact();
//        transact.setWallet_title("hello");
//        transact.setTran_title("tran1");
//        transact.setDate_time(LocalDateTime.now());
//        transact.setTran_amount(1000l);
//        long id = transactDao.insertTransact(transact);

        // delete transact
//        transactDao.deleteTransactById(id);

        //verify deleted
//        Transact deletedTransact = transactDao.getTransactById(id);
//        assertNull(deletedTransact);
    }
}