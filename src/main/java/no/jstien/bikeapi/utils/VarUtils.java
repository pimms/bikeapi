package no.jstien.bikeapi.utils;

public class VarUtils {
    public static String getEnv(String key) {
        String tmp = null;
        tmp = System.getProperty(key);
        if (tmp != null) {
            return tmp;
        }

        tmp = System.getenv(key);
        return tmp;
    }
}
