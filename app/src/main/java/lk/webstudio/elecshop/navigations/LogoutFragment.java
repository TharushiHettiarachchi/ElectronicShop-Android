package lk.webstudio.elecshop.navigations;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import lk.webstudio.elecshop.MainActivity;
import lk.webstudio.elecshop.R;


public class LogoutFragment extends Fragment {


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        try {
            SharedPreferences sharedPreferences = getContext().getSharedPreferences("lk.webstudio.elecshop.userlist", Context.MODE_PRIVATE);
            SharedPreferences.Editor editor = sharedPreferences.edit();
            editor.clear();
            editor.apply();
            Intent i = new Intent(requireContext(), MainActivity.class);
            startActivity(i);
            Toast.makeText(getContext(),"Logged Out Sucessfully",Toast.LENGTH_LONG).show();

        } catch (Exception e) {
            Log.e("ElecLog", String.valueOf(e));
        }


        return inflater.inflate(R.layout.fragment_logout, container, false);
    }
}