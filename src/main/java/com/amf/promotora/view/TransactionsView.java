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
import java.text.DecimalFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.List;

//@Route(value = "transactions", layout = MainLayout.class)
//@PageTitle("Transações | AMF Promotora")
//public class TransactionsView extends VerticalLayout {
//
//    private final AccountService accountService;
//    private final TransactionService transactionService;
//
//    private ComboBox<Account> fromAccountCombo = new ComboBox<>("Conta Origem");
//    private ComboBox<Account> toAccountCombo = new ComboBox<>("Conta Destino");
//    private NumberField amountField = new NumberField("Valor");
//    private Button executeButton = new Button("Executar");
//
//    private DatePicker startDatePicker = new DatePicker("Data Início");
//    private DatePicker endDatePicker = new DatePicker("Data Fim");
//    private Button filterButton = new Button("Filtrar");
//
//    private Grid<Transaction> grid = new Grid<>(Transaction.class, false);
//
//    private static final DateTimeFormatter dtFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss");
//    private static final DecimalFormat decimalFormat = new DecimalFormat("#0.00");
//
//    public TransactionsView(AccountService accountService, TransactionService transactionService) {
//        this.accountService = accountService;
//        this.transactionService = transactionService;
//    }
//
//    @PostConstruct
//    private void init() {
//        setSizeFull();
//        add(new H2("Transações"));
//        configureForm();
//        configureGrid();
//        updateAccounts();
//        updateGrid();
//    }
//
//    private void configureForm() {
//        List<Account> accounts = accountService.findAll();
//        fromAccountCombo.setItems(accounts);
//        fromAccountCombo.setItemLabelGenerator(a -> a.getNumber() + " - Saldo: " + a.getBalance());
//
//        toAccountCombo.setItems(accounts);
//        toAccountCombo.setItemLabelGenerator(Account::getNumber);
//
//        executeButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
//        executeButton.addClickListener(e -> executeTransaction());
//
//        filterButton.addClickListener(e -> {
//            if (fromAccountCombo.getValue() == null) {
//                Notification.show("Selecione a Conta Origem para filtrar!");
//                return;
//            }
//            if ((startDatePicker.getValue() != null && endDatePicker.getValue() == null) ||
//                    (startDatePicker.getValue() == null && endDatePicker.getValue() != null)) {
//                Notification.show("Preencha ambas as datas para filtrar!");
//                return;
//            }
//            updateGrid();
//        });
//
//        HorizontalLayout formLayout = new HorizontalLayout(fromAccountCombo, toAccountCombo, amountField, executeButton);
//        formLayout.setAlignItems(Alignment.END);
//
//        HorizontalLayout filterLayout = new HorizontalLayout(startDatePicker, endDatePicker, filterButton);
//        filterLayout.setAlignItems(Alignment.END);
//
//        add(formLayout, filterLayout, grid);
//    }
//
//    private void configureGrid() {
//        grid.addColumn(t -> t.getCreatedAt() != null ? dtFormatter.format(t.getCreatedAt().atZone(ZoneOffset.UTC)) : "")
//                .setHeader("Data").setAutoWidth(true);
//        grid.addColumn(t -> t.getType().name()).setHeader("Tipo").setAutoWidth(true);
//        grid.addColumn(t -> t.getAmount() != null ? decimalFormat.format(t.getAmount()) : "0.00")
//                .setHeader("Valor").setAutoWidth(true);
//        grid.addColumn(Transaction::getPerformedBy).setHeader("Realizado por").setAutoWidth(true);
//        grid.addColumn(Transaction::getFromAccountId).setHeader("Conta Origem").setAutoWidth(true);
//        grid.addColumn(Transaction::getToAccountId).setHeader("Conta Destino").setAutoWidth(true);
//    }
//
//    private void executeTransaction() {
//        Account from = fromAccountCombo.getValue();
//        Account to = toAccountCombo.getValue();
//        Double amountValue = amountField.getValue();
//
//        if (from == null || amountValue == null || amountValue <= 0) {
//            Notification.show("Preencha todos os campos obrigatórios corretamente!");
//            return;
//        }
//
//        BigDecimal amount = BigDecimal.valueOf(amountValue);
//        try {
//            if (to == null || from.getId().equals(to.getId())) {
//                // Depósito ou Saque
//                if (from.getBalance().compareTo(amount) >= 0) {
//                    transactionService.withdraw(from.getId(), amount, "alexandre"); // Saque
//                } else {
//                    transactionService.deposit(from.getId(), amount, "alexandre"); // Depósito
//                }
//            } else {
//                // Transferência
//                if (from.getBalance().compareTo(amount) < 0) {
//                    Notification.show("Saldo insuficiente para transferência");
//                    return;
//                }
//                transactionService.transfer(from.getId(), to.getId(), amount, "alexandre");
//            }
//
//            Notification.show("Transação realizada com sucesso!");
//            amountField.clear(); // Limpa o campo Valor após execução
//            updateAccounts();
//            updateGrid();
//
//        } catch (Exception ex) {
//            Notification.show("Erro: " + ex.getMessage());
//        }
//    }
//
//    private void updateAccounts() {
//        List<Account> accounts = accountService.findAll();
//        fromAccountCombo.setItems(accounts);
//        toAccountCombo.setItems(accounts);
//    }
//
//    private void updateGrid() {
//        Account from = fromAccountCombo.getValue();
//        if (from == null) return;
//
//        Instant start = startDatePicker.getValue() != null ?
//                startDatePicker.getValue().atStartOfDay().toInstant(ZoneOffset.UTC) : null;
//
//        Instant end = endDatePicker.getValue() != null ?
//                endDatePicker.getValue().atTime(23, 59, 59).toInstant(ZoneOffset.UTC) : null;
//
//        List<Transaction> transactions;
//        if (start != null && end != null) {
//            transactions = transactionService.getTransactions(from.getId(), start, end);
//        } else {
//            transactions = transactionService.getTransactions(from.getId(), null, null);
//        }
//
//        grid.setItems(transactions);
//    }
//}
@Route(value = "transactions", layout = MainLayout.class)
@PageTitle("Transações | AMF Promotora")
public class TransactionsView extends VerticalLayout {

    private final AccountService accountService;
    private final TransactionService transactionService;

    private RadioButtonGroup<String> operationSelector = new RadioButtonGroup<>();

    private ComboBox<Account> fromAccountCombo = new ComboBox<>("Conta Origem");
    private ComboBox<Account> toAccountCombo = new ComboBox<>("Conta Destino");
    private NumberField amountField = new NumberField("Valor");

    private Button executeButton = new Button("Executar");
    private DatePicker startDatePicker = new DatePicker("Data Início");
    private DatePicker endDatePicker = new DatePicker("Data Fim");
    private Button filterButton = new Button("Filtrar");

    private Grid<Transaction> grid = new Grid<>(Transaction.class, false);

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

        filterButton.addClickListener(e -> updateGrid());

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
                        ? t.getCreatedAt().atZone(ZoneOffset.UTC).format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
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
                        Notification.show("Selecione a conta destino");
                        return;
                    }
                    transactionService.transfer(from.getId(), to.getId(), amount, "alexandre");
                    break;

                case "Depósito":
                    transactionService.deposit(from.getId(), amount, "alexandre");
                    break;

                case "Saque":
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
