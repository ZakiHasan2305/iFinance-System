package se2203b.assignments.ifinance;

import java.sql.*;
import java.util.*;


public class AccountGroupsAdapter {
    static Connection connection;
    public AccountGroupsAdapter(Connection conn, Boolean rst) throws SQLException {
        connection = conn;

        if (rst) {
            Statement stmt = connection.createStatement();
            try {
                // Remove tables if database tables have been created.
                // This will throw an exception if the tables do not exist
                stmt.execute("DROP TABLE AccountGroups");
            } catch (SQLException ex) {
                // No need to report an error.
                // The table simply did not exist.
            } finally {
                // Create the table
                stmt.execute("CREATE TABLE AccountGroups ("
                        + "ID INT,"
                        + "GroupName VARCHAR(64),"
                        + "Parent INT,"
                        + "Element VARCHAR(64)"
                        + ")");
                populateAccountGroups();
            }
        }
    }

    public static void createGroup(String grpName, int par, String elem) throws SQLException{
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("INSERT INTO AccountGroups(ID, GroupName, Parent, Element) " +
                "VALUES (" + getMax() + ", '"+grpName+"', "+par+", '"+elem+"')");
    }

    public static void updateGroup(String oName, String nName) throws SQLException {
        Statement myStmt = connection.createStatement();
        myStmt.executeUpdate("UPDATE AccountGroups "
                + "SET GroupName = '" + nName + "' "
                + "WHERE GroupName = '" + oName + "'");
    }

    public static int getMax() throws SQLException {
        int maxVal = 0;
        Statement stmt = connection.createStatement();
        ResultSet rs = stmt.executeQuery("SELECT MAX(ID) FROM AccountGroups");
        if (rs.next()) maxVal = rs.getInt(1);
        return maxVal+1;
    }


    public static void deleteGroup(int iD) throws SQLException {
        Statement stmt = connection.createStatement();
        stmt.executeUpdate("DELETE FROM AccountGroups " +
                "WHERE ID = "+ iD +"");
    }

    public static ArrayList<Group> GroupsList() throws SQLException{
        ArrayList<Group> listOfGrps = new ArrayList<>();
        ResultSet resultSet;
        Statement stmt = connection.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups";
        resultSet = stmt.executeQuery(sqlStatement);


        while(resultSet.next()){
            listOfGrps.add(new Group(resultSet.getInt(1), resultSet.getString(2),
                    getGroup(resultSet.getInt(1)).getParent(),
                    AccountCategoryAdapter.getAccCategory(resultSet.getString(4))));
        }
        return listOfGrps;
    }

    public static int getID(String grpName) throws SQLException{
        int groupID = 0;
        ResultSet resultSet;
        Statement stmt = connection.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE GroupName = '"+grpName+"'";
        resultSet = stmt.executeQuery(sqlStatement);
        while(resultSet.next()){
            groupID = resultSet.getInt(1);
        }
        return groupID;
    }

    public static String getElement(String groupName) throws SQLException{
        ResultSet resSet;
        Statement stmt = connection.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE GroupName = '"+groupName+"'";
        resSet = stmt.executeQuery(sqlStatement);
        String groupElement = null;
        while(resSet.next()){
            groupElement = resSet.getString(4);
        }
        return groupElement;
    }

    public static Group getGroup(int id) throws SQLException{
        ResultSet resultSet;
        Statement stmt = connection.createStatement();
        String sqlStatement = "SELECT * FROM AccountGroups WHERE ID = "+id+"";
        resultSet = stmt.executeQuery(sqlStatement);
        Group groupp = null;


        while(resultSet.next()){
            if (resultSet.getInt(3) != 0){
                int id1 = resultSet.getInt(3);
                ResultSet resultSet1;
                Statement stmt1 = connection.createStatement();
                String sqlStatement1 = "SELECT * FROM AccountGroups WHERE ID = "+id1+"";
                resultSet1 = stmt1.executeQuery(sqlStatement1);
                while(resultSet1.next()){
                    Group grp = new Group(resultSet.getInt(3), resultSet1.getString(2),null,null );
                    groupp = new Group(id, resultSet.getString(2), grp,null);
                }

            }

            else{
                groupp = new Group(id, resultSet.getString(2), null,null);
            }

        }
        return groupp;
    }

    public static void populateAccountGroups() throws SQLException {
        Statement stmt = connection.createStatement();

        List<Object[]> myList = new ArrayList<>();
        myList.add(new Object[]{1,"Fixed assets",0, "Assets"});
        myList.add(new Object[]{2,"Investments",0, "Assets"});
        myList.add(new Object[]{3,"Branch/divisions",0, "Assets"});
        myList.add(new Object[]{4,"Cash in hand",0, "Assets"});
        myList.add(new Object[]{5,"Bank accounts",0, "Assets"});
        myList.add(new Object[]{6,"Deposits (assets)",0, "Assets"});
        myList.add(new Object[]{7,"Advances (assets)",0, "Assets"});
        myList.add(new Object[]{8,"Capital account",0, "Liabilities"});
        myList.add(new Object[]{9,"Long term loans",0, "Liabilities"});
        myList.add(new Object[]{10,"Current liabilities",0, "Liabilities"});
        myList.add(new Object[]{11,"Reserves and surplus",0, "Liabilities"});
        myList.add(new Object[]{12,"Sales account",0, "Income"});
        myList.add(new Object[]{13,"Purchase account",0, "Expenses"});
        myList.add(new Object[]{14,"Expenses (direct)",0, "Expenses"});
        myList.add(new Object[]{15,"Expenses (indirect)",0, "Expenses"});
        myList.add(new Object[]{16,"Secured loans",9, "Liabilities"});
        myList.add(new Object[]{17,"Unsecured loans",9, "Liabilities"});
        myList.add(new Object[]{18,"Duties taxes payable",10, "Liabilities"});
        myList.add(new Object[]{19,"Provisions",10, "Liabilities"});
        myList.add(new Object[]{20,"Sundry creditors",10, "Liabilities"});
        myList.add(new Object[]{21,"Bank od & limits",10, "Liabilities"});

        for (Object[] e : myList) {
            stmt.executeUpdate(String.format("INSERT INTO AccountGroups VALUES(%d,'%s',%d,'%s')",(int)e[0],e[1],(int)e[2],e[3]));
        }

    }
}