package com.example.personalfinance;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
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
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.example.personalfinance.fragment.AccountFragment;
import com.example.personalfinance.fragment.BudgetFragment;
import com.example.personalfinance.fragment.LoanDebtFragment;
import com.example.personalfinance.fragment.NotificationFragment;
import com.example.personalfinance.fragment.SettingFragment;
import com.example.personalfinance.fragment.dialogFragment.SingleChoiceDialogFragment;
import com.example.personalfinance.fragment.transaction.TransactionFragment;
import com.example.personalfinance.fragment.transaction.WalletInfoFragment;
import com.google.android.material.navigation.NavigationView;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "MainActivity";
    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nv;

    private enum FRAGMENT{
        FRAGMENT_TRANSACTION,
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
        tb = findViewById(R.id.tool_bar);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);

        setSupportActionBar(tb);

        ((ImageButton)findViewById(R.id.toolbar_menu)).setOnClickListener((v)-> dl.openDrawer(Gravity.LEFT));

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

        replaceFragment(R.id.fragment_container, new TransactionFragment(), this, false, null);
    }

    public void replaceFragment(int fragmentContainer, Fragment fragment, Context context, boolean addBackStack, String name){
        //set transition for fragment
        fragment.setEnterTransition(TransitionInflater.from(context).inflateTransition(R.transition.enter_from_right));
        fragment.setExitTransition(TransitionInflater.from(context).inflateTransition(R.transition.exit_to_left));
        //move to new fragment
        FragmentTransaction fragmentTransaction = getSupportFragmentManager()
                                                    .beginTransaction()
                                                    .setReorderingAllowed(true)
                                                    .replace(fragmentContainer, fragment);

        if (addBackStack) fragmentTransaction.addToBackStack(name).commit();
        else fragmentTransaction.commit();
    }

    public void configToolbarToReturn(Activity activity, FragmentManager fragmentManager, String title){
        configToolBarTopRightBtn(activity, View.VISIBLE, R.drawable.arrow_back_icon, (view)-> fragmentManager.popBackStack());
        setToolBarMenuBtnVisibility(activity, View.INVISIBLE);
        setToolBarHeaderText(activity, title);
    }

    public void configToolBarTopRightBtn(Activity activity, int viewMode, int imageSrc, View.OnClickListener setAction){
        ImageButton backBtn = getTopRightBtnReference(activity);
        backBtn.setImageResource(imageSrc);
        backBtn.setOnClickListener(setAction);
        backBtn.setVisibility(viewMode);
    }

    public ImageButton getTopRightBtnReference(Activity activity){
        return (ImageButton) activity.findViewById(R.id.toolbar_back);
    }

    public void setToolBarMenuBtnVisibility(Activity activity, int viewMode){
        ImageButton menuBtn = activity.findViewById(R.id.toolbar_menu);
        menuBtn.setVisibility(viewMode);
    }

    public void setToolBarHeaderText(Activity activity, String text){
        TextView textView = activity.findViewById(R.id.toolbar_text);
        textView.setText(text);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.nav_transaction && currentFragment != FRAGMENT.FRAGMENT_TRANSACTION){
            replaceFragment(R.id.fragment_container, new TransactionFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_TRANSACTION;
        }
        else if (id == R.id.nav_budget && currentFragment != FRAGMENT.FRAGMENT_BUDGET){
            replaceFragment(R.id.fragment_container, new BudgetFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_BUDGET;
        }
        else if (id == R.id.nav_loan_debt && currentFragment != FRAGMENT.FRAGMENT_LOAN_DEBT){
            replaceFragment(R.id.fragment_container, new LoanDebtFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_LOAN_DEBT;
        }
        else if (id == R.id.nav_notify && currentFragment != FRAGMENT.FRAGMENT_NOTIFY){
            replaceFragment(R.id.fragment_container, new NotificationFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_NOTIFY;
        }
        else if (id == R.id.nav_setting && currentFragment != FRAGMENT.FRAGMENT_SETTING){
            replaceFragment(R.id.fragment_container, new SettingFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_SETTING;
        }
        else if (id == R.id.nav_account && currentFragment != FRAGMENT.FRAGMENT_ACCOUNT){
            replaceFragment(R.id.fragment_container, new AccountFragment(), this, false, null);
            currentFragment = FRAGMENT.FRAGMENT_ACCOUNT;
        }
        Log.d(TAG, "Run fragment: " + currentFragment);
        //close drawer
        DrawerLayout drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

}