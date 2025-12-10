package com.covoiturage.views;

import com.covoiturage.dto.UserDTO;
import com.covoiturage.security.SecurityService;
import com.covoiturage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
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

@Route("edit-profile")
@PageTitle("Modifier mon profil - Covoiturage")
public class EditProfileView extends VerticalLayout {
    
    private UserService userService;
    private SecurityService securityService;
    private UserDTO currentUser;
    
    private TextField firstNameField;
    private TextField lastNameField;
    private TextField phoneField;
    private TextField emailField;
    private TextArea bioArea;
    private Button saveButton;
    private Button cancelButton;
    
    public EditProfileView(UserService userService, SecurityService securityService) {
        this.userService = userService;
        this.securityService = securityService;
        
        if (!securityService.isAuthenticated()) {
            Notification.show("Vous devez être connecté", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        
        // Get current user from service
        try {
            Long userId = securityService.getAuthenticatedUserId();
            if (userId != null) {
                currentUser = userService.getUserById(userId);
            }
        } catch (Exception e) {
            currentUser = null;
        }
        
        if (currentUser == null) {
            Notification.show("Erreur de chargement du profil", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        
        setSizeFull();
        setAlignItems(FlexComponent.Alignment.CENTER);
        setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        setPadding(true);
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)");
        
        // Form container
        VerticalLayout formContainer = new VerticalLayout();
        formContainer.setWidth("600px");
        formContainer.setAlignItems(FlexComponent.Alignment.STRETCH);
        formContainer.setPadding(true);
        formContainer.setSpacing(true);
        formContainer.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 20px 25px -5px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2.5rem");
        
        // Header
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(true);
        
        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "70px")
            .set("height", "70px")
            .set("border-radius", "14px")
            .set("background", "linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 8px 16px -4px rgba(139, 92, 246, 0.4)")
            .set("margin-bottom", "1rem");
        
        Icon editIcon = VaadinIcon.EDIT.create();
        editIcon.setSize("35px");
        editIcon.getStyle().set("color", "white");
        iconContainer.add(editIcon);
        
        H1 title = new H1("Modifier mon profil");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        header.add(iconContainer, title);
        formContainer.add(header);
        
        // Email field (read-only)
        emailField = new TextField("Email");
        emailField.setValue(currentUser.getEmail());
        emailField.setReadOnly(true);
        emailField.setWidthFull();
        emailField.setPrefixComponent(VaadinIcon.ENVELOPE.create());
        emailField.setHelperText("L'email ne peut pas être modifié");
        emailField.getStyle()
            .set("margin-top", "1rem")
            .set("background", "#f8fafc");
        
        // Name fields
        HorizontalLayout nameRow = new HorizontalLayout();
        nameRow.setWidthFull();
        nameRow.setSpacing(true);
        
        firstNameField = new TextField("Prénom");
        firstNameField.setValue(currentUser.getFirstName());
        firstNameField.setRequired(true);
        firstNameField.setPrefixComponent(VaadinIcon.USER.create());
        firstNameField.getStyle().set("flex", "1");
        
        lastNameField = new TextField("Nom");
        lastNameField.setValue(currentUser.getLastName());
        lastNameField.setRequired(true);
        lastNameField.setPrefixComponent(VaadinIcon.USER.create());
        lastNameField.getStyle().set("flex", "1");
        
        nameRow.add(firstNameField, lastNameField);
        
        // Phone field
        phoneField = new TextField("Téléphone");
        if (currentUser.getPhone() != null) {
            phoneField.setValue(currentUser.getPhone());
        }
        phoneField.setWidthFull();
        phoneField.setPrefixComponent(VaadinIcon.PHONE.create());
        phoneField.setPlaceholder("06 12 34 56 78");
        phoneField.setHelperText("Optionnel");
        
        // Bio field
        bioArea = new TextArea("Bio");
        if (currentUser.getBio() != null) {
            bioArea.setValue(currentUser.getBio());
        }
        bioArea.setWidthFull();
        bioArea.setHeight("150px");
        bioArea.setPlaceholder("Parlez un peu de vous...");
        bioArea.setMaxLength(500);
        bioArea.setHelperText("Maximum 500 caractères");
        
        formContainer.add(emailField, nameRow, phoneField, bioArea);
        
        // Action buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "1.5rem");
        
        saveButton = new Button("💾 Enregistrer", VaadinIcon.CHECK.create());
        saveButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        saveButton.getStyle().set("font-weight", "600");
        saveButton.addClickListener(e -> handleSave());
        
        cancelButton = new Button("Annuler", VaadinIcon.CLOSE.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> {
            getUI().ifPresent(ui -> ui.navigate(ProfileView.class, currentUser.getId()));
        });
        
        buttons.add(saveButton, cancelButton);
        formContainer.add(buttons);
        
        add(formContainer);
    }
    
    private void handleSave() {
        try {
            // Validation
            if (!validateForm()) {
                return;
            }
            
            String firstName = firstNameField.getValue();
            String lastName = lastNameField.getValue();
            String phone = phoneField.getValue();
            String bio = bioArea.getValue();
            
            UserDTO updatedUser = userService.updateProfile(
                currentUser.getId(), 
                firstName, 
                lastName, 
                phone, 
                bio
            );
            
            // Update session if needed (though SecurityService handles this)
            
            Notification notification = Notification.show(
                "✓ Profil mis à jour avec succès! 🎉",
                4000,
                Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            getUI().ifPresent(ui -> ui.navigate(ProfileView.class, updatedUser.getId()));
            
        } catch (Exception ex) {
            Notification notification = Notification.show(
                "⚠ Erreur: " + ex.getMessage(),
                4000,
                Notification.Position.MIDDLE
            );
            notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
        }
    }
    
    private boolean validateForm() {
        if (firstNameField.getValue().trim().isEmpty()) {
            showError("Le prénom est obligatoire");
            firstNameField.focus();
            return false;
        }
        if (lastNameField.getValue().trim().isEmpty()) {
            showError("Le nom est obligatoire");
            lastNameField.focus();
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        Notification notification = Notification.show("⚠ " + message, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
}
