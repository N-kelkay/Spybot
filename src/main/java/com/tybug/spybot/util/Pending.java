package com.tybug.spybot.util;

import java.util.HashMap;

import com.tybug.spybot.commands.WaitingState;

/**
 * Keeps track of actions the bot is waiting to happen.
 * <p>
 * Stores both the id of what you are waiting on and the type of thing you are waiting on (WaitingState).
 * <p>
 * The useful part of this class is the allPendings static hashmap, to check if the id you recieved from the bot is contained here and act upon it if so.
 * @see WaitingState
 * @author Liam DeVoe
 */
public class Pending {
	private static HashMap<String, Pending> allPendings = new HashMap<String, Pending>(); // ID :: Pending
	private WaitingState type;
	private String id;
	
	
	//--------- Constructors ---------

	
	/**
	 * Enum-arg and only constructor for Pending. Never intended to be insantiated, just used in addPending
	 * @param String The ID of the message, user, or other applicable object
	 * @param WaitingState The type of Pending this is (waiting for a name pm, reaction, etc)
	 */
	public Pending(String id, WaitingState type) {
		this.type = type;
		this.id = id;
		allPendings.put(id, this);
	}
	
	
	
	//--------- Static ---------

	
	/**
	 * Adds a new Pending object to the static list of the passed type
	 * @param WaitingState The type of pending this is (waiting for a name, pm, reaction, etc)
	 */
	public static void addPending(String id, WaitingState type) {
		allPendings.put(id, new Pending(id, type));
	}
	
	
	
	
	/**
	 * Removes all objects with the given id as their key in the allPendings Hashmap
	 * @param String The id to remove
	 */
	public static void removePending(String id) {
		allPendings.remove(id);
	}
	
	
	
	
	/**
	 * Gets the current map for pending objects
	 * @return A HashMap mapping the ID of the relevent message/user/etc to its Pending object
	 */
	public static HashMap<String, Pending> getAll() {
		return allPendings;
	}
	
	
	
	
	
	
	/**
	 * Sets allPendings to an empty HashMap, effectively clearing any previously stored Pending objects or ids
	 */
	public static void clearAll() {
		allPendings = new HashMap<String, Pending>();
	}
	
	
	
	
//--------- getters/setters ---------
	
	
	/**
	 * WaitingState getter
	 * @return WaitingState The WaitingState of the object
	 */
	public WaitingState getWaitingState() {
		return type;
	}
	
	
	/**
	 * ID getter
	 * @return String The id of the relevent object the Pending is waiting on
	 */
	public String getId() {
		return id;
	}
	
}