package io.github.shadowdevsthings.pseudoscience;

import net.minecraft.util.StringIdentifiable;

public enum ConduitConnection implements StringIdentifiable
{
	CONDUIT("conduit"),
	CONNECTION("connection"),
	NONE("none"),
	DISCONNECTED("disconnected");

	private final String name;

	ConduitConnection(String name) {
		this.name = name;
	}

	public String toString() {
		return this.asString();
	}

	@Override
	public String asString() {
		return this.name;
	}

	public boolean isConnected() {
		return this != NONE && this != DISCONNECTED;
	}

	public boolean isDisconnected() {return this == DISCONNECTED;}
}
