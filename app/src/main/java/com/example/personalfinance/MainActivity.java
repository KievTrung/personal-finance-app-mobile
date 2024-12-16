package com.example.personalfinance;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextWatcher;
import android.transition.TransitionInflater;
import android.util.Log;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.activity.OnBackPressedCallback;
import androidx.activity.OnBackPressedDispatcher;
import androidx.annotation.LongDef;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.GravityCompat;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.ViewModelProvider;

import com.example.personalfinance.datalayer.local.entity.Item;
import com.example.personalfinance.datalayer.local.entity.Notify;
import com.example.personalfinance.datalayer.local.enums.Currency;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.entry.EntryFragment;
import com.example.personalfinance.fragment.setting.SettingFragment;
import com.example.personalfinance.fragment.budget.BudgetFragment;
import com.example.personalfinance.fragment.notify.NotificationFragment;
import com.example.personalfinance.fragment.setting.repository.UserRepository;
import com.example.personalfinance.fragment.transaction.TransactionFragment;
import com.example.personalfinance.fragment.transaction.model.ItemModel;
import com.example.personalfinance.fragment.wallet.WalletFragment;
import com.google.android.material.navigation.NavigationView;

import java.math.BigDecimal;
import java.util.List;
import java.util.regex.Pattern;

public class MainActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {
    private static final String TAG = "kiev main";
    private Toolbar tb;
    private DrawerLayout dl;
    private NavigationView nv;
    private MainActivityViewModel viewModel;

    private enum FRAGMENT {
        FRAGMENT_TRANSACTION,
        FRAGMENT_WALLET,
        FRAGMENT_BUDGET,
        FRAGMENT_NOTIFY,
        FRAGMENT_SETTING,
        FRAGMENT_ENTRY,
    }

    private FRAGMENT currentFragment = FRAGMENT.FRAGMENT_TRANSACTION;

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
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

        //init view model
        viewModel = new ViewModelProvider(this).get(MainActivityViewModel.class);

        tb = findViewById(R.id.tool_bar);
        nv = findViewById(R.id.nav_view);
        dl = findViewById(R.id.drawer_layout);

        setSupportActionBar(tb);

        findViewById(R.id.toolbar_menu).setOnClickListener((v) -> dl.openDrawer(Gravity.LEFT));

        nv.bringToFront();
        nv.setNavigationItemSelectedListener(this);

