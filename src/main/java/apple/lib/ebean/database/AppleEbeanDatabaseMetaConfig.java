package apple.lib.ebean.database;

import java.io.File;
import java.util.function.Consumer;

public class AppleEbeanDatabaseMetaConfig {

    private static Class<?> mainClass;
    private static File rootFolder;
    private static Consumer<String> errorLogger;
    private static Consumer<String> infoLogger;

    public static void logInfo(String msg) {
        if (infoLogger == null) System.out.println(msg);
        else infoLogger.accept(msg);
    }

    private static void logError(String msg) {
        if (errorLogger == null) System.err.println(msg);
        else errorLogger.accept(msg);
    }

    public static void configureMeta(Class<?> mainClass, File rootFolder, Consumer<String> errorLogger, Consumer<String> infoLogger) {
        setMainClass(mainClass);
        setRootFolder(rootFolder);
        AppleEbeanDatabaseMetaConfig.errorLogger = errorLogger;
        AppleEbeanDatabaseMetaConfig.infoLogger = infoLogger;
    }


    public static Class<?> getMainClass() {
        if (mainClass == null) logError("'mainClass' not set in " + className());
        return mainClass;
    }

    public static void setMainClass(Class<?> mainClass) {
        AppleEbeanDatabaseMetaConfig.mainClass = mainClass;
    }

    public static File getRootFolder() {
        if (rootFolder == null) {
            logError("'rootFolder' not set in " + className());
            throw new IllegalStateException("Set 'rootFolder' in " + className());
        }
        return rootFolder;
    }

    public static void setRootFolder(File rootFolder) {
        AppleEbeanDatabaseMetaConfig.rootFolder = rootFolder;
    }

    private static String className() {
        return AppleEbeanDatabaseMetaConfig.class.getCanonicalName();
    }
}
