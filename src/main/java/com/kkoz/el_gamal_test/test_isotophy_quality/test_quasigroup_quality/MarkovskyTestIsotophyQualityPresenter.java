package com.kkoz.el_gamal_test.test_isotophy_quality.test_quasigroup_quality;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;

public class MarkovskyTestIsotophyQualityPresenter {
    private static final List<String> symbols = List.of(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О",
        "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я",
        "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о",
        "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        " ", ",", ".", ":", ";", "+", "-", "*", "/", "%", "?", "!", "(", ")", "[", "]", "{", "}"
    );
    private static final List<Integer> isotophyCycleSizes = List.of(
        2, 3, 5, 7, 11, 13, 17, 19, 31, 37
    );
    @Getter
    private static final int quasigroupMatrixSize = symbols.size();

    private final MarkovskyTestIsotophyQualityView view;

    public MarkovskyTestIsotophyQualityPresenter(MarkovskyTestIsotophyQualityView view) {
        this.view = view;
    }

    public Map<Integer, Integer> randomizeIsotophyBasic() {
        var possibleTransitions = new ArrayList<>(symbols);
        Collections.shuffle(possibleTransitions);
        var transitions = new HashMap<Integer, Integer>();
        for (var i = 0; i < quasigroupMatrixSize; i++) {
            transitions.put(i, symbols.indexOf(possibleTransitions.get(i)));
        }
        return transitions;
    }

    public String randomizeIsotophy() {
        //Строка цикла преобразований
        var result = new StringBuilder();

        //Массив возможных переходов
        var possibleTransitions = new ArrayList<>(symbols);

        for (var size : isotophyCycleSizes) {
            var cycle = new ArrayList<String>(size);
            for (var i = 0; i < size; i++) {
                var randomIndex = (int) (Math.random() * possibleTransitions.size());
                cycle.add(possibleTransitions.get(randomIndex));
                possibleTransitions.remove(randomIndex);
            }
            //Добавляем цикл в строку
            result.append("<")
                .append(String.join("`", cycle))
                .append(">");
        }
        return result.toString();
    }

    private Map<Integer, Integer> getTransitions(String value) {
        //Массив циклов преобразований
        var cycles = new ArrayList<List<String>>();
        //Таблица преобразований
        var result = new HashMap<Integer, Integer>();

        //Цикл пока не закончатся элементы в основной строке
        while (StringUtils.isNotBlank(value)) {
            //Массив элементов цикла
            var cycle = new ArrayList<String>();
            //Индекс начала цикла
            var startCycle = value.indexOf("<");
            //Индекс конца цикла
            var endCycle = value.indexOf(">");

            //Строка одного цикла преобразований
            var cycleString = value.substring(startCycle + 1, endCycle);
            //Оставшаяся основная строка
            value = value.substring(endCycle + 1);

            //Цикл пока не закончатся элементы в одном цикле преобразований
            while (StringUtils.isNotBlank(cycleString)) {
                //Индекс разделителя
                var delimiterIndex = cycleString.indexOf("`");
                //Если есть разделитель
                if (delimiterIndex != -1) {
                    //Запоминаем элемент цикла
                    var character = cycleString.substring(0, delimiterIndex);
                    //Удаляем элемент цикла из строки
                    cycleString = cycleString.substring(delimiterIndex + 1);

                    //Добавляем элемент цикла в цикл
                    cycle.add(character);
                } else {
                    //Добавляем элемент цикла в цикл
                    cycle.add(cycleString);
                    break;
                }
            }

            //Добавляем цикл в массив циклов
            cycles.add(cycle);
        }

        //Цикл по всем циклам
        for (var cycle : cycles) {
            //Цикл по всем элементам цикла
            for (var i = 0; i < cycle.size(); i++) {
                //Получаем следующий элемент цикла
                var after = cycle.get(i);
                //Получаем текущий элемент цикла
                var before = cycle.get(i - 1 < 0 ? cycle.size() - 1 : i - 1);
                //Заполняем таблицу преобразований
                result.put(symbols.indexOf(before), symbols.indexOf(after));
            }
        }

        //Цикл по всем символам
        for (var symbol : symbols) {
            //Если нет преобразования для данного символа, то добавляем его
            result.putIfAbsent(symbols.indexOf(symbol), symbols.indexOf(symbol));
        }

        //Возвращаем таблицу преобразований
        return result;
    }

    public void testIsotophyQuality() {
        var count = 1000;
        var sum = 0D;
        for (var k = 0; k < count; k++) {
            final double[] localSum = {0D};
//            var transitions = getTransitions(randomizeIsotophy());
            var transitions = randomizeIsotophyBasic();
            transitions.forEach((key, value) -> localSum[0] += Math.abs(key - value));
            localSum[0] /= quasigroupMatrixSize;
            System.out.println(localSum[0]);
            sum += localSum[0];
        }
        sum /= count;
        System.out.println(sum);
    }
}
