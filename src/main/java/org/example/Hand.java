package org.example;

import java.util.*;
import java.util.stream.Collectors;

public class Hand implements Comparable<Hand> {
    private List<String> cards;

    public Hand(List<String> playerCards, List<String> communityCards) {
        this.cards = new ArrayList<>(playerCards);
        this.cards.addAll(communityCards);
        Collections.sort(this.cards, (a, b) -> getRank(b).compareTo(getRank(a))); // Сортировка по убыванию
    }

    private Integer getRank(String card) {
        String rank = card.substring(0, card.length() - 1); // Получаем номинал
        switch (rank) {
            case "2": return 2;
            case "3": return 3;
            case "4": return 4;
            case "5": return 5;
            case "6": return 6;
            case "7": return 7;
            case "8": return 8;
            case "9": return 9;
            case "10": return 10;
            case "J": return 11;
            case "Q": return 12;
            case "K": return 13;
            case "A": return 14;
            default: return 0;
        }
    }

    private String getSuit(String card) {
        return card.substring(card.length() - 1); // Получаем масть
    }

    public HandRank evaluateHand() {
        Map<String, List<String>> rankCounts = new HashMap<>();
        Map<String, Integer> suitCounts = new HashMap<>();

        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1);
            rankCounts.putIfAbsent(rank, new ArrayList<>());
            rankCounts.get(rank).add(card);

            String suit = getSuit(card);
            suitCounts.put(suit, suitCounts.getOrDefault(suit, 0) + 1);
        }

        boolean isFlush = suitCounts.values().stream().anyMatch(count -> count >= 5);
        List<Integer> counts = rankCounts.values().stream().map(List::size).sorted(Comparator.reverseOrder()).collect(Collectors.toList());
        boolean isStraight = checkStraight();

        // Проверка на стрит и флеш
        if (isFlush && isStraight) {
            return (getRank(cards.get(0)) == 14) ? HandRank.ROYAL_FLUSH : HandRank.STRAIGHT_FLUSH;
        }
        if (counts.get(0) == 4) return HandRank.FOUR_OF_A_KIND;
        if (counts.get(0) == 3 && counts.size() > 1 && counts.get(1) == 2) return HandRank.FULL_HOUSE;
        if (isFlush) return HandRank.FLUSH;
        if (isStraight) return HandRank.STRAIGHT;
        if (counts.get(0) == 3) return HandRank.THREE_OF_A_KIND;
        if (counts.get(0) == 2 && counts.size() > 1 && counts.get(1) == 2) return HandRank.TWO_PAIR;
        if (counts.get(0) == 2) return HandRank.ONE_PAIR;
        return HandRank.HIGH_CARD;
    }

    private boolean checkStraight() {
        Set<Integer> uniqueRanks = new HashSet<>();
        for (String card : cards) {
            uniqueRanks.add(getRank(card));
        }

        List<Integer> sortedRanks = new ArrayList<>(uniqueRanks);
        Collections.sort(sortedRanks);

        // Проверка обычного стрита
        for (int i = 0; i <= sortedRanks.size() - 5; i++) {
            if (sortedRanks.get(i + 4) - sortedRanks.get(i) == 4) {
                return true;
            }
        }

        // Проверка стрита с тузом как низкой картой (A, 2, 3, 4, 5)
        if (uniqueRanks.contains(14) && uniqueRanks.contains(2) && uniqueRanks.contains(3) && uniqueRanks.contains(4) && uniqueRanks.contains(5)) {
            return true;
        }

        return false;
    }

    @Override
    public int compareTo(Hand other) {
        HandRank thisRank = this.evaluateHand();
        HandRank otherRank = other.evaluateHand();

        int rankComparison = thisRank.compareTo(otherRank);
        if (rankComparison != 0) {
            return rankComparison; // Сравниваем по рангу комбинации
        }

        // Если у нас одинаковая комбинация, сравниваем старшие карты
        List<Integer> thisHighCards = getHighCards();
        List<Integer> otherHighCards = other.getHighCards();

        for (int i = 0; i < Math.min(thisHighCards.size(), otherHighCards.size()); i++) {
            int comparison = thisHighCards.get(i).compareTo(otherHighCards.get(i));
            if (comparison != 0) {
                return comparison; // Возвращаем результат сравнения старших карт
            }
        }

        return 0; // Если все старшие карты равны, руки равны
    }

    private List<Integer> getHighCards() {
        Map<String, List<String>> rankCounts = new HashMap<>();
        for (String card : cards) {
            String rank = card.substring(0, card.length() - 1);
            rankCounts.putIfAbsent(rank, new ArrayList<>());
            rankCounts.get(rank).add(card);
        }

        // Сортируем ранги по убыванию и берем старшие карты в зависимости от комбинации
        List<Integer> highCards = new ArrayList<>();
        List<Map.Entry<String, List<String>>> sortedEntries = rankCounts.entrySet().stream()
                .sorted((a, b) -> getRank(b.getKey()).compareTo(getRank(a.getKey())))
                .collect(Collectors.toList());

        for (Map.Entry<String, List<String>> entry : sortedEntries) {
            highCards.add(getRank(entry.getKey())); // Добавляем ранг старшей карты
        }

        // Если у нас фулл-хаус, добавляем тройку и пару
        if (highCards.size() >= 2 && (rankCounts.values().stream().anyMatch(list -> list.size() >= 3))) {
            // Добавляем тройку
            highCards.add(highCards.get(0)); // Тройка
            // Добавляем пару
            if (highCards.size() > 1) {
                highCards.add(highCards.get(1)); // Пара
            }
        }

        return highCards;
    }
}
