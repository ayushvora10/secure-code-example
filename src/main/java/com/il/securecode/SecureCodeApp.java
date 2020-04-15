package com.il.securecode;

import io.dropwizard.Application;
import io.dropwizard.setup.Bootstrap;
import io.dropwizard.setup.Environment;
import io.dropwizard.views.ViewBundle;
import io.dropwizard.assets.AssetsBundle;

public class SecureCodeApp extends Application<SecureCodeAppConfig> {
    public static void main(String[] args) throws Exception {
        new SecureCodeApp().run(args);
    }

    @Override
    public void initialize(Bootstrap<SecureCodeAppConfig> bootstrap) {
        bootstrap.addBundle(new ViewBundle<SecureCodeAppConfig>());
        bootstrap.addBundle(new AssetsBundle("/assets/", "/assets/"));
    }

    @Override
    public void run(SecureCodeAppConfig conf, Environment env) {
        env.jersey().register(new AppResource());
    }
}
