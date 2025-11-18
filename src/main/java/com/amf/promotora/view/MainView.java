package com.amf.promotora.view;

import com.amf.promotora.model.Account;
import com.amf.promotora.model.Client;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.repository.AccountRepository;
import com.amf.promotora.repository.ClientRepository;
import com.amf.promotora.repository.TransactionRepository;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "", layout = MainLayout.class)
@PageTitle("Dashboard | AMF Promotora")
public class MainView extends VerticalLayout {

    private final ClientRepository clientRepository;
    private final AccountRepository accountRepository;
    private final TransactionRepository transactionRepository;

    public MainView(ClientRepository clientRepository,
                    AccountRepository accountRepository,
                    TransactionRepository transactionRepository) {

        this.clientRepository = clientRepository;
        this.accountRepository = accountRepository;
        this.transactionRepository = transactionRepository;

        setSizeFull();
        setPadding(true);
        setSpacing(true);

        add(new H2("Dashboard - Visão Geral"));

        add(buildDashboardCards());
    }

    private HorizontalLayout buildDashboardCards() {
        long totalClientes = clientRepository.count();
        long totalContas = accountRepository.count();
        long totalTransacoes = transactionRepository.count();

        Div card1 = createCard("Clientes", totalClientes);
        Div card2 = createCard("Contas", totalContas);
        Div card3 = createCard("Transações", totalTransacoes);

        HorizontalLayout layout = new HorizontalLayout(card1, card2, card3);
        layout.setWidthFull();
        layout.setSpacing(true);

        return layout;
    }

    private Div createCard(String titulo, long valor) {
        Div card = new Div();
        card.getStyle().set("padding", "20px");
        card.getStyle().set("border-radius", "12px");
        card.getStyle().set("background", "#f5f5f5");
        card.getStyle().set("box-shadow", "0 2px 6px rgba(0,0,0,0.15)");
        card.getStyle().set("width", "250px");

        H2 title = new H2(titulo);
        title.getStyle().set("margin", "0");

        Div number = new Div(String.valueOf(valor));
        number.getStyle().set("font-size", "26px");
        number.getStyle().set("font-weight", "bold");

        card.add(title, number);
        return card;
    }
}
