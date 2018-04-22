package com.thnki.queuebreaker.auth;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class AuthSectionsPagerAdapter extends FragmentPagerAdapter {
    public AuthSectionsPagerAdapter(FragmentManager fragmentManager) {
        super(fragmentManager);
    }

    @Override
    public Fragment getItem(int position) {
        return AuthWalkThroughFragment.newInstance(position + 1);
    }

    @Override
    public int getCount() {
        return 4;
    }

}
