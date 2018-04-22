package com.thnki.queuebreaker.auth;

import com.thnki.queuebreaker.generic.AppComponent;

import dagger.Component;

@AuthScope
@Component(dependencies = {AppComponent.class}, modules = {AuthModule.class})
public interface AuthComponent {

    void inject(AuthActivity activity);

}
