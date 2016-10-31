package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.*;

import java.net.Socket;
import java.util.List;
import java.util.concurrent.CyclicBarrier;

/**
 * Класс описывает игрока в блэкджек и все его действия
 * Игроком называется обьект этого класса, а юзером реальная персона
 * которая подключается к серверу игры
 */
final class BJGamer extends Gamer {

    /** Флаг того, что игроку больше не нужно карт */
    private boolean done;

    /** Список остальных игроков */
    private List<BJGamer> players;      //Нужно отказаться, ёбать всё через методы дилера

    /** Барьер конца игры */
    private CyclicBarrier endGame;      //Отказаться

    /** Тот прень, что даст нам карту */
    private BJDealer dealer;

    /** Конструктор срабатывающий при подключении юзера
     *
     * @param name      имя игрока
     * @param dealer    тот парень
     * @param incoming  сокет установленный с юзером
     */
    BJGamer(String name, BJDealer dealer, Socket incoming) {
        super(name, incoming);
        this.dealer = dealer;
        endGame = dealer.getEndGame();
        players = dealer.getPlayers();
        out.println("\nКарочи, у тебя в начале есть 1000 условных бабосов. Из них ты делаешь ставку\n" +
                "hit - это взять ыйсчо карту\n" +
                "stand - типа хватит\n" +
                "Остальные я ещё не далал\n" +
                "exit можно ввести только когда делаешь ставку. тоже пока просто фича\n" +
                "Пожалуйста не нарушай ее, а то сервак упадёт :)\n");
    }

    /** Проверка закончил ли игрок свою руку */
    boolean isDone() {
        return done;
    }

    /** Выход юзера */
    @Override
    public void exit() {
        super.exit();
        dealer.arrivePhaser(false);     //Отключаемся от фазера
        players.remove(this);           //Удаляем себя из списка играющих
    }

    /** Добавляем карту к руке */
    @Override
    public void takeCard(Card card){
        super.takeCard(card);
        out.println("Your hand: " + hand + " (" + hand.getPoints() + ")");
    }

    /** Юзер делает ставку, или выходит
     *
     * @return зачем я вообще чтото возвращаю
     */
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

    /** Юзер делает ход */
    private void makeMove() {

        if (hand.getPoints() >= 21) {
            // При наборе игроком 21 и более очка выставляется флаг завершения
            if (hand.isBlackJack()) out.println("BlackJack!");
            else if(hand.getPoints() > 21) out.println("Busting");
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
                    default:
                        move = null;
                        break;
                }
            } while (move == null);
        }
    }

    /**
     * Ход одной раздачи
     */
    private void game(){
        synchronized (this) {
            //0 фаза
            //Игроки делают ставки или уходят
            hand = new Hand();
            done = false;
            if(!setBet()) return;
            dealer.arrivePhaser(true);

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
                dealer.arrivePhaser(!done);
            }


            endGame = dealer.getEndGame();
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
