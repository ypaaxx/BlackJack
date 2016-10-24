package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.*;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

final class BJGamer extends Gamer {


    private boolean done;
    private List<BJGamer> players;
    private CyclicBarrier endGame;
    private BJDiller diller;


    BJGamer(String name, BJDiller diller, Socket incoming) {
        super(name, incoming);
        this.diller = diller;
        endGame = diller.getEndGame();
        players = diller.getPlayers();
        out.println("\nКарочи, у тебя в начале есть 1000 условных бабосов. Из них ты делаешь ставку\n" +
                "hit - это взять ыйсчо карту\n" +
                "stand - типа хватит\n" +
                "Остальные я ещё не далал\n" +
                "exit можно ввести только когда делаешь ставку. тоже пока просто фича\n" +
                "Пожалуйста не нарушай ее, а то сервак упадёт :)\n");
    }

    boolean isDone() {
        return done;
    }

    @Override
    public void exit() {
        super.exit();
        diller.arrivePhaser(false);
        players.remove(this);
    }

    @Override
    public void takeCard(Card card){
        super.takeCard(card);
        out.println("Your hand: " + hand + " (" + hand.getPoints() + ")");
    }

    private boolean setBet(){
        out.println("Make bet");

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
                    out.println(getName() + ": try again, bitch");
                    continue;
                }
            }
        }while (bet <= 0 || bet > bankroll) ;
        bankroll -= bet;
        return true;
    }

    private void makeMove() {

        if (hand.getPoints() >= 21) {
            if (hand.isBlackJack()) out.println("BlackJack!");
            done = true;
        } else {
            out.println("hit/stand");
            String move;
            do {
                switch (move = in.nextLine()) {
                    case "stand":
                        done = true;
                        break;
                    case "hit":
                        break;
                    case "what":
                        move = null;
                        break;
                    default:
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
                diller.arrivePhaser(!done);
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
