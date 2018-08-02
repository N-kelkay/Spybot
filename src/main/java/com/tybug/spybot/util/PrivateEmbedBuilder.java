package com.tybug.spybot.util;

import java.util.ArrayList;

import net.dv8tion.jda.core.EmbedBuilder;
import net.dv8tion.jda.core.entities.User;
/**
 * Keeps track of a single Embed Builder per user and modifies it through identical methods to an EmbedBuilder.
 * <p> Where applicable, each method returns the modified PEB, so this class is chainable.
 * @author Liam DeVoe
 */
public class PrivateEmbedBuilder {
	private static ArrayList<PrivateEmbedBuilder> allPrivateEmbedBuilders = new ArrayList<PrivateEmbedBuilder>();
	private EmbedBuilder embedBuilder;
	private User user;
	
	public PrivateEmbedBuilder(User user) {
		this.user = user;
		embedBuilder = new EmbedBuilder();
		allPrivateEmbedBuilders.add(this);
	}
	public EmbedBuilder getBuilder() {
		return this.embedBuilder;
	}
	
	/**
	 * 
	 * @param u
	 * @return The PrivateEmbedBuilder with the specified user, or null if none exists
	 */
	public static PrivateEmbedBuilder getBuilder(User u) {
		for(PrivateEmbedBuilder builder : allPrivateEmbedBuilders) {
			if(builder.getUser().equals(u)) {
				return builder;
			}
		}
		return null;
	}
	
	public static ArrayList<PrivateEmbedBuilder> getAllPrivateEmbedBuilders(){
		return allPrivateEmbedBuilders;
	}
	public User getUser() {
		return user;
	}
	public void setUser(User user) {
		this.user = user;
	}
}
