package com.amf.promotora.view;

import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.sidenav.SideNav;
import com.vaadin.flow.component.sidenav.SideNavItem;
import com.vaadin.flow.router.RouterLayout;
import com.vaadin.flow.theme.lumo.LumoUtility;

public class MainLayout extends AppLayout implements RouterLayout {

    public MainLayout() {
        createHeader();
        createSidebar();
    }

    private void createHeader() {
        H1 title = new H1("AMF Promotora - Dashboard");
        title.addClassNames(
                LumoUtility.FontSize.LARGE,
                LumoUtility.Margin.MEDIUM
        );

        addToNavbar(title);
    }

    private void createSidebar() {
        SideNav nav = new SideNav();

        nav.addItem(new SideNavItem("Início", MainView.class));
        nav.addItem(new SideNavItem("Clientes", ClientsView.class));
        nav.addItem(new SideNavItem("Contas", AccountsView.class));
        nav.addItem(new SideNavItem("Transações", TransactionsView.class));

        VerticalLayout wrapper = new VerticalLayout(nav);
        wrapper.setPadding(false);
        wrapper.setSpacing(false);
        wrapper.setSizeFull();

        addToDrawer(wrapper);
    }
}
