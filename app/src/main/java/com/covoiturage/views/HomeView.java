package com.covoiturage.views;

import com.covoiturage.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("")
@PageTitle("Accueil - Covoiturage")
public class HomeView extends VerticalLayout {
    
    private final SecurityService securityService;
    
    public HomeView(SecurityService securityService) {
        this.securityService = securityService;
        
        // Configuration de la page
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.START);
        getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)")
            .set("padding", "2rem");
        
        // Contenu principal
        if (securityService.isAuthenticated()) {
            createAuthenticatedContent();
        } else {
            createPublicContent();
        }
    }
    
    private void createPublicContent() {
        // Section principale
        VerticalLayout mainSection = new VerticalLayout();
        mainSection.setAlignItems(FlexComponent.Alignment.CENTER);
        mainSection.setMaxWidth("800px");
        mainSection.setSpacing(true);
        mainSection.setPadding(true);
        
        // En-tête
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.getStyle().set("margin-bottom", "3rem");
        
        // Icône principale
        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "120px")
            .set("height", "120px")
            .set("border-radius", "24px")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 20px 25px -5px rgba(37, 99, 235, 0.3)")
            .set("margin-bottom", "2rem");
        
        Icon carIcon = VaadinIcon.CAR.create();
        carIcon.setSize("60px");
        carIcon.getStyle().set("color", "white");
        iconContainer.add(carIcon);
        
        H1 title = new H1("Bienvenue sur Co-Voiturage");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "3rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("letter-spacing", "-0.025em")
            .set("text-align", "center");
        
        Paragraph subtitle = new Paragraph("Partagez vos trajets, économisez de l'argent et protégez l'environnement");
        subtitle.getStyle()
            .set("margin", "1rem 0 0 0")
            .set("color", "#64748b")
            .set("font-size", "1.25rem")
            .set("text-align", "center")
            .set("max-width", "600px");
        
        header.add(iconContainer, title, subtitle);
        mainSection.add(header);
        
        // Boutons d'action
        HorizontalLayout actionButtons = new HorizontalLayout();
        actionButtons.setSpacing(true);
        actionButtons.setAlignItems(FlexComponent.Alignment.CENTER);
        actionButtons.getStyle().set("gap", "1rem");
        
        Button searchButton = new Button("Rechercher un trajet", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SearchTripsView.class)));
        
        Button loginButton = new Button("Se connecter", VaadinIcon.SIGN_IN.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        loginButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(LoginView.class)));
        
        Button registerButton = new Button("Créer un compte", VaadinIcon.USER.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
        registerButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(RegisterView.class)));
        
        actionButtons.add(searchButton, loginButton, registerButton);
        mainSection.add(actionButtons);
        
        // Section fonctionnalités
        VerticalLayout featuresSection = new VerticalLayout();
        featuresSection.setAlignItems(FlexComponent.Alignment.CENTER);
        featuresSection.setSpacing(true);
        featuresSection.setPadding(true);
        featuresSection.getStyle()
            .set("margin-top", "4rem")
            .set("width", "100%");
        
        H2 featuresTitle = new H2("Pourquoi choisir Co-Voiturage ?");
        featuresTitle.getStyle()
            .set("margin", "0 0 2rem 0")
            .set("font-size", "2rem")
            .set("font-weight", "600")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        featuresSection.add(featuresTitle);
        
        // Cartes de fonctionnalités
        HorizontalLayout featuresCards = new HorizontalLayout();
        featuresCards.setSpacing(true);
        featuresCards.setAlignItems(FlexComponent.Alignment.STRETCH);
        featuresCards.setWidthFull();
        featuresCards.getStyle().set("gap", "2rem");
        
        // Carte 1
        VerticalLayout feature1 = createFeatureCard(
            VaadinIcon.MONEY.create(),
            "Économisez de l'argent",
            "Partagez les frais de trajet et réduisez vos dépenses de transport jusqu'à 70%"
        );
        
        // Carte 2
        VerticalLayout feature2 = createFeatureCard(
            VaadinIcon.HEART.create(),
            "Écologique",
            "Réduisez votre empreinte carbone en partageant votre véhicule avec d'autres voyageurs"
        );
        
        // Carte 3
        VerticalLayout feature3 = createFeatureCard(
            VaadinIcon.USERS.create(),
            "Communauté",
            "Rencontrez de nouvelles personnes et voyagez en toute sécurité avec notre communauté vérifiée"
        );
        
        featuresCards.add(feature1, feature2, feature3);
        featuresSection.add(featuresCards);
        
        mainSection.add(featuresSection);
        add(mainSection);
    }
    
    private void createAuthenticatedContent() {
        var user = securityService.getAuthenticatedUser();
        
        VerticalLayout content = new VerticalLayout();
        content.setAlignItems(FlexComponent.Alignment.CENTER);
        content.setMaxWidth("800px");
        content.setSpacing(true);
        content.setPadding(true);
        
        // Message de bienvenue
        VerticalLayout welcomeSection = new VerticalLayout();
        welcomeSection.setAlignItems(FlexComponent.Alignment.CENTER);
        welcomeSection.setSpacing(true);
        welcomeSection.getStyle().set("margin-bottom", "2rem");
        
        H1 welcomeTitle = new H1("Bonjour " + user.getFirstName() + " !");
        welcomeTitle.getStyle()
            .set("margin", "0")
            .set("font-size", "2.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        Paragraph welcomeSubtitle = new Paragraph("Que souhaitez-vous faire aujourd'hui ?");
        welcomeSubtitle.getStyle()
            .set("margin", "1rem 0 0 0")
            .set("color", "#64748b")
            .set("font-size", "1.25rem")
            .set("text-align", "center");
        
        welcomeSection.add(welcomeTitle, welcomeSubtitle);
        content.add(welcomeSection);
        
        // Actions rapides
        HorizontalLayout quickActions = new HorizontalLayout();
        quickActions.setSpacing(true);
        quickActions.setAlignItems(FlexComponent.Alignment.CENTER);
        quickActions.getStyle().set("gap", "1rem");
        
        Button searchTripsButton = new Button("Rechercher un trajet", VaadinIcon.SEARCH.create());
        searchTripsButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchTripsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SearchTripsView.class)));
        
        Button createTripButton = new Button("Proposer un trajet", VaadinIcon.PLUS.create());
        createTripButton.addThemeVariants(ButtonVariant.LUMO_SUCCESS, ButtonVariant.LUMO_LARGE);
        createTripButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(CreateTripView.class)));
        
        Button myTripsButton = new Button("Mes trajets", VaadinIcon.CAR.create());
        myTripsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY, ButtonVariant.LUMO_LARGE);
        myTripsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(MyTripsView.class)));
        
        quickActions.add(searchTripsButton, createTripButton, myTripsButton);
        content.add(quickActions);
        
        // Statistiques rapides
        VerticalLayout statsSection = new VerticalLayout();
        statsSection.setAlignItems(FlexComponent.Alignment.CENTER);
        statsSection.setSpacing(true);
        statsSection.getStyle()
            .set("margin-top", "3rem")
            .set("width", "100%");
        
        H2 statsTitle = new H2("Votre activité");
        statsTitle.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "600")
            .set("color", "#1e293b");
        
        statsSection.add(statsTitle);
        
        HorizontalLayout statsCards = new HorizontalLayout();
        statsCards.setSpacing(true);
        statsCards.setAlignItems(FlexComponent.Alignment.STRETCH);
        statsCards.setWidthFull();
        statsCards.getStyle().set("gap", "1.5rem");
        
        // Statistiques (placeholder pour l'instant)
        VerticalLayout stat1 = createStatCard("Trajets proposés", "0", VaadinIcon.CAR.create());
        VerticalLayout stat2 = createStatCard("Réservations", "0", VaadinIcon.CALENDAR.create());
        VerticalLayout stat3 = createStatCard("Économies réalisées", "0€", VaadinIcon.MONEY.create());
        
        statsCards.add(stat1, stat2, stat3);
        statsSection.add(statsCards);
        
        content.add(statsSection);
        add(content);
    }
    
    private VerticalLayout createFeatureCard(Icon icon, String title, String description) {
        VerticalLayout card = new VerticalLayout();
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(true);
        card.setPadding(true);
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)")
            .set("padding", "2rem")
            .set("flex", "1")
            .set("text-align", "center");
        
        // Icône
        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "60px")
            .set("height", "60px")
            .set("border-radius", "12px")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("margin-bottom", "1rem");
        
        icon.setSize("30px");
        icon.getStyle().set("color", "white");
        iconContainer.add(icon);
        
        // Titre
        H3 cardTitle = new H3(title);
        cardTitle.getStyle()
            .set("margin", "0 0 0.75rem 0")
            .set("font-size", "1.25rem")
            .set("font-weight", "600")
            .set("color", "#1e293b");
        
        // Description
        Paragraph cardDescription = new Paragraph(description);
        cardDescription.getStyle()
            .set("margin", "0")
            .set("color", "#64748b")
            .set("font-size", "1rem")
            .set("line-height", "1.5");
        
        card.add(iconContainer, cardTitle, cardDescription);
        return card;
    }
    
    private VerticalLayout createStatCard(String title, String value, Icon icon) {
        VerticalLayout card = new VerticalLayout();
        card.setAlignItems(FlexComponent.Alignment.CENTER);
        card.setSpacing(true);
        card.setPadding(true);
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)")
            .set("padding", "1.5rem")
            .set("flex", "1")
            .set("text-align", "center");
        
        icon.setSize("24px");
        icon.getStyle().set("color", "#2563eb");
        
        Paragraph statValue = new Paragraph(value);
        statValue.getStyle()
            .set("margin", "0.5rem 0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        Paragraph statTitle = new Paragraph(title);
        statTitle.getStyle()
            .set("margin", "0")
            .set("color", "#64748b")
            .set("font-size", "0.875rem");
        
        card.add(icon, statValue, statTitle);
        return card;
    }
}
