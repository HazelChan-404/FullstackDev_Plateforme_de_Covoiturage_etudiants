package com.covoiturage.views;

import com.covoiturage.security.SecurityService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.EmailField;
import com.vaadin.flow.component.textfield.PasswordField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;

@Route("login")
@PageTitle("Connexion - Covoiturage")
public class LoginView extends VerticalLayout {
    
    private final SecurityService securityService;
    
    private EmailField emailField;
    private PasswordField passwordField;
    private Button loginButton;
    
    public LoginView(SecurityService securityService) {
        this.securityService = securityService;
        
        // Configuration de la page
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)")
            .set("padding", "2rem");
        
        // Conteneur principal (carte)
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("450px");
        formContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        formContainer.setSpacing(true);
        formContainer.setPadding(true);
        formContainer.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 20px 25px -5px rgba(0, 0, 0, 0.1), 0 10px 10px -5px rgba(0, 0, 0, 0.04)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "3rem");
        
        // En-tête avec icône
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setSpacing(true);
        header.setPadding(false);
        header.getStyle().set("margin-bottom", "2rem");
        
        // Icône de connexion avec fond gradient
        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "80px")
            .set("height", "80px")
            .set("border-radius", "16px")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 10px 15px -3px rgba(37, 99, 235, 0.3)")
            .set("margin-bottom", "1rem");
        
        Icon lockIcon = VaadinIcon.SIGN_IN.create();
        lockIcon.setSize("40px");
        lockIcon.getStyle().set("color", "white");
        iconContainer.add(lockIcon);
        
        H1 title = new H1("Bon retour!");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("letter-spacing", "-0.025em");
        
        Paragraph subtitle = new Paragraph("Connectez-vous pour continuer");
        subtitle.getStyle()
            .set("margin", "0.5rem 0 0 0")
            .set("color", "#64748b")
            .set("font-size", "1rem")
            .set("text-align", "center");
        
        header.add(iconContainer, title, subtitle);
        formContainer.add(header);
        
        // Formulaire
        VerticalLayout form = new VerticalLayout();
        form.setPadding(false);
        form.setSpacing(true);
        form.getStyle().set("gap", "1.5rem");
        
        // Email
        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setWidthFull();
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setPlaceholder("exemple@email.com");
        emailField.setErrorMessage("Veuillez entrer un email valide");
        emailField.setClearButtonVisible(true);
        
        // Mot de passe
        passwordField = new PasswordField("Mot de passe");
        passwordField.setRequired(true);
        passwordField.setWidthFull();
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setPlaceholder("Entrez votre mot de passe");
        passwordField.setRevealButtonVisible(true);
        
        form.add(emailField, passwordField);
        formContainer.add(form);
        
        // Bouton de connexion
        loginButton = new Button("Se connecter", VaadinIcon.ARROW_RIGHT.create());
        loginButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        loginButton.setWidthFull();
        loginButton.setIconAfterText(true);
        loginButton.addClickListener(e -> handleLogin());
        loginButton.getStyle()
            .set("margin-top", "1rem")
            .set("font-weight", "600");
        
        formContainer.add(loginButton);
        
        // Ligne de séparation
        Div separator = new Div();
        separator.getStyle()
            .set("width", "100%")
            .set("height", "1px")
            .set("background", "#e2e8f0")
            .set("margin", "2rem 0 1.5rem 0");
        formContainer.add(separator);
        
        // Lien vers l'inscription
        HorizontalLayout registerLinkContainer = new HorizontalLayout();
        registerLinkContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        registerLinkContainer.setWidthFull();
        
        Paragraph registerText = new Paragraph("Pas encore de compte? ");
        registerText.getStyle()
            .set("margin", "0")
            .set("color", "#64748b")
            .set("display", "inline");
        
        RouterLink registerLink = new RouterLink("Créer un compte", RegisterView.class);
        registerLink.getStyle()
            .set("color", "#2563eb")
            .set("font-weight", "600")
            .set("text-decoration", "none");
        
        Div registerLinkDiv = new Div(registerText, registerLink);
        registerLinkDiv.getStyle().set("display", "flex").set("gap", "0.25rem");
        
        registerLinkContainer.add(registerLinkDiv);
        formContainer.add(registerLinkContainer);
        
        // Bouton "Parcourir les trajets" (accès sans connexion)
        Button browseButton = new Button("Parcourir les trajets disponibles", VaadinIcon.SEARCH.create());
        browseButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        browseButton.setWidthFull();
        browseButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(SearchTripsView.class)));
        browseButton.getStyle()
            .set("margin-top", "0.5rem")
            .set("color", "#64748b");
        
        formContainer.add(browseButton);
        
        // Ajouter le conteneur principal à la vue
        add(formContainer);
    }
    
    private void handleLogin() {
        String email = emailField.getValue();
        String password = passwordField.getValue();
        
        // Validation des champs
        if (email.isEmpty() || password.isEmpty()) {
            Notification notification = Notification.show(
                "⚠ Veuillez remplir tous les champs",
                3000,
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            
            if (email.isEmpty()) {
                emailField.focus();
            } else {
                passwordField.focus();
            }
            return;
        }
        
        // Validation du format email
        if (!email.contains("@")) {
            Notification notification = Notification.show(
                "⚠ Veuillez entrer un email valide",
                3000,
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
            emailField.focus();
            return;
        }
        
        try {
            // Authentification
            var user = securityService.authenticate(email, password);
            
            if (user != null) {
                // Notification de succès
                Notification notification = Notification.show(
                    "✓ Bienvenue " + user.getFirstName() + "! 🎉",
                    4000,
                    Notification.Position.TOP_CENTER
                );
                notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
                
                // Redirection vers la page d'accueil
                getUI().ifPresent(ui -> ui.navigate(""));
            } else {
                // Notification d'erreur
                Notification notification = Notification.show(
                    "⚠ Email ou mot de passe incorrect",
                    4000,
                    Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                
                // Effacer le mot de passe et focus sur le champ email
                passwordField.clear();
                emailField.focus();
            }
        } catch (Exception ex) {
            Notification notification = Notification.show(
                "⚠ Erreur de connexion: " + ex.getMessage(),
                4000,
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
}
