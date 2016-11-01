package net.ypaaxx.cards.blackjack;

import net.ypaaxx.cards.*;
import java.net.Socket;
import java.util.NoSuchElementException;
import java.util.Timer;

/**
 * Класс описывает игрока в блэкджек и все его действия
 * Игроком называется обьект этого класса, а юзером реальная персона
 * которая подключается к серверу игры
 */
final class BJGamer extends Gamer {

    /** Флаг того, что игроку больше не нужно карт */
    private boolean done;

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
        out.println("\nКарочи, у тебя в начале есть 1000 условных бабосов. Из них ты делаешь ставку\n" +
                "hit - это взять ыйсчо карту\n" +
                "stand - типа хватит\n" +
                "Остальные я ещё не далал\n" +
                "exit можно ввести только когда делаешь ставку. тоже пока просто фича\n" +
                "Пожалуйста не нарушай ее, а то сервак упадёт :)\n");
    }

    /** Проверка закончил ли игрок свою руку */
    public boolean isDone() {
        return done;
    }

    /** Выход юзера */
    @Override
    public void exit() {
        super.exit();
        dealer.arrivePhaser(false);         //Отключаемся от фазера
        dealer.getPlayers().remove(this);   //Удаляем себя из списка играющих
    }

    /** Добавляем карту к руке */
    @Override
    public void takeCard(Card card){
        super.takeCard(card);
        out.println("Your hand: " + hand + " (" + hand.getPoints() + ")");
    }

    /** Юзер делает ставку, или выходит
     *
     * @return false если выходит
     */
    private boolean setBet(){
        out.println("Make bet (max " + bankroll + ")");
        timer = new Timer();
        timer.schedule(new Timeout(), 30000 );
        do {
            String strBet;
            try{
                strBet = in.next();
            }catch (NoSuchElementException e){
                exit();
                return false;
            }
            if (strBet.equals("exit")) {
                exit();
                return false;
            }
            else {
                try {
                    bet = new Integer(strBet);
                    if ((bet <= 0) || (bet > bankroll)) throw new NumberFormatException();
                } catch (NumberFormatException e) {
                    out.println(getName() + ": try again, bitch");
                }
            }
        }while (bet <= 0 || bet > bankroll) ;
        timer.cancel();
        bankroll -= bet;
        return true;
    }

    /** Расчёт по результатам игры
     *
     * @param isWin Положителен в случае победы
     *              Нулевой при ничьей
     *              Отрицательный при проигрыше
     */
    void payTime(int isWin){
        if (isWin > 0) {
            bankroll += 2*bet;
            if (hand.isBlackJack()) bankroll += bet/2;
        } else if (isWin == 0) bankroll += bet;
        bet = 0;
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
            move = in.nextLine();
            do {
                switch (move) {
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
            //0 фаза:
            //Игроки делают ставки или уходят
            hand = new Hand();
            done = false;
            if(!setBet()) return;
            dealer.arrivePhaser(true);

            //1 фаза:
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

            try {
                wait();
            } catch (InterruptedException e) {
                interrupted();
            }
        }

        try{
            dealer.getEndGame().await();
        }catch (Exception e){

        }
    }

    @Override
    public  void run() {
        while (dealer.getPlayers().contains(this)) {
            game();
            if (bankroll == 0) exit();
        }
    }
}
