package com.amf.promotora.view;

import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@PageTitle("Clientes | AMF Promotora")
@Route(value = "clients", layout = MainLayout.class)
public class ClientsView extends VerticalLayout {

    public ClientsView() {
        setSizeFull();
        setPadding(true);

        add(new H2("Gerenciamento de Clientes"));
    }
}
