package com.thnki.queuebreaker.auth;

import com.thnki.queuebreaker.R;
import com.thnki.queuebreaker.generic.ConnectivityUtil;
import com.thnki.queuebreaker.generic.LocalRepository;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class AuthPresenterTest {

    @Mock
    private AuthContract.AuthActivityView activityView;

    @Mock
    private LocalRepository localRepository;

    @Mock
    private ConnectivityUtil connectivityUtil;

    private AuthPresenter mPresenter;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        mPresenter = new AuthPresenter(activityView, localRepository, connectivityUtil);
    }

    @Test
    public void testHandleClicksWithNoInternet() throws Exception {
        when(connectivityUtil.isConnected()).thenReturn(false);
        mPresenter.handleClicks(R.id.auth_dialog);
        verify(activityView).showSnack(R.string.no_internet);
    }

    @Test
    public void testHandleClicksWithInternet() throws Exception {
        when(connectivityUtil.isConnected()).thenReturn(true);
        mPresenter.handleClicks(R.id.auth_dialog);
        verify(activityView).launchLoginDialog();
    }

}