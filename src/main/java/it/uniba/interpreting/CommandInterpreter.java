package it.uniba.interpreting;

import java.io.File;

import it.uniba.controller.DataController;
import it.uniba.controller.FlowController;
import it.uniba.model.WorkspaceSys;
import it.uniba.parsing.CommandParser;
import it.uniba.parsing.CommandParser.*;
import it.uniba.parsing.ZipParser;

public class CommandInterpreter
{
	WorkspaceSys worksys;
	
	 
	
    public FlowController executeCommands(CommandParser parser, FlowController control)
    {
        FlowController newControl = control;
        
        CommBaseArgs baseArgs = parser.getBaseArgs();
        CommLoad load = parser.getCommLoad();
        CommWorkspace workspace = parser.getCommWorkspace();
        CommMembers members = parser.getCommMembers();
        CommChannels channels = parser.getCommChannels();
        
         
        //Argomenti singoli immessi
        if(baseArgs.isActive())
        {
            if(baseArgs.getQuitStatus())
                newControl.setQuitStatus(true);

            else if(baseArgs.getDropStatus())
                newControl = dropWorkspace(newControl);
        }
        
        //load inserito
        if(load.isActive())
        {
            //Percorso valido
            if(load.getPathToZip() != null)
            {
                newControl = loadWorkspace(load.getPathToZip(), newControl);   
                worksys = new WorkspaceSys(); 
                
                //creo la directory nascosta su cui memorizzare a fine esecuzione users e channels 
                worksys.makedirArea(newControl.getCurrWorkspace()); 
            }	
        }
        
        //-w inserito
        else if(workspace.isActive())
        {
            String workspaceName = workspace.getWorkspaceName();
            
            //Tutto tuo, Giova'!
        }
        
        //-m inserito
        else if(members.isActive())
        {
            if(control.getFileParser().hasLoaded())
            {
                //Nessun filtro inserito
                if(members.getChannelFilter() == null)
                    DataController.printMembers(newControl.getFileParser());
                //Canale filtro inserito
                else
                    DataController.channelMembers(newControl.getFileParser(), members.getChannelFilter());
            }
            else
                System.out.println("No workspace loaded");
        }
        
        //-c inserito
        else if(channels.isActive())
        {
            if(control.getFileParser().hasLoaded())
            {
                //Channels estesi inseriti
                if(!channels.getExtendedStatus())
                    DataController.printChannels(newControl.getFileParser());
                //Channels estesi non inseriti
                else
                    DataController.members4Channel(newControl.getFileParser());
            }
            else
                System.out.println("No workspace loaded");
        }
        
        return newControl;
    }
    
    private FlowController dropWorkspace(FlowController control)
    {
        if(!control.getFileParser().hasLoaded())
            System.out.println("No workspace to drop");
        else
        {
            control.setFileParser(new ZipParser());
            control.setCurrWorkspace("");
        }
        
        return control;
    }
    
    private FlowController loadWorkspace(String path, FlowController control)
    {
        control.getFileParser().load(path);    
        
        //Aggiorna il nome del workspace corrente if il comando "load" e' andato a buon fine
        if(control.getFileParser().hasLoaded())
        {
            File tempFile = new File(path);
            String fileName = tempFile.getName();
            // ## attenzione a questi scope 
            if(fileName != null)
                control.setCurrWorkspace(fileName);
            else
                control.setCurrWorkspace(path);
        }
        
        return control;
    }

	public WorkspaceSys getSysws() {
		// TODO Auto-generated method stub
		return worksys;
	}
}
