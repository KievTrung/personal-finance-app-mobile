package com.example.personalfinance.fragment.setting;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.fragment.setting.viewmodel.SettingViewModel;

public class SettingFragment extends Fragment{
    private static final String TAG = "kiev";
    private SettingViewModel viewModel;
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init viewModel
        viewModel = new ViewModelProvider(this).get(SettingViewModel.class);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v =  inflater.inflate(R.layout.fragment_setting, container, false);
        init(v);
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
        activity.setToolBarHeaderText("Setting");
        activity.setToolBarMenuBtnVisibility(View.VISIBLE);
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    public void init(View v){
        activity = (MainActivity)getActivity();

        v.findViewById(R.id.switch_currency).setOnClickListener(v1 -> {
            CurrencyDialogFragment dialog = new CurrencyDialogFragment();
            dialog.setNoticeDialogListener((dialog1, currency) -> {
                viewModel.compositeDisposable.add(
                        viewModel
                                .switchCurrency(currency)
                                .subscribe(() -> Toast.makeText(requireContext(), "switch to " + currency, Toast.LENGTH_LONG).show()
                                        ,throwable -> Log.d(TAG, "error: " + throwable.getMessage()))
                );
            });
            dialog.show(getParentFragmentManager(), null);
        });

        Button notify_btn = v.findViewById(R.id.show_notification);
        viewModel.compositeDisposable.add(
                viewModel.getNotifyPer().subscribe(allowNotify -> {
                    notify_btn.setText((allowNotify) ? "Notification : on" : "Notification : off");
                })
        );
        notify_btn.setOnClickListener(v1 -> {
            viewModel.compositeDisposable.add(
                    viewModel.setNotify(!viewModel.allowNotify).subscribe(() -> {

                        //get permission
                        if (!viewModel.allowNotify && ContextCompat.checkSelfPermission(activity, Manifest.permission.POST_NOTIFICATIONS) != PackageManager.PERMISSION_GRANTED)
                            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.POST_NOTIFICATIONS}, 1);

                        viewModel.allowNotify = !viewModel.allowNotify;
                        notify_btn.setText((viewModel.allowNotify) ? "Notification : on" : "Notification : off");
                    })
            );
        });

        v.findViewById(R.id.report).setOnClickListener(v1 -> activity.replaceFragment(new ReportFragment(), true, null));
    }

}