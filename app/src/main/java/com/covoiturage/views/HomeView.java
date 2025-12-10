package com.covoiturage.views;

import com.covoiturage.security.SecurityService;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.H3;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("")
@PageTitle("Accueil - Covoiturage")
public class HomeView extends VerticalLayout {
    
    private final SecurityService securityService;
    private final UserService userService;
    
    public HomeView(SecurityService securityService, UserService userService) {
        this.securityService = securityService;
        this.userService = userService;
        
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
        
        // User actions section (profile and logout)
        HorizontalLayout userActions = new HorizontalLayout();
        userActions.setSpacing(true);
        userActions.setAlignItems(FlexComponent.Alignment.CENTER);
        userActions.getStyle()
            .set("gap", "1rem")
            .set("margin-bottom", "2rem")
            .set("justify-content", "center");
        
        Button editProfileButton = new Button("Modifier mon profil", VaadinIcon.EDIT.create());
        editProfileButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        editProfileButton.addClickListener(e -> {
            var userForEdit = securityService.getAuthenticatedUser();
            if (userForEdit != null) {
                getUI().ifPresent(ui -> ui.navigate(EditProfileView.class));
            }
        });
        
        Button notificationsButton = new Button("Notifications", VaadinIcon.BELL.create());
        notificationsButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        notificationsButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(NotificationsView.class)));
        
        Button logoutButton = new Button("Déconnexion", VaadinIcon.SIGN_OUT.create());
        logoutButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        logoutButton.getStyle()
            .set("color", "#ef4444")
            .set("font-weight", "600");
        logoutButton.addClickListener(e -> handleLogout());
        
        userActions.add(editProfileButton, notificationsButton, logoutButton);
        content.add(userActions);
        
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
        
        // Recommandations et suggestions
        VerticalLayout recommendationsSection = new VerticalLayout();
        recommendationsSection.setAlignItems(FlexComponent.Alignment.CENTER);
        recommendationsSection.setSpacing(true);
        recommendationsSection.getStyle()
            .set("margin-top", "3rem")
            .set("width", "100%");
        
        H2 recommendationsTitle = new H2("Suggestions pour vous");
        recommendationsTitle.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "600")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        recommendationsSection.add(recommendationsTitle);
        
        // Cartes de suggestions
        HorizontalLayout recommendationsCards = new HorizontalLayout();
        recommendationsCards.setSpacing(true);
        recommendationsCards.setAlignItems(FlexComponent.Alignment.STRETCH);
        recommendationsCards.setWidthFull();
        recommendationsCards.getStyle().set("gap", "1.5rem");
        
        // Suggestion 1
        VerticalLayout suggestion1 = createSuggestionCard(
            "Proposez un trajet",
            "Gagnez de l'argent et partagez vos trajets avec la communauté",
            VaadinIcon.PLUS_CIRCLE,
            "#10b981"
        );
        
        // Suggestion 2
        VerticalLayout suggestion2 = createSuggestionCard(
            "Complétez votre profil",
            "Ajoutez une photo et une bio pour inspirer plus confiance",
            VaadinIcon.USER,
            "#f59e0b"
        );
        
        // Suggestion 3
        VerticalLayout suggestion3 = createSuggestionCard(
            "Activez les notifications",
            "Soyez informé des nouveaux trajets correspondant à vos recherches",
            VaadinIcon.BELL,
            "#2563eb"
        );
        
        recommendationsCards.add(suggestion1, suggestion2, suggestion3);
        recommendationsSection.add(recommendationsCards);
        
        // Section d'aide rapide
        Div quickHelpSection = createQuickHelpSection();
        recommendationsSection.add(quickHelpSection);
        
        content.add(recommendationsSection);
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
    
    private VerticalLayout createSuggestionCard(String title, String description, VaadinIcon icon, String color) {
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
            .set("text-align", "center")
            .set("border", "2px solid " + color + "20")
            .set("cursor", "pointer")
            .set("transition", "all 0.2s ease");
        
        // Add hover effect
        card.getElement().addEventListener("mouseenter", e -> {
            card.getStyle()
                .set("transform", "translateY(-4px)")
                .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1), 0 4px 6px -2px rgba(0, 0, 0, 0.05)");
        });
        
        card.getElement().addEventListener("mouseleave", e -> {
            card.getStyle()
                .set("transform", "translateY(0)")
                .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06)");
        });
        
        Icon cardIcon = icon.create();
        cardIcon.setSize("32px");
        cardIcon.getStyle().set("color", color);
        
        H3 cardTitle = new H3(title);
        cardTitle.getStyle()
            .set("margin", "0.75rem 0")
            .set("font-size", "1.1rem")
            .set("font-weight", "600")
            .set("color", "#1e293b");
        
        Paragraph cardDescription = new Paragraph(description);
        cardDescription.getStyle()
            .set("margin", "0")
            .set("color", "#64748b")
            .set("font-size", "0.9rem")
            .set("line-height", "1.5");
        
        card.add(cardIcon, cardTitle, cardDescription);
        return card;
    }
    
    private Div createQuickHelpSection() {
        Div helpSection = new Div();
        helpSection.setWidthFull();
        helpSection.getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)")
            .set("border-radius", "12px")
            .set("padding", "2rem")
            .set("margin-top", "2rem")
            .set("border", "1px solid #e2e8f0");
        
        H3 helpTitle = new H3("Besoin d'aide ?");
        helpTitle.getStyle()
            .set("margin", "0 0 1rem 0")
            .set("font-size", "1.25rem")
            .set("font-weight", "600")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        Paragraph helpText = new Paragraph("Découvrez comment utiliser notre plateforme, consulter notre FAQ ou contacter le support si vous avez des questions.");
        helpText.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("color", "#64748b")
            .set("text-align", "center")
            .set("line-height", "1.6");
        
        HorizontalLayout helpButtons = new HorizontalLayout();
        helpButtons.setSpacing(true);
        helpButtons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        helpButtons.setWidthFull();
        helpButtons.getStyle().set("gap", "1rem");
        
        Button guideButton = new Button("Guide d'utilisation", VaadinIcon.BOOK.create());
        guideButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        guideButton.addClickListener(e -> {
            // TODO: Navigate to guide page
            showInfo("Guide en cours de développement");
        });
        
        Button faqButton = new Button("FAQ", VaadinIcon.QUESTION_CIRCLE.create());
        faqButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        faqButton.addClickListener(e -> {
            // TODO: Navigate to FAQ page
            showInfo("FAQ en cours de développement");
        });
        
        Button supportButton = new Button("Support", VaadinIcon.COMMENTS.create());
        supportButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        supportButton.addClickListener(e -> {
            // TODO: Navigate to support page
            showInfo("Support en cours de développement");
        });
        
        helpButtons.add(guideButton, faqButton, supportButton);
        
        helpSection.add(helpTitle, helpText, helpButtons);
        return helpSection;
    }
    
    private void openEditProfileDialog() {
        var user = securityService.getAuthenticatedUser();
        
        Dialog dialog = new Dialog();
        dialog.setWidth("600px");
        dialog.setHeight("auto");
        
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        dialogLayout.setAlignItems(FlexComponent.Alignment.STRETCH);
        
        // Header
        H2 title = new H2("👤 Modifier mon profil");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        // Form fields
        TextField firstNameField = new TextField("Prénom");
        firstNameField.setValue(user.getFirstName());
        firstNameField.setRequired(true);
        firstNameField.setWidthFull();
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        
        TextField lastNameField = new TextField("Nom");
        lastNameField.setValue(user.getLastName());
        lastNameField.setRequired(true);
        lastNameField.setWidthFull();
        lastNameField.setPrefixComponent(VaadinIcon.USER.create());
        
        TextField emailField = new TextField("Email");
        emailField.setValue(user.getEmail());
        emailField.setReadOnly(true);
        emailField.setWidthFull();
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setHelperText("L'email ne peut pas être modifié");
        emailField.getStyle().set("background", "#f8fafc");
        
        TextField phoneField = new TextField("Téléphone");
        if (user.getPhone() != null) {
            phoneField.setValue(user.getPhone());
        }
        phoneField.setWidthFull();
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setPlaceholder("06 12 34 56 78");
        phoneField.setHelperText("Optionnel");
        
        TextArea bioArea = new TextArea("Bio");
        if (user.getBio() != null) {
            bioArea.setValue(user.getBio());
        }
        bioArea.setWidthFull();
        bioArea.setHeight("120px");
        bioArea.setPlaceholder("Parlez un peu de vous...");
        bioArea.setMaxLength(500);
        bioArea.setHelperText("Maximum 500 caractères");
        
        // Buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.END);
        buttons.setSpacing(true);
        
        Button cancelButton = new Button("Annuler", VaadinIcon.CLOSE.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> dialog.close());
        
        Button saveButton = new Button("Enregistrer", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        saveButton.addClickListener(e -> handleProfileUpdate(
            dialog, user.getId(), firstNameField, lastNameField, phoneField, bioArea
        ));
        
        buttons.add(cancelButton, saveButton);
        
        dialogLayout.add(title, firstNameField, lastNameField, emailField, phoneField, bioArea, buttons);
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private void handleProfileUpdate(Dialog dialog, Long userId, TextField firstNameField, 
                                   TextField lastNameField, TextField phoneField, TextArea bioArea) {
        try {
            // Validation
            if (firstNameField.getValue().trim().isEmpty()) {
                showError("Le prénom est obligatoire");
                firstNameField.focus();
                return;
            }
            if (lastNameField.getValue().trim().isEmpty()) {
                showError("Le nom est obligatoire");
                lastNameField.focus();
                return;
            }
            
            // Update user
            UserDTO updatedUser = userService.updateProfile(
                userId,
                firstNameField.getValue().trim(),
                lastNameField.getValue().trim(),
                phoneField.getValue().trim(),
                bioArea.getValue().trim()
            );
            
            // Update session
            VaadinSession.getCurrent().setAttribute("currentUser", updatedUser);
            
            showSuccess("✓ Profil mis à jour avec succès! 🎉");
            dialog.close();
            
            // Refresh the view to show updated user info
            getUI().ifPresent(ui -> ui.navigate(""));
            
        } catch (Exception ex) {
            showError("⚠ Erreur: " + ex.getMessage());
        }
    }
    
    private void handleLogout() {
        try {
            // Clear session
            VaadinSession.getCurrent().setAttribute(UserDTO.class, null);
            VaadinSession.getCurrent().close();
            
            showSuccess("Déconnexion réussie");
            
            // Navigate to home page
            getUI().ifPresent(ui -> {
                ui.getPage().reload();
            });
            
        } catch (Exception ex) {
            showError("Erreur lors de la déconnexion: " + ex.getMessage());
        }
    }
    
    private void showInfo(String message) {
        com.vaadin.flow.component.notification.Notification notification = 
            com.vaadin.flow.component.notification.Notification.show("ℹ️ " + message, 3000, 
            com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        notification.addThemeVariants(
            com.vaadin.flow.component.notification.NotificationVariant.LUMO_CONTRAST);
    }
    
    private void showSuccess(String message) {
        com.vaadin.flow.component.notification.Notification notification = 
            com.vaadin.flow.component.notification.Notification.show(message, 4000, 
            com.vaadin.flow.component.notification.Notification.Position.TOP_CENTER);
        notification.addThemeVariants(
            com.vaadin.flow.component.notification.NotificationVariant.LUMO_SUCCESS);
    }
    
    private void showError(String message) {
        com.vaadin.flow.component.notification.Notification notification = 
            com.vaadin.flow.component.notification.Notification.show(message, 3000, 
            com.vaadin.flow.component.notification.Notification.Position.MIDDLE);
        notification.addThemeVariants(
            com.vaadin.flow.component.notification.NotificationVariant.LUMO_ERROR);
    }
}
