package com.amf.promotora.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Contas | AMF Promotora")
@Route(value = "accounts", layout = MainLayout.class)
public class AccountsView extends VerticalLayout {

    public AccountsView() {
        setSizeFull();
        setPadding(true);

        add(new H2("Gerenciamento de Contas"));
    }
}
