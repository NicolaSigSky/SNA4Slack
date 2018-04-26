package it.uniba.controller;

import it.uniba.workdata.*;
import it.uniba.parsing.ZipParser;

public class Controller
{
    public static void printChannels(ZipParser fileParser)
    {
        for(Channel x : fileParser.getChannels().values())
            System.out.println(x.getName());
    }
    
    public static void printMembers(ZipParser fileParser)
    {
         for( User utente : fileParser.getUsers().values())
         { 
             System.out.println(utente.getId() +"\t"+ utente.getName() 
                +"\t"+  utente.getRealName() +"\t"+  utente.getTeamId());
         }
    }
    
    public static void channelMembers(ZipParser fileParser,String channel)
    {
    	System.out.print(" + " + channel + "\n");
    	// -l "/home/phinkie/Downloads/ingsw.zip"
 
    	 
    	for(String key : fileParser.getChannels().get(channel).getMemberList()) // key from value 
    	{
    		User utente = fileParser.getUsers().get(key);
    		System.out.print("\t -" + utente.getRealName() + " @" + utente.getName() + "\n");
    	}
    	System.out.println();
    }
    
    /*
    public static void printUsers(ZipParser fileParser)
    {
        for(User x : fileParser.getUsers().values())
            System.out.println(x.getName());
    }
    */
}
