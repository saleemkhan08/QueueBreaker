package com.thnki.queuebreaker.auth;

import android.graphics.Typeface;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.utils.SquareImageView;

import butterknife.BindView;
import butterknife.ButterKnife;

import static com.thnki.queuebreaker.auth.AuthActivity.LOGIN_STATUS;

public class AuthWalkThroughFragment extends Fragment {
    private static final String WALK_THROUGH_PAGE_NUMBER = "section_number";

    @BindView(R.id.walk_through_image)
    SquareImageView walkThroughImageView;

    @BindView(R.id.title)
    TextView titleView;

    @BindView(R.id.walk_through_description)
    TextView descriptionView;

    @BindView(R.id.loginContainer)
    FrameLayout loginContainerView;

    public AuthWalkThroughFragment() {
    }

    public static AuthWalkThroughFragment newInstance(int sectionNumber) {
        AuthWalkThroughFragment fragment = new AuthWalkThroughFragment();
        Bundle args = new Bundle();
        args.putInt(WALK_THROUGH_PAGE_NUMBER, sectionNumber);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_login, container, false);
        ButterKnife.bind(this, rootView);
        Typeface typeface = Typeface.createFromAsset(getActivity().getAssets(), "Gabriola.ttf");
        descriptionView.setTypeface(typeface);
        descriptionView.setVisibility(View.VISIBLE);
        walkThroughImageView.setVisibility(View.VISIBLE);

        titleView.setTypeface(typeface);
        titleView.setVisibility(View.INVISIBLE);
        loginContainerView.setVisibility(View.INVISIBLE);

        View pageIndicatorView = rootView.findViewById(R.id.pageIndicatorViewContainer);
        pageIndicatorView.setVisibility(View.INVISIBLE);


        switch (getArguments().getInt(WALK_THROUGH_PAGE_NUMBER)) {
            case 1:
                walkThroughImageView.setImageResource(R.mipmap.scan);
                descriptionView.setText(R.string.scan);
                break;
            case 2:
                walkThroughImageView.setImageResource(R.mipmap.order);
                descriptionView.setText(R.string.order);
                break;
            case 3:
                walkThroughImageView.setImageResource(R.mipmap.payment);
                descriptionView.setText(R.string.pay);
                break;
            case 4:
                walkThroughImageView.setImageResource(R.mipmap.feedback);
                descriptionView.setText(R.string.feedback);
                break;
        }

        if (PreferenceManager.getDefaultSharedPreferences(getActivity()).getBoolean(LOGIN_STATUS, false)) {
            Log.d("TextVisibilityIssue", "Hiding");
            descriptionView.setVisibility(View.GONE);
        }
        return rootView;
    }
}
