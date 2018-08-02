package com.tybug.spybot.util;
/**
 * Used for easily creating new threads
 * @author Liam DeVoe
 */
public class ThreadCreator {

	/**
	 * Creates a thread, starts it, and returns it.
	 * @param run A runnable object to pass to the thread
	 * @return Thread the thread, with run passed and already started
	 */
    public static Thread create(Runnable run){
        Thread thread = new Thread(run);
        thread.start();
        return thread;
    }
    
}