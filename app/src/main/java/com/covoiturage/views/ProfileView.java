package com.covoiturage.views;

import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("profile")
@PageTitle("Profil - Covoiturage")
public class ProfileView extends VerticalLayout implements HasUrlParameter<Long> {
    
    private UserService userService;
    private Long userId;
    private UserDTO user;
    
    public ProfileView(UserService userService) {
        this.userService = userService;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)");
    }
    
    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.userId = parameter;
        loadUserProfile();
    }
    
    private void loadUserProfile() {
        try {
            user = userService.getUserById(userId);
            buildUI();
        } catch (Exception ex) {
            Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(HomeView.class));
        }
    }
    
    private void buildUI() {
        removeAll();
        
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.getStyle().set("max-width", "800px").set("margin", "0 auto");
        
        // Profile header
        Div profileHeader = createProfileHeader();
        
        // Info card
        Div infoCard = createInfoCard();
        
        // Driver stats (if exists)
        if (user.getTotalTripsDriver() != null && user.getTotalTripsDriver() > 0) {
            Div statsCard = createDriverStatsCard();
            mainContainer.add(profileHeader, infoCard, statsCard);
        } else {
            mainContainer.add(profileHeader, infoCard);
        }
        
        // Action buttons
        HorizontalLayout actions = createActionButtons();
        mainContainer.add(actions);
        
        add(mainContainer);
    }
    
    private Div createProfileHeader() {
        Div header = new Div();
        header.setWidthFull();
        header.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2.5rem")
            .set("text-align", "center");
        
        // Avatar
        Div avatar = new Div();
        avatar.getStyle()
            .set("width", "100px")
            .set("height", "100px")
            .set("border-radius", "50%")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("display", "inline-flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-size", "3rem")
            .set("font-weight", "800")
            .set("color", "white")
            .set("margin", "0 auto 1.5rem")
            .set("box-shadow", "0 10px 20px rgba(37, 99, 235, 0.3)");
        avatar.setText(user.getFirstName().substring(0, 1).toUpperCase());
        
        H1 name = new H1(user.getFirstName() + " " + user.getLastName());
        name.getStyle()
            .set("margin", "0 0 0.5rem 0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        Div verifiedBadge = new Div();
        if (user.getIsVerified()) {
            verifiedBadge.setText("✓ Compte vérifié");
            verifiedBadge.getStyle()
                .set("display", "inline-block")
                .set("padding", "6px 16px")
                .set("border-radius", "20px")
                .set("background", "rgba(16, 185, 129, 0.1)")
                .set("color", "#10b981")
                .set("font-weight", "600")
                .set("font-size", "0.875rem");
        } else {
            verifiedBadge.setText("⚠ Compte non vérifié");
            verifiedBadge.getStyle()
                .set("display", "inline-block")
                .set("padding", "6px 16px")
                .set("border-radius", "20px")
                .set("background", "rgba(245, 158, 11, 0.1)")
                .set("color", "#f59e0b")
                .set("font-weight", "600")
                .set("font-size", "0.875rem");
        }
        
        header.add(avatar, name, verifiedBadge);
        return header;
    }
    
    private Div createInfoCard() {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2rem");
        
        H2 title = new H2("Informations");
        title.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        VerticalLayout infoList = new VerticalLayout();
        infoList.setPadding(false);
        infoList.setSpacing(true);
        infoList.getStyle().set("gap", "1rem");
        
        infoList.add(
            createInfoItem(VaadinIcon.ENVELOPE, "Email", user.getEmail())
        );
        
        if (user.getPhone() != null && !user.getPhone().isEmpty()) {
            infoList.add(createInfoItem(VaadinIcon.PHONE, "Téléphone", user.getPhone()));
        }
        
        if (user.getBio() != null && !user.getBio().isEmpty()) {
            Div bioBox = new Div();
            bioBox.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "1rem")
                .set("border-radius", "8px")
                .set("border-left", "4px solid #2563eb")
                .set("margin-top", "0.5rem");
            
            Paragraph bioTitle = new Paragraph("💬 Bio");
            bioTitle.getStyle()
                .set("margin", "0 0 0.5rem 0")
                .set("font-weight", "700")
                .set("color", "#1e293b");
            
            Paragraph bioText = new Paragraph(user.getBio());
            bioText.getStyle()
                .set("margin", "0")
                .set("color", "#64748b")
                .set("line-height", "1.6");
            
            bioBox.add(bioTitle, bioText);
            infoList.add(bioBox);
        }
        
        card.add(title, infoList);
        return card;
    }
    
    private Div createInfoItem(VaadinIcon iconType, String label, String value) {
        Div item = new Div();
        item.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "1rem")
            .set("padding", "0.75rem")
            .set("border-radius", "8px")
            .set("background", "#f8fafc");
        
        Icon icon = iconType.create();
        icon.setSize("24px");
        icon.getStyle().set("color", "#2563eb");
        
        VerticalLayout textBox = new VerticalLayout();
        textBox.setPadding(false);
        textBox.setSpacing(false);
        
        Paragraph labelText = new Paragraph(label);
        labelText.getStyle()
            .set("margin", "0")
            .set("font-size", "0.875rem")
            .set("font-weight", "600")
            .set("color", "#64748b");
        
        Paragraph valueText = new Paragraph(value);
        valueText.getStyle()
            .set("margin", "0")
            .set("font-size", "1rem")
            .set("font-weight", "500")
            .set("color", "#1e293b");
        
        textBox.add(labelText, valueText);
        item.add(icon, textBox);
        return item;
    }
    
    private Div createDriverStatsCard() {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2rem");
        
        H2 title = new H2("🚗 En tant que conducteur");
        title.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setJustifyContentMode(FlexComponent.JustifyContentMode.AROUND);
        stats.getStyle().set("flex-wrap", "wrap");
        
        if (user.getTotalTripsDriver() != null) {
            stats.add(createStatBox("🚗", String.valueOf(user.getTotalTripsDriver()), "Trajets", "#2563eb"));
        }
        
        if (user.getAverageRatingDriver() != null && user.getAverageRatingDriver().doubleValue() > 0) {
            stats.add(createStatBox("⭐", user.getAverageRatingDriver() + "/5", "Note", "#f59e0b"));
        }
        
        card.add(title, stats);
        return card;
    }
    
    private Div createStatBox(String emoji, String value, String label, String color) {
        Div box = new Div();
        box.getStyle()
            .set("text-align", "center")
            .set("padding", "1.5rem")
            .set("background", "#f8fafc")
            .set("border-radius", "12px")
            .set("min-width", "150px")
            .set("border", "2px solid " + color + "20");
        
        Paragraph emojiText = new Paragraph(emoji);
        emojiText.getStyle()
            .set("margin", "0 0 0.5rem 0")
            .set("font-size", "2rem");
        
        Paragraph valueText = new Paragraph(value);
        valueText.getStyle()
            .set("margin", "0")
            .set("font-size", "1.75rem")
            .set("font-weight", "800")
            .set("color", color);
        
        Paragraph labelText = new Paragraph(label);
        labelText.getStyle()
            .set("margin", "0")
            .set("font-size", "0.875rem")
            .set("font-weight", "600")
            .set("color", "#64748b");
        
        box.add(emojiText, valueText, labelText);
        return box;
    }
    
    private HorizontalLayout createActionButtons() {
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "1.5rem");
        
        Button backButton = new Button("← Retour", VaadinIcon.ARROW_LEFT.create());
        backButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        backButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.getPage().getHistory().back())
        );
        
        UserDTO currentUser = (UserDTO) VaadinSession.getCurrent().getAttribute("currentUser");
        if (currentUser != null && currentUser.getId().equals(userId)) {
            Button editButton = new Button("✏ Modifier", VaadinIcon.EDIT.create());
            editButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
            editButton.getStyle().set("font-weight", "600");
            editButton.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate(EditProfileView.class))
            );
            buttons.add(backButton, editButton);
        } else {
            buttons.add(backButton);
        }
        
        return buttons;
    }
}