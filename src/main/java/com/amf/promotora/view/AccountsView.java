package com.amf.promotora.view;

import com.amf.promotora.dto.AccountDTO;
import com.amf.promotora.model.Account;
import com.amf.promotora.model.Client;
import com.amf.promotora.service.AccountService;
import com.amf.promotora.service.ClientService;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;

@Route(value = "accounts", layout = MainLayout.class)
@PageTitle("Contas | AMF Promotora")
public class AccountsView extends VerticalLayout {

    private final AccountService accountService;
    private final ClientService clientService;

    final private Grid<Account> grid = new Grid<>(Account.class, false);
    private Dialog formDialog;

    final private ComboBox<Client> clientField = new ComboBox<>("Cliente");
    final private ComboBox<String> typeField = new ComboBox<>("Tipo");

    final private Button saveButton = new Button("Salvar");
    final private Button cancelButton = new Button("Cancelar");

    private Account currentAccount;

    public AccountsView(AccountService accountService, ClientService clientService) {
        this.accountService = accountService;
        this.clientService = clientService;
    }

    @PostConstruct
    private void init() {
        setSizeFull();
        add(new H2("Contas"));
        configureGrid();
        add(buildToolbar(), grid);
        updateList();
    }

    private Component buildToolbar() {
        Button addAccountButton = new Button("Nova Conta");
        addAccountButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addAccountButton.addClickListener(e -> openForm(new Account()));

        HorizontalLayout toolbar = new HorizontalLayout(addAccountButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        return toolbar;
    }

    private void configureGrid() {

        grid.addColumn(a -> clientService.findByIdOptional(a.getClientId())
                        .map(Client::getFullName)
                        .orElse(""))
                .setHeader("Cliente")
                .setAutoWidth(true);

        grid.addColumn(Account::getNumber).setHeader("Número");
        grid.addColumn(Account::getType).setHeader("Tipo");
        grid.addColumn(Account::getBalance).setHeader("Saldo");

        grid.addComponentColumn(acc -> {
            Button edit = new Button("Editar", click -> openForm(acc));
            Button delete = new Button("Excluir", click -> deleteAccount(acc));

            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);

            return new HorizontalLayout(edit, delete);
        }).setHeader("Ações");
    }

    private void openForm(Account account) {
        this.currentAccount = account;

        if (formDialog == null) {
            formDialog = new Dialog();
            formDialog.setHeaderTitle("Conta");

            FormLayout form = new FormLayout();

            clientField.setItems(clientService.findAll());
            clientField.setItemLabelGenerator(Client::getFullName);

            typeField.setItems("CORRENTE", "POUPANÇA");

            form.add(clientField, typeField);

            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            cancelButton.addClickListener(e -> formDialog.close());
            saveButton.addClickListener(e -> saveAccount());

            HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
            formDialog.add(form, actions);
        }

        if (account.getClientId() != null) {
            clientService.findByIdOptional(account.getClientId())
                    .ifPresent(clientField::setValue);
        } else {
            clientField.clear();
        }

        typeField.setValue(account.getType());

        formDialog.open();
    }

    private void saveAccount() {

        if (clientField.getValue() == null || typeField.getValue() == null) {
            Notification.show("Preencha todos os campos!");
            return;
        }

        AccountDTO dto = new AccountDTO();
        dto.setClientId(clientField.getValue().getId());
        dto.setType(typeField.getValue());

        if (currentAccount.getId() == null) {
            accountService.create(dto);
        } else {
            dto.setId(currentAccount.getId());
            accountService.update(dto);
        }

        Notification.show("Conta salva com sucesso!");
        formDialog.close();
        updateList();
    }

    private void deleteAccount(Account acc) {
        accountService.delete(acc.getId());
        Notification.show("Conta excluída");
        updateList();
    }

    private void updateList() {
        grid.setItems(accountService.findAll());
    }
}
