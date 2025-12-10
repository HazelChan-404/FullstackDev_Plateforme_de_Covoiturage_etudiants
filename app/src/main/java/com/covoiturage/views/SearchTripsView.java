package com.covoiturage.views;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.covoiturage.dto.TripDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.security.SecurityService;
import com.covoiturage.service.BookingService;
import com.covoiturage.service.TripService;
import com.covoiturage.service.UserService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.datepicker.DatePicker;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.textfield.NumberField;
import com.vaadin.flow.component.textfield.TextArea;
import com.vaadin.flow.component.textfield.TextField;
import com.vaadin.flow.data.provider.Query;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("search-trips")
@PageTitle("Rechercher un trajet - Covoiturage")
public class SearchTripsView extends VerticalLayout {
    
    private TripService tripService;
    private BookingService bookingService;
    private SecurityService securityService;
    private UserService userService;
    
    private TextField departureCityField;
    private TextField arrivalCityField;
    private DatePicker datePicker;
    private Button searchButton;
    private Grid<TripDTO> resultsGrid;
    
    public SearchTripsView(TripService tripService, BookingService bookingService, SecurityService securityService, UserService userService) {
        this.tripService = tripService;
        this.bookingService = bookingService;
        this.securityService = securityService;
        this.userService = userService;
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)");
        
