package com.kkoz.el_gamal_test.basic;

import lombok.Getter;
import org.apache.commons.lang3.StringUtils;

import java.util.*;
import java.util.stream.Collectors;

public class MarkovskyPresenter {
    private final MarkovskyView view;
    private final int[][] presetQuasigroupMatrix = new int[][] {
        new int[] { 1, 5, 3, 4, 2},
        new int[] { 4, 3, 1, 2, 5},
        new int[] { 3, 1, 2, 5, 4},
        new int[] { 2, 4, 5, 3, 1},
        new int[] { 5, 2, 4, 1, 3}
    };

    @Getter
    private int quasigroupMatrixSize;
    private int[][] quasigroupMatrix;

    public MarkovskyPresenter(MarkovskyView view) {
        this.view = view;
    }

    public void setQuasigroupMatrixSize(Integer value) {
        quasigroupMatrixSize = value;
        quasigroupMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];
    }

    public void updateQuasigroup(String stringValue, int i, int j) {
        quasigroupMatrix[i][j] = Integer.parseInt(stringValue);
    }

    public void presetQuasigroup() {
        setQuasigroupMatrixSize(5);

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                quasigroupMatrix[i][j] = presetQuasigroupMatrix[i][j];
            }
        }

        view.refreshQuasigroupMatrix(quasigroupMatrix);
    }

    public void randomUpdateQuasigroup() {
        quasigroupMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        var row = new int[quasigroupMatrixSize];
        for (int i = 0; i < quasigroupMatrixSize; i++) {
            var randomNumber = (int) (Math.random() * quasigroupMatrixSize) + 1;
            if (!contains(row, randomNumber)) {
                row[i] = randomNumber;
            } else {
                i--;
            }
        }

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
                quasigroupMatrix[i][j] = row[(j + offset[i]) % quasigroupMatrixSize];
            }
        }

        view.refreshQuasigroupMatrix(quasigroupMatrix);
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
                result.append("(")
                    .append(cycle.stream().map(String::valueOf).collect(Collectors.joining(",")))
                    .append(")");
            }
        }

        return result.toString();
    }

    private Map<Integer, Integer> getTransitions(String value) {
        var cycles = new ArrayList<List<Integer>>();
        var result = new HashMap<Integer, Integer>();

        while (StringUtils.isNotBlank(value)) {
            var cycle = new ArrayList<Integer>();
            var startCycle = value.indexOf("(");
            var endCycle = value.indexOf(")");

            var cycleString = value.substring(startCycle + 1, endCycle);
            value = value.substring(endCycle + 1);

            while (StringUtils.isNotBlank(cycleString)) {
                var delimiterIndex = cycleString.indexOf(",");
                if (delimiterIndex != -1) {
                    var number = cycleString.substring(0, delimiterIndex);
                    cycleString = cycleString.substring(delimiterIndex + 1);

                    cycle.add(Integer.parseInt(number));
                } else {
                    cycle.add(Integer.parseInt(cycleString));
                    break;
                }
            }
            cycles.add(cycle);
        }

        for (var cycle : cycles) {
            for (var i = 0; i < cycle.size(); i++) {
                var after = cycle.get(i);
                var before = cycle.get(i - 1 < 0 ? cycle.size() - 1 : i - 1);
                result.put(before, after);
            }
        }

        for (var i = 1; i <= quasigroupMatrixSize; i++) {
            result.putIfAbsent(i, i);
        }

        return result;
    }

    private Map<Integer, Integer> powTransitions(Map<Integer, Integer> oldTransitions, int pow) {
        var result = new HashMap<>(oldTransitions);

        for (var i = 1; i < pow; i++) {
            for (var j = 1; j <= result.size(); j++) {
                result.put(j, oldTransitions.get(result.get(j)));
            }
        }

        return result;
    }

    private Map<Integer, Integer> reverseTransitions(Map<Integer, Integer> oldTransitions) {
        var result = new HashMap<Integer, Integer>();

        for (var i = 1; i <= oldTransitions.size(); i++) {
            result.put(oldTransitions.get(i), i);
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
        var leader = 1;

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
            var row = quasigroupMatrix[resultAlphaTransitions.get(i + 1) - 1];
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                alphaMatrix[i][j] = row[j];
            }
        }

        var resultBetaTransitions = powTransitions(publicBetaTransitions, randomBeta);
        int[][] betaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                betaMatrix[i][j] = alphaMatrix[i][resultBetaTransitions.get(j + 1) - 1];
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
            var character = Integer.parseInt(String.valueOf(message.charAt(i)));
            var encryptedCharacter = gammaMatrix[leader - 1][character - 1];
            encryptedMessageBuilder.append(encryptedCharacter);
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
        var leader = 1;

        var secretAlpha = Integer.parseInt(secretAlphaValue);
        var secretBeta = Integer.parseInt(secretBetaValue);
        var secretGamma = Integer.parseInt(secretGammaValue);

        var randomAlphaTransitions = getTransitions(randomAlphaIsotophyValue);
        var randomBetaTransitions = getTransitions(randomBetaIsotophyValue);
        var randomGammaTransitions = getTransitions(randomGammaIsotophyValue);

        var resultAlphaTransitions = powTransitions(randomAlphaTransitions, secretAlpha);
        int[][] alphaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            var row = quasigroupMatrix[resultAlphaTransitions.get(i + 1) - 1];
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                alphaMatrix[i][j] = row[j];
            }
        }

        var resultBetaTransitions = powTransitions(randomBetaTransitions, secretBeta);
        int[][] betaMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];

        for (var i = 0; i < quasigroupMatrixSize; i++) {
            for (var j = 0; j < quasigroupMatrixSize; j++) {
                betaMatrix[i][j] = alphaMatrix[i][resultBetaTransitions.get(j + 1) - 1];
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
                parastrof[i][gammaValue - 1] = j + 1;
            }
        }

        var decryptedMessageBuilder = new StringBuilder();

        for (var i = 0; i < message.length(); i++) {
            var character = Integer.parseInt(String.valueOf(message.charAt(i)));
            var decryptedCharacter = parastrof[leader - 1][character - 1];
            decryptedMessageBuilder.append(decryptedCharacter);
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
        var possibleTransitions = new ArrayList<Integer>();
        for (var i = 1; i <= quasigroupMatrixSize; i++) {
            possibleTransitions.add(i);
        }
        Collections.shuffle(possibleTransitions);
        var transitions = new HashMap<Integer, Integer>();
        for (var i = 0; i < quasigroupMatrixSize; i++) {
            transitions.put(i + 1, possibleTransitions.get(i));
        }
        view.getRandomizedIsotophyTextField().setValue(getCycle(transitions));
    }
}
