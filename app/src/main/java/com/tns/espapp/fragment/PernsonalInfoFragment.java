package com.tns.espapp.fragment;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.tns.espapp.R;

/**
 * A simple {@link Fragment} subclass.
 */
public class PernsonalInfoFragment extends Fragment {


    public PernsonalInfoFragment() {
        // Required empty public constructor
    }

    public PernsonalInfoFragment getInstance(String name){
        PernsonalInfoFragment myFragment = new PernsonalInfoFragment();

        Bundle args = new Bundle();
        args.putString("personalinfo", name);
        myFragment.setArguments(args);

        return myFragment;

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_pernsonal_info, container, false);
    }

}
