package apple.lib.ebean.database.config;

import io.ebean.datasource.DataSourceConfig;
import io.ebean.migration.DbPlatformNames;
import java.io.File;

public class AppleEbeanSqliteConfig extends AppleEbeanBaseConfig {

    private final transient File file;

    public AppleEbeanSqliteConfig(File file) {
        this.file = file;
    }

    public String getUrl() {
        return "jdbc:sqlite:" + file.getAbsolutePath();
    }

    @Override
    public DataSourceConfig configure(DataSourceConfig dataSourceConfig) {
        dataSourceConfig.setPlatform(DbPlatformNames.SQLITE);
        dataSourceConfig.setUrl(this.getUrl());
        dataSourceConfig.setUsername("root");
        dataSourceConfig.setPassword("");
        return dataSourceConfig;
    }
}
