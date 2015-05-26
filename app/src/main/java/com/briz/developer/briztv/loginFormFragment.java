package com.briz.developer.briztv;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;


/**
 * Класс фрагмента формы авторизации IPTV
 * @author Vladislav Deltsov, E-mail: seo.deltsov@gmail.com
 * @version 26/05/2015
 */
public class loginFormFragment extends Fragment implements View.OnClickListener{

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "userLogin";
    private static final String ARG_PARAM2 = "userPassword";

    private String mUserLogin;
    private String mUserPassword;
    private View mView;

    Button eLoginBtn;
    EditText inUsername;
    EditText inPassword;
    LoginActivity rootActivity;


    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 UserLogin.
     * @param param2 UserPassword
     * @return A new instance of fragment loginFormFragment.
     */

    public static loginFormFragment newInstance(String param1, String param2) {
        loginFormFragment fragment = new loginFormFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public loginFormFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mUserLogin = getArguments().getString(ARG_PARAM1);
            mUserPassword = getArguments().getString(ARG_PARAM2);
        }


    }

    @Override
    public void onResume() {

        super.onResume();

        rootActivity = (LoginActivity) getActivity();


        eLoginBtn = (Button) mView.findViewById(R.id.btnOpenPlayList);
        inUsername = (EditText) mView.findViewById(R.id.fieldUsername);
        inPassword = (EditText) mView.findViewById(R.id.fieldPassword);

        eLoginBtn.setOnClickListener(this);

        inUsername.setText(mUserLogin);
        inPassword.setText(mUserPassword);
        inUsername.clearFocus();
        inPassword.clearFocus();


    }

    private void savePreferenseIfNessesary() {

            rootActivity.savePreferenses(inUsername.getText().toString(), inPassword.getText().toString());

        //}

    }


    @Override
    public void onClick(View v) {

        if (v == eLoginBtn) {

            this.savePreferenseIfNessesary();
            rootActivity.loginUser(true);
        }


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_login, container, false);

        mView = rootView;

        return rootView;
    }


}
