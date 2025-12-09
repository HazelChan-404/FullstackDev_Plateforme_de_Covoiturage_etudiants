package com.covoiturage.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.covoiturage.dto.BookingDTO;
import com.covoiturage.dto.TripDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.BookingService;
import com.covoiturage.service.TripService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("my-trips")
@PageTitle("Mes trajets - Covoiturage")
public class MyTripsView extends VerticalLayout {
    
    private TripService tripService;
    private BookingService bookingService;
    private UserDTO currentUser;
    
    private Grid<TripDTO> tripsGrid;
    
    public MyTripsView(TripService tripService, BookingService bookingService) {
        this.tripService = tripService;
        this.bookingService = bookingService;
        
        currentUser = (UserDTO) VaadinSession.getCurrent().getAttribute("currentUser");
        if (currentUser == null) {
            Notification.show("Vous devez être connecté", 3000, Notification.Position.MIDDLE);
            getUI().ifPresent(ui -> ui.navigate(LoginView.class));
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
        Div header = new Div();
        header.getStyle()
            .set("background", "white")
            .set("padding", "2rem")
            .set("border-radius", "16px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0")
            .set("margin-bottom", "1.5rem");
        
        H1 title = new H1("Mes trajets publiés");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        header.add(title);
        
        // Grid
        tripsGrid = createTripsGrid();
        
        mainContainer.add(header, tripsGrid);
        add(mainContainer);
        
        loadMyTrips();
    }
    
    private Grid<TripDTO> createTripsGrid() {
        Grid<TripDTO> grid = new Grid<>(TripDTO.class, false);
        grid.setHeightFull();
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 4px 6px -1px rgba(0, 0, 0, 0.1)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(trip -> trip.getDepartureCity() + " → " + trip.getArrivalCity())
            .setHeader("Trajet")
            .setWidth("300px")
            .setFlexGrow(0)
            .setAutoWidth(true);
        
        grid.addColumn(trip -> 
            trip.getDepartureDatetime().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm"))
        ).setHeader("Date").setWidth("150px").setFlexGrow(0);
        
        grid.addColumn(trip -> 
            trip.getAvailableSeats() + "/" + trip.getTotalSeats()
        ).setHeader("Places").setWidth("100px").setFlexGrow(0);
        
        grid.addColumn(trip -> trip.getPricePerSeat() + "€")
            .setHeader("Prix")
            .setWidth("100px")
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
        }).setHeader("Statut").setWidth("120px").setFlexGrow(0);
        
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
    
    private void loadMyTrips() {
        try {
            List<TripDTO> trips = tripService.getTripsByDriverId(currentUser.getId());
            tripsGrid.setItems(trips);
            
            if (trips.isEmpty()) {
                Notification.show("Vous n'avez pas encore publié de trajet", 
                                3000, Notification.Position.MIDDLE);
            }
        } catch (Exception ex) {
            showError("Erreur: " + ex.getMessage());
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
        
        // Store dialog reference for use in buttons
        Dialog[] dialogRef = new Dialog[1];
        
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
                    // Refresh both trips and bookings data
                    loadMyTrips();
                    // Refresh the current dialog to show updated data
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
