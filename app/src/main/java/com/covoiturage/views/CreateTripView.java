package com.covoiturage.views;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import org.springframework.beans.factory.annotation.Autowired;

import com.covoiturage.dto.TripDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datetimepicker.DateTimePicker;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("create-trip")
@PageTitle("Créer un trajet - Covoiturage")
public class CreateTripView extends VerticalLayout {
    
    @Autowired
    private TripService tripService;
    
    private TextField departureAddressField;
    private TextField departureCityField;
    private TextField arrivalAddressField;
    private TextField arrivalCityField;
    private DateTimePicker departureDatetimePicker;
    private NumberField totalSeatsField;
    private NumberField pricePerSeatField;
    private TextArea descriptionArea;
    private Button publishButton;
    
    public CreateTripView(TripService tripService) {
        this.tripService = tripService;
        
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
        
        // Header with icon
        VerticalLayout header = new VerticalLayout();
        header.setAlignItems(FlexComponent.Alignment.CENTER);
        header.setPadding(false);
        header.setSpacing(true);
        
        Div iconContainer = new Div();
        iconContainer.getStyle()
            .set("width", "70px")
            .set("height", "70px")
            .set("border-radius", "14px")
            .set("background", "linear-gradient(135deg, #10b981 0%, #059669 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 8px 16px -4px rgba(16, 185, 129, 0.4)")
            .set("margin-bottom", "1rem");
        
        Icon carIcon = VaadinIcon.CAR.create();
        carIcon.setSize("35px");
        carIcon.getStyle().set("color", "white");
        iconContainer.add(carIcon);
        
        H1 title = new H1("Créer un trajet");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        header.add(iconContainer, title);
        formContainer.add(header);
        
        // Departure section
        Div departureSection = createSection("Départ", "#2563eb");
        
        departureAddressField = new TextField("Adresse");
        departureAddressField.setRequired(true);
        departureAddressField.setWidthFull();
        departureAddressField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        departureAddressField.setPlaceholder("15 rue de la Paix");
        
        departureCityField = new TextField("Ville");
        departureCityField.setRequired(true);
        departureCityField.setWidthFull();
        departureCityField.setPrefixComponent(VaadinIcon.HOME.create());
        departureCityField.setPlaceholder("Paris");
        
        departureSection.add(departureAddressField, departureCityField);
        
        // Arrival section
        Div arrivalSection = createSection("Arrivée", "#f59e0b");
        
        arrivalAddressField = new TextField("Adresse");
        arrivalAddressField.setRequired(true);
        arrivalAddressField.setWidthFull();
        arrivalAddressField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        arrivalAddressField.setPlaceholder("10 place Bellecour");
        
        arrivalCityField = new TextField("Ville");
        arrivalCityField.setRequired(true);
        arrivalCityField.setWidthFull();
        arrivalCityField.setPrefixComponent(VaadinIcon.HOME.create());
        arrivalCityField.setPlaceholder("Lyon");
        
        arrivalSection.add(arrivalAddressField, arrivalCityField);
        
        // Trip details section
        Div detailsSection = createSection("Détails du trajet", "#8b5cf6");
        
        departureDatetimePicker = new DateTimePicker("Date et heure de départ");
        departureDatetimePicker.setWidthFull();
        departureDatetimePicker.setMin(LocalDateTime.now());
        departureDatetimePicker.setHelperText("Le trajet doit être dans le futur");
        
        HorizontalLayout seatsPrice = new HorizontalLayout();
        seatsPrice.setWidthFull();
        seatsPrice.setSpacing(true);
        
        totalSeatsField = new NumberField("Places");
        totalSeatsField.setRequired(true);
        totalSeatsField.setMin(1);
        totalSeatsField.setMax(8);
        totalSeatsField.setValue(3.0);
        totalSeatsField.setStep(1);
        totalSeatsField.setPrefixComponent(VaadinIcon.USERS.create());
        totalSeatsField.setHelperText("1-8 places");
        totalSeatsField.getStyle().set("flex", "1");
        
        pricePerSeatField = new NumberField("Prix/place (€)");
        pricePerSeatField.setRequired(true);
        pricePerSeatField.setMin(0);
        pricePerSeatField.setValue(10.0);
        pricePerSeatField.setStep(0.5);
        pricePerSeatField.setPrefixComponent(VaadinIcon.EURO.create());
        pricePerSeatField.setHelperText("En euros");
        pricePerSeatField.getStyle().set("flex", "1");
        
        seatsPrice.add(totalSeatsField, pricePerSeatField);
        
        descriptionArea = new TextArea("Description (optionnel)");
        descriptionArea.setWidthFull();
        descriptionArea.setHeight("100px");
        descriptionArea.setPlaceholder("Départ à l'heure, musique autorisée, pas de fumeur...");
        descriptionArea.setMaxLength(500);
        
        detailsSection.add(departureDatetimePicker, seatsPrice, descriptionArea);
        
        formContainer.add(departureSection, arrivalSection, detailsSection);
        
        // Buttons
        HorizontalLayout buttons = new HorizontalLayout();
        buttons.setWidthFull();
        buttons.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        buttons.setSpacing(true);
        buttons.getStyle().set("margin-top", "1.5rem");
        
        publishButton = new Button("Publier le trajet", VaadinIcon.CHECK.create());
        publishButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        publishButton.getStyle().set("font-weight", "600");
        publishButton.addClickListener(e -> handlePublish());
        
        Button cancelButton = new Button("Annuler", VaadinIcon.CLOSE.create());
        cancelButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        cancelButton.addClickListener(e -> getUI().ifPresent(ui -> ui.navigate(HomeView.class)));
        
        buttons.add(publishButton, cancelButton);
        formContainer.add(buttons);
        
        add(formContainer);
    }
    
