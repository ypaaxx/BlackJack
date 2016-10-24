package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.Gamer;
import net.ypaaxx.cards.Hand;


import java.util.ArrayList;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;
import java.util.concurrent.Phaser;


class BJGamer extends Gamer {

    private String move;
    private boolean done;
    private ArrayList<BJGamer> players;
    private Phaser phaser;
    private CyclicBarrier endGame;
    private BJDiller diller;

    public BJGamer(String name, BJDiller diller) {
        super(name);
        this.diller = diller;
        phaser = diller.getPhaser();
        endGame = diller.getEndGame();
        players = diller.getPlayers();
        start();
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void exit(){
        super.exit();
        System.out.println(getName() + ": exit");
        phaser.arriveAndDeregister();
        players.remove(this);
    }

    protected boolean setBet(){
        synchronized (System.out) {
            System.out.println(getName() + ": сделайте ставку");

            Scanner in = new Scanner(System.in);
            do {
                String strBet = in.next();
                if (strBet.equals("exit")) {
                    exit();
                    return false;
                }
                else {
                    try {
                        bet = new Integer(strBet);
                        if (bet <= 0 || bet > bankroll) throw new NumberFormatException();
                    } catch (Exception e) {
                        System.out.println(getName() + ": ещё раз попробуй, умник");
                        continue;
                    }
                }
            }while (bet <= 0 || bet > bankroll) ;
        }
        System.out.println(getName() + ": ставит " + bet);
        bankroll -= bet;
        return true;
    }

    private void makeMove() {
        synchronized (System.in) {
            System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
            if (hand.getPoints() >= 21) {
                if (hand.isBlackJack()) System.out.println(getName() + ": BlackJack!");
                done = true;
            } else {
                System.out.println(getName() + ": Сделать ход hit/take ");
                Scanner in = new Scanner(System.in);
                do {
                    switch (move = in.nextLine()) {
                        case "hit":
                            System.out.println(getName() + ": " + move);
                            done = true;
                            break;
                        case "take":
                            System.out.println(getName() + ": " + move);
                            break;
                        case "what":
                            System.out.println(getName() + ": " + move);
                            System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
                            move = null;
                            break;
                        default:
                            System.out.println(getName() + ": " + move);
                            move = null;
                            break;
                    }
                } while (move == null);
            }
        }
    }

    private void game(){
        synchronized (this) {
            //0 фаза
            //Игроки делают ставки или уходят
            System.out.println(getName() + ": start");
            hand = new Hand();
            done = false;
            if(!setBet()) return;
            phaser.arriveAndAwaitAdvance();

            //1 фаза
            //Состав игроков сформирован
            //Диллер раздает карты, пока игроки ожидают
            while (!done) {
                try {
                    wait();
                } catch (InterruptedException e) {
                    interrupted();
                }
                makeMove();
                if (done) phaser.arriveAndDeregister();
                else phaser.arriveAndAwaitAdvance();
            }


            endGame = diller.getEndGame();
            try {
                wait();
            } catch (InterruptedException e) {
                interrupted();
            }
        }

        phaser = diller.getPhaser();
        phaser.register();
        try{
            endGame.await();
        }catch (Exception e){

        }
    }

    @Override
    public  void run() {
        while (players.contains(this)) {
            game();
        }
    }
}
