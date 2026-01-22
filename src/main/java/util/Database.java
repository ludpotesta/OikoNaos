package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

/* Questa classe serve a centralizzare la gestione delle connessioni al DB */
public class Database {

    private static DataSource dataSource;

    static {
        try {
            /* Qui viene preso il pool delle connessioni di tomcat */
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/OikoNaosDB");
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Connection getConnection() throws Exception {
        return dataSource.getConnection(); /* getConnection occupa e poi libera una connessione dal pool estratto in precedenza */
    }
}

