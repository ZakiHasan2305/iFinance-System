package se2203b.assignments.ifinance;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

public class AccountCategoryAdapter {

    static Connection connection;
    public AccountCategoryAdapter(Connection conn, Boolean reset) throws SQLException {
        connection = conn;

        Statement stmt = connection.createStatement();
        if (reset) {
            try {
                // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                stmt.execute("DROP TABLE AccountCategory");
            } catch (SQLException ex) {
                // No need to report an error.
                // The table simply did not exist.
            }
        }

        try {
            // Create the table
            stmt.execute("CREATE TABLE AccountCategory ("
                    + "AccountName VARCHAR(64),"
                    + "AccountType VARCHAR(64)"
                    + ")");
        } catch (SQLException ex) {
            // No need to report an error.
            // The table exists and may have some data.
        }

        try {
            fillAccCategory();
        } catch (SQLException ex) {
            // No need to report an error.
            // The table exists and may have some data.
        }
    }

    public static AccountCategory getAccCategory(String name) throws SQLException{
        ResultSet resultSet;
        Statement stmt = connection.createStatement();
        String sqlStatement = "SELECT * FROM AccountCategory WHERE AccountName = '"+name+"'";
        resultSet = stmt.executeQuery(sqlStatement);
        AccountCategory accountCategory = null;

        while(resultSet.next()){
            accountCategory = new AccountCategory(name, resultSet.getString(2));
        }
        return accountCategory;
    }

    public static void fillAccCategory() throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate(String.format("INSERT INTO AccountCategory VALUES('%s','%s')", "Assets", "Debit"));
        stmt.executeUpdate(String.format("INSERT INTO AccountCategory VALUES('%s','%s')", "Liabilities", "Credit"));
        stmt.executeUpdate(String.format("INSERT INTO AccountCategory VALUES('%s','%s')", "Income", "Credit"));
        stmt.executeUpdate(String.format("INSERT INTO AccountCategory VALUES('%s','%s')", "Expenses", "Debit"));
    }
}
