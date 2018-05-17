package it.uniba.controller.input;

import java.io.IOException;
import java.util.zip.ZipException;

import it.uniba.controller.DataController;

import it.uniba.controller.input.CommandParser;
import it.uniba.controller.input.CommandParser.*;

// da prendere datacontroller 
public class CommandInterpreter {

	public void executeCommands(CommandParser parser, DataController dataCtr) throws ZipException, IOException {
		CommBaseArgs baseArgs = parser.getBaseArgs();
		CommWorkspace workspace = parser.getCommWorkspace();

		// Argomenti singoli immessi
		if (baseArgs.isActive()) {
			// Ho chiesto help
			if (baseArgs.getHelpStatus())
				showHelp();
		}

		// -w nomeWorkspace inserito
		else if (workspace.isActive()) {
			// nomeWorkspace valido
			if (workspace.isValidWorkspace()) {
				String workspaceName = workspace.getWorkspaceName();
				// fileParser.load(workspaceName); // load workspace
				dataCtr.updateModel(workspaceName);
				// Fileparser ha caricato qualcosa
				if (dataCtr.hasLoaded()) {
					// -u inserito
					if (workspace.getMembersStatus())
						dataCtr.printMembers();

					// -c inserito
					else if (workspace.getChannelsStatus())
						dataCtr.printChannels();

					// -cu inserito
					else if (workspace.getExtChannelsStatus())
						dataCtr.printMembers4Channel();

					// -uc inserito
					else if (workspace.isValidFilter())
						dataCtr.printChannelMembers(workspace.getChannelFilter());

					// -m riconosciuto
					else if (workspace.getMentionParams() != null) { // issue#37
																		// -m
						if (workspace.getMentionParams().length == 0) {
							// Stampa tutti i mention del workspace
							dataCtr.printMention("");
						}
						// -m in x
						else if (wantsIn(workspace.getMentionParams())) {
							String inChannel = workspace.getMentionParams()[1];
							// Stampa tutti i mention in un channel x
							if (!inChannel.equals(""))
								dataCtr.printMention(inChannel);
						}

						// issue#38
						// -m from x
						else if (wantsFrom(workspace.getMentionParams())) {
							String fromWho = workspace.getMentionParams()[1];
							// Stampa tutti i mention effettuati da x
							dataCtr.printMentionsFromUser(fromWho, "");
						}
						// -m from x in y
						else if (wantsFromIn(workspace.getMentionParams())) {
							String fromWho = workspace.getMentionParams()[1];
							String inChannel = workspace.getMentionParams()[3];
							if (!inChannel.equals(""))
								dataCtr.printMentionsFromUser(fromWho, inChannel);
							// Stampa tutti i mention effettuati da x nel channel y

						}
						// issue#39
						// -m to x
						else if (wantsTo(workspace.getMentionParams())) {
							String toWho = workspace.getMentionParams()[1];
							dataCtr.printMentionsToUser(toWho, "");
							// Stampa tutti i mention in cui viene menzionato x
						}

						// -m to x in y
						else if (wantsToIn(workspace.getMentionParams())) {
							String toWho = workspace.getMentionParams()[1];
							String inChannel = workspace.getMentionParams()[3];
							dataCtr.printMentionsToUser(toWho, inChannel);
							// Stampa tutti i mention in cui � menzionato x nel channel y
						} else
							throw new IllegalStateException();
					} else
						throw new IllegalStateException();
				} else
					throw new IllegalStateException();
			} else
				throw new IllegalStateException();
		} else
			throw new IllegalStateException();
	}

	public void showHelp() {
		System.out.println("Usage:");
		System.out.println("help - shows this help\n");
		System.out.println(
				"-w \"path\\to\\file.zip\" ( -c | -u | -uc <channelFilter> | -cu | -m [from <x>] | [to <y>] [in <z>])");
		System.out.println("\t-w parses a workspace");
		System.out.println("\t-c prints all the channels in the specified workspace");
		System.out.println("\t-u prints all the users in the specified workspace");
		System.out.println("\t-uc <channelFilter> prints all the users in the specified channel ");
		System.out.println("\t-cu prints all the channels with their users\n");
		System.out.println("\t-m prints all the mentions in a workspace");
		System.out.println("\t\t-m from <user> filters the mentioner");
		System.out.println("\t\t-m to <user> filters the mentioned");
		System.out.println("\t\t-m in filters the channel");
		System.out.println(
				"\t\t-m from <user> OR to <user> in <channel> filters the mentioner or mentioned in the channel");
	}

	private Boolean wantsFrom(String[] mentionParams) {
		if ((mentionParams.length == 2) && mentionParams[0].equals("from") && mentionParams[1] != null)
			return true;
		else
			return false;
	}

	private Boolean wantsTo(String[] mentionParams) {
		if ((mentionParams.length == 2) && mentionParams[0].equals("to") && mentionParams[1] != null)
			return true;
		else
			return false;
	}

	private Boolean wantsIn(String[] mentionParams) {
		if ((mentionParams.length == 2) && mentionParams[0].equals("in") && mentionParams[1] != null)
			return true;
		else
			return false;
	}

	private Boolean wantsFromIn(String[] mentionParams) {
		if ((mentionParams.length == 4) && mentionParams[0].equals("from") && mentionParams[1] != null
				&& mentionParams[2].equals("in") && mentionParams[1] != null)
			return true;
		else
			return false;
	}

	private Boolean wantsToIn(String[] mentionParams) {
		if ((mentionParams.length == 4) && mentionParams[0].equals("to") && mentionParams[1] != null
				&& mentionParams[2].equals("in") && mentionParams[1] != null)
			return true;
		else
			return false;
	}
}