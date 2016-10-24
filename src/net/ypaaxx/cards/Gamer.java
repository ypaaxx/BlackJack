package net.ypaaxx.cards;

import java.util.Scanner;

public class Gamer extends Thread {

    protected int bankroll;
    protected Hand hand;
    protected int bet;

    public int getBankroll(){
        return bankroll;
    }

    public void exit(){
        hand = null;
        bet = 0;
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

    public Gamer(String name){
        super(name);
        bankroll = 1000;
    }

}
