package at.jku.ce.ue.service;

import akka.actor.AbstractActor;

public class SampleActor extends AbstractActor {

	@Override
	public Receive createReceive() {
		return receiveBuilder().build();
	}

}
