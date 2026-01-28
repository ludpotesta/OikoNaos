package util;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.sql.DataSource;
import java.sql.Connection;

public class database {

    private static DataSource dataSource;

    public static Connection getConnection() throws Exception {


        // In un TEST questo IF non verrà mai eseguito (grazie al Mock).
        // Sul SITO verrà eseguito la prima volta e funzionerà come prima.
        if (dataSource == null) {
            Context ctx = new InitialContext();
            dataSource = (DataSource) ctx.lookup("java:comp/env/jdbc/OikoNaosDB");
        }
        return dataSource.getConnection();
    }
}

