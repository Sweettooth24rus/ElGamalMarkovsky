package com.kkoz.el_gamal_test.text_optimal_isotophy;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.stream.Collectors;

@Service
public class MarkovskyTextOptimalIsotophyPresenter {
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

    private final MarkovskyTextOptimalIsotophyView view;

    private int[][] quasigroupMatrix;

    public MarkovskyTextOptimalIsotophyPresenter(MarkovskyTextOptimalIsotophyView view) {
        this.view = view;
    }

    //Генерация случайной матрицы квазигруппы
    public void randomUpdateQuasigroup() {
        //Матрица квазигруппы
        quasigroupMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        //Массив возможных символов
        var possibleSymbols = new ArrayList<>(symbols);
        //Перемешиваем значения
        Collections.shuffle(possibleSymbols);

        //Массив отступов
        var offset = new int[quasigroupMatrixSize];
        //Цикл по всем строкам
        for (int i = 1; i < quasigroupMatrixSize; i++) {
            //Генерируем случайное число
            var randomNumber = (int) (Math.random() * quasigroupMatrixSize);
            //Если не содержится в массиве отступов, то добавляем
            if (!contains(offset, randomNumber)) {
                offset[i] = randomNumber;
            } else {
                i--;
            }
        }

        //Цикл по всем строкам
        for (int i = 0; i < quasigroupMatrixSize; i++) {
            //Цикл по всем столбцам
            for (int j = 0; j < quasigroupMatrixSize; j++) {
                //Заполняем строки квазигруппы, используя строку возможных символов и отступ
                quasigroupMatrix[i][j] = symbols.indexOf(possibleSymbols.get((j + offset[i]) % quasigroupMatrixSize));
            }
        }
    }

    private boolean contains(int[] array, int number) {
        for (var k = 0; k < array.length; k++) {
            if (array[k] == number) {
                return true;
            }
        }
        return false;
    }

    //Получение цикла преобразований
    private String getCycle(Map<Integer, Integer> transitions) {
        //Строка цикла преобразований
        var result = new StringBuilder();

        //Массив возможных переходов
        var possibleTransitions = new ArrayList<>(transitions.keySet());

        //Цикл пока не закончатся элементы переходов
        while (!possibleTransitions.isEmpty()) {
            //Первый элемент переходов
            var initialTransition = possibleTransitions.get(0);
            //Удаляем первый элемент переходов
            possibleTransitions.remove(initialTransition);
            //Следующий элемент
            var nextTransition = transitions.get(initialTransition);
            //Массив цикла
            var cycle = new ArrayList<Integer>();
            //Добавляем первый элемент цикла
            cycle.add(initialTransition);
            //Цикл пока текущий элемент не равен следующему элементу
            while (!Objects.equals(initialTransition, nextTransition)) {
                //Добавляем следующий элемент в цикл
                cycle.add(nextTransition);
                //Удаляем следующий элемент из переходов
                possibleTransitions.remove(nextTransition);
                //Меняем следующий элемент
                nextTransition = transitions.get(nextTransition);
            }
            //Если цикл не пустой и не единичный
            if (cycle.size() != 1) {
                //Добавляем цикл в строку
                result.append("<")
                    .append(cycle.stream().map(symbols::get).collect(Collectors.joining("`")))
                    .append(">");
            }
        }

        //Возвращаем строку цикла
        return result.toString();
    }

    //Получение таблицы преобразований
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

    //Метод возведения изотопии в степень
    private Map<Integer, Integer> powTransitions(Map<Integer, Integer> oldTransitions, long pow) {
        var symbolsCount = symbols.size();
        //Новая изотопия
        var resultMap = new HashMap<>(oldTransitions);

        var oldIsotophyValues = symbols.stream().map(symbols::indexOf)
            .map(oldTransitions::get)
            .toArray();

        var result = new int[symbolsCount];
        for (var i = 0; i < symbolsCount; i++) {
            result[i] = (int) oldIsotophyValues[i];
        }

        //Цикл по значению степени
        for (var i = 1L; i < pow; i++) {
            var newResult = new int[symbolsCount];
            //Цикл по всем символам
            for (var index = 0; index < symbolsCount; index++) {
                //В качестве ключа записываем символ, а в качестве значения значение прошлой изотопии, которое прошло преобразование через изотопию в первоначальной степени
                newResult[index] = (int) oldIsotophyValues[result[index]];
            }
            result = newResult;
        }

        for (var i = 0; i < symbolsCount; i++) {
            resultMap.put(i, result[i]);
        }

        //Возвращаем новую изотопию
        return resultMap;
    }

    //Метод обратного преобразования
    private Map<Integer, Integer> reverseTransitions(Map<Integer, Integer> oldTransitions) {
        //Новая изотопия
        var result = new HashMap<Integer, Integer>();

        //Цикл по всем символам
        for (var symbol : symbols) {
            //В качестве значения записываем символ, а в качестве ключа значение прошлой изотопии
            result.put(oldTransitions.get(symbols.indexOf(symbol)), symbols.indexOf(symbol));
        }

        //Возвращаем новую изотопию
        return result;
    }

