package com.briz.developer.briztv;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * Класс фрагмента прогрессбара занятости активности авторизации
 *
 * @author Vladislav Deltsov, E-mail: seo.deltsov@gmail.com
 * @version 26/05/2015
 */
public class LoginBusyFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_busy, container, false);

        return rootView;
    }


}
