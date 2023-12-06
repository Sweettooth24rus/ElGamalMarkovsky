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
    private final TextField benchmarkEncryptResultCountTextField = new TextField("Количество элементов");
    @Getter
    private final TextField benchmarkEncryptResultTimeTextField = new TextField("Время");
    @Getter
    private final TextField benchmarkDecryptResultCountTextField = new TextField("Количество элементов");
    @Getter
    private final TextField benchmarkDecryptResultTimeTextField = new TextField("Время");

    public MarkovskyBenchmarkView() {
        presenter = new MarkovskyBenchmarkPresenter(this);

        benchmarkEncryptResultCountTextField.setWidthFull();
        benchmarkEncryptResultTimeTextField.setWidthFull();
        benchmarkDecryptResultCountTextField.setWidthFull();
        benchmarkDecryptResultTimeTextField.setWidthFull();

        add(createBenchmarkEncryptSection(), createBenchmarkDecryptSection());
    }

    private Component createBenchmarkEncryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Тестирование скорости работы шифрования");

        var button = new Button("Запустить", e -> presenter.benchmarkEncrypt());

        container.add(header, button, benchmarkEncryptResultCountTextField, benchmarkEncryptResultTimeTextField);

        return container;
    }

    private Component createBenchmarkDecryptSection() {
        var container = new VerticalLayout();

        var header = new H1("Тестирование скорости работы расшифрования");

        var button = new Button("Запустить", e -> presenter.benchmarkDecrypt());

        container.add(header, button, benchmarkDecryptResultCountTextField, benchmarkDecryptResultTimeTextField);

        return container;
    }
}
