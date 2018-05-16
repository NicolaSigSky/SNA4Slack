package it.uniba.controller;

import it.uniba.view.View;

import it.uniba.workdata.User;

import it.uniba.model.Model;

import java.util.Collection;

import it.uniba.model.Edge;
import it.uniba.parsing.ZipParser;

public class Controller {
	Model mod = new Model();
	View view = new View();

	public Controller() {
	}

	// o utenti e channel e messaggi?
	// public static void loadModel(zipParser)
	public void printMembers(ZipParser fileParser) {
		// model.getUsers()
		view.printMembers(fileParser.getUsers().values());
	}

	public void printChannels(ZipParser fileParser) {
		// View.printChannels(model.getChannel().values());
		view.printChannels(fileParser.getChannels().values());
	}

	public void printMembers4Channel(ZipParser fileParser) {
		// View.printMembers4Channel(model.getUsers.values(),
		view.printMembers4Channel(fileParser.getUsers(), fileParser.getChannels().values());
	}

	public void printChannelMembers(ZipParser fileParser, final String _nameChannel) {
		view.printChannelMembers(fileParser.getUsers(), fileParser.getChannels(), _nameChannel);
	}

	public void printMention(ZipParser fileParser, final String _inChannel) {
		if (_inChannel.equals("")) {// -m
			fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(), "");
			view.printMention(fileParser.getMentionGraph().edgesOutDegree(null), false);
		} else { // validazione canale -m in _inChannel
			if (fileParser.getChannels().containsKey(_inChannel)) {
				fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(), _inChannel);
				view.printMention(fileParser.getMentionGraph().edgesOutDegree(null), false);
			} else {
				View.missingChannel(_inChannel);
			}
		}
	}

	// #38
	public void printMentionsFromUser(ZipParser fileParser, final String _user, final String _inChannel,
			final boolean _from, final boolean _weigth) {
		String idUser = getUserFromId(fileParser, _user);
		if (fileParser.getUsers().containsKey(idUser)) // l'utente esiste nel workspace
		{
			if (fileParser.getChannels().containsKey(_inChannel) || (_inChannel.equals("") || _inChannel == null)) {
				Collection<Edge> edgesneeded;
				if (_from) {
					edgesneeded = fileParser.getMentionGraph().edgesOutDegree(fileParser.getUsers().get(idUser));
				} else {
					edgesneeded = fileParser.getMentionGraph().edgesInDegree(fileParser.getUsers().get(idUser));
				}
				if (_inChannel.equals("") || _inChannel == null) { // -m from (User) x
					fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(), "");
				} else { // -m from (User) x in (Channel) y
					fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(),
							_inChannel);
				}

				view.printMention(edgesneeded, _weigth);
			}
		} else {
			View.missingUser(_user);
		}
		if (!(_inChannel.equals("") || _inChannel == null) && (!fileParser.getChannels().containsKey(_inChannel))) {
			View.missingChannel(_inChannel);
		}
	}

	// #39
	public void printMentionsToUser(ZipParser fileParser, final String _user, final String _inChannel) {
		String idUser = getUserFromId(fileParser, _user);
		if (_inChannel.equals("")) {
			if (fileParser.getUsers().containsKey(idUser)) // l'utente esiste nel workspace
			{
				fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(), "");

			} else {
				System.out.println("The user specified doesn't exist.");
			}
		} else {
			if (fileParser.getUsers().containsKey(idUser) && fileParser.getChannels().containsKey(_inChannel)) {
				fileParser.getMentionGraph().parseMessages(fileParser.getMessages(), fileParser.getUsers(), _inChannel);
				// parse
				// dei
				// mention
				// sul
				// grafo
				// fileParser.getMentionGraph().printEdgesInDegree(fileParser.getUsers().get(idUser));
			} else {
				if (!fileParser.getUsers().containsKey(idUser))
					System.out.println("The user specified doesn't exist.");
				if (!fileParser.getChannels().containsKey(_inChannel))
					System.out.println("The channel specified doesn't exist.");
			}
		}
	}

	static String getUserFromId(ZipParser fileParser, String name) {
		// possiamo aggiunger eccezione
		for (User x : fileParser.getUsers().values()) {
			String disName = x.getDisplayNameNorm();
			String rn = x.getRealName();
			String _name = x.getName();

			if (disName != null)
				if (disName.equals(name))
					return x.getId();
			if (rn != null)
				if (rn.equals(name))
					return x.getId();
			if (_name != null)
				if (_name.equals(name))
					return x.getId();
		}
		return "";
	}
}
