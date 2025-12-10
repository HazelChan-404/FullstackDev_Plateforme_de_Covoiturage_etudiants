package com.covoiturage.views;

import com.covoiturage.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent.Alignment;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.auth.AccessAnnotationChecker;

@CssImport("./themes/default/styles.css")
public class MainLayout extends AppLayout {
    
    private final SecurityService securityService;
    
    public MainLayout(SecurityService securityService, AccessAnnotationChecker accessChecker) {
        this.securityService = securityService;
        
        createHeader();
        createDrawer();
    }
    
    private void createHeader() {
        DrawerToggle toggle = new DrawerToggle();
        toggle.getStyle().set("color", "white");
        
        H1 logo = new H1("🚗 Co-Voiturage");
        logo.getStyle()
            .set("font-size", "1.5rem")
            .set("margin", "0")
            .set("color", "white")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.025em");
        
        HorizontalLayout header = new HorizontalLayout(toggle, logo);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.setWidthFull();
        header.setSpacing(true);
        header.getStyle()
            .set("padding", "0 1.5rem")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("min-height", "64px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("backdrop-filter", "blur(10px)");
        
        addToNavbar(header);
    }
    
    private void createDrawer() {
        VerticalLayout drawerContent = new VerticalLayout();
        drawerContent.setSizeFull();
        drawerContent.setPadding(false);
        drawerContent.setSpacing(false);
        drawerContent.getStyle()
            .set("background", "white");
        
        if (securityService.isAuthenticated()) {
            drawerContent.add(createUserInfo());
            drawerContent.add(createDivider());
        }
        
        Tabs menu = createMenu();
        drawerContent.add(menu);
        
        addToDrawer(drawerContent);
    }
    
    private Div createUserInfo() {
        Div userInfo = new Div();
        userInfo.getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)")
            .set("padding", "1.5rem")
            .set("margin", "1rem")
            .set("border-radius", "12px")
            .set("border", "1px solid #e2e8f0")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)");
        
        var user = securityService.getAuthenticatedUser();
        if (user != null) {
            // Avatar
            Div avatar = new Div();
            avatar.getStyle()
                .set("width", "50px")
                .set("height", "50px")
                .set("border-radius", "50%")
                .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("font-size", "1.5rem")
                .set("font-weight", "700")
                .set("color", "white")
                .set("margin", "0 auto 0.75rem")
                .set("box-shadow", "0 4px 8px rgba(37, 99, 235, 0.3)");
            avatar.setText(user.getFirstName().substring(0, 1).toUpperCase());
            
            Paragraph name = new Paragraph(user.getFirstName() + " " + user.getLastName());
            name.getStyle()
                .set("margin", "0")
                .set("font-weight", "700")
                .set("font-size", "1.1rem")
                .set("color", "#1e293b")
                .set("text-align", "center");
            
            Paragraph email = new Paragraph(user.getEmail());
            email.getStyle()
                .set("margin", "0.25rem 0 0 0")
                .set("font-size", "0.875rem")
                .set("color", "#64748b")
                .set("text-align", "center");
            
            userInfo.add(avatar, name, email);
        }
        
