package com.example.personalfinance.fragment.transaction;

import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.personalfinance.MainActivity;
import com.example.personalfinance.R;
import com.github.barteksc.pdfviewer.PDFView;

import java.io.File;

public class PdfViewerFragment extends Fragment {
    private PDFView pdfView;
    private MainActivity activity;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        activity = (MainActivity) getActivity();
    }

    @RequiresApi(api = Build.VERSION_CODES.TIRAMISU)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_pdf_viewer, container, false);

        pdfView = v.findViewById(R.id.pdfView);

        getParentFragmentManager().setFragmentResultListener("file", this, (requestKey, result) -> {
            File file = result.getSerializable("payload", File.class);
            pdfView.fromFile(file).load();
        });
        return v;
    }

    @Override
    public void onStart() {
        super.onStart();
        //set tool bar
        activity.setToolBarReturnBtnVisibility(View.VISIBLE);
        activity.setToolBarHeaderText("PDF Viewer");
        activity.setToolBarMenuBtnVisibility(View.INVISIBLE);
        activity.configToolbarToReturn(v -> getParentFragmentManager().popBackStack());
    }
}