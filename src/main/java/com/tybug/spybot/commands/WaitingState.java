package com.tybug.spybot.commands;

/**
 * The kind of action (name pm, reaction, message send, etc) the Pending is waiting on.
 * @see Pending
 */
public enum WaitingState {
	NAME, //Name PM when a user first joins the server
	ART_REACTION, //#creative-corner, possible future command to make art dumps more fun/easy/automated
	OPTION_REACT; //Reaction from the user about setting an option
}