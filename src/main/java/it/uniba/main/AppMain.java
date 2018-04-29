package it.uniba.main;

import java.util.Scanner;

import it.uniba.controller.FlowController;
import it.uniba.interpreting.CommandInterpreter;
import it.uniba.parsing.CommandParser;
import it.uniba.parsing.ZipParser;

public final class AppMain 
{
	// path zip : /home/phinkie/Downloads/ingsw.zip 
	public static void main(final String[] args) 
	{ 
		Scanner scanLine = new Scanner(System.in);
		FlowController control = new FlowController("", false, new ZipParser());

		CommandParser commandParser = null;
		CommandInterpreter interpreter = null;

		try
		{
			//Valida gli argomenti, riesegue il loop se trova discordanze
			commandParser = new CommandParser(args);
		}
		catch(Exception e)
		{
			System.out.println("Invalid syntax. Refer to 'help' command");
		}

		interpreter = new CommandInterpreter();
		control = interpreter.executeCommands(commandParser, control);
		
		// salvataggio dei dizionari e dei workspace creati 
		interpreter.getSysws().DictSerial(control.getCurrWorkspace(), control.getFileParser());
		interpreter.getSysws().saveWorkspace();
	}
}