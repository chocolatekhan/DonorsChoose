package donorschoose;

import com.dropbox.core.DbxRequestConfig;
import com.dropbox.core.v2.DbxClientV2;
import com.dropbox.core.v2.files.FileMetadata;
import com.dropbox.core.v2.files.WriteMode;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Class for connection to Dropbox Server via Dropbox API.
 * Used to sync .db file.
 * @author Alvi Khan (180041229)
 */
public class DatabaseSync
{
    private static final String ACCESS_TOKEN = "So9iwkJ6VG4AAAAAAAAAATza_tSIz_zOisJItrHIfidDPOoCN0RH8D7aRybw_3cG";
    private DbxClientV2 client;
    
    /**
     * Connect to Dropbox Server.
     */
    public void connectServer()
    {
        try
        {
            DbxRequestConfig config = DbxRequestConfig.newBuilder("dropbox/java-tutorial").build();
            client = new DbxClientV2(config, ACCESS_TOKEN);
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Download .db file.
     */
    public void downloadDB()
    {
        try
        {
            String localPath = "main.db";   // location of .db file
            OutputStream outputStream = new FileOutputStream(localPath);    // output stream to write to .db file
            FileMetadata metadata = client.files()
                    .downloadBuilder("/main.db")
                    .download(outputStream);    // read file from Dropbox
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
    
    /**
     * Upload .db file.
     */
    public void uploadDB()
    {
        try
        {
            String localPath = "main.db";   // location of .db file
            InputStream input = new FileInputStream(localPath);     // input stream to read from .db file
            FileMetadata file = client.files().uploadBuilder("/main.db")
                    .withMode(WriteMode.OVERWRITE)
                    .uploadAndFinish(input);    // write file to Dropbox
        }
        catch (Exception e)
        {
            System.out.println(e);
        }
    }
}
