package at.jku.ce.ue.service;

import akka.actor.ActorRef;
import akka.actor.ActorSystem;
import akka.actor.Props;
import at.jku.ce.ue.helper.CEHelper;
import com.typesafe.config.Config;
import com.typesafe.config.ConfigFactory;

public class ChatService {

	public static void main(String[] args) {

		Config config = ConfigFactory.load("application.conf");
		ActorSystem chatServiceSystem = ActorSystem.create("chat-service");
		CEHelper helper = new CEHelper(chatServiceSystem, config);

		// sample actor
		ActorRef sampleActor = chatServiceSystem.actorOf(Props.create(SampleActor.class), "sample-actor");

		// get fully qualified path of sample actor
		System.out.println(helper.getActorPath(sampleActor));

	}

}
