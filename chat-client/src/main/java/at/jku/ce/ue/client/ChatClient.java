package at.jku.ce.ue.client;

import akka.actor.ActorRef;
import akka.actor.ActorSelection;
import akka.actor.ActorSystem;
import akka.actor.Props;
import at.jku.ce.ue.api.*;
import at.jku.ce.ue.data.Room;
import at.jku.ce.ue.helper.CEHelper;
import com.typesafe.config.ConfigFactory;

import java.util.Scanner;

public class ChatClient {

    public static void main(String[] args) {

        Room curRoom = null;
        String curRoomString = "", text = "", service ="";

        ActorSystem chatClientSystem = ActorSystem.create("Chat-Slave");
        Scanner scanner = new Scanner(System.in);
        ActorRef acthor = chatClientSystem.actorOf(Props.create(ChatClientActor.class), "services-actor");
        CEHelper ceHelper = new CEHelper(chatClientSystem, ConfigFactory.load("application.conf"));

        System.out.println("Hello. This is the Communication Engineering Chat Client. /n " +
                "Available operations: /n /esc to exit /n /services to list chat services /n /joinroom x to join a specific room /n" +
                "/leaveroom to leave the current room /n /showrooms to show all available rooms /n /send to send a text");
        text =  scanner.nextLine();

        while (!text.equals("/esc")) {
            if(text.equals("/services")) {
                ActorSelection chats = chatClientSystem.actorSelection(ceHelper.getChatServiceRegistry());
                chats.tell(new GetAvailableChatServices(), acthor);
            }
            else if(text.startsWith("/send")) {
                String tempText = text.substring(5);
                ActorSelection send = chatClientSystem.actorSelection(service);
                send.tell(new SendMessage(curRoom,tempText),acthor);
            }

            else if(text.equals("/leaveroom"))
            {
                if(!curRoomString.equals(""))
                {
                    ActorSelection leaveRoom = chatClientSystem.actorSelection(service);
                    for (Room x : ChatClientActor.getStaticRoomSet()) {
                        if (x.getName().equals(curRoomString)) {
                            curRoom = x;
                            leaveRoom.tell(new LeaveRoom(curRoom), acthor);
                            curRoomString ="";
                        }
                    }
                }
            }
            else if(text.startsWith("/showrooms")) {
                String [] split = text.split(" ");
                service = split[1];
                ActorSelection showRooms = chatClientSystem.actorSelection(service);
                showRooms.tell(new GetAvailableRooms(), acthor);
            }
            else if(text.startsWith("/joinroom")) {
                if(service.isEmpty()) {
                    System.out.println("Choose on of the following:");
                    ActorSelection chats = chatClientSystem.actorSelection(ceHelper.getChatServiceRegistry());
                    chats.tell(new GetAvailableChatServices(), acthor);
                    service = scanner.nextLine();
                }
                String [] s = text.split(" ");
                curRoomString= s[1];
                ActorSelection joinRoom = chatClientSystem.actorSelection(service);
                for(Room x : ChatClientActor.getStaticRoomSet()) {
                    if(x.getName().equals(curRoomString))
                    {
                        curRoom = x;
                        joinRoom.tell(new JoinRoom(curRoom , acthor.toString()), acthor);
                    }
                }
            } else {
                System.out.println("Please try again.");
                System.out.println("Operations: /n /esc to exit /n /services to list chat services /n /joinroom x to join a specific room /n" +
                        "/leaveroom to leave the current room /n /showrooms to show all available rooms /n /send to send a text");
            }
            text = scanner.nextLine();
        }
        System.out.println("Good Bye. Have a nice day!");
    }
}