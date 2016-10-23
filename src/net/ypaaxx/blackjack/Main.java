package net.ypaaxx.blackjack;

import java.util.*;
import java.util.concurrent.Phaser;

public class Main {

    private static List <BJGamer> players;
    private static final int NUMBER_OF_PLAYER = 4;
    private static Phaser phaser;
    private static Diller diller;

    private static class Diller extends Thread {

        private Pack pack;
        private Hand hand;

        public Diller() {
            super("diller");
            pack = new Pack();
            hand = new Hand();
            start();
        }

        private boolean isAllDone(List <BJGamer> players){
            boolean allDone = true;
            for (BJGamer player:players) {
                allDone = allDone & player.isDone();
            }
            return allDone;
        }

        @Override
        public void run() {

            System.out.println(getName() + ": start");
            //0 отмашка
            phaser.arriveAndAwaitAdvance();

            // раздаем карты

            hand.add(pack.getRandomCard());
            hand.add(pack.getRandomCard());
            System.out.println(getName() + ": " + " *" + hand.getLast());
            for (BJGamer player:players) {
                synchronized (player) {
                    player.takeCard(pack.getRandomCard());
                    player.takeCard(pack.getRandomCard());
                    player.notify();
                }
            }

            do {
                // игроки сделали ход
                phaser.arriveAndAwaitAdvance();
                for (BJGamer player:players) {
                    synchronized (player) {
                        if (!player.isDone()) player.takeCard(pack.getRandomCard());
                        player.notify();
                    }
                }
            } while (!isAllDone(players));

            //раздаём себе
            System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
            while (hand.getPoints() < 17) {
                hand.add(pack.getRandomCard());
                System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
            }

            //Публикация результатов
            for (BJGamer player:players) {
                player.payTime(player.getHand().compareTo(hand) > 0);
                if (player.getHand().compareTo(hand) > 0)
                    System.out.println(getName() + ": выйграл игрок " + player.getName() + " (" + player.getBankroll() + ")");
                else if (player.getHand().compareTo(hand) == 0)
                    System.out.println(getName() + ": ничья с " + player.getName() + " (" + player.getBankroll() + ")");
                else if (player.getHand().compareTo(hand) < 0)
                    System.out.println(getName() + ": выйграло казино " + player.getName() + " (" + player.getBankroll() + ")");
            }
        }
    }

    private static class BJGamer extends Gamer {

        private String move;
        private boolean done;

        public BJGamer(String name) {
            super(name);
            start();
        }

        public boolean isDone() {
            return done;
        }

        private void makeMove() {
            synchronized (System.in) {
                System.out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
                if (hand.getPoints() >= 21) {
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

        @Override
        public synchronized void run() {
            System.out.println(getName() + ": start");
            hand = new Hand();
            done = false;

            setBet();
            // 0 Отмашка
            phaser.arriveAndAwaitAdvance();

            while (!done) {
                //Ждём раздачи карт

                try{
                    wait();
                }catch (InterruptedException e){
                    interrupted();
                }

                makeMove();
                if(done) phaser.arriveAndDeregister();
                else phaser.arriveAndAwaitAdvance();
            }

            /*
            //новая игра
            synchronized (System.in) {
                Scanner in = new Scanner(System.in);
                String againOrExit = null;
                while (againOrExit == null) {
                    switch (againOrExit = in.nextLine()) {
                        case "again":
                            System.out.println(getName() + ": " + againOrExit);
                            phaser.arriveAndAwaitAdvance();
                            break;
                        case "exit":
                            System.out.println(getName() + ": " + againOrExit);
                            return;
                        default:
                            againOrExit = null;
                            break;
                    }
                }
            }
            */
        }
    }

    public static void main(String[] args) {

        players = new ArrayList<BJGamer>(NUMBER_OF_PLAYER);
        Diller diller = new Diller();
        phaser = new Phaser(3);

        players.add(new BJGamer("gamer1"));
        players.add(new BJGamer("gamer2"));
    }
}

