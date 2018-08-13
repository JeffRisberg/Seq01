package com.company.seq01;

import com.company.seq01.services.JobExecutionService;
import com.company.seq01.services.JobService;
import com.google.inject.servlet.ServletModule;

public class MainModule extends ServletModule {

    @Override
    protected void configureServlets() {
        bind(JobService.class);
        bind(JobExecutionService.class);
    }
}