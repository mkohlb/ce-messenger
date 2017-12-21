package at.jku.ce.ue.client;

import akka.actor.ActorSystem;

public class ChatClient {

    public static void main(String[] args) {

        ActorSystem chatClientSystem = ActorSystem.create("chat-client");

    }

}
