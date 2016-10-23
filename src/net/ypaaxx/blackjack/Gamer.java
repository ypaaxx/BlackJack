package net.ypaaxx.blackjack;

import java.util.Scanner;

class Gamer extends Thread {

    protected int bankroll;
    protected Hand hand;
    private int bet;

    public int getBankroll(){
        return bankroll;
    }

    protected void setBet(){
        synchronized (System.out) {
            System.out.println(getName() + ": сделайте ставку");
            Scanner in = new Scanner(System.in);
            do {
                bet = in.nextInt();
            } while (bet > bankroll);
        }
        bankroll -= bet;
    }

    public void payTime(boolean isWin){
        if (isWin) bankroll += 2*bet;
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
