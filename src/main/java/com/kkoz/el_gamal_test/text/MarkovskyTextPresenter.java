package com.kkoz.el_gamal_test.text;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MarkovskyTextPresenter {
        private static final List<String> symbols = List.of(
        "A", "B", "C", "D", "E", "F", "G", "H", "I", "J", "K", "L", "M",
        "N", "O", "P", "Q", "R", "S", "T", "U", "V", "W", "X", "Y", "Z",
        "a", "b", "c", "d", "e", "f", "g", "h", "i", "j", "k", "l", "m",
        "n", "o", "p", "q", "r", "s", "t", "u", "v", "w", "x", "y", "z",
        "А", "Б", "В", "Г", "Д", "Е", "Ё", "Ж", "З", "И", "Й", "К", "Л", "М", "Н", "О",
        "П", "Р", "С", "Т", "У", "Ф", "Х", "Ц", "Ч", "Ш", "Щ", "Ъ", "Ы", "Ь", "Э", "Ю", "Я",
        "а", "б", "в", "г", "д", "е", "ё", "ж", "з", "и", "й", "к", "л", "м", "н", "о",
        "п", "р", "с", "т", "у", "ф", "х", "ц", "ч", "ш", "щ", "ъ", "ы", "ь", "э", "ю", "я",
        "0", "1", "2", "3", "4", "5", "6", "7", "8", "9",
        " ", ",", ".", ":", ";", "+", "-", "*", "/", "%", "?", "!", "(", ")", "[", "]", "{", "}"
    );
//    private static final List<String> symbols = List.of(
//        "A", "B", "C", "D", "E"
//    );
    @Getter
    private static final int quasigroupMatrixSize = symbols.size();

    private final MarkovskyTextView view;

    private int[][] quasigroupMatrix;

    public MarkovskyTextPresenter(MarkovskyTextView view) {
        this.view = view;
    }

    public void randomUpdateQuasigroup() {
        quasigroupMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        var possibleSymbols = new ArrayList<>(symbols);
        Collections.shuffle(possibleSymbols);

        var offset = new int[quasigroupMatrixSize];
        for (int i = 1; i < quasigroupMatrixSize; i++) {
            var randomNumber = (int) (Math.random() * quasigroupMatrixSize);
            if (!contains(offset, randomNumber)) {
                offset[i] = randomNumber;
            } else {
                i--;
            }
        }

        for (int i = 0; i < quasigroupMatrixSize; i++) {
            for (int j = 0; j < quasigroupMatrixSize; j++) {
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

    private String getCycle(Map<Integer, Integer> transitions) {
        var result = new StringBuilder();

        var possibleTransitions = new ArrayList<>(transitions.keySet());

        while (!possibleTransitions.isEmpty()) {
            var initialTransition = possibleTransitions.get(0);
            possibleTransitions.remove(initialTransition);
            var nextTransition = transitions.get(initialTransition);
            var cycle = new ArrayList<Integer>();
            cycle.add(initialTransition);
            while (!Objects.equals(initialTransition, nextTransition)) {
                cycle.add(nextTransition);
                possibleTransitions.remove(nextTransition);
                nextTransition = transitions.get(nextTransition);
            }
            if (cycle.size() != 1) {
                result.append("<")
                    .append(cycle.stream().map(symbols::get).collect(Collectors.joining("`")))
                    .append(">");
            }
        }

        return result.toString();
    }

    private Map<Integer, Integer> getTransitions(String value) {
        var cycles = new ArrayList<List<String>>();
        var result = new HashMap<Integer, Integer>();

        while (StringUtils.isNotBlank(value)) {
            var cycle = new ArrayList<String>();
            var startCycle = value.indexOf("<");
            var endCycle = value.indexOf(">");

            var cycleString = value.substring(startCycle + 1, endCycle);
            value = value.substring(endCycle + 1);

            while (StringUtils.isNotBlank(cycleString)) {
                var delimiterIndex = cycleString.indexOf("`");
                if (delimiterIndex != -1) {
                    var character = cycleString.substring(0, delimiterIndex);
                    cycleString = cycleString.substring(delimiterIndex + 1);

                    cycle.add(character);
                } else {
                    cycle.add(cycleString);
                    break;
                }
            }
            cycles.add(cycle);
        }

        for (var cycle : cycles) {
            for (var i = 0; i < cycle.size(); i++) {
                var after = cycle.get(i);
                var before = cycle.get(i - 1 < 0 ? cycle.size() - 1 : i - 1);
                result.put(symbols.indexOf(before), symbols.indexOf(after));
            }
        }

        for (var symbol : symbols) {
            result.putIfAbsent(symbols.indexOf(symbol), symbols.indexOf(symbol));
        }

        return result;
    }

    private Map<Integer, Integer> powTransitions(Map<Integer, Integer> oldTransitions, int pow) {
        var result = new HashMap<>(oldTransitions);

        for (var i = 1; i < pow; i++) {
            for (var symbol : symbols) {
                result.put(symbols.indexOf(symbol), oldTransitions.get(result.get(symbols.indexOf(symbol))));
            }
        }

        return result;
    }

    private Map<Integer, Integer> reverseTransitions(Map<Integer, Integer> oldTransitions) {
        var result = new HashMap<Integer, Integer>();

        for (var symbol : symbols) {
            result.put(oldTransitions.get(symbols.indexOf(symbol)), symbols.indexOf(symbol));
        }

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
        var pow = Integer.parseInt(powValue);

        var transitions = powTransitions(getTransitions(isotophyValue), pow);
        view.getPowedIsotophyTextField().setValue(getCycle(transitions));
    }

    public void randomizeIsotophy() {
        var possibleTransitions = new ArrayList<>(symbols);
        Collections.shuffle(possibleTransitions);
        var transitions = new HashMap<Integer, Integer>();
        for (var i = 0; i < quasigroupMatrixSize; i++) {
            transitions.put(i, symbols.indexOf(possibleTransitions.get(i)));
        }
        view.getRandomizedIsotophyTextField().setValue(getCycle(transitions));
    }
}
