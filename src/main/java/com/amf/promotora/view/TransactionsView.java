package com.amf.promotora.view;

import com.amf.promotora.model.Account;
import com.amf.promotora.model.Transaction;
import com.amf.promotora.service.AccountService;
import com.amf.promotora.service.TransactionService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.combobox.ComboBox;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.radiobutton.RadioButtonGroup;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

@Route(value = "transactions", layout = MainLayout.class)
@PageTitle("Transações | AMF Promotora")
public class TransactionsView extends VerticalLayout {

    private final AccountService accountService;
    private final TransactionService transactionService;

    final private RadioButtonGroup<String> operationSelector = new RadioButtonGroup<>();

    final private ComboBox<Account> fromAccountCombo = new ComboBox<>("Conta Origem");
    final private ComboBox<Account> toAccountCombo = new ComboBox<>("Conta Destino");
    final private NumberField amountField = new NumberField("Valor");

    final private Button executeButton = new Button("Executar");
    final private DatePicker startDatePicker = new DatePicker("Data Início");
    final private DatePicker endDatePicker = new DatePicker("Data Fim");
    final private Button filterButton = new Button("Filtrar");

    final private Grid<Transaction> grid = new Grid<>(Transaction.class, false);

    public TransactionsView(AccountService accountService, TransactionService transactionService) {
        this.accountService = accountService;
        this.transactionService = transactionService;
    }

    @PostConstruct
    private void init() {
        setSizeFull();
        add(new H2("Transações"));

        configureOperationSelector();
        configureForm();
        configureGrid();

        updateAccounts();
        updateGrid();
    }

    private void configureOperationSelector() {
        operationSelector.setLabel("Operação");
        operationSelector.setItems("Transferência", "Depósito", "Saque");
        operationSelector.addValueChangeListener(e -> toggleFields());
        add(operationSelector);
    }

    private void toggleFields() {
        String op = operationSelector.getValue();
        toAccountCombo.setVisible("Transferência".equals(op));
    }

    private void configureForm() {
        List<Account> accounts = accountService.findAll();
        fromAccountCombo.setItems(accounts);
        fromAccountCombo.setItemLabelGenerator(a -> a.getNumber() + " - Saldo: " + a.getBalance());

        toAccountCombo.setItems(accounts);
        toAccountCombo.setItemLabelGenerator(Account::getNumber);
        toAccountCombo.setVisible(false);

        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        executeButton.addClickListener(e -> executeTransaction());

        HorizontalLayout formLayout = new HorizontalLayout(
                fromAccountCombo,
                toAccountCombo,
                amountField,
                executeButton
        );
        formLayout.setAlignItems(Alignment.END);

        filterButton.addClickListener(e -> validateAndUpdateGrid());

        HorizontalLayout filterLayout = new HorizontalLayout(
                startDatePicker,
                endDatePicker,
                filterButton
        );
        filterLayout.setAlignItems(Alignment.END);

        add(formLayout, filterLayout, grid);
    }

    private void configureGrid() {
        grid.addColumn(t -> t.getCreatedAt() != null
                        ? t.getCreatedAt().atZone(ZoneOffset.UTC)
                        .format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
                        : "")
                .setHeader("Data");

        grid.addColumn(t -> t.getType().name()).setHeader("Tipo");

        grid.addColumn(t -> t.getAmount() != null ? t.getAmount().toString() : "0")
                .setHeader("Valor");

        grid.addColumn(Transaction::getPerformedBy).setHeader("Realizado por");
        grid.addColumn(Transaction::getFromAccountId).setHeader("Origem");
        grid.addColumn(Transaction::getToAccountId).setHeader("Destino");
    }

    private void executeTransaction() {
        String op = operationSelector.getValue();
        Account from = fromAccountCombo.getValue();
        Account to = toAccountCombo.getValue();
        Double amountValue = amountField.getValue();

        if (op == null || from == null || amountValue == null || amountValue <= 0) {
            Notification.show("Preencha todos os campos obrigatórios!");
            return;
        }

        try {
            BigDecimal amount = BigDecimal.valueOf(amountValue);

            switch (op) {
                case "Transferência":
                    if (to == null) {
                        Notification.show("Selecione a conta destino!");
                        return;
                    }
                    if (from.getBalance().compareTo(amount) < 0) {
                        Notification.show("Saldo insuficiente para transferência!");
                        return;
                    }
                    transactionService.transfer(from.getId(), to.getId(), amount, "alexandre");
                    break;

                case "Depósito":
                    transactionService.deposit(from.getId(), amount, "alexandre");
                    break;

                case "Saque":
                    if (from.getBalance().compareTo(amount) < 0) {
                        Notification.show("Saldo insuficiente para saque!");
                        return;
                    }
                    transactionService.withdraw(from.getId(), amount, "alexandre");
                    break;
            }

            Notification.show("Operação realizada com sucesso!");
            amountField.clear();
            updateAccounts();
            updateGrid();

        } catch (Exception ex) {
            Notification.show("Erro: " + ex.getMessage());
        }
    }

    private void validateAndUpdateGrid() {
        Account from = fromAccountCombo.getValue();
        LocalDate start = startDatePicker.getValue();
        LocalDate end = endDatePicker.getValue();

        if (from == null) {
            Notification.show("Selecione a Conta Origem para filtrar!");
            return;
        }

        if ((start != null && end == null) || (start == null && end != null)) {
            Notification.show("Preencha ambas as datas para filtrar!");
            return;
        }

        if (start != null && end != null && start.isAfter(end)) {
            Notification.show("Data Início não pode ser maior que Data Fim!");
            return;
        }

        updateGrid();
    }

    private void updateAccounts() {
        List<Account> accounts = accountService.findAll();
        fromAccountCombo.setItems(accounts);
        toAccountCombo.setItems(accounts);
    }

    private void updateGrid() {
        Account from = fromAccountCombo.getValue();
        if (from == null) return;

        Instant start = startDatePicker.getValue() != null
                ? startDatePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC)
                : null;

        Instant end = endDatePicker.getValue() != null
                ? endDatePicker.getValue().atTime(23, 59, 59).toInstant(ZoneOffset.UTC)
                : null;

        List<Transaction> transactions = transactionService.getTransactions(from.getId(), start, end);
        grid.setItems(transactions);
    }
}

