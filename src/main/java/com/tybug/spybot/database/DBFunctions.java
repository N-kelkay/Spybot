package com.tybug.spybot.database;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Contains database input/output functions. Databases: [am, info, dd]
 * @author Liam DeVoe
 */
public class DBFunctions {
	static boolean printSql = false;
	
	
	
	
	//---------------------------------- HELPER functions ------------------------		
	
	/**
	 * Changes the content of the given database according to the given sql statement
	 * @param database The name of the database to modify
	 * @param args The strings to insert into the prepared sql statement
	 * @param sql The sql statement to execute
	 */
	public static void modifyDatabase(String database, List<String> args, String sql) {
		Connection c = null;
		PreparedStatement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			stmt = c.prepareStatement(sql); 
			for(String s : args) {
				stmt.setString(args.indexOf(s) + 1, s);
			}
			stmt.executeUpdate();
			if(printSql) System.out.println("\t" + sql);
			c.commit();

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
		} finally {
			close(stmt, c);
		}
	}
	
	/**
	 * Checks if the condition given in the sql statement is true
	 * @param database The name of the database to check
	 * @param sql The sql statement to execute
	 * @return boolean True if the sql statement returns a value, false otherwise
	 */
	public static boolean conditionExists(String database, String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return true;
			}
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return false;
		} finally {
			close(stmt, c);
		}
		return false;
	}
	
	/**
	 * Gets the number of rows with the given condition in the database
	 * @param database The name of the database to look through
	 * @param sql The sql statement to execute
	 * @return int The value contained in the first row returned by the sql statement (as the statement itself returns the number of rows)
	 */
	public static int getNumRows(String database, String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getInt(1);
			}
			return 0;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return 0;
		} finally {
			close(stmt, c);
		}
	}
	

	/**
	 * Gets a single string with the given condition from a database
	 * @param database The name of the database to look through
	 * @param arg The table to add contents to the list from
	 * @param sql The sql statement to execute
	 * @return String The first item returned by the sql statement, selected from the table with name arg
	 */
	public static String getStringFromDatabase(String database, String arg, String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			if (rs.next()) {
				return rs.getString(arg);
			}
			return null;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	
	
	/**
	 * Gets a list of Strings with the given condition from a database
	 * @param database The name of the database to look through
	 * @param sql The sql statement to execute
	 * @param arg The name of the column to add contents to the list from
	 * @return ArrayList A list of items returned by the sql statement from the table specified by arg
	 */
	public static ArrayList<String> getListFromDatabase(String database, String arg, String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			ArrayList<String> ret = new ArrayList<String>();
			while (rs.next()) {
				ret.add(rs.getString(arg));
			}
			return ret;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	
	
	/**
	 * Creates a map of String to String from the given database
	 * @param database The name of the database to look through
	 * @param sql The sql statement to execute
	 * @param arg An array, the first element is the column name to get the key value from and the second element is the column name to get the map value from
	 * @return Map<String, String> A map of String to String of the args from the database
	 */
	public static HashMap<String, String> getMapFromDatabase(String database, String[] arg, String sql) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);
			
			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			HashMap<String, String> ret = new HashMap<String, String>();
			while (rs.next()) {
				ret.put(rs.getString(arg[0]), rs.getString(arg[1]));
			}
			return ret;
		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	/*
	 * 			String sql = "SELECT DISTINCT `USERID` FROM `MESSAGES`";
				String sql2 = "SELECT COUNT(*) FROM `MESSAGES` WHERE `CHANNELID` = \"" + channelId + "\" AND `USERID` = \"" + id + "\"";

	 */
	
	
	/**
	 * Returns a map of each value in the ARG column resulted by the first sql statement to the number of rows returned by the second sql statement
	 * @param database The name of the database to look through
	 * @param arg The name of the column to get the value from 
	 * @param sql The first sql statement to execute and then iterate over the results
	 * @param sql2 The second sql statement to execute and then put the number of rows resulted by it into the map
	 * @return
	 */
	public static Map<String, Integer> getMapIncrement(String database, String arg, String sql, String sql2) { 
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/" + database + ".db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			ResultSet rs = stmt.executeQuery(sql);
			Map<String, Integer> ret = new HashMap<String, Integer>();
			while(rs.next()) {
				String id = rs.getString(arg);
				ret.put(id, getNumRows("am", sql2));
			}
			return ret;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	
	/**
	 * Closes the given statement and connection.
	 * @param stmt The statement to close
	 * @param c The connection to close
	 * @return True if the operation was successful, false otherwise
	 */
	static boolean close(Statement stmt, Connection c) {
		try {
			stmt.close();
			c.close();
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}
	
	
	
	
	
	
	
	//---------------------------------- MODIFY (REMOVE/ADD/SET) functions ---------------------------------------	
	
	
	public static void setName(String id, String name) {
		if(nameExists(id)) {
			modifyDatabase("info", Arrays.asList(name, id), "UPDATE `NAMES` SET `NAME` = ? WHERE `ID` = ?");
		}
		
		else {
			modifyDatabase("info", Arrays.asList(id, name), "INSERT INTO `NAMES` (`ID`, `NAME`) VALUES (?, ?)");
		}
	}
	
	public static boolean nameExists(String id) { 
		return conditionExists("info", "SELECT * FROM `NAMES` WHERE `ID` = \"" + id + "\"");
	}
	
	public static void addOrUpdateColorRole(String userId, String roleId) {
		if(userHasColorRole(userId)) {
			List<String> args = Arrays.asList(roleId, userId);
			String sql = "UPDATE `COLOR` SET `ROLEID` = ? WHERE `USERID` = ?";
			modifyDatabase("info", args, sql);
			return;
		}
		
		addColorRole(userId, roleId);
	}
	
	
	
	public static boolean userHasColorRole(String userId) {
		String sql = "SELECT * FROM `COLOR` WHERE `USERID` = \"" + userId + "\"";
		return conditionExists("info", sql);
	}

	/**
	 * Removes an entry from the COLOR table of the INFO database with the given ROLEID
	 * @param roleId The roleId of the entry to remove. NOTE: Not the PK so might remove more than one!
	 */
	public static void removeColorRoleFromRoleId(String roleId) {
		List<String> args = Arrays.asList(roleId);
		String sql = "DELETE FROM `COLOR` WHERE `ROLEID` = ?";
		modifyDatabase("info", args, sql);
	}
	
	
	/**
	 * Removes an entry from the COLOR table of the INFO database with the given USERID
	 * @param userId The userId of the entry to remove
	 */
	public static void removeColorRole(String userId) {
		List<String> args = Arrays.asList(userId);
		String sql = "DELETE FROM `COLOR` WHERE `USERID` = ?";
		modifyDatabase("info", args, sql);
	}
	
	
	/**
	 * Adds an entry to the COLOR table of the INFO database with the given USERID and ROLEID
	 * @param userId The user id to add
	 * @param roleId The role id to add
	 */
	public static void addColorRole(String userId, String roleId) {
		List<String> args = Arrays.asList(userId, roleId);
		String sql = "INSERT INTO `COLOR` (`USERID`, `ROLEID`) VALUES (?, ?)";
		modifyDatabase("info", args, sql);
	}
	

	
	/**
	 * Deletes an entry from `UPDATEPREF` where "ID" is the passed id
	 * @param id The id of the user to remove
	 */
	public static void removeUpdateSquad(String id) {
		List<String> args = Arrays.asList(id);
		String sql = "DELETE FROM `UPDATEPREF` WHERE `ID` = " + id;
		modifyDatabase("info", args, sql);
	}
	
	
	
	
	/**
	 * Adds a message to the MESSAGES table with the given information
	 * 
	 * @param messageId The id of the message
	 * @param userId The id of the author of the message
	 * @param channelId The id of the channel this message was sent in
	 * @param contentRaw The raw content contained in the message
	 * @param contentDisplay The display content contained in the message
	 * @param timestamp The time the message was sent, in uuuu-MM-dd'T'HH:mmXXXXX format
	 */
	public static void addMessage(String messageId, String userId, String channelId, String contentRaw, String contentDisplay, String timestamp) {
		List<String> args = Arrays.asList(messageId, userId, channelId, contentRaw, contentDisplay, timestamp);
		String sql = "INSERT INTO `MESSAGES` (`MESSAGEID`,`USERID`, `CHANNELID`, `CONTENTRAW`, `CONTENTDISPLAY`, `TIMESTAMP`) " +
				"VALUES (?, ?, ?, ?, ?, ?)";
		modifyDatabase("am", args, sql);

	}
	
	
	
	
	
	/**
	 * Adds an entry with the given id and the given timestamp to the JOINS table.
	 * @param userId The id of the user that joined
	 * @param timestamp The time the user joined
	 */
	public static void addJoin(String userId, String timestamp) {
		List<String> args = Arrays.asList(userId, timestamp);
		String sql = "INSERT INTO `JOINS` (`USERID`,`TIMESTAMP`) " +
				"VALUES (?, ?)";
		modifyDatabase("am", args, sql);
	}
	
	
	
	/**
	 * Adds an entry with the given id and the given name to the NAMES table in the INFO db
	 * @param userId The id of the user
	 * @param name The real life name of the user
	 */
	public static void addName(String userId, String name) {
		List<String> args = Arrays.asList(userId, name);
		String sql = "INSERT INTO `NAMES` (`ID`,`NAME`) " +
				"VALUES (?, ?)";
		modifyDatabase("info", args, sql);
	}
	
	

	/**
	 * Inserts a new entry into the UPDATEPREF table of the INFO db
	 * <p>
	 * Sets ID to id, PREF to pref, and SOURCECODE to sourcecode
	 * @param id The id of the user
	 * @param pref Which updates to send a pm for
	 * @param sourcecode 1 if they can recieve source code, 0 otherwise
	 */
	public static void addUpdateSquad(String id, int pref) {
		List<String> args = Arrays.asList(id, pref + "");
		String sql = "INSERT INTO `UPDATEPREF` (`ID`, `PREF`, `SOURCECODE`) VALUES (?, ?, \"" + 0 + "\")";
		modifyDatabase("info", args, sql);
	}
	
	public static void setCurrentVersion(String version) {
		List<String> args = Arrays.asList(version);
		String sql = "UPDATE `VERSION` SET `VERSION` = ?";
		modifyDatabase("info", args, sql);
	}
	
	public static void setUpdatePreference(String id, int preference) {
		List<String> args = Arrays.asList(preference + "", id);
		String sql = "UPDATE `UPDATEPREF` SET `PREF` = ? WHERE `ID` = ?";
		modifyDatabase("info", args, sql);
	}
	
	
	
	
	
	
	//---------------------------------- EXISTS functions ---------------------------------------

	
	/**
	 * Indicates whether or not a message with the given timestamp exists in the MESSAGES table.
	 *
	 * @param timestamp A string, the timestamp to look for.	
	 * @return true if the database contains an entry with the given timestamp, false otherwise.
	 */
	public static boolean timestampExists(String timestamp) {
		return conditionExists("am", "SELECT * FROM `MESSAGES` WHERE `TIMESTAMP` = \"" + timestamp + "\"");
	}
	
	
	/**
	 * Indicates whether or not a message with the given MESSAGEID exists in the MESSAGES db.
	 *
	 * @param id A string, the id to look for.	
	 * @return true if the database contains an entry with the given id as its key, false otherwise.
	 */
	public static boolean messageExists(String id) {
		return conditionExists("am", "SELECT * FROM `MESSAGES` WHERE `MESSAGEID` = \"" + id + "\"");
	}
	

	

	
	/**
	 * returns true if a user exists in the "UPDATEPREF" table of the "INFO" db where "ID" is the passed id
	 * @param id The id of the user to check for
	 */
	public static boolean updateSquadExists(String id) {
		return conditionExists("info", "SELECT * FROM `UPDATEPREF` WHERE `ID` = \"" + id + "\"");
	}
	
	
	
	
	
	
	//---------------------------------- GET functions ---------------------------------------
	
	
	/**
	 * Returns the raw content contained in the entry has the given id as its key
	 *
	 * @param id The id of the message to get the content of	
	 * @return The raw content of the entry, or an empty string if there is no such entry.
	 */
	public static String getContentRaw(String id) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `MESSAGEID` = \"" + id + "\"";
		return getStringFromDatabase("am", "CONTENTRAW", sql);
	}
	
	
	/**
	 * Returns the number of entries in MESSAGES that have CHANNELID as the passed id
	 *
	 * @param id The id of the channel to query for
	 * @return The number of messages with the given id as their CHANNELID
	 */
	public static int getNumMessagesSentByChannel(String id) {
		String sql = "SELECT COUNT(*) FROM `MESSAGES` WHERE `CHANNELID` = \"" + id + "\"";
		return getNumRows("am", sql);
	}
	
	
	
	
	
	
	
	
	/**
	 * Returns the number of entries in MESSAGES that have USERID as the passed id
	 *
	 * @param id The id of the user to query for
	 * @return The number of messages with the given id as their USERID
	 */
	public static int getNumMessagesSentByUser(String id) {
		String sql = "SELECT COUNT(*) FROM `MESSAGES` WHERE `USERID` = \"" + id + "\"";
		return getNumRows("am", sql);
	}
	
	
	/**
	 * Returns the raw content of all messages sent by the given user
	 *
	 * @param id The id of the user to query for
	 * @return A list containing CONTENTRAW of entries with the given id as their USERID
	 */
	public static List<String> getContentRawOfMessagesSentByUser(String id) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `USERID` = \"" + id + "\"";
		return getListFromDatabase("am", "CONTENTRAW", sql);
	}
	
	
	
	/**
	 * Returns the display content of all messages sent by the given user
	 *
	 * @param id The id of the user to query for
	 * @return A list containing CONTENTDISPLAY of entries with the given id as their USERID
	 */
	public static List<String> getContentDisplayOfMessagesSentByUser(String id) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `USERID` = \"" + id + "\"";
		return getListFromDatabase("am", "CONTENTDISPLAY", sql);
	}
	
	
	
	
	/**
	 * Returns the display content of all messages sent in the given channel
	 *
	 * @param id The id of the channel to query for
	 * @return A list containing CONTENTDISPLAY of entries with the given id as their CHANNELID
	 */
	public static List<String> getContentDisplayOfMessagesSentByChannel(String id) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `CHANNELID` = \"" + id + "\"";
		return getListFromDatabase("am", "CONTENTDISPLAY", sql);
	}
	
	
	
	
	/**
	 * Returns the display content of all messages sent by the given user
	 *
	 * @param id The id of the user to query for
	 * @return A list containing CONTENTDISPLAY of entries with the given id as their USERID
	 */
	public static List<String> getContentDisplayOfMessagesSentByUserWithExclusions(String id, List<String> exclusions) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/am.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT * FROM `MESSAGES` WHERE `USERID` = \"" + id + "\"";
			ResultSet rs = stmt.executeQuery(sql);
			List<String> ret = new ArrayList<String>();
			while(rs.next()) {
				if(exclusions.contains(rs.getString("CHANNELID"))) {
					continue;
				}
				ret.add(rs.getString("CONTENTDISPLAY"));
			}
			return ret;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	
	
	
	
	/**
	 * Returns the number of entries in the MESSAGES table
	 *
	 * @return The number of entries in the MESSAGES table
	 */
	public static int getTotalMessages() {
		String sql = "SELECT COUNT(*) FROM `MESSAGES`";
		return getNumRows("am", sql);
	}
	
	
	
	
	/**
	 * Returns the number of entries in the MESSAGES table
	 *
	 * @return The number of entries in the MESSAGES table
	 */
	public static List<String> getAllTimestamps() {
		String sql = "SELECT * FROM `MESSAGES`";
		return getListFromDatabase("am", "TIMESTAMP", sql);
	}
	
	
	

	
	
	/**
	 * Returns a map mapping the id to the number of messages of every user who has said anything in the given channel
	 * @param channelId The id of the channel to look for activity in
	 * @return A map containing the id of every user and the amount of messages they have sent in the given channel
	 */
	public static Map<String, Integer> getActivityInChannel(String channelId) {
		Connection c = null;
		Statement stmt = null;
		try {
			Class.forName("org.sqlite.JDBC");
			c = DriverManager.getConnection("jdbc:sqlite:db/am.db");
			c.setAutoCommit(false);

			stmt = c.createStatement();
			String sql = "SELECT * FROM `MESSAGES` WHERE `CHANNELID` = \"" + channelId + "\"";
			ResultSet rs = stmt.executeQuery(sql);
			Map<String, Integer> ret = new HashMap<String, Integer>();
			while(rs.next()) {
				String id = rs.getString("USERID");
				ret.putIfAbsent(id, 0); //if it's not there already, add it. Will get incremented next so set to zero
				ret.put(id, ret.get(id) + 1); //Increment, guaranteed to be here because of above
			}
			return ret;

		} catch (Exception e) {
			System.err.println(e.getClass().getName() + ": " + e.getMessage());
			return null;
		} finally {
			close(stmt, c);
		}
	}
	
	
	
	/**
	 * Returns a map mapping the id of every channel to the number of messages the given user has sent in that channel
	 * @param userId The id of the user to look for activity of all channels for
	 * @return A map containing the id of the channel and the amount of messages the user has sent in a specific channel
	 */
	public static Map<String, Integer> getActivityByUserPerChannel(String userId) {
		String sql = "SELECT DISTINCT `CHANNELID` FROM `MESSAGES`";
		String sql2 = "SELECT COUNT(*) FROM (SELECT * WHERE `CHANNELID` = ?)";
		return getMapIncrement("am", "CHANNELID", sql, sql2);
	}
	
	
	/**
	 * Returns a map mapping the id of every user who has said anything to the number of messages
	 * @return Map<String, Integer> A map containing the id of the user and the amount of messages they have sent in the guild
	 */
	public static Map<String, Integer> getTotalActivity() {
		String sql = "SELECT DISTINCT `USERID` FROM `MESSAGES`";
		String sql2 = "SELECT COUNT(*) FROM (SELECT * WHERE `USERID` = ?)";
		return getMapIncrement("am", "USERID", sql, sql2);
	}
	
	
	
	/**
	 * Gets the timestamp of all entries that have the given id as their userId
	 *
	 * @param userId The id of the user to get the messages of	
	 * @return A list where each entry is a timestamp contained in each entry of the MESSAGES table that has the given id as its USERID, or an empty 
	 * list if no entry was found
	 */
	public static List<String> getTimestamps(String userId) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `USERID` = \"" + userId + "\"";
		return getListFromDatabase("am", "TIMESTAMP", sql);
	}
	
	
	
	/**
	 * Gets the current version of the bot
	 *	
	 * @return A String; the current version
	 */
	public static String getCurrentVersion() {
		String sql = "SELECT * FROM `VERSION`";
		return getStringFromDatabase("info", "VERSION", sql);
	}
	
	/**
	 * Retrieves from the "PREF" column of the "UPDATEPREF" table of the "INFO" database where "ID" is the given id
	 *	@param id The id of the user to get the preference of
	 * @return String The update pref of the user
	 */
	public static String getUpdatePreference(String id) {
		String sql = "SELECT * FROM `UPDATEPREF` WHERE `ID` = \"" + id + "\"";
		return getStringFromDatabase("info", "PREF", sql);
	}
	
	
	
	/**
	 * Gets the update preference of all users who can recieve source code
	 *	
	 * @return A map of user id to update preference
	 */
	public static Map<String, String> getUpdateSource() {
		String sql = "SELECT * FROM `UPDATEPREF` WHERE `SOURCECODE` = 1";
		return getMapFromDatabase("info", new String[] {"ID", "PREF"}, sql);
	
	}
	
	
	
	
	
	/**
	 * Gets the update preference of all users who cannot recieve source code
	 *	
	 * @return A map of user id to update preference
	 */
	public static Map<String, String> getUpdateNoSource() {
		String sql = "SELECT * FROM `UPDATEPREF` WHERE `SOURCECODE` = 0";
		return getMapFromDatabase("info", new String[] {"ID", "PREF"}, sql);
	}
	

	
	/**
	 * Gets all the names in the info database ("NAMES" table, "NAME" category)
	 *	
	 * @return A map of user id to name
	 */
	public static Map<String, String> getNames() {
		String sql = "SELECT * FROM `NAMES`";
		return getMapFromDatabase("info", new String[] {"ID", "NAME"}, sql);
	}
	
	
	
	
	
	/**
	 * Gets a list of raw contents, each entry backed by the timestamp. 
	 *
	 * @param timestamp The timestamp to get the raw contents for
	 * @return An arraylist of the raw contents of each message with the given timestamp, or null
	 *  If there is no entry in the database with the given timestamp.
	 */
	public static List<String> getRawsFromTimestamp(String timestamp) {
		String sql = "SELECT * FROM `MESSAGES` WHERE `TIMESTAMP` = \"" + timestamp + "\"";
		return getListFromDatabase("am", "CONTENTRAW", sql);
	}

	/**
	 * Gets the id of the current color role of the given user. Specifically, queries the COLOR table in the INFO database.
	 * @param userId The id of the user to get the color role for
	 * @return The id of the current color role, or null if they have no color role
	 */
	public static String getColorRole(String userId) {
		String sql = "SELECT * FROM `COLOR` WHERE `USERID` = \"" + userId + "\"";
		return getStringFromDatabase("info", "ROLEID", sql);
	}

	public static List<String> getMakerMessages() {
		String sql = "SELECT * FROM `LOGGER`";
		return getListFromDatabase("am", "MARKERMESSAGEID", sql);
	}
	
	
	public static HashMap<String, String> getChannelSizes() {
		String sql = "SELECT * FROM `LOGGER`";
		return getMapFromDatabase("am", new String[] {"CHANNELID", "CHANNELSIZE"}, sql);
	}

	
	public static int getChannelSize(String channelId) {
		String sql = "SELECT * FROM `LOGGER` WHERE `CHANNELID` = " + channelId;
		String size = getStringFromDatabase("am", "CHANNELSIZE", sql);
		return size == null ? 0 : Integer.parseInt(size); //return zero if the channel doesn't exist yet
	}
}
