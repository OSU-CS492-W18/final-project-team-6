package com.example.me.war;

import android.os.Bundle;

public class RetainedFragment extends android.app.Fragment {

    // data object we want to retain
    private retaineData data;

    // this method is only called once for this fragment
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // retain this fragment
        setRetainInstance(true);
    }

    public void setData(retaineData data) {
        this.data = data;
    }

    public retaineData getData() {
        return data;
    }
}
