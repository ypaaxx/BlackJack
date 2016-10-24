package net.ypaaxx.cards;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.Scanner;

public class Gamer extends Thread {

    private static int numGamer;

    protected int bankroll;
    protected Hand hand;
    protected int bet;
    private Socket socket;
    protected Scanner in;
    protected PrintWriter out;

    public int getBankroll(){
        return bankroll;
    }

    public void exit(){
        hand = null;
        bet = 0;
        try {
            socket.close();
        }catch (Exception e){

        }
    }

    public void sendText(String str){
        out.println(str);
    }

    public void payTime(int isWin){
        if (isWin > 0) {
            bankroll += 2*bet;
            if (hand.isBlackJack()) bankroll += bet/2;
        } else if (isWin == 0) bankroll += bet;
    }

    public Hand getHand(){
        return hand;
    }

    public void takeCard(Card card){
        this.hand.add(card);
    }

    public Gamer(String name, Socket incoming){
        super(name + ++numGamer);
        try {
            socket = incoming;
            in = new Scanner(socket.getInputStream());
            out = new  PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){

        }
        bankroll = 1000;
    }

}
