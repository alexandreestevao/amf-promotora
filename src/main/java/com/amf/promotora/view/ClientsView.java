package com.amf.promotora.view;

import com.amf.promotora.model.Client;
import com.amf.promotora.service.ClientService;
import com.amf.promotora.util.CpfUtils;
import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.formlayout.FormLayout;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.binder.Binder;
import com.vaadin.flow.data.binder.ValidationException;
import com.vaadin.flow.data.binder.ValidationResult;
import com.vaadin.flow.data.binder.Validator;
import com.vaadin.flow.data.value.ValueChangeMode;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import jakarta.annotation.PostConstruct;

import java.time.format.DateTimeFormatter;

@Route(value = "clients", layout = MainLayout.class)
@PageTitle("Clientes | AMF Promotora")
public class ClientsView extends VerticalLayout {

    private final ClientService clientService;

    final private Grid<Client> grid = new Grid<>(Client.class, false);
    private Dialog formDialog;

    final private TextField fullNameField = new TextField("Nome Completo");
    final private TextField cpfField = new TextField("CPF");
    final private DatePicker birthDateField = new DatePicker("Data de Nascimento");

    final private Button saveButton = new Button("Salvar");
    final private Button cancelButton = new Button("Cancelar");

    private Client currentClient;
    final private Binder<Client> binder = new Binder<>(Client.class);

    public ClientsView(ClientService clientService) {
        this.clientService = clientService;
    }

    @PostConstruct
    private void init() {
        setSizeFull();
        add(new H2("Clientes"));
        configureGrid();
        configureBinder();
        add(buildToolbar(), grid);
        updateList();
    }

    private Component buildToolbar() {
        Button addClientButton = new Button("Novo Cliente");
        addClientButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        addClientButton.addClickListener(e -> openForm(new Client()));

        HorizontalLayout toolbar = new HorizontalLayout(addClientButton);
        toolbar.setWidthFull();
        toolbar.setJustifyContentMode(FlexComponent.JustifyContentMode.START);

        return toolbar;
    }

    private void configureGrid() {
        grid.addColumn(Client::getFullName).setHeader("Nome").setAutoWidth(true);
        grid.addColumn(Client::getCpf).setHeader("CPF").setAutoWidth(true);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        grid.addColumn(client -> {
                    if (client.getBirthDate() != null) {
                        return client.getBirthDate().format(formatter);
                    } else {
                        return "";
                    }
                }).setHeader("Nascimento")
                .setAutoWidth(true);

        grid.addComponentColumn(client -> {
            Button edit = new Button("Editar", click -> openForm(client));
            Button delete = new Button("Excluir", click -> deleteClient(client));
            delete.addThemeVariants(ButtonVariant.LUMO_ERROR);
            return new HorizontalLayout(edit, delete);
        }).setHeader("Ações");
    }

    private void configureBinder() {
        binder.forField(fullNameField)
                .asRequired("Nome é obrigatório")
                .bind(Client::getFullName, Client::setFullName);

        binder.forField(cpfField)
                .asRequired("CPF é obrigatório")
                .withValidator((Validator<String>) (value, context) -> {
                    String numericCpf = value.replaceAll("\\D", "");
                    if (numericCpf.length() != 11 || !CpfUtils.isValid(numericCpf)) {
                        return ValidationResult.error("CPF inválido");
                    }
                    return ValidationResult.ok();
                })
                .bind(Client::getCpf, Client::setCpf);

        binder.forField(birthDateField)
                .asRequired("Data de nascimento é obrigatória")
                .bind(Client::getBirthDate, Client::setBirthDate);

        cpfField.setValueChangeMode(ValueChangeMode.EAGER);
        cpfField.addValueChangeListener(e -> {
            String value = e.getValue().replaceAll("\\D", "");
            if (value.length() > 11) value = value.substring(0, 11);
            StringBuilder masked = new StringBuilder();
            for (int i = 0; i < value.length(); i++) {
                masked.append(value.charAt(i));
                if (i == 2 || i == 5) masked.append('.');
                if (i == 8) masked.append('-');
            }
            cpfField.setValue(masked.toString());
        });
    }

    private void openForm(Client client) {
        this.currentClient = client;

        if (formDialog == null) {
            formDialog = new Dialog();
            formDialog.setHeaderTitle("Cliente");

            FormLayout form = new FormLayout();
            form.add(fullNameField, cpfField, birthDateField);

            saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            cancelButton.addClickListener(e -> formDialog.close());
            saveButton.addClickListener(e -> saveClient());

            HorizontalLayout actions = new HorizontalLayout(saveButton, cancelButton);
            formDialog.add(form, actions);
        }

        fullNameField.setValue(client.getFullName() != null ? client.getFullName() : "");
        cpfField.setValue(client.getCpf() != null ? client.getCpf() : "");
        birthDateField.setValue(client.getBirthDate());

        formDialog.open();
    }

    private void saveClient() {
        try {
            binder.writeBean(currentClient);
            clientService.save(currentClient);

            Notification.show("Cliente salvo com sucesso!");
            formDialog.close();
            updateList();
        } catch (ValidationException e) {
            Notification.show("Erro de validação: verifique os campos");
        } catch (Exception e) {
            Notification.show("Erro: " + e.getMessage());
        }
    }

    private void deleteClient(Client client) {
        clientService.delete(client.getId());
        Notification.show("Cliente excluído");
        updateList();
    }

    private void updateList() {
        grid.setItems(clientService.findAll());
    }
}
