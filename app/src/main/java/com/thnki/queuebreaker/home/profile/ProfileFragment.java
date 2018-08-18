package com.thnki.queuebreaker.home.profile;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.auth.AuthActivity;
import com.thnki.queuebreaker.utils.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;
import butterknife.Unbinder;

public class ProfileFragment extends Fragment {

    private Unbinder unbinder;

    public static ProfileFragment getInstance() {
        return new ProfileFragment();
    }

    @BindView(R.id.fullname)
    TextView fullname;

    @BindView(R.id.profile_image)
    SquareImageView profileImage;

    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View parentView = inflater.inflate(R.layout.profile_fragment, container, false);
        unbinder = ButterKnife.bind(this, parentView);
        fullname.setText(AuthActivity.currentUser.getFullName());
        profileImage.setImageURI(AuthActivity.currentUser.getPhotoUrl() + "?sz=300");
        return parentView;
    }

    @OnClick(R.id.logout_btn)
    public void logout() {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(getActivity(), AuthActivity.class));
        getActivity().finish();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        unbinder.unbind();
    }
}
