package com.covoiturage.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.covoiturage.dto.BookingDTO;
import com.covoiturage.dto.TripDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.security.SecurityService;
import com.covoiturage.service.BookingService;
import com.covoiturage.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.dialog.Dialog;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.html.H2;
import com.vaadin.flow.component.html.Paragraph;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.notification.NotificationVariant;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route("my-trips")
@PageTitle("Mes trajets - Covoiturage")
public class MyTripsView extends VerticalLayout {
    
    private TripService tripService;
    private BookingService bookingService;
    private SecurityService securityService;
    private UserDTO currentUser;
    
    private Grid<TripDTO> publishedTripsGrid;
    private Grid<BookingDTO> bookedTripsGrid;
    private VerticalLayout publishedContent;
    private VerticalLayout bookedContent;
    
    public MyTripsView(TripService tripService, BookingService bookingService, SecurityService securityService) {
        this.tripService = tripService;
        this.bookingService = bookingService;
        this.securityService = securityService;
        
        currentUser = securityService.getAuthenticatedUser();
        if (currentUser == null) {
            Notification.show("Vous devez être connecté", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate("login"));
            return;
        }
        
        setSizeFull();
        setPadding(false);
        setSpacing(false);
        getStyle().set("background", "linear-gradient(135deg, #f8fafc 0%, #f1f5f9 100%)");
        
        VerticalLayout mainContainer = new VerticalLayout();
        mainContainer.setSizeFull();
        mainContainer.setPadding(true);
        mainContainer.setSpacing(true);
        mainContainer.getStyle().set("max-width", "1400px").set("margin", "0 auto");
        
        // Header
        Div header = createHeader();
        mainContainer.add(header);
        
        // Tabs for switching between sections
        Tabs tabs = new Tabs();
        Tab publishedTab = new Tab(" Mes trajets publiés");
        Tab bookedTab = new Tab(" Mes réservations");
        tabs.add(publishedTab, bookedTab);
        tabs.setWidthFull();
        tabs.getStyle()
            .set("margin-bottom", "1.5rem")
            .set("background", "white")
            .set("border-radius", "12px")
            .set("padding", "0.5rem")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)");
        
        // Content containers
        this.publishedContent = new VerticalLayout();
        this.bookedContent = new VerticalLayout();
        this.publishedContent.setSizeFull();
        this.bookedContent.setSizeFull();
        this.publishedContent.setPadding(false);
        this.bookedContent.setPadding(false);
        
        // Create grids
        publishedTripsGrid = createPublishedTripsGrid();
        bookedTripsGrid = createBookedTripsGrid();
        
        publishedContent.add(publishedTripsGrid);
        bookedContent.add(bookedTripsGrid);
        
        // Tab switching logic
        publishedContent.setVisible(true);
        bookedContent.setVisible(false);
        
        tabs.addSelectedChangeListener(event -> {
            publishedContent.setVisible(event.getSelectedTab() == publishedTab);
            bookedContent.setVisible(event.getSelectedTab() == bookedTab);
        });
        
        mainContainer.add(tabs, publishedContent, bookedContent);
        add(mainContainer);
        
