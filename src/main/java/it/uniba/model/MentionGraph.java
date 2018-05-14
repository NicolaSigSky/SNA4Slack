package it.uniba.model;

import it.uniba.workdata.Message;
import it.uniba.workdata.User;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.common.graph.MutableValueGraph;
import com.google.common.graph.ValueGraphBuilder;

public class MentionGraph {
	private MutableValueGraph<User, Integer> snagraph = ValueGraphBuilder.directed().build();

	// lista comandi che presentano un @Mention ma che non dovranno essere parsati
	// perchè non presentano la struttura del messaggio: "utente ---> @mention"
	String[] commignore = { "has joined the channel", "set the channel purpose", "cleared channel topic",
			"uploaded a file", "commented on", "was added to this conversation", "set the channel topic",
			"pinned a message to this channel", "pinned", "has renamed the channel", "un-archived the channel",
			"archived the channel", "cleared channel purpose", "has left the channel", "shared a file" };
	// aggiornare lista comandi da ignorare {deleted} trovare riferimento ufficiale

	public MentionGraph() {
	}

	public boolean containsItems(String inputStr) {
		return Arrays.stream(commignore).parallel().anyMatch(inputStr::contains);
	}

	public void parseMessages(HashMap<String, ArrayList<Message>> message, HashMap<String, User> users,
			String inChannel) {
		if (inChannel == "")
			for (ArrayList<Message> mess : message.values())
				parsing(mess, users);
		else if (message.containsKey(inChannel))
			parsing(message.get(inChannel), users);
	}

	void parsing(ArrayList<Message> mess, HashMap<String, User> users) {
		for (Message msg : mess) {
			if (msg.getText().contains("<@") && !containsItems(msg.getText())) {
				/*
				 * Attivare le due stampe in caso di null pointer per vedere eventuali stringhe
				 * di controllo usate da slack per gestire il singolo utente
				 */
				// System.out.println("----------- Testo Grabbato: \n" + msg.getText());
				// System.out.println("\t\t Scritto da: " + msg.getUser() + "\n #############
				// \n\n");
				User utenteu = users.get(msg.getUser());
				/* controlli esistenza dei nodi prima di inserirli */
				if (!snagraph.nodes().contains(utenteu))
					snagraph.addNode(utenteu);

				Pattern pattern = Pattern.compile("\\<@.*?\\>");
				Matcher matcher = pattern.matcher(msg.getText()); // msg.getText
				while (matcher.find()) {
					String dataparse = matcher.group(0);
					String filterstring = dataparse.replaceAll("<@", " ").replaceAll(">", "").trim();
					User utentev = users.get(filterstring);
					if (!utentev.equals(utenteu)) {
						if (!snagraph.nodes().contains(utentev))
							snagraph.addNode(utentev);

						/*
						 * controllo se esiste gia un arco tra i due utenti: Se esiste aggiungo +1 al
						 * mention altrimenti se non esiste creo l'arco
						 */
						if (!snagraph.hasEdgeConnecting(utenteu, utentev))
							snagraph.putEdgeValue(utenteu, utentev, 1); // dobbiamo pescarlo dal grafo e poi inserire
																		// l'arco (pot ghess)
						else {
							int mentioncount = snagraph.edgeValue(utenteu, utentev).get() + 1;
							// cercare un modifica del peso dell'arco tra due nodi
							snagraph.removeEdge(utenteu, utentev);
							snagraph.putEdgeValue(utenteu, utentev, mentioncount);
						}
					}
				}
			}
		}

	}

	// issue#39
	public void printEdgesInDegree(User user) {
		if (snagraph.nodes().contains(user)) {
			if (snagraph.inDegree(user) > 0) {
				int inEdges = snagraph.inDegree(user); // mi conto quanti nodi sono in entrata sull'utente preso in
														// analisi
				String nameUser = user.getRealName();
				for (User to : snagraph.nodes()) // per ogni nodo nel grafo controllo se ha un arco con l'utente presoin
													// analisi
				{
					if (snagraph.hasEdgeConnecting(to, user)) {
						// stampo l'arco tra i due utenti interessati
						System.out.println("From: " + to.getRealName() + "\tTo: " + nameUser + "\t n. mention: "
								+ snagraph.edgeValue(to, user).get());
						inEdges--; // diminuisco il grado di entrata del nodo per ottimizzare la ricerca a grado 0
						if (inEdges == 0)
							break;
					}
				}
			} else
				System.out.println("There aren't mention.");
		} else
			System.out.println("The user specified doesn't belong to this channel.");
	}

	// issue37 && issue#38
	public void printEdges(User user) {
		int numNodesPrinted = 0;
		if (user == null) {
			for (User x : snagraph.nodes())
				for (User adiacenti : snagraph.adjacentNodes(x))
					if (snagraph.hasEdgeConnecting(x, adiacenti)) {
						System.out.println("From: " + x.getRealName() + "\tTo: " + adiacenti.getRealName()
								+ "\t n. mention: " + snagraph.edgeValue(x, adiacenti).get());
						numNodesPrinted++;
					}
			if (numNodesPrinted == 0) // eccezione: non ci sono mention nel workspace
				System.out.println("There aren't mention.");
		} else {
			if (snagraph.nodes().contains(user)) {
				for (User adiacenti : snagraph.adjacentNodes(user))
					if (snagraph.hasEdgeConnecting(user, adiacenti)) {
						System.out.println("From: " + user.getRealName() + "\tTo: " + adiacenti.getRealName()
								+ "\t n. mention: " + snagraph.edgeValue(user, adiacenti).get());
						numNodesPrinted++;
					}
				if (numNodesPrinted == 0) // eccezione: non ci sono mention nel channel specificato
					System.out.println("There aren't mention in the channel specified.");
			} else {
				// eccezione : user non presente nel canale specificato
				System.out.println("The user specified doesn't belong to this channel.");
			}
		}
	}

	public MutableValueGraph<User, Integer> getGraph() {
		return snagraph;
	}
}
