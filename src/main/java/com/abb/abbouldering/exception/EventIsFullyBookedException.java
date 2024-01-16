package com.abb.abbouldering.exception;

public class EventIsFullyBookedException extends Exception{
	
	public EventIsFullyBookedException() {
		super("Event is fully booked");
	}
	
}
