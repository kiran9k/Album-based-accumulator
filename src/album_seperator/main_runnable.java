/*
 * Aim : To get the list of files in a folder .
 * Create folder for each of the album in the folder. 
 * Sort files based on the Album into s folder 
 * 
 */
package album_seperator;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.parser.Parser;
import org.apache.tika.parser.mp3.Mp3Parser;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class main_runnable {
	public static String directory_path="/home/kiran/Music/Songs";
	public static String output_directory="/home/kiran/Music/temp";
	public static String delete_option="false";
	public static String miscellaneous_name="Miscellaneous songs";
	public static String file_extension="mp3";
	public static File[] get_files_list(String dir)
	{
		File f=new File(dir);
		File[] list=null;
		if(f.isDirectory())
		{
			list=f.listFiles();			
		}
		return list;
	}
	
	   public static void file_copier(File f1,File f2)
	    {	
	 
	    	InputStream inStream = null;
	    	OutputStream outStream = null;	    
	    	try{	 
	    	    inStream = new FileInputStream(f1);
	    	    outStream = new FileOutputStream(f2);	 
	    	    byte[] buffer = new byte[1024];	 
	    	    int length;
	    	    //copy the file content in bytes	    	    
	    	    while ((length = inStream.read(buffer)) > 0){	 
	    	    	outStream.write(buffer, 0, length);	 
	    	    }	 
	    	    inStream.close();
	    	    outStream.close();	 
	    	    System.out.println("File "+f1.getName()+" copied successful!");	 
	    	    if(delete_option.contains("true"))
	    	    {
	    	    	f1.delete();
	    	    }
	    	}catch(IOException e){
	    		e.printStackTrace();
	    	}
	    }
	
	public static void config_reader()
	{
		Properties prop = new Properties();
		InputStream input = null;	 
		try {	 
			input = new FileInputStream("config");	 
			// load a properties file
			prop.load(input);	 
			// get the property value and print it out
			directory_path=prop.getProperty("input_directory");
			output_directory=prop.getProperty("output_directory");
			delete_option=prop.getProperty("delete_original");
			miscellaneous_name=prop.getProperty("no_album_name");
			file_extension=prop.getProperty("file_extension");
		} catch (IOException ex) {
			ex.printStackTrace();
		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
	}
	public static void individual_file_prop(File f) throws IOException, SAXException, TikaException
	{

        InputStream input = new FileInputStream(f);
        ContentHandler handler = new DefaultHandler();
        Metadata metadata = new Metadata();
        Parser parser = new Mp3Parser();
        ParseContext parseCtx = new ParseContext();
        parser.parse(input, handler, metadata, parseCtx);
        input.close();

        // List all metadata
        String[] metadataNames = metadata.names();

      /*  for(String name : metadataNames){
        System.out.println(name + ": " + metadata.get(name));
        }*/
        String album=metadata.get("xmpDM:album");
        if(album==null)
        {
        	album=miscellaneous_name;
        }
        if( album.length()>0)
        {
        	//replace abc.com
        	album=album.replace("Download Free Songs","");
        	if(album.contains(".com"))
        	{
        		String[] x=album.split(" ");
        		album="";
        		int i=0;
        		for(String y:x)
        		{
        			i+=1;
        			if(y.contains(".com"))
        			{
        				//
        			}
        			else
        			{
        				album+=y;
        			}
        			if(i!=x.length)
        			{
        				album+=" ";
        			}
        		}
        	}
        	//create a folder
        	File new_folder=new File(output_directory+File.separator+album);
        	System.out.println(album);
        	if(!new_folder.exists())
        	{
        		if(new_folder.mkdirs())
        		{
        			System.out.println("Successfully created dir :"+new_folder.getAbsolutePath());
        		}        		
        	}
        	////copy file from original path
        	File f2=new File(output_directory+File.separator+album+File.separator+f.getName());
        	file_copier(f,f2);
        }
        // Retrieve the necessary info from metadata
        // Names - title, xmpDM:artist etc. - mentioned below may differ based
      /*  System.out.println("----------------------------------------------");
        System.out.println("Title: " + metadata.get("title"));
        System.out.println("Artists: " + metadata.get("xmpDM:artist"));
        System.out.println("Composer : "+metadata.get("xmpDM:composer"));
        System.out.println("Genre : "+metadata.get("xmpDM:genre"));
        System.out.println("Album : "+metadata.get("xmpDM:album"));*/
	}
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		config_reader();
		File[] list=get_files_list(directory_path);
		int count=0;
		for(File temp:list)
		{
			if(!temp.getName().endsWith(file_extension))
			{
				continue;
			}
			try {
				System.out.println("######################################");
				individual_file_prop(temp);
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SAXException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (TikaException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		
	}

}
