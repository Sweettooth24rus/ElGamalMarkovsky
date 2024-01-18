package com.kkoz.el_gamal_test.test_isotophy_quality.test_quasigroup_quality;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("test-isotophy-quality")
public class MarkovskyTestIsotophyQualityView extends VerticalLayout {
    private final MarkovskyTestIsotophyQualityPresenter presenter;

    public MarkovskyTestIsotophyQualityView() {
        presenter = new MarkovskyTestIsotophyQualityPresenter(this);

        add(createRandomIsotophySection());
    }

    private Component createRandomIsotophySection() {
        var container = new VerticalLayout();

        var header = new H1("Генерация случайной изотопии");

        var generateButton = new Button(
            "Вычислить",
            e -> presenter.testIsotophyQuality()
        );

        container.add(header, generateButton);

        return container;
    }
}
