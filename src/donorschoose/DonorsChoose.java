package donorschoose;

/**
 * Main class for program.
 */
public class DonorsChoose
{
    public static void main(String[] args)
    {
        // TESTING COMMANDS
        /*
        DatabaseSync dbSync = new DatabaseSync();   // new dropbox sync object
        dbSync.connectServer();                     // use object to connect
        dbSync.downloadDB();                        // download .db file
        
        SQLite db = new SQLite();                   // new SQLite object to connect to database
        db.establishConnection();                   // connect to database
        db.executeQuery("SELECT * FROM charity");   // query
        //db.executeUpdate("INSERT INTO charity VALUES (3, 'new')");    // command
        db.executeQuery("SELECT * FROM charity");   // query
        db.closeConnection();                       // close connection to database
        
        dbSync.uploadDB();                          // upload .db file
        */
        // no need to disconnect form Dropbox. Automatic.
        
        GUI app = new GUI();                        // new graphical interface object
        app.launchApp();                            // user GUI object to launch graphical interface
    }
}
