package com.kkoz.el_gamal_test.test_quasigroup_quality;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class MarkovskyTestQuasigroupQualityPresenter {
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
    @Getter
    private static final int quasigroupMatrixSize = symbols.size();

    private final MarkovskyTestQuasigroupQualityView view;

    private int[][] quasigroupMatrix;

    public MarkovskyTestQuasigroupQualityPresenter(MarkovskyTestQuasigroupQualityView view) {
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

    public void testQuasigroupQuality() {
        var count = 1000;
        var sum = 0D;
        for (var k = 0; k < count; k++) {
            var localSum = 0D;
            randomUpdateQuasigroup();
            for (var i = 0; i < quasigroupMatrixSize; i++) {
                for (var j = 1; j < quasigroupMatrixSize; j++) {
                    localSum += Math.abs(quasigroupMatrix[i][j - 1] - quasigroupMatrix[i][j]);
                }
            }
            localSum /= quasigroupMatrixSize;
            localSum /= (quasigroupMatrixSize - 1);
            System.out.println(localSum);
            sum += localSum;
        }
        sum /= count;
        System.out.println(sum);
    }
}
