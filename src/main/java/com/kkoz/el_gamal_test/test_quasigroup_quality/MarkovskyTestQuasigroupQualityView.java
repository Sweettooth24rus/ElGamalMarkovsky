package com.kkoz.el_gamal_test.test_quasigroup_quality;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("test-quasigroup-quality")
public class MarkovskyTestQuasigroupQualityView extends VerticalLayout {
    private final MarkovskyTestQuasigroupQualityPresenter presenter;

    public MarkovskyTestQuasigroupQualityView() {
        presenter = new MarkovskyTestQuasigroupQualityPresenter(this);

        add(createQuasigroupSection());
    }

    private Component createQuasigroupSection() {
        var container = new VerticalLayout();

        var header = new H1("Заполнение квазигруппы");

        var randomButton = new Button("Выполнитель вычисления", e -> presenter.testQuasigroupQuality());

        container.add(header, randomButton);

        return container;
    }
}
