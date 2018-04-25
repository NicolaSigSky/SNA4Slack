package it.uniba.parsing;

import java.util.Map;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import com.google.gson.*;

import it.uniba.workdata.Channel;
import it.uniba.workdata.User;

public class ZipParser 
{
    //I due dizionari da riempire
    private Map<String, User> users = new HashMap<String, User>();
    private Map<String, Channel> channels = new HashMap<String, Channel>();;

    public Map<String, User> getUsers()
    {
        return users;
    }
    public Map<String, Channel> getChannels()
    {
        return channels;
    }
    
    public void load(String path) 
    {
        try 
        {
//            int count = 0;
            ZipFile zip = new ZipFile(path);
            Enumeration <? extends ZipEntry> entries = zip.entries();

            while (entries.hasMoreElements()) 
            {
                ZipEntry entry = entries.nextElement();
                
                if (entry.getName().equals("channels.json") || entry.getName().equals("users.json")) 
                {
                    InputStream stream = zip.getInputStream(entry);
                    Reader lecturer = new InputStreamReader(stream);
//                    System.out.println(entry.getName() + " " + ++count);

                    GsonBuilder builder = new GsonBuilder();
                    Gson gson = builder.create();

                    if (entry.getName().equals("users.json")) 
                    {
                        User[] tempUser = gson.fromJson(lecturer, User[].class);
                        for(User x : tempUser)
                        {
//                            System.out.println(x.getId());
                            users.put(x.getId(), x);
                        }     
                    } 
                    else 
                    {
                        Channel[] tempUser = gson.fromJson(lecturer, Channel[].class);
                        for(Channel x : tempUser)
                        {
//                            System.out.println(x.getId());
                            channels.put(x.getId(), x);
                        }     
                    } 

                    lecturer.close();
                }
            }

            zip.close();
        } 
        catch (NullPointerException e) 
        {System.out.println("Specify zip file to load");} 
        catch (ZipException e) 
        {System.out.println("Unable to open. Damaged or wrong file");} 
        catch (IOException e) 
        {System.out.println("Invalid file");} 
        catch (JsonParseException e) 
        {System.out.println( e.toString() );} 
        catch (Exception e) 
        {System.out.println("Critical exception, I'm out!");}
    }
}