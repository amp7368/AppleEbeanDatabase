package apple.lib.ebean.database.config;

import io.ebean.datasource.DataSourceConfig;

public interface AppleEbeanDatabaseConfig {

    String getUrl();

    DataSourceConfig configure(DataSourceConfig dataSourceConfig);

    boolean shouldGenerateMigration();

    boolean shouldCreateDatabase();

    boolean shouldRunMigration();
}
