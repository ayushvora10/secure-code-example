package com.il.securecode;

import io.dropwizard.Configuration;
import com.fasterxml.jackson.annotation.JsonProperty;

public class SecureCodeAppConfig extends Configuration {
    @JsonProperty
    public String ig;
}

