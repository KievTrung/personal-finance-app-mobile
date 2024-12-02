package com.example.personalfinance;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.fragment.account.AccountFragment;
import com.example.personalfinance.fragment.BudgetFragment;
import com.example.personalfinance.fragment.LoanDebtFragment;
import com.example.personalfinance.fragment.NotificationFragment;
import com.example.personalfinance.fragment.SettingFragment;
import com.example.personalfinance.fragment.transaction.transaction.TransactionFragment;
import com.example.personalfinance.fragment.transaction.wallet.WalletFragment;
import com.google.android.material.navigation.NavigationView;

import java.text.DecimalFormat;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "kiev main";
    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nv;
    private MainActivityViewModel viewModel;

    private enum FRAGMENT{
        FRAGMENT_TRANSACTION,
        FRAGMENT_WALLET,
        FRAGMENT_ACCOUNT,
        FRAGMENT_BUDGET,
        FRAGMENT_NOTIFY,
        FRAGMENT_SETTING,
        FRAGMENT_LOAN_DEBT;
    }

    private FRAGMENT currentFragment = FRAGMENT.FRAGMENT_TRANSACTION;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.drawer_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        init();
    }

    private void init(){
        //init view model
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        tb = findViewById(R.id.tool_bar);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);

        setSupportActionBar(tb);

        findViewById(R.id.toolbar_menu).setOnClickListener((v)-> dl.openDrawer(Gravity.LEFT));

        nv.bringToFront();
        nv.setNavigationItemSelectedListener(this);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dl.isDrawerOpen(GravityCompat.START)){
                    dl.closeDrawer(GravityCompat.START);
                }
            }
        });

//        initOpenFragment();
        replaceFragment(new AccountFragment(), false, null);
        currentFragment = FRAGMENT.FRAGMENT_ACCOUNT;
    }

    public void replaceFragment(Fragment fragment, boolean addBackStack, Bundle args){
        //set transition for fragment
        fragment.setEnterTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.enter_from_right));
        fragment.setExitTransition(TransitionInflater.from(getApplicationContext()).inflateTransition(R.transition.exit_to_left));
        //move to new fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setReorderingAllowed(true)
                                                    .replace(R.id.fragment_container, fragment.getClass(), args);

        if (addBackStack) fragmentTransaction.addToBackStack(null).commit();
        else fragmentTransaction.commit();
    }

    public void configToolbarToReturn(View.OnClickListener listener){
        configToolBarTopRightBtn(View.VISIBLE, R.drawable.arrow_back_icon, listener);
        setToolBarMenuBtnVisibility(View.INVISIBLE);
    }

    public void configToolBarTopRightBtn(int viewMode, int imageSrc, View.OnClickListener setAction){
        ImageButton backBtn = getTopRightBtnReference();
        backBtn.setImageResource(imageSrc);
        backBtn.setOnClickListener(setAction);
        backBtn.setVisibility(viewMode);
    }

    public ImageButton getTopRightBtnReference(){
        return this.findViewById(R.id.toolbar_back);
    }

    public void setToolBarReturnBtnVisibility(int viewMode){
        ImageButton returnBtn = getTopRightBtnReference();
        returnBtn.setVisibility(viewMode);
    }

    public void setToolBarMenuBtnVisibility(int viewMode){
        ImageButton menuBtn = this.findViewById(R.id.toolbar_menu);
        menuBtn.setVisibility(viewMode);
    }

    public void setToolBarHeaderText(String text){
        TextView textView = this.findViewById(R.id.toolbar_text);
        textView.setText(text);
    }

    @SuppressLint("CheckResult")
    public void initOpenFragment(){
        viewModel.compositeDisposable.add(
                viewModel
                        .getUseWallet()
                        .subscribe(walletModel -> {
                            Log.d(TAG, "initOpenFragment: " + walletModel.getId());
                            if (walletModel.getId() == -1)
                                replaceFragment(new WalletFragment(), false, null);
                            else
                                replaceFragment(new TransactionFragment(),false, null);
                        })
        );
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_transaction && currentFragment != FRAGMENT.FRAGMENT_TRANSACTION){
            initOpenFragment();
            currentFragment = FRAGMENT.FRAGMENT_TRANSACTION;
        }
        else if (id == R.id.nav_budget && currentFragment != FRAGMENT.FRAGMENT_BUDGET){
            replaceFragment(new BudgetFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_BUDGET;
        }
        else if (id == R.id.nav_loan_debt && currentFragment != FRAGMENT.FRAGMENT_LOAN_DEBT){
            replaceFragment(new LoanDebtFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_LOAN_DEBT;
        }
        else if (id == R.id.nav_notify && currentFragment != FRAGMENT.FRAGMENT_NOTIFY){
            replaceFragment(new NotificationFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_NOTIFY;
        }
        else if (id == R.id.nav_setting && currentFragment != FRAGMENT.FRAGMENT_SETTING){
            replaceFragment(new SettingFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_SETTING;
        }
        else if (id == R.id.nav_account && currentFragment != FRAGMENT.FRAGMENT_ACCOUNT){
            replaceFragment(new AccountFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_ACCOUNT;
        }
        Log.d(TAG, "Run fragment: " + currentFragment);
        //close drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    public static void setMaxDecimalInEditText(EditText editText, int maxDecimalPlaces) {
        // Add a TextWatcher to enforce the decimal limit dynamically
        editText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {}

            @Override
            public void afterTextChanged(Editable s) {
                String text = s.toString();
                if (text.contains(".")) {
                    // Limit decimal places
                    String[] parts = text.split("\\.");
                    if (parts.length > 1 && parts[1].length() > maxDecimalPlaces) {
                        s.replace(0, s.length(), parts[0] + "." + parts[1].substring(0, maxDecimalPlaces));
                    }
                }
            }
        });

        // Add an InputFilter to restrict invalid inputs
        InputFilter filter = (source, start, end, dest, dstart, dend) -> {
            if (source.toString().matches("[^0-9.]")) {
                return ""; // Block non-numeric input
            }
            if (source.toString().equals(".") && dest.toString().contains(".")) {
                return ""; // Block multiple dots
            }
            return null; // Accept valid input
        };

        editText.setFilters(new InputFilter[]{filter});
    }

}