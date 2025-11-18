package com.amf.promotora.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Transações | AMF Promotora")
@Route(value = "transactions", layout = MainLayout.class)
public class TransactionsView extends VerticalLayout {

    public TransactionsView() {
        setSizeFull();
        setPadding(true);

        add(new H2("Gerenciamento de Transações"));
    }
}
