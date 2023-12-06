package com.kkoz.el_gamal_test.text;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.Getter;

@Route("text")
public class MarkovskyTextView extends VerticalLayout {
    private final MarkovskyTextPresenter presenter;
    @Getter
    private final TextField encryptedMessageTextField = new TextField("Зашифрованное сообщение");
    @Getter
    private final TextField decryptedMessageTextField = new TextField("Расшифрованное сообщение");
    @Getter
    private final TextField randomAlphaIsotophyTextField = new TextField("Альфа");
    @Getter
    private final TextField randomBetaIsotophyTextField = new TextField("Бета");
    @Getter
    private final TextField randomGammaIsotophyTextField = new TextField("Гамма");
    @Getter
    private final TextField powedIsotophyTextField = new TextField("Результат");
    @Getter
    private final TextField randomizedIsotophyTextField = new TextField("Результат");

    public MarkovskyTextView() {
        presenter = new MarkovskyTextPresenter(this);

        randomizedIsotophyTextField.setWidthFull();
        powedIsotophyTextField.setWidthFull();
        encryptedMessageTextField.setWidthFull();
        decryptedMessageTextField.setWidthFull();

        add(
            createRandomIsotophySection(),
            createComputeIsotophySection(),
            createQuasigroupSection(),
            createEncryptSection(),
            createDecryptSection()
        );
    }

    private Component createQuasigroupSection() {
        var container = new VerticalLayout();

        var header = new H1("Заполнение квазигруппы");

        var randomButton = new Button("Заполнить случайно", e -> presenter.randomUpdateQuasigroup());

        container.add(header, randomButton);

        return container;
    }

    private Component createComputeIsotophySection() {
        var container = new VerticalLayout();

        var header = new H1("Вычисление изотопии в степени");

        var isotophyTextField = new TextField("Изотопия");
        isotophyTextField.setWidthFull();
        var isotophyPowTextField = new TextField("Степень");

        var computeButton = new Button(
            "Вычислить",
            e -> presenter.powIsotophy(isotophyTextField.getValue(), isotophyPowTextField.getValue())
        );

        container.add(header, isotophyTextField, isotophyPowTextField, computeButton, powedIsotophyTextField);

        return container;
    }

    private Component createRandomIsotophySection() {
        var container = new VerticalLayout();

        var header = new H1("Генерация случайной изотопии");

        var generateButton = new Button(
            "Вычислить",
            e -> presenter.randomizeIsotophy()
        );

        container.add(header, generateButton, randomizedIsotophyTextField);

        return container;
    }

    private Component createEncryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Шифрование");

        var messageTextField = new TextField("Сообщение");
        messageTextField.setWidthFull();

        var isotophyContainer = new HorizontalLayout();

        var isotophyHeader = new H3("Изотопия");

        var alphaIsotophyTextField = new TextField("Альфа");
        var betaIsotophyTextField = new TextField("Бета");
        var gammaIsotophyTextField = new TextField("Гамма");

        isotophyContainer.add(
            alphaIsotophyTextField,
            betaIsotophyTextField,
            gammaIsotophyTextField
        );

        var publicIsotophyContainer = new HorizontalLayout();

        var publicIsotophyHeader = new H3("Публичный ключ");

        var publicAlphaIsotophyTextField = new TextField("Альфа");
        var publicBetaIsotophyTextField = new TextField("Бета");
        var publicGammaIsotophyTextField = new TextField("Гамма");

        publicIsotophyContainer.add(
            publicAlphaIsotophyTextField,
            publicBetaIsotophyTextField,
            publicGammaIsotophyTextField
        );

        var randomContainer = new HorizontalLayout();

        var randomHeader = new H3("Случайные числа");

        var randomAlphaTextField = new TextField("r");
        var randomBetaTextField = new TextField("s");
        var randomGammaTextField = new TextField("t");

        randomContainer.add(randomAlphaTextField, randomBetaTextField, randomGammaTextField);

        var submitButton = new Button(
            "Вычислить",
            e -> presenter.encrypt(
                messageTextField.getValue(),
                alphaIsotophyTextField.getValue(),
                betaIsotophyTextField.getValue(),
                gammaIsotophyTextField.getValue(),
                publicAlphaIsotophyTextField.getValue(),
                publicBetaIsotophyTextField.getValue(),
                publicGammaIsotophyTextField.getValue(),
                randomAlphaTextField.getValue(),
                randomBetaTextField.getValue(),
                randomGammaTextField.getValue()
            )
        );

        var resultHeader = new H2("Результат");

        var randomIsotophyContainer = new HorizontalLayout();

        var randomIsotophyHeader = new H3("Случайная изотопия");

        randomIsotophyContainer.add(
            randomAlphaIsotophyTextField,
            randomBetaIsotophyTextField,
            randomGammaIsotophyTextField
        );

        container.add(
            header,
            messageTextField,
            isotophyHeader,
            isotophyContainer,
            publicIsotophyHeader,
            publicIsotophyContainer,
            randomHeader,
            randomContainer,
            submitButton,
            resultHeader,
            encryptedMessageTextField,
            randomIsotophyHeader,
            randomIsotophyContainer
        );

        return container;
    }

    private Component createDecryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Расшифрование");

        var messageTextField = new TextField("Зашифрованное сообщение");
        messageTextField.setWidthFull();

        var secretContainer = new HorizontalLayout();

        var secretHeader = new H3("Секретный ключ");

        var secretAlphaTextField = new TextField("m");
        var secretBetaTextField = new TextField("n");
        var secretGammaTextField = new TextField("k");

        secretContainer.add(secretAlphaTextField, secretBetaTextField, secretGammaTextField);

        var randomIsotophyContainer = new HorizontalLayout();

        var randomIsotophyHeader = new H3("Полученная изотопия");

        var randomAlphaIsotophyTextField = new TextField("Альфа");
        var randomBetaIsotophyTextField = new TextField("Бета");
        var randomGammaIsotophyTextField = new TextField("Гамма");

        randomIsotophyContainer.add(
            randomAlphaIsotophyTextField,
            randomBetaIsotophyTextField,
            randomGammaIsotophyTextField
        );

        var submitButton = new Button(
            "Вычислить",
            e -> presenter.decrypt(
                messageTextField.getValue(),
                secretAlphaTextField.getValue(),
                secretBetaTextField.getValue(),
                secretGammaTextField.getValue(),
                randomAlphaIsotophyTextField.getValue(),
                randomBetaIsotophyTextField.getValue(),
                randomGammaIsotophyTextField.getValue()
            )
        );

        var resultHeader = new H2("Результат");

        container.add(
            header,
            messageTextField,
            secretHeader,
            secretContainer,
            randomIsotophyHeader,
            randomIsotophyContainer,
            submitButton,
            resultHeader,
            decryptedMessageTextField
        );

        return container;
    }
}
