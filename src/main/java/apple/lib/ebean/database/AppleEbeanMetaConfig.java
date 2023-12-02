package apple.lib.ebean.database;

import java.io.File;
import org.apache.logging.log4j.Logger;

public class AppleEbeanMetaConfig {

    private static Class<?> mainClass;
    private static Logger logger;
    private static File rootFolder;

    public static void logInfo(String msg) {
        if (logger == null) System.out.println(msg);
        else logger.info(msg);
    }

    private static void logError(String msg) {
        if (logger == null) System.err.println(msg);
        else logger.error(msg);
    }

    public static void configureMeta(Class<?> mainClass, File rootFolder, Logger logger) {
        setMainClass(mainClass);
        setRootFolder(rootFolder);
        setLogger(logger);
    }

    public static void setLogger(Logger logger) {
        AppleEbeanMetaConfig.logger = logger;
    }

    public static Class<?> getMainClass() {
        if (mainClass == null) logError("'mainClass' not set in " + className());
        return mainClass;
    }

    public static void setMainClass(Class<?> mainClass) {
        AppleEbeanMetaConfig.mainClass = mainClass;
    }

    public static File getRootFolder() {
        if (rootFolder == null) {
            logError("'rootFolder' not set in " + className());
            throw new IllegalStateException("Set 'rootFolder' in " + className());
        }
        return rootFolder;
    }

    public static void setRootFolder(File rootFolder) {
        AppleEbeanMetaConfig.rootFolder = rootFolder;
    }

    private static String className() {
        return AppleEbeanMetaConfig.class.getCanonicalName();
    }
}
