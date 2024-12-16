package com.example.personalfinance.fragment.setting;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Environment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.example.personalfinance.error.MessageCode;
import com.example.personalfinance.fragment.dialog.ConfirmDialogFragment;
import com.example.personalfinance.fragment.setting.adapter.ReportRecyclerViewAdapter;
import com.example.personalfinance.fragment.transaction.PdfViewerFragment;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ReportFragment extends Fragment{
    private static final String TAG = "kiev";
    private RecyclerView recyclerView;
    private ReportRecyclerViewAdapter adapter;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_report, container, false);
        try {
            init(v);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.VISIBLE);
        activity.setToolBarHeaderText("Report");
        activity.setToolBarMenuBtnVisibility(View.INVISIBLE);
        activity.configToolbarToReturn(v -> getParentFragmentManager().popBackStack());
    }

    private MainActivity activity;
    private File directory;

    private void init(View v) throws IOException {
        activity = (MainActivity)getActivity();

        adapter = new ReportRecyclerViewAdapter();
        adapter.setItemOnClickListener(file -> {
            Bundle bundle = new Bundle();
            bundle.putSerializable("payload", file);
            getParentFragmentManager().setFragmentResult("file", bundle);

            activity.replaceFragment(new PdfViewerFragment(), true, null);
        });
        adapter.setItemOnLongClickListener(file -> {
            ConfirmDialogFragment dialog = ConfirmDialogFragment.newInstance("Do you want to delete this file ?");
            dialog.setNoticeDialogListener(dialog1 -> {
                //delete file
                if (file.exists() && file.delete())
                    Toast.makeText(activity, MessageCode.success_deletion, Toast.LENGTH_SHORT).show();
                else
                    Toast.makeText(activity, MessageCode.fail_deletion, Toast.LENGTH_SHORT).show();
                updateFileList();
            });
            dialog.show(getParentFragmentManager(), null);
        });

        recyclerView = v.findViewById(R.id.recycler_view_files);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(adapter);

        //get permission
        if (ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED
                ||ContextCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }

        // Check if external storage is available
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            //get file list
            directory = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), getString(R.string.app_name));
            if (!directory.exists()){
                if (directory.mkdirs()) Log.d(TAG, "create dir success");
                else Log.d(TAG, "create dir failed");
            }
            else updateFileList();
        }
    }

    private void updateFileList(){
        List<File> files = Arrays.stream(directory.listFiles()).collect(Collectors.toList());
        Log.d(TAG, "file list: " + files);
        adapter.update(files);
    }
}