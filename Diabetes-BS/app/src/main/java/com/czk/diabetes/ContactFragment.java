package com.czk.diabetes;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

/**
 * Created by 陈忠凯 on 2017/3/7.
 */
public class ContactFragment extends Fragment {
    private View fragment;

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        fragment = inflater.inflate(com.czk.diabetes.R.layout.fragment_contact, container, false);
        return fragment;
    }
}
