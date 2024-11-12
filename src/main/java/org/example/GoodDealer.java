package org.example; //указываем пакет

import java.util.ArrayList; //указываем импорты
import java.util.Collections;
import java.util.HashSet;
import java.util.List;

public class GoodDealer implements Dealer { //создаем класс хороший диллер который реализует интерфейс Dealer
    //определяем поля класса
    private List<String> deck; //это колода
    private int currentCardIndex; //это переменная которая хранит текущий индекс карты в колоде

    public GoodDealer() { //это конструктор
        this.deck = createDeck(); //здесь вызвается метод создания колоды карт и присваивается переменной
        this.currentCardIndex = 0; //индекс списка начинается с 0
        Collections.shuffle(deck); //это метод класса коллекций который перемешивает элементы в списке, в данном случаем колоду карт
    }

    private List<String> createDeck() { //метод возвращает список строк
        List<String> deck = new ArrayList<>(); //создаем объект класса ArrayList<>(), который будет хранить карты
        String[] suits = {"C", "D", "H", "S"}; //определяем масти C — червы (Clubs) D — бубны (Diamonds) H — червы (Hearts) S — пики (Spades)
        String[] ranks = {"2", "3", "4", "5", "6", "7", "8", "9", "10", "J", "Q", "K", "A"}; //определяем ранги номера от 2 до 10, а также валет (J), дама (Q), король (K) и туз (A)
        //создаем карты
        for (String suit : suits) { //перебираем масти
            for (String rank : ranks) { //перебираем ранги
                deck.add(rank + suit); //добавляем созданную строку в список deck
            }
        }
        return deck; //возвращаем колоду
    }

    private String dealCard() { //это метод раздачи колоды
        if (currentCardIndex >= deck.size()) { //проверяем достигнут ли конец колоды
            throw new IllegalStateException("Закончились карты в колоде");
        }
        return deck.get(currentCardIndex++); //возвращаем карту по текущему индексу и увеличиваем на 1
    }

  //переопределяем метод интерфейса раздача карт игрокам
    @Override
    public Board dealCardsToPlayers() {
        String playerOne = dealCard() + dealCard(); // Раздаем две карты первому игроку
        String playerTwo = dealCard() + dealCard(); // Раздаем две карты второму игроку

        // Убедимся, что карты уникальны
        if (playerOne.equals(playerTwo)) {
            throw new IllegalStateException("Карты игроков не могут быть одинаковыми");
        }

        return new Board(playerOne, playerTwo, null, null, null);
    }

    @Override //переопределяем метод интерфейса раздача 3 карт общих для игроков
    public Board dealFlop(Board board) {
        String flop = dealCard() + dealCard() + dealCard(); //раздача 3 карт
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), flop, null, null); //возвращаем на игровую доску уже разданные карты игрока и флоп
    }

    @Override //переопределяем метод интерфейса раздача еще 1 общей карты для игроков
    public Board dealTurn(Board board) {
        String turn = dealCard();
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), turn, null); //возвращаем на игровую доску уже разданные карты игрока, флоп и турн
    }

    @Override //переопределяем метод интерфейса раздача еще 1 общей карты для игроков
    public Board dealRiver(Board board) {
        String river = dealCard();
        return new Board(board.getPlayerOne(), board.getPlayerTwo(), board.getFlop(), board.getTurn(), river); //возвращаем на игровую доску уже разданные карты игрока, флоп и турн и ривер
    }

    @Override
    public PokerResult decideWinner(Board board) throws InvalidPokerBoardException {
        validateBoard(board);

        List<String> playerOneCards = new ArrayList<>();
        Collections.addAll(playerOneCards, board.getPlayerOne().split("(?<=\\G.{2})"));
        List<String> playerTwoCards = new ArrayList<>();
        Collections.addAll(playerTwoCards, board.getPlayerTwo().split("(?<=\\G.{2})"));

        List<String> communityCards = new ArrayList<>();
        if (board.getFlop() != null) {
            Collections.addAll(communityCards, board.getFlop().split("(?<=\\G.{2})"));
        }
        if (board.getTurn() != null) {
            communityCards.add(board.getTurn());
        }
        if (board.getRiver() != null) {
            communityCards.add(board.getRiver());
        }

        Hand playerOneHand = new Hand(playerOneCards, communityCards);
        Hand playerTwoHand = new Hand(playerTwoCards, communityCards);

        int comparisonResult = playerOneHand.compareTo(playerTwoHand);

        if (comparisonResult > 0) {
            return PokerResult.PLAYER_ONE_WIN;
        } else if (comparisonResult < 0) {
            return PokerResult.PLAYER_TWO_WIN;
        } else {
            return PokerResult.DRAW;
        }
    }

    private void validateBoard(Board board) {
        HashSet<String> allCards = new HashSet<>();

        // Разделяем карты игрока 1
        Collections.addAll(allCards, board.getPlayerOne().split("(?<=\\G(?:\\d{1,2}|[JQKA])[CDHS])"));

        // Разделяем карты игрока 2
        Collections.addAll(allCards, board.getPlayerTwo().split("(?<=\\G(?:\\d{1,2}|[JQKA])[CDHS])"));

        // Разделяем карты флопа, если они есть
        if (board.getFlop() != null) {
            Collections.addAll(allCards, board.getFlop().split("(?<=\\G(?:\\d{1,2}|[JQKA])[CDHS])"));
        }

        // Добавляем карту тёрна, если она есть
        if (board.getTurn() != null) {
            allCards.add(board.getTurn());
        }

        // Добавляем карту ривера, если она есть
        if (board.getRiver() != null) {
            allCards.add(board.getRiver());
        }

        // Проверка на дубликаты
        int expectedCardCount = (board.getPlayerOne().length() + board.getPlayerTwo().length()) / 2 +
                (board.getFlop() != null ? 3 : 0) +
                (board.getTurn() != null ? 1 : 0) +
                (board.getRiver() != null ? 1 : 0);

        if (allCards.size() != expectedCardCount) {
            throw new InvalidPokerBoardException("Найдены дубликаты на игровом столе или неверное количество карт");
        }
    }
    }


