package com.kkoz.el_gamal_test.el_gamal;

import java.math.BigInteger;

public class GamalPresenter {
    private final GamalView view;

    public GamalPresenter(GamalView view) {
        this.view = view;
    }

    public String getPrimitiveRoot(String value) {
        return PrimitiveRootUtil.findPrimitiveRoot(new BigInteger(value)).toString();
    }

    public BigInteger getPrimitiveRoot(BigInteger value) {
        return PrimitiveRootUtil.findPrimitiveRoot(value);
    }

    public String encrypt(String primaryValue,
                          String daValue,
                          String randomNumberValue,
                          String messageValue) {
        var primary = new BigInteger(primaryValue);
        var primaryRoot = getPrimitiveRoot(primary);
        var da = new BigInteger(daValue);
        var randomNumber = Integer.parseInt(randomNumberValue);
        var message = new BigInteger(messageValue);

        var r = primaryRoot.pow(randomNumber).mod(primary);
        var e = message.multiply(da.pow(randomNumber)).mod(primary);

        return String.format("%s %s", r, e);
    }

    public String decrypt(String primaryValue,
                          String encrypt1Value,
                          String encrypt2Value,
                          String secretAValue) {
        var primary = new BigInteger(primaryValue);
        var encrypt1 = new BigInteger(encrypt1Value);
        var encrypt2 = new BigInteger(encrypt2Value);
        var secretA = new BigInteger(secretAValue);

        return encrypt2.multiply(
                encrypt1.pow(
                    primary.add(BigInteger.ONE.negate())
                        .add(secretA.negate()).intValue()
                )
            )
            .mod(primary)
            .toString();
    }
}
