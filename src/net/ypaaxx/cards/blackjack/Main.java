package net.ypaaxx.cards.blackjack;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    private static LinkedList<BJGamer> gamers;

    public static void main(String[] args) {
        gamers = new LinkedList<BJGamer>();
        BJDealer dealer = new BJDealer(gamers);

        try (ServerSocket server = new ServerSocket(8189)){
            while(true){
                Socket incoming = server.accept();
                gamers.addLast(new BJGamer("gamer", dealer, incoming));
                Thread.sleep(5000);
            }
        }catch (Exception e){

        }


    }
}

