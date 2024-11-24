package com.example.personalfinance.fragment.category;

import android.annotation.SuppressLint;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.viewpager2.widget.ViewPager2;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.enums.CategoryType;
import com.example.personalfinance.fragment.category.adapter.CategoryViewPagerAdapter;
import com.example.personalfinance.fragment.category.viewmodel.EarningCategoryViewModel;
import com.example.personalfinance.fragment.category.viewmodel.SpendingCategoryViewModel;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.List;

import io.reactivex.rxjava3.annotations.NonNull;
import io.reactivex.rxjava3.core.CompletableObserver;
import io.reactivex.rxjava3.core.SingleObserver;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import io.reactivex.rxjava3.disposables.Disposable;

public class CategoryFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "kiev ui";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CategoryViewPagerAdapter adapter;
    private SpendingCategoryViewModel spendingViewModel;
    private EarningCategoryViewModel earningViewModel;
    private MainActivity activity;
    private final CategorySpendingFragment spendingFragment = new CategorySpendingFragment();
    private final CategoryEarningFragment earningFragment = new CategoryEarningFragment();

    @SuppressLint("CheckResult")
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //init view model
        spendingViewModel = new ViewModelProvider(requireActivity()).get(SpendingCategoryViewModel.class);
        earningViewModel = new ViewModelProvider(requireActivity()).get(EarningCategoryViewModel.class);

        //init page adapter
        adapter = new CategoryViewPagerAdapter(this);
        adapter.addFragment(spendingFragment);
        adapter.addFragment(earningFragment);

        //get host activity
        activity = (MainActivity) getActivity();

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        init(v);
        return v;
    }


    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "onStart: category");
        //set up tool bar return btn
        activity.configToolbarToReturn(view -> getParentFragmentManager().popBackStack());
    }

    private void init(View v){
        viewPager = v.findViewById(R.id.viewPager);
        viewPager.setAdapter(adapter);

        tabLayout = v.findViewById(R.id.category_tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText((position == 0) ? "spending" : "earning")).attach();

        v.findViewById(R.id.new_category_btn).setOnClickListener(this);

        Log.d(TAG, "init: category fragment");
    }

    @Override
    public void onClick(View view) {
        CategoryDialogFragment dialog = CategoryDialogFragment.newInstance(CategoryDialogFragment.Action.insert, null);
        dialog.setPositive(this::onDialogPositiveClick);
        dialog.show(getParentFragmentManager(), TAG);
    }

    @SuppressLint("CheckResult")
    public void onDialogPositiveClick(DialogFragment dialog, CategoryModel categoryModel) {
        Log.d(TAG, "onDialogPositiveClick: " + categoryModel);
        //add new category
        ConfirmDialogFragment confirm = ConfirmDialogFragment.newInstance("Confirm adding ?");

        confirm.setNoticeDialogListener((confirmDialog) -> {
            if (tabLayout.getSelectedTabPosition() == 0)
            {
                //todo: need dispose
                categoryModel.setCategoryType(CategoryType.spending);
                earningViewModel.compositeDisposable.add(
                        spendingViewModel
                                .insert(categoryModel)
                                .andThen(spendingViewModel.fetchCategory())
                                .subscribe(categoryModels -> {
                                            Toast.makeText(requireContext(), "Adding successfully", Toast.LENGTH_LONG).show();
                                            spendingFragment.adapter.update(categoryModels);
                                        }
                                        , throwable -> {
                                            Log.d(TAG, "onError, insertTransact category: " + throwable.getMessage());
                                            Toast.makeText(requireContext(), "Adding failed", Toast.LENGTH_SHORT).show();
                                            if (throwable.getMessage().contains("UNIQUE"))
                                                Toast.makeText(requireContext(), "This category has existed, please try again", Toast.LENGTH_LONG).show();
                                            else
                                                Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        })
                );
            }
            else
            {
                categoryModel.setCategoryType(CategoryType.earning);
                earningViewModel.compositeDisposable.add(
                        earningViewModel
                                .insert(categoryModel)
                                .andThen(earningViewModel.fetchCategory())
                                .subscribe(categoryModels -> {
                                            Toast.makeText(requireContext(), "Adding successfully", Toast.LENGTH_LONG).show();
                                            earningFragment.adapter.update(categoryModels);
                                        }
                                        , throwable -> {
                                            Log.d(TAG, "onError, insertTransact category: " + throwable.getMessage());
                                            Toast.makeText(requireContext(), "Adding failed", Toast.LENGTH_SHORT).show();
                                            if (throwable.getMessage().contains("UNIQUE"))
                                                Toast.makeText(requireContext(), "This category has existed, please try again", Toast.LENGTH_LONG).show();
                                            else
                                                Toast.makeText(requireContext(), throwable.getMessage(), Toast.LENGTH_LONG).show();
                                        })
                );
            }
            confirmDialog.dismiss();
            dialog.dismiss();
        });
        confirm.show(getParentFragmentManager(), TAG);
    }

    @Override
    public void onResume() {
        super.onResume();
        activity.setToolBarHeaderText("Categories");
    }
}