    private Div createSection(String title, String color) {
        Div section = new Div();
        section.getStyle()
            .set("padding", "1.25rem")
            .set("background", "#f8fafc")
            .set("border-radius", "12px")
            .set("border-left", "4px solid " + color)
            .set("margin", "0.75rem 0");
        
        H1 sectionTitle = new H1(title);
        sectionTitle.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("margin", "0 0 1rem 0");
        
        section.add(sectionTitle);
        return section;
    }
    
    private void handlePublish() {
        try {
            // Validation
            if (!validateForm()) {
                return;
            }
            
            String departureAddress = departureAddressField.getValue();
            String departureCity = departureCityField.getValue();
            String arrivalAddress = arrivalAddressField.getValue();
            String arrivalCity = arrivalCityField.getValue();
            LocalDateTime departureDatetime = departureDatetimePicker.getValue();
            Integer totalSeats = totalSeatsField.getValue().intValue();
            BigDecimal pricePerSeat = BigDecimal.valueOf(pricePerSeatField.getValue());
            String description = descriptionArea.getValue();
            
            UserDTO currentUser = (UserDTO) VaadinSession.getCurrent().getAttribute("currentUser");
            if (currentUser == null) {
                Notification notification = Notification.show(
                    "Vous devez être connecté pour publier un trajet",
                    3000,
                    Notification.Position.MIDDLE
                );
                notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
                getUI().ifPresent(ui -> ui.navigate(LoginView.class));
                return;
            }
            
            TripDTO trip = tripService.createTrip(
                currentUser.getId(),
                departureAddress,
                departureCity,
                arrivalAddress,
                arrivalCity,
                departureDatetime,
                totalSeats,
                pricePerSeat,
                description
            );
            
            Notification notification = Notification.show(
                "✓ Trajet publié avec succès! 🎉",
                4000,
                Notification.Position.TOP_CENTER
            );
            notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
            
            clearForm();
            getUI().ifPresent(ui -> ui.navigate(MyTripsView.class));
            
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
        if (departureAddressField.getValue().trim().isEmpty()) {
            showError("L'adresse de départ est obligatoire");
            departureAddressField.focus();
            return false;
        }
        if (departureCityField.getValue().trim().isEmpty()) {
            showError("La ville de départ est obligatoire");
            departureCityField.focus();
            return false;
        }
        if (arrivalAddressField.getValue().trim().isEmpty()) {
            showError("L'adresse d'arrivée est obligatoire");
            arrivalAddressField.focus();
            return false;
        }
        if (arrivalCityField.getValue().trim().isEmpty()) {
            showError("La ville d'arrivée est obligatoire");
            arrivalCityField.focus();
            return false;
        }
        if (departureDatetimePicker.getValue() == null) {
            showError("La date de départ est obligatoire");
            departureDatetimePicker.focus();
            return false;
        }
        if (departureDatetimePicker.getValue().isBefore(LocalDateTime.now())) {
            showError("La date doit être dans le futur");
            departureDatetimePicker.focus();
            return false;
        }
        return true;
    }
    
    private void showError(String message) {
        Notification notification = Notification.show("⚠ " + message, 3000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
    private void clearForm() {
        departureAddressField.clear();
        departureCityField.clear();
        arrivalAddressField.clear();
        arrivalCityField.clear();
        departureDatetimePicker.clear();
        totalSeatsField.setValue(3.0);
        pricePerSeatField.setValue(10.0);
        descriptionArea.clear();
    }
}