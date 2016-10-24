package net.ypaaxx.cards.blackjack;

import java.net.ServerSocket;
import java.net.Socket;
import java.util.*;

public class Main {

    private static LinkedList<BJGamer> gamers;
    private static final int NUMBER_OF_PLAYER = 4;

    public static void main(String[] args) {
        int num = 1;
        gamers = new LinkedList<BJGamer>();
        BJDiller diller = new BJDiller(gamers);

        try (ServerSocket server = new ServerSocket(8189)){
            while(true){
                Socket incoming = server.accept();
                gamers.addLast(new BJGamer("gamer"+ num++, diller, incoming));
            }
        }catch (Exception e){

        }


    }
}

