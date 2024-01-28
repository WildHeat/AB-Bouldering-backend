package com.abb.abbouldering.exception;

public class EventIsFullyBookedException extends Exception{
	
	private static final long serialVersionUID = 626459298231721570L;

	public EventIsFullyBookedException() {
		super("Event is fully booked");
	}
	
}
