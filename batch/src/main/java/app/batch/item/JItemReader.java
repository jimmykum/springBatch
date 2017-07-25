package app.batch.item;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Paths;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;

public class JItemReader implements ItemReader<String> {
	
	private File[] files  ;
    private String folder;
    
    public JItemReader(String folder) {
		super();
		this.folder = folder;
		this.files = Paths.get(folder).toFile().listFiles();
	}
	public int  count=0;
     
     
    @Override
    public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
         System.out.println("reding....");
        if(count < files.length){
        	File f = files[count++];
            return f.getName() +";"+getFileChecksum(f);
        }else{
            count=0;
        }
        return null;
    }
    private static String getFileChecksum( File file) throws IOException
    {
    	MessageDigest digest;
		try {
			digest = MessageDigest.getInstance("MD5");
		} catch (NoSuchAlgorithmException e) {
			return "";
		}
        //Get file input stream for reading the file content
        FileInputStream fis = new FileInputStream(file);
         
        //Create byte array to read data in chunks
        byte[] byteArray = new byte[1024];
        int bytesCount = 0; 
          
        //Read file data and update in message digest
        while ((bytesCount = fis.read(byteArray)) != -1) {
            digest.update(byteArray, 0, bytesCount);
        };
         
        //close the stream; We don't need it now.
        fis.close();
         
        //Get the hash's bytes
        byte[] bytes = digest.digest();
         
        //This bytes[] has bytes in decimal format;
        //Convert it to hexadecimal format
        StringBuilder sb = new StringBuilder();
        for(int i=0; i< bytes.length ;i++)
        {
            sb.append(Integer.toString((bytes[i] & 0xff) + 0x100, 16).substring(1));
        }
         
        //return complete hash
       return sb.toString();
    }
	public String getFolder() {
		return folder;
	}
	public void setFolder(String folder) {
		this.folder = folder;
	}

}
