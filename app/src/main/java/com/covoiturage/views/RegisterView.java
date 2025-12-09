package com.covoiturage.views;

import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.UserService;
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
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.VaadinSession;

@Route("register")
@PageTitle("Inscription - Covoiturage")
public class RegisterView extends VerticalLayout {
    
    private final UserService userService;
    
    private EmailField emailField;
    private PasswordField passwordField;
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField phoneField;
    private Button registerButton;
    
    public RegisterView(UserService userService) {
        this.userService = userService;
        
        // Configuration de la page
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        getStyle()
            .set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)")
            .set("padding", "2rem");
        
        // Conteneur principal (carte)
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("500px");
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
        
        // Icône de l'utilisateur avec fond gradient
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
        
        Icon userIcon = VaadinIcon.USER_CARD.create();
        userIcon.setSize("40px");
        userIcon.getStyle().set("color", "white");
        iconContainer.add(userIcon);
        
        H1 title = new H1("Créer un compte");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("letter-spacing", "-0.025em");
        
        Paragraph subtitle = new Paragraph("Rejoignez notre communauté de covoiturage");
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
        form.getStyle().set("gap", "1rem");
        
        // Ligne pour prénom et nom
        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.setSpacing(true);
        nameRow.getStyle().set("gap", "1rem");
        
        firstNameField = new TextField("Prénom");
        firstNameField.setRequired(true);
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        firstNameField.setPlaceholder("Entrez votre prénom");
        firstNameField.getStyle().set("flex", "1");
        
        lastNameField = new TextField("Nom");
        lastNameField.setRequired(true);
        lastNameField.setPrefixComponent(VaadinIcon.USER.create());
        lastNameField.setPlaceholder("Entrez votre nom");
        lastNameField.getStyle().set("flex", "1");
        
        nameRow.add(firstNameField, lastNameField);
        
        // Email
        emailField = new EmailField("Email");
        emailField.setRequired(true);
        emailField.setWidthFull();
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setPlaceholder("exemple@email.com");
        emailField.setErrorMessage("Veuillez entrer un email valide");
        
        // Mot de passe
        passwordField = new PasswordField("Mot de passe");
        passwordField.setRequired(true);
        passwordField.setWidthFull();
        passwordField.setPrefixComponent(VaadinIcon.LOCK.create());
        passwordField.setPlaceholder("Minimum 6 caractères");
        passwordField.setHelperText("Le mot de passe doit contenir au moins 6 caractères");
        
        // Téléphone
        phoneField = new TextField("Téléphone");
        phoneField.setWidthFull();
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setPlaceholder("06 12 34 56 78");
        phoneField.setHelperText("Optionnel - Pour faciliter la communication");
        
        form.add(nameRow, emailField, passwordField, phoneField);
        formContainer.add(form);
        
        // Bouton d'inscription
        registerButton = new Button("Créer mon compte", VaadinIcon.CHECK.create());
        registerButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        registerButton.setWidthFull();
        registerButton.addClickListener(e -> handleRegister());
        registerButton.getStyle()
            .set("margin-top", "1rem")
            .set("font-weight", "600");
        
        formContainer.add(registerButton);
        
        // Lien vers la connexion
        HorizontalLayout loginLinkContainer = new HorizontalLayout();
        loginLinkContainer.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        loginLinkContainer.setWidthFull();
        loginLinkContainer.getStyle()
            .set("margin-top", "1.5rem")
            .set("padding-top", "1.5rem")
            .set("border-top", "1px solid #e2e8f0");
        
        Paragraph loginText = new Paragraph("Vous avez déjà un compte? ");
        loginText.getStyle()
            .set("margin", "0")
            .set("color", "#64748b")
            .set("display", "inline");
        
        RouterLink loginLink = new RouterLink("Se connecter", LoginView.class);
        loginLink.getStyle()
            .set("color", "#2563eb")
            .set("font-weight", "600")
            .set("text-decoration", "none");
        
        Div loginLinkDiv = new Div(loginText, loginLink);
        loginLinkDiv.getStyle().set("display", "flex").set("gap", "0.25rem");
        
        loginLinkContainer.add(loginLinkDiv);
        formContainer.add(loginLinkContainer);
        
        // Ajouter le conteneur principal à la vue
        add(formContainer);
    }
    
    private void handleRegister() {
        try {
            // Validation
            if (!validateForm()) {
                return;
            }
            
            String email = emailField.getValue();
            String password = passwordField.getValue();
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String phone = phoneField.getValue();
            
            // Enregistrement
            UserDTO user = userService.register(email, password, firstName, lastName, phone);
            
            // Sauvegarde dans la session
            VaadinSession.getCurrent().setAttribute("currentUser", user);
            
            // Notification de succès
            Notification notification = Notification.show(
                "✓ Inscription réussie! Bienvenue " + user.getFirstName() + " 🎉",
                5000,
                Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            // Redirection vers la page d'accueil
            getUI().ifPresent(ui -> ui.navigate(HomeView.class));
            
        } catch (Exception ex) {
            Notification notification = Notification.show(
                "⚠ Erreur: " + ex.getMessage(),
                5000,
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private boolean validateForm() {
        // Validation du prénom
        if (firstNameField.getValue().trim().isEmpty()) {
            Notification.show("⚠ Le prénom est obligatoire", 
                            3000, Notification.Position.MIDDLE)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            firstNameField.focus();
            return false;
        }
        
        // Validation du nom
        if (lastNameField.getValue().trim().isEmpty()) {
            Notification.show("⚠ Le nom est obligatoire", 
                            3000, Notification.Position.MIDDLE)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            lastNameField.focus();
            return false;
        }
        
        // Validation de l'email
        if (emailField.getValue().trim().isEmpty() || !emailField.getValue().contains("@")) {
            Notification.show("⚠ Veuillez entrer un email valide", 
                            3000, Notification.Position.MIDDLE)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            emailField.focus();
            return false;
        }
        
        // Validation du mot de passe
        if (passwordField.getValue().length() < 6) {
            Notification.show("⚠ Le mot de passe doit contenir au moins 6 caractères", 
                            3000, Notification.Position.MIDDLE)
                       .addThemeVariants(NotificationVariant.LUMO_ERROR);
            passwordField.focus();
            return false;
        }
        
        return true;
    }
}