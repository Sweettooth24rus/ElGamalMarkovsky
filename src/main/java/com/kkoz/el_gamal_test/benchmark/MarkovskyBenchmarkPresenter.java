package com.kkoz.el_gamal_test.benchmark;

import lombok.Getter;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MarkovskyBenchmarkPresenter {
    private final MarkovskyBenchmarkView view;
    private final int[] benchmarkSizes = new int[]{
        10, 20, 30, 40, 50, 60, 70, 80, 90, 100,
        200, 300, 400, 500, 600, 700, 800, 900, 1000,
        2000, 3000, 4000, 5000, 6000, 7000, 8000, 9000, 10000
    };
    private final int secretAlpha = 5;
    private final int secretBeta = 5;
    private final int secretGamma = 3;
    private final int randomAlpha = 4;
    private final int randomBeta = 6;
    private final int randomGamma = 5;
    private final String message = "1234567452467565352987246786673642143544653542443463243437654421432475653424";

    @Getter
    private int quasigroupMatrixSize;
    private int[][] quasigroupMatrix;
    private Map<Integer, Integer> alphaTransitions;
    private Map<Integer, Integer> betaTransitions;
    private Map<Integer, Integer> gammaTransitions;
    private Map<Integer, Integer> publicAlphaTransitions;
    private Map<Integer, Integer> publicBetaTransitions;
    private Map<Integer, Integer> publicGammaTransitions;
    private Map<Integer, Integer> randomAlphaTransitions;
    private Map<Integer, Integer> randomBetaTransitions;
    private Map<Integer, Integer> randomGammaTransitions;

    public MarkovskyBenchmarkPresenter(MarkovskyBenchmarkView view) {
        this.view = view;
    }

    public void benchmarkEncrypt() {
        for (var benchmarkSize : benchmarkSizes) {
            setQuasigroupMatrixSize(benchmarkSize);
            randomUpdateQuasigroup();

            alphaTransitions = randomizeIsotophy();
            betaTransitions = randomizeIsotophy();
            gammaTransitions = randomizeIsotophy();

            publicAlphaTransitions = powTransitions(alphaTransitions, secretAlpha);
            publicBetaTransitions = powTransitions(betaTransitions, secretBeta);
            publicGammaTransitions = powTransitions(gammaTransitions, secretGamma);

            var start = System.currentTimeMillis();
            encrypt();
            var end = System.currentTimeMillis() - start;
            view.getBenchmarkEncryptResultCountTextField().setValue(view.getBenchmarkEncryptResultCountTextField().getValue() + ", " + benchmarkSize);
            view.getBenchmarkEncryptResultTimeTextField().setValue(view.getBenchmarkEncryptResultTimeTextField().getValue() + ", " + end);
        }
    }

    public void benchmarkDecrypt() {
        for (var benchmarkSize : benchmarkSizes) {
            setQuasigroupMatrixSize(benchmarkSize);
            randomUpdateQuasigroup();

            alphaTransitions = randomizeIsotophy();
            betaTransitions = randomizeIsotophy();
            gammaTransitions = randomizeIsotophy();

            randomAlphaTransitions = powTransitions(alphaTransitions, randomAlpha);
            randomBetaTransitions = powTransitions(betaTransitions, randomBeta);
            randomGammaTransitions = powTransitions(gammaTransitions, randomGamma);

            var start = System.currentTimeMillis();
            decrypt();
            var end = System.currentTimeMillis() - start;
            view.getBenchmarkDecryptResultCountTextField().setValue(view.getBenchmarkDecryptResultCountTextField().getValue() + ", " + benchmarkSize);
            view.getBenchmarkDecryptResultTimeTextField().setValue(view.getBenchmarkDecryptResultTimeTextField().getValue() + ", " + end);
        }
    }

    public void setQuasigroupMatrixSize(Integer value) {
        quasigroupMatrixSize = value;
        quasigroupMatrix = new int[quasigroupMatrixSize][quasigroupMatrixSize];
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
    }

    private boolean contains(int[] array, int number) {
        for (var k = 0; k < array.length; k++) {
            if (array[k] == number) {
                return true;
            }
        }
        return false;
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

    public void encrypt() {
        var leader = 1;

        var randomAlphaTransitions = powTransitions(alphaTransitions, randomAlpha);
        var randomBetaTransitions = powTransitions(betaTransitions, randomBeta);
        var randomGammaTransitions = powTransitions(gammaTransitions, randomGamma);

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
    }

    public void decrypt() {
        var leader = 1;

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
    }

    public Map<Integer, Integer> randomizeIsotophy() {
        var possibleTransitions = new ArrayList<Integer>();
        for (var i = 1; i <= quasigroupMatrixSize; i++) {
            possibleTransitions.add(i);
        }
        Collections.shuffle(possibleTransitions);
        var transitions = new HashMap<Integer, Integer>();
        for (var i = 0; i < quasigroupMatrixSize; i++) {
            transitions.put(i + 1, possibleTransitions.get(i));
        }
        return transitions;
    }
}