        return userInfo;
    }
    
    private Div createDivider() {
        Div divider = new Div();
        divider.getStyle()
            .set("height", "1px")
            .set("background", "#e2e8f0")
            .set("margin", "0 1rem");
        return divider;
    }
    
    private Tabs createMenu() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        tabs.setWidthFull();
        tabs.getStyle()
            .set("padding", "1rem 0.5rem");
        
        if (securityService.isAuthenticated()) {
            addAuthenticatedMenuItems(tabs);
        } else {
            addPublicMenuItems(tabs);
        }
        
        return tabs;
    }
    
    private void addPublicMenuItems(Tabs tabs) {
        tabs.add(
            createTab(VaadinIcon.HOME, "Accueil", HomeView.class),
            createTab(VaadinIcon.SEARCH, "Rechercher", SearchTripsView.class),
            createTab(VaadinIcon.SIGN_IN, "Connexion", LoginView.class),
            createTab(VaadinIcon.USER, "Inscription", RegisterView.class)
        );
    }
    
    private void addAuthenticatedMenuItems(Tabs tabs) {
        tabs.add(
            createTab(VaadinIcon.HOME, "Accueil", HomeView.class),
            createTab(VaadinIcon.SEARCH, "Rechercher", SearchTripsView.class),
            createTab(VaadinIcon.PLUS_CIRCLE, "Créer un trajet", CreateTripView.class),
            createTab(VaadinIcon.CAR, "Mes trajets", MyTripsView.class),
            createTab(VaadinIcon.CALENDAR, "Mes réservations", MyBookingsView.class),
            createTab(VaadinIcon.BELL, "Notifications", NotificationsView.class),
            createProfileTab()
        );
        
        // Admin section
        if (securityService.isAdmin()) {
            tabs.add(createDividerTab());
            tabs.add(createTab(VaadinIcon.SHIELD, "Administration", AdminView.class));
        }
        
        // Logout button
        tabs.add(createDividerTab());
        tabs.add(createLogoutTab());
    }
    
    private Tab createTab(VaadinIcon icon, String title, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        RouterLink link = new RouterLink();
        link.setRoute(navigationTarget);
        link.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "0.75rem")
            .set("padding", "0.75rem 1rem")
            .set("border-radius", "8px")
            .set("text-decoration", "none")
            .set("color", "#64748b")
            .set("font-weight", "500")
            .set("transition", "all 0.2s ease")
            .set("width", "100%");
        
        link.getElement().addEventListener("mouseenter", e -> {
            link.getStyle()
                .set("background", "#f8fafc")
                .set("color", "#2563eb")
                .set("transform", "translateX(4px)");
        });
        
        link.getElement().addEventListener("mouseleave", e -> {
            link.getStyle()
                .set("background", "transparent")
                .set("color", "#64748b")
                .set("transform", "translateX(0)");
        });
        
        Div iconWrapper = new Div();
        iconWrapper.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center");
        iconWrapper.add(icon.create());
        
        Paragraph text = new Paragraph(title);
        text.getStyle()
            .set("margin", "0")
            .set("font-size", "0.95rem");
        
        link.add(iconWrapper, text);
        
        Tab tab = new Tab(link);
        tab.getStyle()
            .set("padding", "0")
            .set("margin", "0.25rem 0.5rem");
        
        return tab;
    }
    
    private Tab createDividerTab() {
        Tab dividerTab = new Tab();
        dividerTab.setEnabled(false);
        dividerTab.getStyle()
            .set("height", "1px")
            .set("background", "#e2e8f0")
            .set("margin", "0.75rem 1rem")
            .set("padding", "0");
        return dividerTab;
    }
    
    private Tab createProfileTab() {
        Button profileButton = new Button("Mon profil");
        profileButton.setIcon(VaadinIcon.USER.create());
        profileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        profileButton.getStyle()
            .set("width", "100%")
            .set("justify-content", "flex-start")
            .set("color", "#64748b")
            .set("font-weight", "500");
        
        profileButton.addClickListener(e -> {
            var user = securityService.getAuthenticatedUser();
            if (user != null) {
                UI.getCurrent().navigate(ProfileView.class, user.getId());
            }
        });
        
        profileButton.getElement().addEventListener("mouseenter", e -> {
            profileButton.getStyle()
                .set("background", "#f8fafc")
                .set("color", "#2563eb")
                .set("transform", "translateX(4px)")
                .set("transition", "all 0.2s ease");
        });
        
        profileButton.getElement().addEventListener("mouseleave", e -> {
            profileButton.getStyle()
                .set("background", "transparent")
                .set("color", "#64748b")
                .set("transform", "translateX(0)");
        });
        
        Tab tab = new Tab(profileButton);
        tab.getStyle()
            .set("padding", "0")
            .set("margin", "0.25rem 0.5rem");
        
        return tab;
    }
    
    private Tab createLogoutTab() {
        Button logoutButton = new Button("Déconnexion");
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.getStyle()
            .set("width", "100%")
            .set("justify-content", "flex-start")
            .set("color", "#ef4444")
            .set("font-weight", "500");
        
        logoutButton.addClickListener(e -> {
            securityService.logout();
            UI.getCurrent().navigate("");
        });
        
        Tab tab = new Tab(logoutButton);
        tab.getStyle()
            .set("padding", "0")
            .set("margin", "0.25rem 0.5rem");
        
        return tab;
    }
    
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
    }
}