        // Main container
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);
        mainContainer.getStyle()
            .set("max-width", "1400px")
            .set("margin", "0 auto")
            .set("width", "100%")
            .set("min-height", "100vh");
        
        // Search form
        Div searchForm = createSearchForm();
        
        // Results grid
        resultsGrid = createResultsGrid();
        
        mainContainer.add(searchForm, resultsGrid);
        add(mainContainer);
        
        // Load all trips initially
        loadAllActiveTrips();
        
        // Check for pending booking after login
        checkPendingBooking();
    }
    
    private Div createSearchForm() {
        Div form = new Div();
        form.getStyle()
            .set("background", "white")
            .set("padding", "2rem")
            .set("border-radius", "16px")
            .set("box-shadow", "0 10px 15px -3px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("margin-bottom", "2rem");
        
        H1 title = new H1("Rechercher un trajet");
        title.getStyle()
            .set("margin", "0 0 1.5rem 0")
            .set("font-size", "1.75rem")
            .set("font-weight", "700")
            .set("color", "#1e293b")
            .set("text-align", "center");
        
        HorizontalLayout fields = new HorizontalLayout();
        fields.setWidthFull();
        fields.setSpacing(true);
        fields.setAlignItems(FlexComponent.Alignment.END);
        fields.getStyle().set("flex-wrap", "wrap");
        
        departureCityField = new TextField("Départ");
        departureCityField.setPlaceholder("Paris");
        departureCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        departureCityField.setWidthFull();
        departureCityField.getStyle().set("flex", "1");
        
        arrivalCityField = new TextField("Arrivée");
        arrivalCityField.setPlaceholder("Lyon");
        arrivalCityField.setPrefixComponent(VaadinIcon.MAP_MARKER.create());
        arrivalCityField.setWidthFull();
        arrivalCityField.getStyle().set("flex", "1");
        
        datePicker = new DatePicker("Date");
        datePicker.setPlaceholder("Optionnel");
        datePicker.setMin(LocalDate.now());
        datePicker.setClearButtonVisible(true);
        datePicker.getStyle().set("flex", "1");
        
        searchButton = new Button("Rechercher", VaadinIcon.SEARCH.create());
        searchButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_LARGE);
        searchButton.getStyle()
            .set("font-weight", "600")
            .set("padding", "0.75rem 2rem");
        searchButton.addClickListener(e -> handleSearch());
        
        fields.add(departureCityField, arrivalCityField, datePicker, searchButton);
        
        VerticalLayout formLayout = new VerticalLayout(title, fields);
        formLayout.setPadding(false);
        formLayout.setSpacing(true);
        
        form.add(formLayout);
        return form;
    }
    
    private Grid<TripDTO> createResultsGrid() {
        Grid<TripDTO> grid = new Grid<>(TripDTO.class, false);
        grid.setHeightFull();
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(trip -> trip.getDepartureCity() + " → " + trip.getArrivalCity())
            .setHeader("Trajet")
            .setWidth("250px")
            .setFlexGrow(0);
        
        grid.addColumn(trip -> 
            trip.getDepartureDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ).setHeader("Date").setWidth("180px").setFlexGrow(0);
        
        grid.addColumn(TripDTO::getAvailableSeats)
            .setHeader("Places")
            .setWidth("150px")
            .setFlexGrow(0);
        
        grid.addColumn(trip -> trip.getPricePerSeat() + "€")
            .setHeader("Prix")
            .setWidth("150px")
            .setFlexGrow(0);
        
        grid.addColumn(TripDTO::getDriverName)
            .setHeader("Conducteur")
            .setWidth("200px")
            .setFlexGrow(0);
        
        grid.addComponentColumn(trip -> {
            Button viewButton = new Button("Voir", VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            viewButton.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate(TripDetailsView.class, trip.getId()))
            );
            
            Button bookButton = new Button("Réserver", VaadinIcon.CHECK.create());
            boolean hasAvailableSeats = trip.getAvailableSeats() != null && trip.getAvailableSeats() > 0;
            
            if (hasAvailableSeats) {
                bookButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
                bookButton.addClickListener(e -> handleBooking(trip));
            } else {
                bookButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
                bookButton.setEnabled(false);
                bookButton.setText("Complet");
                bookButton.getStyle().set("color", "#94a3b8");
            }
            
            HorizontalLayout actions = new HorizontalLayout(viewButton, bookButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Actions").setWidth("400px").setFlexGrow(0);
        
        return grid;
    }
    
    private void handleSearch() {
        try {
            String departureCity = departureCityField.getValue();
            String arrivalCity = arrivalCityField.getValue();
            
            if (departureCity == null || departureCity.trim().isEmpty()) {
                showError("Veuillez entrer une ville de départ");
                departureCityField.focus();
                return;
            }
            if (arrivalCity == null || arrivalCity.trim().isEmpty()) {
                showError("Veuillez entrer une ville d'arrivée");
                arrivalCityField.focus();
                return;
            }
            
            List<TripDTO> trips = tripService.searchTrips(departureCity, arrivalCity);
            
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                trips = trips.stream()
                    .filter(trip -> trip.getDepartureDatetime().toLocalDate().equals(selectedDate))
                    .collect(java.util.stream.Collectors.toList());
            }
            
            resultsGrid.setItems(trips);
            
            if (trips.isEmpty()) {
                showInfo("Aucun trajet trouvé");
            } else {
                showSuccess(trips.size() + " trajet(s) trouvé(s)");
            }
            
        } catch (Exception ex) {
            showError("Erreur: " + ex.getMessage());
        }
    }
    
    private void handleBooking(TripDTO trip) {
        if (!securityService.isAuthenticated()) {
            showError("Vous devez être connecté pour réserver");
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        
        UserDTO currentUser = securityService.getAuthenticatedUser();
        if (currentUser == null || trip.getDriverId().equals(currentUser.getId())) {
            showError("Vous ne pouvez pas réserver votre propre trajet");
            return;
        }
        
        Dialog dialog = new Dialog();
        dialog.setHeaderTitle("Réserver ce trajet");
        dialog.setWidth("500px");
        
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        
        // Trip info
        Div tripInfo = new Div();
        tripInfo.getStyle()
            .set("background", "#f8fafc")
            .set("padding", "1rem")
            .set("border-radius", "8px")
            .set("margin-bottom", "1rem");
        
        tripInfo.add(
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
        
        dialogLayout.add(tripInfo, seatsField, messageArea);
        
        Button confirmButton = new Button("Confirmer", VaadinIcon.CHECK.create());
        confirmButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        confirmButton.addClickListener(e -> {
            try {
                Integer seats = seatsField.getValue().intValue();
                String message = messageArea.getValue();
                
                bookingService.createBooking(currentUser.getId(), trip.getId(), seats, message);
                
                showSuccess("Réservation envoyée avec succès!");
                dialog.close();
                // Refresh the results grid to update available seats
                loadAllActiveTrips();
                
                // Navigate to MyBookingsView after successful booking with delay
                getUI().ifPresent(ui -> {
                    // Use a timer to ensure UI updates before navigation
                    new java.util.Timer().schedule(new java.util.TimerTask() {
                        @Override
                        public void run() {
                            ui.access(() -> {
                                ui.navigate(MyBookingsView.class);
                            });
                        }
                    }, 400); // 0.7 second delay
                });
                
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
    
    private void loadAllActiveTrips() {
        try {
            List<TripDTO> allTrips = tripService.getAllActiveTrips();
            resultsGrid.setItems(allTrips);
        } catch (Exception ex) {
            showError("Erreur lors du chargement: " + ex.getMessage());
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
    
    private void showInfo(String message) {
        Notification.show("ℹ " + message, 3000, Notification.Position.MIDDLE);
    }
    
    private void checkPendingBooking() {
        // Check if there's a pending booking from before login
        Long pendingTripId = (Long) VaadinSession.getCurrent().getAttribute("pendingBookingTrip");
        if (pendingTripId != null) {
            // Clear the pending booking from session
            VaadinSession.getCurrent().setAttribute("pendingBookingTrip", null);
            
            // Get current user
            UserDTO currentUser = VaadinSession.getCurrent().getAttribute(UserDTO.class);
            if (currentUser != null) {
                try {
                    // Load the trip and proceed with booking directly
                    TripDTO trip = tripService.getTripById(pendingTripId);
                    if (trip != null) {
                        // Use Java Timer to delay the dialog opening
                        new java.util.Timer().schedule(new java.util.TimerTask() {
                            @Override
                            public void run() {
                                getUI().ifPresent(ui -> ui.access(() -> {
                                    handleBooking(trip);
                                }));
                            }
                        }, 1500); // 1.5 second delay
                    }
                } catch (Exception e) {
                    showError("Erreur lors du chargement du trajet: " + e.getMessage());
                }
            }
        }
    }
}
