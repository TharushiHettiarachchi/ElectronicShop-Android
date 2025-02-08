package lk.webstudio.elecshop.navigations;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import lk.webstudio.elecshop.R;


public class PurchasedItemFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_purchased_item, container, false);



        return rootView ;
    }
}