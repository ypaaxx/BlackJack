package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.Gamer;
import net.ypaaxx.cards.Hand;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.CyclicBarrier;



class BJGamer extends Gamer {

    private String move;
    private boolean done;
    private List<BJGamer> players;
    private CyclicBarrier endGame;
    private BJDiller diller;
    private Socket socket;
    private Scanner in;
    private PrintWriter out;

    public BJGamer(String name, BJDiller diller, Socket incoming) {
        super(name);

        try {
            socket = incoming;
            in = new Scanner(socket.getInputStream());
            out = new  PrintWriter(socket.getOutputStream(), true);
        }catch (IOException e){

        }
        this.diller = diller;
        endGame = diller.getEndGame();
        players = diller.getPlayers();
    }

    public void sendText(String str){
        out.println(str);
    }

    public boolean isDone() {
        return done;
    }

    @Override
    public void exit() {
        super.exit();
        diller.arrivePhaser(false);
        players.remove(this);
        try {
            socket.close();
        }catch (Exception e){

        }
    }

    protected boolean setBet(){
        out.println("Cделайте ставку");

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
                    out.println(getName() + ": ещё раз попробуй, умник");
                    continue;
                }
            }
        }while (bet <= 0 || bet > bankroll) ;
        //out.println(getName() + ": ставит " + bet);
        bankroll -= bet;
        return true;
    }

    private void makeMove() {
        out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
        if (hand.getPoints() >= 21) {
            if (hand.isBlackJack()) out.println( "BlackJack!");
            done = true;
        } else {
            out.println("Сделать ход hit/take ");
            do {
                switch (move = in.nextLine()) {
                    case "hit":
                        //out.println(getName() + ": " + move);
                        done = true;
                        break;
                    case "take":
                        //out.println(getName() + ": " + move);
                        break;
                    case "what":
                        //out.println(getName() + ": " + move);
                        //out.println(getName() + ": " + hand + " (" + hand.getPoints() + ")");
                        move = null;
                        break;
                    default:
                        //out.println(getName() + ": " + move);
                        move = null;
                        break;
                }
            } while (move == null);
        }
    }

    private void game(){
        synchronized (this) {
            //0 фаза
            //Игроки делают ставки или уходят
            //out.println(getName() + ": start");
            hand = new Hand();
            done = false;
            if(!setBet()) return;
            diller.arrivePhaser(true);

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
                diller.arrivePhaser(!false);
            }


            endGame = diller.getEndGame();
            try {
                wait();
            } catch (InterruptedException e) {
                interrupted();
            }
        }

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
