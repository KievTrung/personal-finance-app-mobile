package com.example.personalfinance.fragment.notify;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.datalayer.local.entity.Notify;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.notify.adapter.NotifyRecyclerViewAdapter;
import com.example.personalfinance.fragment.notify.viewmodel.NotifyViewModel;

public class NotificationFragment extends Fragment {
    private static final String TAG = "kiev";
    private RecyclerView recyclerView;
    private NotifyRecyclerViewAdapter adapter;
    private NotifyViewModel viewModel;
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity)getActivity();

        //init view model
        viewModel = new ViewModelProvider(this).get(NotifyViewModel.class);
        //init adapter
        adapter = new NotifyRecyclerViewAdapter();
        adapter.setItemOnClickListener(position -> {
            Notify notify = viewModel.getNotifies().get(position);
            String[] arr = notify.getHeader().split("Budget ");
            notify.setRead(true);
            viewModel.compositeDisposable.add(
                    viewModel
                            .hasRead(notify.getNotify_id())
                            .andThen(viewModel.getBudgetId(arr[1].trim()))
                            .subscribe(budgetId -> {
                                Intent intent = new Intent(getContext(), MainActivity.class);
                                intent.putExtra("budgetid", budgetId);
                                startActivity(intent);
                            },throwable -> {
                                Toast.makeText(getContext(), MessageCode.budget_non_existed, Toast.LENGTH_LONG).show();
                            })
            );
        });
        adapter.setItemOnLongClickListener(position -> {
            //delete notification
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this notification ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                viewModel.compositeDisposable.add(
                        viewModel
                                .delete(viewModel.getNotifies().get(position))
                                .andThen(viewModel.getAll())
                                .subscribe(notifies -> {
                                    Toast.makeText(requireContext(), MessageCode.success_deletion, Toast.LENGTH_LONG).show();
                                    adapter.update(notifies);
                                })
                );
            });
            dialog.show(getParentFragmentManager(), TAG);
        });

        //fetch local
        viewModel.compositeDisposable.add(
                viewModel.getAll().subscribe(notifies -> adapter.update(notifies))
        );
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_notification, container, false);
        init(v);
        return v;
    }

    private void init(View v) {
        //init recycler view
        recyclerView = v.findViewById(R.id.recycler_view_notify);
        recyclerView.setLayoutManager(new LinearLayoutManager(requireContext()));
        recyclerView.setAdapter(adapter);
    }

    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.INVISIBLE);
        activity.setToolBarHeaderText("Notification");
        activity.setToolBarMenuBtnVisibility(View.VISIBLE);
    }
}