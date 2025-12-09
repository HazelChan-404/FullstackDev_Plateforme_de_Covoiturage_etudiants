package com.covoiturage.views;

import com.covoiturage.security.SecurityService;
import com.vaadin.flow.component.UI;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.applayout.DrawerToggle;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.dependency.CssImport;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Image;
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
        
        // Create header
        createHeader();
        
        // Create navigation menu
        Tabs menu = createMenu();
        addToDrawer(createDrawerContent(menu));
    }
    
    private void createHeader() {
        H1 logo = new H1("Co-Voiturage");
        logo.getStyle()
            .set("font-size", "1.75rem")
            .set("margin", "0")
            .set("color", "white")
            .set("font-weight", "700")
            .set("letter-spacing", "-0.025em");
        
        HorizontalLayout header = new HorizontalLayout(new DrawerToggle(), logo);
        header.setDefaultVerticalComponentAlignment(Alignment.CENTER);
        header.setWidthFull();
        header.getStyle()
            .set("padding", "0 2rem")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("min-height", "72px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)")
            .set("backdrop-filter", "blur(10px)");
        
        addToNavbar(header);
    }
    
    private Tabs createMenu() {
        Tabs tabs = new Tabs();
        tabs.setOrientation(Tabs.Orientation.VERTICAL);
        
        // Add menu items based on authentication status
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
            createTab(VaadinIcon.SEARCH, "Rechercher des trajets", SearchTripsView.class),
            createTab(VaadinIcon.SIGN_IN, "Connexion", LoginView.class),
            createTab(VaadinIcon.USER, "Inscription", RegisterView.class)
        );
    }
    
    private void addAuthenticatedMenuItems(Tabs tabs) {
        tabs.add(
            createTab(VaadinIcon.HOME, "Accueil", HomeView.class),
            createTab(VaadinIcon.SEARCH, "Rechercher des trajets", SearchTripsView.class),
            createTab(VaadinIcon.PLUS, "Créer un trajet", CreateTripView.class),
            createTab(VaadinIcon.CAR, "Mes trajets", MyTripsView.class),
            createTab(VaadinIcon.CALENDAR, "Mes réservations", MyBookingsView.class),
            createTab(VaadinIcon.ENVELOPE, "Messages", NotificationsView.class),
            createTab(VaadinIcon.STAR, "Avis", ProfileView.class),
            createTab(VaadinIcon.USER, "Mon profil", ProfileView.class)
        );
        
        // Logout button
        Button logoutButton = new Button("Déconnexion", e -> {
            securityService.logout();
            UI.getCurrent().navigate("");
        });
        logoutButton.setIcon(VaadinIcon.SIGN_OUT.create());
        tabs.add(new Tab(logoutButton));
    }
    
    private Tab createTab(VaadinIcon icon, String title, Class<? extends com.vaadin.flow.component.Component> navigationTarget) {
        if (navigationTarget == null) {
            return new Tab(icon.create(), new com.vaadin.flow.component.html.Span(title));
        }
        
        RouterLink link = new RouterLink();
        link.add(icon.create());
        link.add(title);
        link.setRoute(navigationTarget);
        
        Tab tab = new Tab(link);
        return tab;
    }
    
    private VerticalLayout createDrawerContent(Tabs menu) {
        VerticalLayout layout = new VerticalLayout();
        
        // Add user info if authenticated
        if (securityService.isAuthenticated()) {
            var user = securityService.getAuthenticatedUser();
            if (user != null) {
                HorizontalLayout userInfo = new HorizontalLayout();
                userInfo.addClassName("user-info");
                
                Image avatar = new Image("images/avatar.png", "Avatar");
                avatar.setWidth("40px");
                avatar.setHeight("40px");
                avatar.getStyle().set("border-radius", "50%");
                
                VerticalLayout userText = new VerticalLayout();
                userText.add(new com.vaadin.flow.component.html.Span(user.getFirstName() + " " + user.getLastName()));
                com.vaadin.flow.component.html.Span emailSpan = new com.vaadin.flow.component.html.Span(user.getEmail());
                emailSpan.setClassName("text-secondary");
                userText.add(emailSpan);
                userText.setPadding(false);
                userText.setSpacing(false);
                
                userInfo.add(avatar, userText);
                userInfo.setAlignItems(Alignment.CENTER);
                userInfo.setWidthFull();
                userInfo.setMargin(false);
                
                layout.add(userInfo);
                layout.add(new com.vaadin.flow.component.html.Hr());
            }
        }
        
        layout.add(menu);
        layout.setSizeFull();
        layout.setPadding(false);
        layout.setSpacing(false);
        
        return layout;
    }
    
    @Override
    protected void afterNavigation() {
        super.afterNavigation();
        // Simplified navigation handling
    }
}