    public void encrypt(String message,
                        String alphaIsotophyValue,
                        String betaIsotophyValue,
                        String gammaIsotophyValue,
                        String publicAlphaIsotophyValue,
                        String publicBetaIsotophyValue,
                        String publicGammaIsotophyValue,
                        String randomAlphaValue,
                        String randomBetaValue,
                        String randomGammaValue) {
        var leader = 0;

        var randomAlpha = Integer.parseInt(randomAlphaValue);
        var randomBeta = Integer.parseInt(randomBetaValue);
        var randomGamma = Integer.parseInt(randomGammaValue);

        var alphaTransitions = powTransitions(getTransitions(alphaIsotophyValue), randomAlpha);
        view.getRandomAlphaIsotophyTextField().setValue(getCycle(alphaTransitions));
        var betaTransitions = powTransitions(getTransitions(betaIsotophyValue), randomBeta);
        view.getRandomBetaIsotophyTextField().setValue(getCycle(betaTransitions));
        var gammaTransitions = powTransitions(getTransitions(gammaIsotophyValue), randomGamma);
        view.getRandomGammaIsotophyTextField().setValue(getCycle(gammaTransitions));

        var publicAlphaTransitions = getTransitions(publicAlphaIsotophyValue);
        var publicBetaTransitions = getTransitions(publicBetaIsotophyValue);
        var publicGammaTransitions = getTransitions(publicGammaIsotophyValue);

        var resultAlphaTransitions = powTransitions(publicAlphaTransitions, randomAlpha);
        int[][] alphaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            var row = quasigroupMatrix[resultAlphaTransitions.get(i)];
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                alphaMatrix[i][j] = row[j];
            }
        }

        var resultBetaTransitions = powTransitions(publicBetaTransitions, randomBeta);
        int[][] betaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                betaMatrix[i][j] = alphaMatrix[i][resultBetaTransitions.get(j)];
            }
        }

        var resultGammaTransitions = reverseTransitions(powTransitions(publicGammaTransitions, randomGamma));
        var gammaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                gammaMatrix[i][j] = resultGammaTransitions.get(betaMatrix[i][j]);
            }
        }

        var encryptedMessageBuilder = new StringBuilder();

        for (var i = 0; i < message.length(); i++) {
            var character = symbols.indexOf(String.valueOf(message.charAt(i)));
            var encryptedCharacter = gammaMatrix[leader][character];
            encryptedMessageBuilder.append(symbols.get(encryptedCharacter));
            leader = encryptedCharacter;
        }

        view.getEncryptedMessageTextField().setValue(encryptedMessageBuilder.toString());
    }

    public void decrypt(String message,
                        String secretAlphaValue,
                        String secretBetaValue,
                        String secretGammaValue,
                        String randomAlphaIsotophyValue,
                        String randomBetaIsotophyValue,
                        String randomGammaIsotophyValue) {
        var leader = 0;

        var secretAlpha = Integer.parseInt(secretAlphaValue);
        var secretBeta = Integer.parseInt(secretBetaValue);
        var secretGamma = Integer.parseInt(secretGammaValue);

        var randomAlphaTransitions = getTransitions(randomAlphaIsotophyValue);
        var randomBetaTransitions = getTransitions(randomBetaIsotophyValue);
        var randomGammaTransitions = getTransitions(randomGammaIsotophyValue);

        var resultAlphaTransitions = powTransitions(randomAlphaTransitions, secretAlpha);
        int[][] alphaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            var row = quasigroupMatrix[resultAlphaTransitions.get(i)];
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                alphaMatrix[i][j] = row[j];
            }
        }

        var resultBetaTransitions = powTransitions(randomBetaTransitions, secretBeta);
        int[][] betaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                betaMatrix[i][j] = alphaMatrix[i][resultBetaTransitions.get(j)];
            }
        }

        var resultGammaTransitions = reverseTransitions(powTransitions(randomGammaTransitions, secretGamma));
        var gammaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                gammaMatrix[i][j] = resultGammaTransitions.get(betaMatrix[i][j]);
            }
        }

        var parastrof = new int[quasigroupMatrixSize][quasigroupMatrixSize];
        for (var i = 0; i < quasigroupMatrixSize; i++) {
            parastrof[i] = new int[quasigroupMatrixSize];
        }

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                var gammaValue = gammaMatrix[i][j];
                parastrof[i][gammaValue] = j;
            }
        }

        var decryptedMessageBuilder = new StringBuilder();

        for (var i = 0; i < message.length(); i++) {
            var character = symbols.indexOf(String.valueOf(message.charAt(i)));
            var decryptedCharacter = parastrof[leader][character];
            decryptedMessageBuilder.append(symbols.get(decryptedCharacter));
            leader = character;
        }

        view.getDecryptedMessageTextField().setValue(decryptedMessageBuilder.toString());
    }

    public void powIsotophy(String isotophyValue, String powValue) {
        var pow = Long.parseLong(powValue);

        try {
            CompletableFuture.runAsync(() -> {
                var transitions = powTransitions(getTransitions(isotophyValue), pow);
                System.out.println(getCycle(transitions));
            }).get();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } catch (ExecutionException e) {
            throw new RuntimeException(e);
        }
//        view.getPowedIsotophyTextField().setValue(getCycle(transitions));
    }

    public void randomizeIsotophy() {
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
        view.getRandomizedIsotophyTextField().setValue(result.toString());
    }
}
