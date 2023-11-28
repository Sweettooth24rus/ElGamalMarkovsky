package com.kkoz.el_gamal_test.el_gamal;

import lombok.experimental.UtilityClass;

import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

import static java.math.BigInteger.ZERO;
import static java.math.BigInteger.ONE;
import static java.math.BigInteger.TWO;

@UtilityClass
public class PrimitiveRootUtil {

    public BigInteger findPrimitiveRoot(BigInteger n) {
        for (var i = TWO; i.compareTo(n) < 0; i = i.add(ONE)) {
            if (isPrimitiveRoot(i, n)) {
                return i;
            }
        }
        return ZERO; // Если первообразный корень не найден
    }

    private boolean isPrimitiveRoot(BigInteger a, BigInteger n) {
        var phi = phiFunction(n);
        var factors = primeFactors(phi);

        for (var factor : factors) {
            if (powerMod(a, phi.divide(factor), n).equals(ONE)) {
                return false;
            }
        }
        return true;
    }

    private BigInteger phiFunction(BigInteger n) {
        var result = n;

        for (var i = TWO; i.multiply(i).compareTo(n) <= 0; i = i.add(ONE)) {
            if (n.mod(i).equals(ZERO)) {
                while (n.mod(i).equals(ZERO)) {
                    n = n.divide(i);
                }
                result = result.subtract(result.divide(i));
            }
        }

        if (n.compareTo(ONE) > 0) {
            result = result.subtract(result.divide(n));
        }

        return result;
    }

    private List<BigInteger> primeFactors(BigInteger n) {
        var factors = new ArrayList<BigInteger>();

        for (var i = TWO; i.multiply(i).compareTo(n) <= 0; i = i.add(ONE)) {
            while (n.mod(i).equals(ZERO)) {
                factors.add(i);
                n = n.divide(i);
            }
        }

        if (n.compareTo(ONE) > 0) {
            factors.add(n);
        }

        return factors;
    }

    private BigInteger powerMod(BigInteger a, BigInteger b, BigInteger m) {
        if (b.equals(ZERO)) {
            return ONE;
        }

        var result = powerMod(a, b.divide(TWO), m);
        result = result.multiply(result).mod(m);

        if (b.mod(TWO).equals(ONE)) {
            result = result.multiply(a).mod(m);
        }

        return result;
    }
}
