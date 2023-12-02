package apple.lib.ebean.database.config;

import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.DbPlatformNames;

public class AppleEbeanMysqlConfig extends AppleEbeanBaseConfig {

    protected String username = "${username}";
    protected String password = "${password}";
    protected String database = "${database}";
    protected String port = "${port}";
    protected String host = "${host}";

    @Override
    public DataSourceConfig configure(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setUsername(getUsername());
        dataSourceConfig.setPassword(getPassword());
        dataSourceConfig.setUrl(getUrl());
        dataSourceConfig.setPlatform(DbPlatformNames.MYSQL);
        return dataSourceConfig;
    }

    @Override
    public String getUrl() {
        return "jdbc:mysql://" + this.host + ":" + this.port + "/" + this.database;
    }

    public String getDatabase() {
        return this.database;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }
}
