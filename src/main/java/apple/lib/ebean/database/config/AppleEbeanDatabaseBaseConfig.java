package apple.lib.ebean.database.config;

public abstract class AppleEbeanDatabaseBaseConfig implements AppleEbeanDatabaseConfig {

    protected boolean shouldGenerateMigration = false;
    protected boolean shouldCreateDatabase = false;
    protected boolean shouldRunMigration = false;

    @Override
    public boolean shouldGenerateMigration() {
        return this.shouldGenerateMigration;
    }

    @Override
    public boolean shouldCreateDatabase() {
        return shouldCreateDatabase;
    }

    @Override
    public boolean shouldRunMigration() {
        return shouldRunMigration;
    }
}
