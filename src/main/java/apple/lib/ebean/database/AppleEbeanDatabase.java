package apple.lib.ebean.database;

import apple.lib.ebean.database.config.AppleEbeanDatabaseConfig;
import com.google.gson.Gson;
import io.ebean.Database;
import io.ebean.DatabaseFactory;
import io.ebean.config.DatabaseConfig;
import io.ebean.datasource.DataSourceConfig;
import io.ebean.dbmigration.DbMigration;
import io.ebean.migration.MigrationConfig;
import io.ebean.migration.MigrationRunner;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.lang.reflect.InvocationTargetException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public abstract class AppleEbeanDatabase {

    private static final List<AppleEbeanDatabase> databases = new ArrayList<>();
    private final Database db;

    public AppleEbeanDatabase() {
        DataSourceConfig dataSourceConfig = getConfig().configure(new DataSourceConfig());
        DatabaseConfig dbConfig = configureDatabase(dataSourceConfig);
        // We should use the classloader that loaded this plugin
        // because this plugin has our ebean dependencies
        ClassLoader originalClassLoader = Thread.currentThread().getContextClassLoader();
        ClassLoader pluginClassLoader = AppleEbeanDatabaseMetaConfig.getMainClass().getClassLoader();

        // Set the current thread's contextClassLoader to the classLoader with the ebean dependencies
        // This allows the class to initialize itself with access to the required class dependencies
        Thread.currentThread().setContextClassLoader(pluginClassLoader);

        // create the DatabaseFactory with the classloader containing ebean dependencies
        this.db = DatabaseFactory.createWithContextClassLoader(dbConfig, pluginClassLoader);

        if (getConfig().shouldGenerateMigration())
            generateMigration(dbConfig);
        dbConfig.setDdlRun(getConfig().shouldCreateDatabase());
        dbConfig.setRunMigration(getConfig().shouldRunMigration());

        // invoke the static initialization of every class that contains a querybean.
        // Note that any method in the class will initialize the class.
        for (Class<?> clazz : getQueryBeans()) {
            try {
                clazz.getConstructor().newInstance();
            } catch (InstantiationException | InvocationTargetException | IllegalAccessException | NoSuchMethodException e) {
                e.printStackTrace();
            }
        }
        // Restore the contextClassLoader to what it was originally
        Thread.currentThread().setContextClassLoader(originalClassLoader);

        AppleEbeanDatabaseMetaConfig.logInfo("Successfully created database");
    }

    private static void makeFile(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }
    }

    public Database getDB() {
        return this.db;
    }

    private DatabaseConfig configureDatabase(DataSourceConfig dataSourceConfig) {
        DatabaseConfig dbConfig = new DatabaseConfig();
        dbConfig.setDataSourceConfig(dataSourceConfig);
        dbConfig.setRunMigration(true);
        dbConfig.addAll(getEntities());
        dbConfig.setObjectMapper(this.getObjectMapper());
        dbConfig.setDefaultServer(isDefault());
        dbConfig.setName(this.getName());
        return dbConfig;
    }

    private void generateMigration(DatabaseConfig dbConfig) {
        DbMigration dbMigration = DbMigration.create();
        dbMigration.setServer(getDB());
        try {
            File file = migrationFile();
            dbMigration.setPathToResources(file.getPath());
            dbMigration.generateMigration();
            MigrationConfig config = new MigrationConfig();
            config.setName(getName());
            // I hate this
            String path = file.getPath();
            config.setMigrationPath(path);
            config.setMigrationInitPath(path);
            MigrationRunner runner = new MigrationRunner(config);
            runner.run(getDB().dataSource());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private File migrationFile() {
        File folder = AppleEbeanDatabaseMetaConfig.getRootFolder();
        File file = new File(folder, Path.of("db", getName()).toString());
        file.mkdirs();
        return file;
    }

    protected boolean isDefault() {
        return false;
    }

    protected Object getObjectMapper() {
        return null;
    }

    protected abstract List<Class<?>> getEntities();

    protected abstract Collection<Class<?>> getQueryBeans();

    protected abstract AppleEbeanDatabaseConfig getConfig();

    protected AppleEbeanDatabaseConfig makeConfig(File file, Class<? extends AppleEbeanDatabaseConfig> type) {
        AppleEbeanDatabaseConfig config;
        try (Reader reader = new BufferedReader(new FileReader(file))) {
            config = new Gson().fromJson(reader, type);
        } catch (IOException e) {
            config = null;
        }
        makeFile(file);
        try (Writer writer = new BufferedWriter(new FileWriter(file))) {
            if (config == null)
                config = type.getConstructor().newInstance();
            new Gson().toJson(config, writer);
            return config;
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException | IOException e) {
            throw new RuntimeException(e);
        }
    }

    protected abstract String getName();
}
