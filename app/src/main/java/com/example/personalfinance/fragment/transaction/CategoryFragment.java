package com.example.personalfinance.fragment.transaction;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.viewpager2.widget.ViewPager2;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

public class CategoryFragment extends Fragment implements View.OnClickListener{
    private static final String TAG = "CategoryFragment";
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    private CategoryViewPagerAdapter categoryViewPagerAdapter;
    private MainActivity activity;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_category, container, false);
        init(v);
        return v;
    }

    private void init(View v){
        activity = (MainActivity) getActivity();
        categoryViewPagerAdapter = new CategoryViewPagerAdapter(this);
        categoryViewPagerAdapter.addFragment(new CategoryEarningFragment());
        categoryViewPagerAdapter.addFragment(new CategorySpendingFragment());
        viewPager = (ViewPager2)v.findViewById(R.id.viewPager);
        viewPager.setAdapter(categoryViewPagerAdapter);

        tabLayout = (TabLayout)v.findViewById(R.id.category_tabLayout);
        new TabLayoutMediator(tabLayout, viewPager, (tab, position) -> tab.setText((position == 0) ? "earning" : "spending")).attach();

        ((Button)v.findViewById(R.id.new_category_btn)).setOnClickListener(this);
        ((Button)v.findViewById(R.id.save_category_btn)).setOnClickListener(this);
    }

    @Override
    public void onResume() {
        activity.setToolBarHeaderText(activity, "Categories");
        super.onResume();
    }

    @Override
    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.save_category_btn){}
        else if (id == R.id.new_category_btn){
            new CategoryNewDialogFragment().show(getParentFragmentManager(), TAG);
        }
    }
}