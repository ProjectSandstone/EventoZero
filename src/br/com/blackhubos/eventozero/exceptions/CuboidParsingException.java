/**
 *
 * This file ("CuboidLoadException.java") is part from EventoZero (Alpha) Project.
 *
 * Copyright (c) 2015 ReedLine Chambers. All rights reserved.
 * REEDLINE CORPORATION, PROPRIETARY/CONFIDENTIAL. Use its subject to license terms (You can see it in license.txt file)
 *
 */
package br.com.blackhubos.eventozero.exceptions;

import java.util.Optional;

import br.com.blackhubos.eventozero.factory.Event;

public final class CuboidParsingException extends Throwable
{

	private static final long serialVersionUID = 9000866786168535204L;
	private Optional<String> min = Optional.empty(), max = Optional.empty();
	private Optional<Event> event = Optional.empty();
	private String reason = "Unknown reason", action = "Unknown action";

	public CuboidParsingException(final String min, final String max, final Event event, final String reason, final String action)
	{
		super("Failed to parse a Cuboid in " + (event != null ? event.getName() : "(event is null)") + " when " + action + "(" + reason + ") at min=" + ((min != null) && !min.isEmpty() ? min : "(min point is null)") + " and max=" + ((max != null) && !max.isEmpty() ? max : "(max point is null)") + ".");
		this.min = Optional.of(min);
		this.max = Optional.of(max);
		this.event = Optional.of(event);
		this.reason = reason;
		this.action = action;
	}

	@Override
	public String getMessage()
	{
		return "Failed to parse a Cuboid in " + (this.event.isPresent() ? this.event.get().getName() : "(event is null)") + " when " + this.action + "(" + this.reason + ") at min=" + ((this.min.isPresent()) && !this.min.get().isEmpty() ? this.min : "(min point is null)") + " and max=" + ((this.max.isPresent()) && !this.max.get().isEmpty() ? this.max : "(max point is null)") + ".";
	}

	public String getAction()
	{
		return this.action;
	}

	public String getReason()
	{
		return this.reason;
	}

	public Optional<String> getMaximumPoint()
	{
		return this.max;
	}

	public Optional<String> getMinimumPoint()
	{
		return this.min;
	}

	public Optional<Event> getEvent()
	{
		return this.event;
	}

}
