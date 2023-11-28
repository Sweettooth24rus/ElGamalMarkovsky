package com.kkoz.el_gamal_test.el_gamal;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;

@Route("gamal")
public class GamalView extends VerticalLayout {
    private final GamalPresenter presenter;

    public GamalView() {
        presenter = new GamalPresenter(this);

        add(
            createPrimitiveRootSection(),
            new HorizontalLayout(
                createEncryptSection(),
                createDecryptSection()
            )
        );
    }

    private Component createPrimitiveRootSection() {
        var outputPrimaryRootResult = new Span();

        var container = new VerticalLayout();

        var header = new H1("Получение первообразного корня");

        var inputContainer = new HorizontalLayout();

        var inputLabel = new Span("Введите число");

        var inputTextField = new TextField();
        inputTextField.setValue("719");

        var inputHint = new Span("75983000072189348711 / 1753");

        var inputButton = new Button("Вычислить");
        inputButton.addClickListener(e ->
            outputPrimaryRootResult.setText(presenter.getPrimitiveRoot(inputTextField.getValue()))
        );

        inputContainer.add(inputLabel, inputTextField, inputButton, inputHint);

        var outputContainer = new HorizontalLayout();

        var outputLabel = new Span("Первообразный корень равен");
        outputContainer.add(outputLabel, outputPrimaryRootResult);

        container.add(header, inputContainer, outputContainer);
        return container;
    }

    private Component createEncryptSection() {
        var outputEncryptResult = new Span();

        var container = new VerticalLayout();

        var header = new H1("Шифрование");

        var inputPrimaryContainer = new HorizontalLayout();

        var inputPrimaryLabel = new Span("Введите простое число");

        var inputPrimaryTextField = new TextField();
        inputPrimaryTextField.setValue("719");

        inputPrimaryContainer.add(inputPrimaryLabel, inputPrimaryTextField);

        var inputAContainer = new HorizontalLayout();

        var inputALabel = new Span("Введите публичный ключ");

        var inputATextField = new TextField();
        inputATextField.setValue("711");

        inputAContainer.add(inputALabel, inputATextField);

        var inputBContainer = new HorizontalLayout();

        var inputRandomNumberContainer = new HorizontalLayout();

        var inputRandomNumberLabel = new Span("Введите случайное число");

        var inputRandomNumberTextField = new TextField();
        inputRandomNumberTextField.setValue("97");

        inputRandomNumberContainer.add(inputRandomNumberLabel, inputRandomNumberTextField);

        var inputMessageContainer = new HorizontalLayout();

        var inputMessageLabel = new Span("Введите сообщение");

        var inputMessageTextField = new TextField();
        inputMessageTextField.setValue("506");

        var inputButton = new Button("Вычислить");
        inputButton.addClickListener(e ->
            outputEncryptResult.setText(
                presenter.encrypt(
                    inputPrimaryTextField.getValue(),
                    inputATextField.getValue(),
                    inputRandomNumberTextField.getValue(),
                    inputMessageTextField.getValue()
                )
            )
        );

        inputMessageContainer.add(inputMessageLabel, inputMessageTextField, inputButton);

        var outputContainer = new HorizontalLayout();

        var outputLabel = new Span("Результат");
        outputContainer.add(outputLabel, outputEncryptResult);

        container.add(
            header,
            inputPrimaryContainer,
            inputAContainer,
            inputBContainer,
            inputRandomNumberContainer,
            inputMessageContainer,
            outputContainer
        );
        return container;
    }

    private Component createDecryptSection() {
        var outputDecryptResult = new Span();

        var container = new VerticalLayout();

        var header = new H1("Дешифрование");

        var inputPrimaryContainer = new HorizontalLayout();

        var inputPrimaryLabel = new Span("Введите простое число");

        var inputPrimaryTextField = new TextField();
        inputPrimaryTextField.setValue("719");

        inputPrimaryContainer.add(inputPrimaryLabel, inputPrimaryTextField);

        var inputEncrypt1Container = new HorizontalLayout();

        var inputEncrypt1Label = new Span("Введите первое число");

        var inputEncrypt1TextField = new TextField();
        inputEncrypt1TextField.setValue("371");

        inputEncrypt1Container.add(inputEncrypt1Label, inputEncrypt1TextField);

        var inputEncrypt2Container = new HorizontalLayout();

        var inputEncrypt2Label = new Span("Введите второе число");

        var inputEncrypt2TextField = new TextField();
        inputEncrypt2TextField.setValue("267");

        inputEncrypt2Container.add(inputEncrypt2Label, inputEncrypt2TextField);

        var inputAContainer = new HorizontalLayout();

        var inputALabel = new Span("Введите секретное число");

        var inputATextField = new TextField();
        inputATextField.setValue("23");

        var inputButton = new Button("Вычислить");
        inputButton.addClickListener(e ->
            outputDecryptResult.setText(
                presenter.decrypt(
                    inputPrimaryTextField.getValue(),
                    inputEncrypt1TextField.getValue(),
                    inputEncrypt2TextField.getValue(),
                    inputATextField.getValue()
                )
            )
        );

        inputAContainer.add(inputALabel, inputATextField, inputButton);

        var outputContainer = new HorizontalLayout();

        var outputLabel = new Span("Исходное сообщение");
        outputContainer.add(outputLabel, outputDecryptResult);

        container.add(
            header,
            inputPrimaryContainer,
            inputEncrypt1Container,
            inputEncrypt2Container,
            inputAContainer,
            outputContainer
        );
        return container;
    }
}
