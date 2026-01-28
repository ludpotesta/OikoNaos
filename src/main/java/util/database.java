package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class database {

    private static DataSource dataSource;

    public static Connection getConnection() throws Exception {
        // MODIFICA FONDAMENTALE:
        // Spostiamo la connessione qui dentro.
        // Se siamo in un TEST, questo IF non verrà mai eseguito (grazie al Mock).
        // Se siamo sul SITO VERO, verrà eseguito la prima volta e funzionerà come prima.
        if (dataSource == null) {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/OikoNaosDB");
        }
        return dataSource.getConnection();
    }
}

