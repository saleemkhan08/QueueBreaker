package com.thnki.queuebreaker.home.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.auth.AuthActivity;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ProfileFragment extends Fragment {

    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.profile_fragment, container, false);
        ButterKnife.bind(this, parentView);
        return parentView;
    }

    @OnClick(R.id.logout_btn)
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), AuthActivity.class));
        getActivity().finish();
    }

}