        OnBackPressedDispatcher onBackPressedDispatcher = getOnBackPressedDispatcher();
        onBackPressedDispatcher.addCallback(this, new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (dl.isDrawerOpen(GravityCompat.START)) {
                    dl.closeDrawer(GravityCompat.START);
                }
            }
        });
        init();
    }

    private void init() {

        //respond to a budget exceeded notification
        Intent intent = getIntent();
        int budgetId = intent.getIntExtra("budgetid", -1);
        long notifyid = intent.getLongExtra("notifyid", -1);
        if (budgetId != -1){
            Bundle bundle = new Bundle();
            bundle.putInt("payloadbudget", budgetId);
            bundle.putLong("payloadnotify", notifyid);
            getSupportFragmentManager().setFragmentResult("budgetid", bundle);

            replaceFragment(new BudgetFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_BUDGET;
        }
        else{
            //default entry fragment
            replaceFragment(new EntryFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_ENTRY;
        }
    }

    //notification
    public static class NotifyObject {
        public Integer id;
        public Notification notification;

        public NotifyObject(Integer id, Notification notification) {
            this.id = id;
            this.notification = notification;
        }
    }
    private static final String CHANNEL_ID = "app_channel";
    private static final String GROUP_ID = "app_group";

    public void showNotification(List<NotifyObject> notifyObjects) {

        if (!notifyObjects.isEmpty()){
            //show dialog
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Your budget have exceeded by creating this transaction\nPlease check notification");
            dialog.setNoticeDialogListener(DialogFragment::dismiss);
            dialog.show(getSupportFragmentManager(), null);
        }
        else return;

        viewModel.compositeDisposable.add(
                viewModel.getNotifyPermission().subscribe(permission -> {
                    if (permission && ContextCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED) return;

                    //set up notify channel
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                        NotificationChannel channel = new NotificationChannel(CHANNEL_ID, "MY CHANNEL", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager manager = MainActivity.this.getSystemService(NotificationManager.class);
                        manager.createNotificationChannel(channel);
                    }

                    Notify summaryNotify = new Notify();
                    summaryNotify.setHeader("Budget exceeded");
                    summaryNotify.setContent("Click to view detail");
                    summaryNotify.setType(Notify.Type.budget);

                    //post notify
                    NotificationManagerCompat managerCompat = NotificationManagerCompat.from(this);
                    if (ActivityCompat.checkSelfPermission(getApplicationContext(), Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED){
                        for (NotifyObject notify : notifyObjects)
                            managerCompat.notify(notify.id, notify.notification);
                        managerCompat.notify(0, createNotification(getApplicationContext(), summaryNotify, null, true));
                    }
                })
        );

    }

    public static Notification createNotification(Context context, @NonNull Notify notify, @Nullable Intent intent, boolean isGroupSummary) {
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(setNotifyIcon(notify.getType()))
                .setContentTitle(notify.getHeader())
                .setContentText(notify.getContent())
                .setStyle(new NotificationCompat.BigTextStyle().bigText(notify.getContent()))
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                .setGroup(GROUP_ID)
                .setGroupSummary(isGroupSummary)
                .setOnlyAlertOnce(true)
                .setAutoCancel(true);

        if (intent != null) {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_MUTABLE);
            builder.setContentIntent(pendingIntent);
        }
        return builder.build();
    }

    public static int setNotifyIcon(Notify.Type type){
        switch(type){
            default:
                return R.drawable.notifications;
        }
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

    public boolean checkTitle(String title){
        if (title == null || title.isEmpty()) {
            Toast.makeText(this, MessageCode.field_title_required, Toast.LENGTH_LONG).show();
            return false;
        }
        title = title.trim();
        //only alow alphabet
        if (Pattern.compile("\\d+").matcher(title).find()) {
            Toast.makeText(this, MessageCode.field_title_alpha_only, Toast.LENGTH_LONG).show();
            /*error*/
            return false;
        }
        //check length
        if (title.length() > 20 || title.length() < 5){
            /*error*/
            Toast.makeText(this, MessageCode.field_title_char_length_allow, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
    }

    public boolean checkAmount(String amount, Currency currency){
        if (amount == null || amount.isEmpty()) {
            Toast.makeText(this, MessageCode.field_amount_required, Toast.LENGTH_LONG).show();
            return false;
        }

        amount = amount.trim();
        double amount_ = Double.parseDouble(amount);
        //check amount
        if ((currency == Currency.vnd && amount_ == 0d) || (currency == Currency.usd && amount_ == 0.00d)
                || UserRepository.backCurrency(amount_, currency) > MessageCode.amount_limit){
            Toast.makeText(this, MessageCode.field_amount_limit_allow, Toast.LENGTH_LONG).show();
            return false;
        }

        /*pass*/
        return true;
    }

    public boolean isExceedBillion(@NonNull List<ItemModel> itemModels){
        BigDecimal sum = BigDecimal.ZERO;

        for(ItemModel itemModel : itemModels)
            sum = sum.add(BigDecimal.valueOf(itemModel.getItem_price()).multiply(BigDecimal.valueOf(itemModel.getQuantity())));

        return sum.doubleValue() > MessageCode.amount_limit;
    }

    public boolean checkDescription(String description){
        if (description != null && description.trim().length() > 40){
            Toast.makeText(this, MessageCode.field_description_max_char, Toast.LENGTH_LONG).show();
            return false;
        }
        return true;
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
        else if (id == R.id.nav_notify && currentFragment != FRAGMENT.FRAGMENT_NOTIFY){
            replaceFragment(new NotificationFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_NOTIFY;
        }
        else if (id == R.id.nav_setting && currentFragment != FRAGMENT.FRAGMENT_SETTING){
            replaceFragment(new SettingFragment(), false, null);
            currentFragment = FRAGMENT.FRAGMENT_SETTING;
        }
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