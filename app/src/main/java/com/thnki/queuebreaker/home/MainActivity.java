package com.thnki.queuebreaker.home;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.v4.view.ViewPager;
import android.view.MenuItem;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.home.explore.ExploreFragment;
import com.thnki.queuebreaker.home.profile.ProfileFragment;
import com.thnki.queuebreaker.home.scan.ScannerFragment;
import com.thnki.queuebreaker.utils.BaseActivity;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends BaseActivity implements
        BottomNavigationView.OnNavigationItemSelectedListener, ViewPager.OnPageChangeListener {

    @BindView(R.id.main_view_pager)
    ViewPager mainViewPager;

    @BindView(R.id.navigation)
    BottomNavigationView bottomNavigationView;

    private MenuItem prevMenuItem;

    @Override
    protected void onPostCreateView(Bundle savedInstanceState) {
        ButterKnife.bind(this);
        bottomNavigationView.setOnNavigationItemSelectedListener(this);
        setupMainViewPager();
        configureDependencies();
    }

    @Override
    protected int getContentView() {
        return R.layout.activity_main;
    }

    private void configureDependencies() {

    }

    private void setupMainViewPager() {

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());

        ExploreFragment exploreFragment = ExploreFragment.getInstance();
        ScannerFragment scannerFragment = ScannerFragment.getInstance();
        ProfileFragment profileFragment = ProfileFragment.getInstance();

        adapter.addFragment(exploreFragment);
        adapter.addFragment(scannerFragment);
        adapter.addFragment(profileFragment);

        mainViewPager.setAdapter(adapter);
        mainViewPager.addOnPageChangeListener(this);
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.navigation_explore:
                mainViewPager.setCurrentItem(0);
                break;
            case R.id.navigation_scan:
                mainViewPager.setCurrentItem(1);
                break;
            case R.id.navigation_profile:
                mainViewPager.setCurrentItem(2);
                break;
        }
        return false;
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        if (prevMenuItem != null) {
            prevMenuItem.setChecked(false);
        } else {
            bottomNavigationView.getMenu().getItem(0).setChecked(false);
        }
        bottomNavigationView.getMenu().getItem(position).setChecked(true);
        prevMenuItem = bottomNavigationView.getMenu().getItem(position);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

}
