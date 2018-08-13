package com.company.jersey04;

import com.company.jersey04.services.CharityService;
import com.company.jersey04.services.DonorService;
import com.google.inject.servlet.ServletModule;

public class MainModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(CharityService.class);
        bind(DonorService.class);
    }
}