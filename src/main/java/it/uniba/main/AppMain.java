package it.uniba.main;

import java.io.File;
import java.util.Scanner;

import it.uniba.parsing.CommandParser;
import it.uniba.parsing.ZipParser;

public final class AppMain 
{
	public static void main(final String[] args) 
	{ 
	    String currWorkspace = new String();
	    Scanner scanLine = new Scanner(System.in);
	    ZipParser fileParser = new ZipParser();
	    
	    //Main loop
	    do
	    {
	        //Printa il workspace caricato
	        System.out.print("(" + currWorkspace + ") >> ");
	        //Regex per ignorare gli spazi tra quotes ("")
	        String[] currParams = scanLine.nextLine().split("\\s(?=(?:[^'\"`]*(['\"`])[^'\"`]*\\1)*[^'\"`]*$)");
	        
//	        for(String x: currParams)
//	            System.out.println(x);
	        
	        CommandParser commander = new CommandParser(currParams);
	        String path = commander.getParsedArgs().getZipFile();
	        boolean sigKill = commander.getParsedArgs().getSigKill();
	        boolean toDrop = commander.getParsedArgs().getDrop();
	        
	        //Comando "quit" invocato
            if(sigKill)
                break;
	        
            //Abbiamo effettivamente invocato il "load" da parametro
	        if(path != null) 
	        {
	            fileParser.load(path);    
	            
	            //Aggiorna il nome del workspace corrente se il comando "load" � andato a buon fine
	            if(fileParser.isSuccessful())
	            {
	                File tempFile = new File(path);
	                String fileName = tempFile.getName();
	                if(fileName != null)
	                    currWorkspace = fileName;
	                else
	                    currWorkspace = path;
	            }
	        }
	        
	        //Abbiamo chiesto il drop del workspace
	        if(toDrop)
	        {
	            if(!fileParser.isSuccessful())
	                System.out.println("No workspace to drop");
	            else
	            {
	                fileParser = new ZipParser();
	                currWorkspace = "";            
	            }
	        }
	    }
	    while(true);
	    
	    scanLine.close();
	}
}