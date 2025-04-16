package com.sellular.commons.core.filter;

import jakarta.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@NameBinding
@Retention(value = RetentionPolicy.RUNTIME)
public @interface AccessTokenRequired { }
