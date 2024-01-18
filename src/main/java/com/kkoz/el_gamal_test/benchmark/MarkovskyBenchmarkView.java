package com.kkoz.el_gamal_test.benchmark;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.Route;
import lombok.Getter;

@Route("benchmark")
public class MarkovskyBenchmarkView extends VerticalLayout {
    private final MarkovskyBenchmarkPresenter presenter;
    @Getter
    private final TextField benchmarkResultCountTextField = new TextField("Количество элементов");
    @Getter
    private final TextField benchmarkResultTimeTextField = new TextField("Время");

    public MarkovskyBenchmarkView() {
        presenter = new MarkovskyBenchmarkPresenter(this);

        benchmarkResultCountTextField.setWidthFull();
        benchmarkResultTimeTextField.setWidthFull();

        add(
            createBenchmarkResultSection(),
            createBenchmarkEncryptSection(),
            createBenchmarkDecryptSection(),
            createBenchmarkMessageLengthSection()
        );
    }

    private Component createBenchmarkResultSection() {
        var container = new VerticalLayout();

        var header = new H1("Результаты");

        container.add(header, benchmarkResultCountTextField, benchmarkResultTimeTextField);

        return container;
    }

    private Component createBenchmarkEncryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Тестирование скорости работы шифрования");

        var button = new Button("Запустить", e -> presenter.benchmarkEncrypt());

        container.add(header, button);

        return container;
    }

    private Component createBenchmarkDecryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Тестирование скорости работы расшифрования");

        var button = new Button("Запустить", e -> presenter.benchmarkDecrypt());

        container.add(header, button);

        return container;
    }

    private Component createBenchmarkMessageLengthSection() {
        var container = new VerticalLayout();

        var header = new H1("Тестирование скорости работы для текста разной длины");

        var button = new Button("Запустить", e -> presenter.benchmarkTextLength());

        container.add(header, button);

        return container;
    }
}
