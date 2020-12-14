package donorschoose;
import java.sql.*;

/**
 * Class to connect to database.
 * @author Alvi Khan (180041229)
 */
public class SQLite
{
    private Connection conn = null;
    
    /**
     * Connect to database.
     */
    public void establishConnection()
    {
        try
        {
            Class.forName("org.sqlite.JDBC");                           // database type
            conn = DriverManager.getConnection("jdbc:sqlite:main.db");  // .db file to connect to
            //System.out.println("SQLite DB Connected!");               // testing
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Close connection to database. IMPORTANT.
     */
    public void closeConnection()
    {
        try
        {
            if (conn != null)   conn.close();       // if the connection is not already closed, close it
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Execute queries (not insertions).
     * @param str query to execute
     */
    public void executeQuery (String str)
    {
        try
        {
            Statement stmt = conn.createStatement();    // create a statement object to perform search
            ResultSet results = stmt.executeQuery(str); // retrieve results and store
        
            while (results.next())
            {
                System.out.println(results.getInt(1) + " " + results.getString(2)); // print out results one by one
            }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    // TODO the function above needs to be changed. We actually have to use prepared statements which we will pass to a private function to execute.
    
    /**
     * Execute insertions and updates (not searches).
     * @param str SQLite command
     */
    public void executeUpdate (String str)
    {
        try
        {
            Statement stmt = conn.createStatement();    // create a statement object to perform insertion
            stmt.executeUpdate(str);                    // insertion
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    public int addNewUser(String username, String password)
    {
        String str = "INSERT INTO donor(username, password) VALUES (?, ?)";
        establishConnection();
        
        try
        {
            PreparedStatement pstmt = conn.prepareStatement(str);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            pstmt.executeUpdate();
            closeConnection();
            return 1;
        }
        catch (Exception e)
        {
            System.out.println(e);
            closeConnection();
            return 0;
        }
    }
    
    public int searchUser(String username, String password)
    {
        establishConnection();
        
        try
        {
            String query = "SELECT (count(*) > 0) as found FROM donor WHERE username = ? AND password = ?";
            PreparedStatement pstmt = conn.prepareStatement(query);
            pstmt.setString(1, username);
            pstmt.setString(2, password);
            
            try (ResultSet rs = pstmt.executeQuery()) {
            if (rs.next()) {
                boolean found = rs.getBoolean(1); // "found" column
                if (found) {
                    System.out.println("YES");
                    return 1;
                } else {
                    System.out.println("NO");
                    return 0;
                }
            }
        }
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
        
        closeConnection();
        return 0;
    }
}
