package com.covoiturage.views;

import java.time.format.DateTimeFormatter;

import com.covoiturage.dto.TripDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.BookingService;
import com.covoiturage.service.TripService;
import com.covoiturage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.router.BeforeEvent;
import com.vaadin.flow.router.HasUrlParameter;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("trip")
@PageTitle("Détails du trajet - Covoiturage")
public class TripDetailsView extends VerticalLayout implements HasUrlParameter<Long> {
    
    private TripService tripService;
    private UserService userService;
    private BookingService bookingService;
    
    private Long tripId;
    private TripDTO trip;
    private UserDTO driver;
    
    public TripDetailsView(TripService tripService, UserService userService, BookingService bookingService) {
        this.tripService = tripService;
        this.userService = userService;
        this.bookingService = bookingService;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)");
    }
    
    @Override
    public void setParameter(BeforeEvent event, Long parameter) {
        this.tripId = parameter;
        loadTripDetails();
    }
    
    private void loadTripDetails() {
        try {
            trip = tripService.getTripById(tripId);
            driver = userService.getUserById(trip.getDriverId());
            buildUI();
        } catch (Exception ex) {
            Notification.show("Erreur: " + ex.getMessage(), 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(SearchTripsView.class));
        }
    }
    
    private void buildUI() {
        removeAll();
        
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);
        mainContainer.setAlignItems(FlexComponent.Alignment.CENTER);
        mainContainer.getStyle().set("max-width", "900px").set("margin", "0 auto");
        
        // Title
        H1 title = new H1(trip.getDepartureCity() + " → " + trip.getArrivalCity());
        title.getStyle()
            .set("margin", "1rem 0")
            .set("font-size", "2.5rem")
            .set("font-weight", "800")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        // Trip info card
        Div tripCard = createTripInfoCard();
        
        // Driver info card
        Div driverCard = createDriverInfoCard();
        
        // Action buttons
        HorizontalLayout actions = createActionButtons();
        
        mainContainer.add(title, tripCard, driverCard, actions);
        add(mainContainer);
    }
    
    private Div createTripInfoCard() {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2rem");
        
        H2 cardTitle = new H2("Informations du trajet");
        cardTitle.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        VerticalLayout infoList = new VerticalLayout();
        infoList.setPadding(false);
        infoList.setSpacing(true);
        infoList.getStyle().set("gap", "1rem");
        
        infoList.add(
            createInfoItem(VaadinIcon.MAP_MARKER, "Départ", 
                trip.getDepartureAddress() + ", " + trip.getDepartureCity(), "#2563eb"),
            createInfoItem(VaadinIcon.MAP_MARKER, "Arrivée", 
                trip.getArrivalAddress() + ", " + trip.getArrivalCity(), "#f59e0b"),
            createInfoItem(VaadinIcon.CALENDAR, "Date", 
                trip.getDepartureDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy à HH:mm")), "#8b5cf6"),
            createInfoItem(VaadinIcon.USERS, "Places", 
                trip.getAvailableSeats() + "/" + trip.getTotalSeats() + " disponibles", "#10b981"),
            createInfoItem(VaadinIcon.EURO, "Prix", 
                trip.getPricePerSeat() + "€ par place", "#ef4444")
        );
        
        if (trip.getDescription() != null && !trip.getDescription().isEmpty()) {
            Div descBox = new Div();
            descBox.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "1rem")
                .set("border-radius", "8px")
                .set("border-left", "4px solid #2563eb")
                .set("margin-top", "1rem");
            
            Paragraph descTitle = new Paragraph("📝 Description");
            descTitle.getStyle()
                .set("margin", "0 0 0.5rem 0")
                .set("font-weight", "700")
                .set("color", "#1e293b");
            
            Paragraph descText = new Paragraph(trip.getDescription());
            descText.getStyle()
                .set("margin", "0")
                .set("color", "#64748b")
                .set("line-height", "1.6");
            
            descBox.add(descTitle, descText);
            infoList.add(descBox);
        }
        
        card.add(cardTitle, infoList);
        return card;
    }
    
    private Div createInfoItem(VaadinIcon iconType, String label, String value, String color) {
        Div item = new Div();
        item.getStyle()
            .set("display", "flex")
            .set("align-items", "center")
            .set("gap", "1rem")
            .set("padding", "0.75rem")
            .set("border-radius", "8px")
            .set("background", "#f8fafc");
        
        Div iconBox = new Div();
        iconBox.getStyle()
            .set("width", "40px")
            .set("height", "40px")
            .set("border-radius", "8px")
            .set("background", "linear-gradient(135deg, " + color + " 0%, " + adjustColor(color) + " 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("flex-shrink", "0");
        
        Icon icon = iconType.create();
        icon.setSize("20px");
        icon.getStyle().set("color", "white");
        iconBox.add(icon);
        
        VerticalLayout textBox = new VerticalLayout();
        textBox.setPadding(false);
        textBox.setSpacing(false);
        textBox.getStyle().set("flex", "1");
        
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
            .set("font-weight", "600")
            .set("color", "#1e293b");
        
        textBox.add(labelText, valueText);
        item.add(iconBox, textBox);
        return item;
    }
    
    private Div createDriverInfoCard() {
        Div card = new Div();
        card.setWidthFull();
        card.getStyle()
            .set("background", "white")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "2rem");
        
        H2 cardTitle = new H2("Conducteur");
        cardTitle.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        HorizontalLayout driverHeader = new HorizontalLayout();
        driverHeader.setAlignItems(FlexComponent.Alignment.CENTER);
        driverHeader.setSpacing(true);
        
        Div avatar = new Div();
        avatar.getStyle()
            .set("width", "60px")
            .set("height", "60px")
            .set("border-radius", "50%")
            .set("background", "linear-gradient(135deg, #2563eb 0%, #1d4ed8 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("font-size", "1.5rem")
            .set("font-weight", "700")
            .set("color", "white");
        avatar.setText(driver.getFirstName().substring(0, 1).toUpperCase());
        
        Button nameButton = new Button(driver.getFirstName() + " " + driver.getLastName(), 
            VaadinIcon.USER.create());
        nameButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        nameButton.getStyle()
            .set("font-size", "1.25rem")
            .set("font-weight", "700")
            .set("color", "#2563eb");
        nameButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate(ProfileView.class, driver.getId()))
        );
        
        driverHeader.add(avatar, nameButton);
        
        VerticalLayout driverInfo = new VerticalLayout();
        driverInfo.setPadding(false);
        driverInfo.setSpacing(true);
        driverInfo.getStyle().set("gap", "0.75rem").set("margin-top", "1rem");
        
        if (driver.getPhone() != null && !driver.getPhone().isEmpty()) {
            Paragraph phone = new Paragraph("📞 " + driver.getPhone());
            phone.getStyle().set("margin", "0").set("color", "#64748b");
            driverInfo.add(phone);
        }
        
        if (driver.getTotalTripsDriver() != null && driver.getTotalTripsDriver() > 0) {
            Paragraph trips = new Paragraph("🚗 " + driver.getTotalTripsDriver() + " trajets publiés");
            trips.getStyle().set("margin", "0").set("color", "#64748b");
            driverInfo.add(trips);
        }
        
        if (driver.getAverageRatingDriver() != null && driver.getAverageRatingDriver().doubleValue() > 0) {
            Paragraph rating = new Paragraph("⭐ " + driver.getAverageRatingDriver() + "/5");
            rating.getStyle().set("margin", "0").set("color", "#64748b");
            driverInfo.add(rating);
        }
        
        if (driver.getBio() != null && !driver.getBio().isEmpty()) {
            Div bioBox = new Div();
            bioBox.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "1rem")
                .set("border-radius", "8px")
                .set("margin-top", "0.5rem");
            
            Paragraph bio = new Paragraph("💬 " + driver.getBio());
            bio.getStyle()
                .set("margin", "0")
                .set("color", "#64748b")
                .set("line-height", "1.6")
                .set("font-style", "italic");
            
            bioBox.add(bio);
            driverInfo.add(bioBox);
        }
        
        card.add(cardTitle, driverHeader, driverInfo);
        return card;
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
        if (currentUser != null && !currentUser.getId().equals(trip.getDriverId())) {
            Button bookButton = new Button("Réserver ce trajet", VaadinIcon.CHECK.create());
            bookButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
            bookButton.getStyle().set("font-weight", "600");
            bookButton.addClickListener(e -> handleBooking());
            buttons.add(backButton, bookButton);
        } else {
            buttons.add(backButton);
        }
        
        return buttons;
    }
    
    private void handleBooking() {
        UserDTO currentUser = (UserDTO) VaadinSession.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            showError("Vous devez être connecté pour réserver");
            getUI().ifPresent(ui -> {
                // Store the current trip ID in session for redirect after login
                ui.getSession().setAttribute("redirectAfterLogin", "/trip/" + tripId);
                ui.navigate(LoginView.class);
            });
            return;
        }
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Réserver ce trajet");
        dialog.setWidth("500px");
        
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        
        Div tripSummary = new Div();
        tripSummary.getStyle()
            .set("background", "#f8fafc")
            .set("padding", "1rem")
            .set("border-radius", "8px");
        
        tripSummary.add(
            new Paragraph("🚗 " + trip.getDepartureCity() + " → " + trip.getArrivalCity()),
            new Paragraph("📅 " + trip.getDepartureDatetime().format(
                DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))),
            new Paragraph("💰 " + trip.getPricePerSeat() + "€ / place"),
            new Paragraph("👥 " + trip.getAvailableSeats() + " places disponibles")
        );
        
        NumberField seatsField = new NumberField("Nombre de places");
        seatsField.setValue(1.0);
        seatsField.setMin(1);
        seatsField.setMax(trip.getAvailableSeats());
        seatsField.setStep(1);
        seatsField.setWidthFull();
        
        TextArea messageArea = new TextArea("Message au conducteur (optionnel)");
        messageArea.setWidthFull();
        messageArea.setHeight("100px");
        messageArea.setPlaceholder("Ex: J'arriverai 5 minutes en avance...");
        
        dialogLayout.add(tripSummary, seatsField, messageArea);
        
        Button confirmButton = new Button("Confirmer", VaadinIcon.CHECK.create());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(e -> {
            try {
                Integer seats = seatsField.getValue().intValue();
                String message = messageArea.getValue();
                
                bookingService.createBooking(currentUser.getId(), trip.getId(), seats, message);
                
                showSuccess("Réservation envoyée avec succès!");
                dialog.close();
                loadTripDetails();
                
            } catch (Exception ex) {
                showError("Erreur: " + ex.getMessage());
            }
        });
        
        Button cancelButton = new Button("Annuler", e -> dialog.close());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        
        HorizontalLayout buttonsLayout = new HorizontalLayout(confirmButton, cancelButton);
        dialogLayout.add(buttonsLayout);
        
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private String adjustColor(String color) {
        switch (color) {
            case "#2563eb": return "#1d4ed8";
            case "#f59e0b": return "#d97706";
            case "#8b5cf6": return "#7c3aed";
            case "#10b981": return "#059669";
            case "#ef4444": return "#dc2626";
            default: return color;
        }
    }
    
    private void showError(String message) {
        Notification notification = Notification.show("⚠ " + message, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
    private void showSuccess(String message) {
        Notification notification = Notification.show("✓ " + message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
