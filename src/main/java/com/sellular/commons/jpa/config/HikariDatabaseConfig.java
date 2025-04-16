package com.sellular.commons.jpa.config;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class HikariDatabaseConfig {

    private String driverClass;

    private String url;

    private String user;

    private String password;

}