        // Load data
        loadMyTrips();
        loadMyBookings();
    }
    
    private Div createHeader() {
        Div header = new Div();
        header.getStyle()
            .set("background", "white")
            .set("padding", "2rem")
            .set("border-radius", "16px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("margin-bottom", "1.5rem");
        
        // Header content with back button
        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        // Title section
        VerticalLayout titleSection = new VerticalLayout();
        titleSection.setPadding(false);
        titleSection.setSpacing(false);
        
        H1 title = new H1("Mes trajets");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        Paragraph subtitle = new Paragraph("Gérez vos trajets publiés et vos réservations");
        subtitle.getStyle()
            .set("margin", "0.5rem 0 0 0")
            .set("color", "#64748b")
            .set("font-size", "1rem");
        
        titleSection.add(title, subtitle);
        
        // Return button
        Button returnButton = new Button("🏠 Retour à l'accueil", VaadinIcon.HOME.create());
        returnButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY);
        returnButton.getStyle()
            .set("font-weight", "600")
            .set("padding", "0.75rem 1.5rem");
        returnButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate(HomeView.class))
        );
        
        headerContent.add(titleSection, returnButton);
        header.add(headerContent);
        return header;
    }
    
    private Grid<TripDTO> createPublishedTripsGrid() {
        Grid<TripDTO> grid = new Grid<>(TripDTO.class, false);
        grid.setHeight("400px");
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(trip -> trip.getDepartureCity() + " → " + trip.getArrivalCity())
            .setHeader("Trajet")
            .setWidth("250px")
            .setFlexGrow(0)
            .setAutoWidth(true);
        
        grid.addColumn(trip -> 
            trip.getDepartureDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ).setHeader("Date").setWidth("150px").setFlexGrow(0);
        
        grid.addColumn(trip -> 
            trip.getAvailableSeats() + "/" + trip.getTotalSeats()
        ).setHeader("Places").setWidth("150px").setFlexGrow(0);
        
        grid.addColumn(trip -> trip.getPricePerSeat() + "€")
            .setHeader("Prix")
            .setWidth("150px")
            .setFlexGrow(0);
        
        grid.addComponentColumn(trip -> {
            Div statusBadge = new Div();
            String status = trip.getStatus();
            String text, color, bgColor;
            
            switch (status) {
                case "active":
                    text = "✓ Actif";
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                case "completed":
                    text = "✓ Terminé";
                    color = "#6b7280";
                    bgColor = "rgba(107, 114, 128, 0.1)";
                    break;
                case "cancelled":
                    text = "✗ Annulé";
                    color = "#ef4444";
                    bgColor = "rgba(239, 68, 68, 0.1)";
                    break;
                default:
                    text = status;
                    color = "#64748b";
                    bgColor = "rgba(100, 116, 139, 0.1)";
            }
            
            statusBadge.setText(text);
            statusBadge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("font-weight", "600")
                .set("color", color)
                .set("background", bgColor)
                .set("display", "inline-block");
            
            return statusBadge;
        }).setHeader("Statut").setWidth("150px").setFlexGrow(0);
        
        grid.addComponentColumn(trip -> {
            Button viewBookingsButton = new Button("Réservations", VaadinIcon.USERS.create());
            viewBookingsButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewBookingsButton.addClickListener(e -> showBookings(trip));
            
            Button viewDetailsButton = new Button("Détails", VaadinIcon.EYE.create());
            viewDetailsButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            viewDetailsButton.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate(TripDetailsView.class, trip.getId()))
            );
            
            HorizontalLayout actions = new HorizontalLayout(viewBookingsButton, viewDetailsButton);
            actions.setSpacing(true);
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
        
        return grid;
    }
    
    private Grid<BookingDTO> createBookedTripsGrid() {
        Grid<BookingDTO> grid = new Grid<>(BookingDTO.class, false);
        grid.setHeight("400px");
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(booking -> 
            booking.getDepartureCity() + " → " + booking.getArrivalCity()
        ).setHeader("Trajet").setWidth("250px").setFlexGrow(0);
        
        grid.addColumn(booking -> 
            booking.getDepartureDatetime() != null ? 
            booking.getDepartureDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm")) : 
            "N/A"
        ).setHeader("Date").setWidth("150px").setFlexGrow(0);
        
        grid.addColumn(BookingDTO::getSeatsBooked)
            .setHeader("Places")
            .setWidth("80px")
            .setFlexGrow(0);
        
        grid.addComponentColumn(booking -> {
            Div statusBadge = new Div();
            String status = booking.getStatus();
            String text, color, bgColor;
            
            switch (status) {
                case "pending":
                    text = "⏳ En attente";
                    color = "#f59e0b";
                    bgColor = "rgba(245, 158, 11, 0.1)";
                    break;
                case "accepted":
                    text = "✓ Accepté";
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                case "rejected":
                    text = "✗ Refusé";
                    color = "#ef4444";
                    bgColor = "rgba(239, 68, 68, 0.1)";
                    break;
                case "cancelled":
                    text = "🚫 Annulé";
                    color = "#6b7280";
                    bgColor = "rgba(107, 114, 128, 0.1)";
                    break;
                default:
                    text = status;
                    color = "#64748b";
                    bgColor = "rgba(100, 116, 139, 0.1)";
            }
            
            statusBadge.setText(text);
            statusBadge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("font-size", "0.875rem")
                .set("font-weight", "600")
                .set("color", color)
                .set("background", bgColor)
                .set("display", "inline-block");
            
            return statusBadge;
        }).setHeader("Statut").setWidth("140px").setFlexGrow(0);
        
        grid.addComponentColumn(booking -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            
            Button viewButton = new Button("Voir", VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewButton.addClickListener(e -> {
                if (booking.getTripId() != null) {
                    getUI().ifPresent(ui -> ui.navigate(TripDetailsView.class, booking.getTripId()));
                }
            });
            
            // Add cancel button for pending and accepted bookings
            if ("pending".equals(booking.getStatus()) || "accepted".equals(booking.getStatus())) {
                Button cancelButton = new Button("Annuler", VaadinIcon.CLOSE.create());
                cancelButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_ERROR);
                cancelButton.addClickListener(e -> handleCancelBooking(booking));
                actions.add(viewButton, cancelButton);
            } else {
                actions.add(viewButton);
            }
            
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
        
        return grid;
    }
    
    private void loadMyTrips() {
        try {
            System.out.println("Loading trips for user ID: " + currentUser.getId());
            List<TripDTO> trips = tripService.getTripsByDriverId(currentUser.getId());
            System.out.println("Found " + trips.size() + " trips");
            publishedTripsGrid.setItems(trips);
            
            if (trips.isEmpty()) {
                // Show empty state message
                Div emptyState = new Div();
                emptyState.getStyle()
                    .set("text-align", "center")
                    .set("padding", "3rem")
                    .set("color", "#64748b")
                    .set("font-size", "1.1rem")
                    .set("font-weight", "500");
                emptyState.add("🚗 Vous n'avez pas encore publié de trajet");
                publishedContent.add(emptyState);
            }
            
        } catch (Exception ex) {
            System.out.println("Error loading trips: " + ex.getMessage());
            ex.printStackTrace();
            showError("Erreur lors du chargement des trajets publiés: " + ex.getMessage());
        }
    }
    
    private void loadMyBookings() {
        try {
            System.out.println("Loading bookings for user ID: " + currentUser.getId());
            List<BookingDTO> bookings = bookingService.getBookingsByPassengerId(currentUser.getId());
            System.out.println("Found " + bookings.size() + " bookings");
            bookedTripsGrid.setItems(bookings);
            
            if (bookings.isEmpty()) {
                // Show empty state message
                Div emptyState = new Div();
                emptyState.getStyle()
                    .set("text-align", "center")
                    .set("padding", "3rem")
                    .set("color", "#64748b")
                    .set("font-size", "1.1rem")
                    .set("font-weight", "500");
                emptyState.add("🎫 Vous n'avez pas encore de réservation");
                bookedContent.add(emptyState);
            }
            
        } catch (Exception ex) {
            System.out.println("Error loading bookings: " + ex.getMessage());
            ex.printStackTrace();
            showError("Erreur lors du chargement des réservations: " + ex.getMessage());
        }
    }
    
    private void showBookings(TripDTO trip) {
        Dialog dialog = new Dialog();
        dialog.setWidth("900px");
        dialog.setHeight("80%");
        dialog.setHeaderTitle("Réservations - " + trip.getDepartureCity() + " → " + trip.getArrivalCity());
        
        VerticalLayout dialogLayout = new VerticalLayout();
        dialogLayout.setPadding(true);
        dialogLayout.setSpacing(true);
        
        try {
            List<BookingDTO> bookings = bookingService.getBookingsByTripId(trip.getId());
            
            if (bookings.isEmpty()) {
                Paragraph noBookings = new Paragraph("Aucune réservation pour ce trajet");
                noBookings.getStyle()
                    .set("text-align", "center")
                    .set("color", "#64748b")
                    .set("padding", "2rem");
                dialogLayout.add(noBookings);
            } else {
                for (BookingDTO booking : bookings) {
                    Div bookingCard = createBookingCard(booking, trip, dialog);
                    dialogLayout.add(bookingCard);
                }
            }
            
        } catch (Exception ex) {
            dialogLayout.add(new Paragraph("Erreur: " + ex.getMessage()));
        }
        
        Button closeButton = new Button("Fermer", e -> dialog.close());
        closeButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        dialogLayout.add(closeButton);
        
        dialog.add(dialogLayout);
        dialog.open();
    }
    
    private Div createBookingCard(BookingDTO booking, TripDTO trip, Dialog dialog) {
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("border", "1px solid #e2e8f0")
            .set("border-radius", "12px")
            .set("padding", "1.25rem")
            .set("margin-bottom", "1rem")
            .set("box-shadow", "0 2px 4px rgba(0,0,0,0.05)");
        
        VerticalLayout content = new VerticalLayout();
        content.setPadding(false);
        content.setSpacing(true);
        
        // Passenger info
        HorizontalLayout passengerInfo = new HorizontalLayout();
        passengerInfo.setAlignItems(FlexComponent.Alignment.CENTER);
        
        Button passengerButton = new Button(booking.getPassengerName(), VaadinIcon.USER.create());
        passengerButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        passengerButton.getStyle()
            .set("font-size", "1.1rem")
            .set("font-weight", "700")
            .set("color", "#2563eb");
        passengerButton.addClickListener(e -> 
            getUI().ifPresent(ui -> ui.navigate(ProfileView.class, booking.getPassengerId()))
        );
        
        Div statusBadge = new Div();
        String status = booking.getStatus();
        String text, color, bgColor;
        
        switch (status) {
            case "pending":
                text = "⏳ En attente";
                color = "#f59e0b";
                bgColor = "rgba(245, 158, 11, 0.1)";
                break;
            case "accepted":
                text = "✓ Accepté";
                color = "#10b981";
                bgColor = "rgba(16, 185, 129, 0.1)";
                break;
            case "rejected":
                text = "✗ Refusé";
                color = "#ef4444";
                bgColor = "rgba(239, 68, 68, 0.1)";
                break;
            case "cancelled":
                text = "🚫 Annulé";
                color = "#6b7280";
                bgColor = "rgba(107, 114, 128, 0.1)";
                break;
            default:
                text = status;
                color = "#64748b";
                bgColor = "rgba(100, 116, 139, 0.1)";
        }
        
        statusBadge.setText(text);
        statusBadge.getStyle()
            .set("padding", "4px 12px")
            .set("border-radius", "12px")
            .set("font-size", "0.875rem")
            .set("font-weight", "600")
            .set("color", color)
            .set("background", bgColor);
        
        passengerInfo.add(passengerButton, statusBadge);
        
        Paragraph seats = new Paragraph("👥 Places réservées: " + booking.getSeatsBooked());
        seats.getStyle().set("margin", "0").set("color", "#64748b");
        
        content.add(passengerInfo, seats);
        
        if (booking.getMessageToDriver() != null && !booking.getMessageToDriver().isEmpty()) {
            Div messageBox = new Div();
            messageBox.getStyle()
                .set("background", "#f8fafc")
                .set("padding", "0.75rem")
                .set("border-radius", "8px")
                .set("margin-top", "0.5rem")
                .set("border-left", "3px solid #2563eb");
            
            Paragraph message = new Paragraph("💬 " + booking.getMessageToDriver());
            message.getStyle()
                .set("margin", "0")
                .set("color", "#1e293b")
                .set("font-style", "italic");
            
            messageBox.add(message);
            content.add(messageBox);
        }
        
        if ("pending".equals(booking.getStatus())) {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            actions.getStyle().set("margin-top", "0.75rem");
            
            Button acceptButton = new Button("Accepter", VaadinIcon.CHECK.create());
            acceptButton.addThemeVariants(ButtonVariant.LUMO_PRIMARY, ButtonVariant.LUMO_SUCCESS);
            acceptButton.addClickListener(e -> {
                try {
                    bookingService.acceptBooking(booking.getId(), currentUser.getId());
                    showSuccess("Réservation acceptée!");
                    loadMyTrips();
                    dialog.close();
                    showBookings(trip);
                } catch (Exception ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            });
            
            Button rejectButton = new Button("Refuser", VaadinIcon.CLOSE.create());
            rejectButton.addThemeVariants(ButtonVariant.LUMO_ERROR);
            rejectButton.addClickListener(e -> {
                try {
                    bookingService.rejectBooking(booking.getId(), currentUser.getId());
                    showInfo("Réservation refusée");
                    loadMyTrips();
                    dialog.close();
                    showBookings(trip);
                } catch (Exception ex) {
                    showError("Erreur: " + ex.getMessage());
                }
            });
            
            actions.add(acceptButton, rejectButton);
            content.add(actions);
        }
        
        card.add(content);
        return card;
    }
    
    private void handleCancelBooking(BookingDTO booking) {
        try {
            bookingService.cancelBooking(booking.getId(), currentUser.getId());
            showSuccess("Réservation annulée avec succès");
            loadMyBookings();
        } catch (Exception ex) {
            showError("Erreur lors de l'annulation: " + ex.getMessage());
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
}
