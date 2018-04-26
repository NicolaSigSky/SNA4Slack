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
 // Channel:
	// -Membro
	// -Membro 
    public static void members4Channel(ZipParser fileParser)
    {
    	for (Channel canale : fileParser.getChannels().values())
    	{
    		System.out.print(" + " + canale.getName() + "\n\t");
    		for(String membro : canale.getMemberList())
    		{
    			User utente = fileParser.getUsers().get(membro);
    			System.out.print(" - " + utente.getRealName() + "\t@ " + utente.getName() + "\n\t");
    		}
    		System.out.println();
    	}
    }
    
    /*
    public static void printUsers(ZipParser fileParser)
    {
        for(User x : fileParser.getUsers().values())
            System.out.println(x.getName());
    }
    */
}
