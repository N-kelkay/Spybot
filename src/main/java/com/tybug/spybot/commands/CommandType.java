package com.tybug.spybot.commands;
/**
 * The type of command (HELP, REMINDME, PAUSE, etc) or INVALID if that command does not exist
 * @see Command
 * @author Liam DeVoe
 */
public enum CommandType {
	//Usable by anyone; help first, then homework commands, then commands that impact your user, then commands that impact the guuld, and then information commands
	HELP, EVAL, LATEX, SOL, ROLE, TIMEOUT, REMINDME, OPTIONS, EMOJI, CHANGELOG, SERVERINFO, STATS,
	//All the embed ones since they're quirky although technically usable by everyone
	EMBED_ADD, EMBED_TITLE, EMBED_DESCRIPTION, EMBED_RESET, EMBED_CLEAR, EMBED_SEND,
	//Usable by certain people other than author (may vary, but none are usable by anyone)
	UPDATEME,
	//Usable by author (clearance < 1) only
	LOGHISTORY, LOGJOINS, WRITEMESSAGES, STARTSNOW, STOPSNOW, PAUSE, UNPAUSE, STATUS,
	//And of course, invalid
	INVALID;
	
	public String toString() {
		return this.name().replaceAll("_", " "); //replace _ with spaces for command names
	}
}
