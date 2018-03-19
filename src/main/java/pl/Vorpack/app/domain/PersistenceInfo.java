package pl.Vorpack.app.domain;

import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

/**
 * Created by Paweł on 2018-03-19.
 */
public class PersistenceInfo {

    private static String databaseUrl = System.getenv("DATABASE_URL");
    private static StringTokenizer st = new StringTokenizer(databaseUrl, ":@/");
    private static String dbVendor = st.nextToken(); //if DATABASE_URL is set
    private static String userName = st.nextToken();
    private static  String password = st.nextToken();
    private static String host = st.nextToken();
    private static String port = st.nextToken();
    private static String databaseName = st.nextToken();
    private static String jdbcUrl = String.format("jdbc:postgresql://%s:%s/%s?ssl=true&sslfactory=org.postgresql.ssl.NonValidatingFactory", host, port, databaseName);
    private static Map<String, String> properties = new HashMap<String, String>();

    public static String getDatabaseUrl() {
        return databaseUrl;
    }

    public static void setDatabaseUrl(String databaseUrl) {
        PersistenceInfo.databaseUrl = databaseUrl;
    }

    public static StringTokenizer getSt() {
        return st;
    }

    public static void setSt(StringTokenizer st) {
        PersistenceInfo.st = st;
    }

    public static String getDbVendor() {
        return dbVendor;
    }

    public static void setDbVendor(String dbVendor) {
        PersistenceInfo.dbVendor = dbVendor;
    }

    public static String getUserName() {
        return userName;
    }

    public static void setUserName(String userName) {
        PersistenceInfo.userName = userName;
    }

    public static String getPassword() {
        return password;
    }

    public static void setPassword(String password) {
        PersistenceInfo.password = password;
    }

    public static String getHost() {
        return host;
    }

    public static void setHost(String host) {
        PersistenceInfo.host = host;
    }

    public static String getPort() {
        return port;
    }

    public static void setPort(String port) {
        PersistenceInfo.port = port;
    }

    public static String getDatabaseName() {
        return databaseName;
    }

    public static void setDatabaseName(String databaseName) {
        PersistenceInfo.databaseName = databaseName;
    }

    public static String getJdbcUrl() {
        return jdbcUrl;
    }

    public static void setJdbcUrl(String jdbcUrl) {
        PersistenceInfo.jdbcUrl = jdbcUrl;
    }

    public static Map<String, String> getProperties() {
        return properties;
    }

    public static void setProperties(Map<String, String> properties) {
        PersistenceInfo.properties = properties;
    }

    public static Map<String, String>  putTokens(){

        properties.put("javax.persistence.jdbc.url", databaseUrl );
        properties.put("javax.persistence.jdbc.user", userName );
        properties.put("javax.persistence.jdbc.password", password );
        properties.put("javax.persistence.jdbc.driver", "org.postgresql.Driver");
        properties.put("hibernate.dialect", "org.hibernate.dialect.PostgreSQL95Dialect");

        return properties;
    }
}
