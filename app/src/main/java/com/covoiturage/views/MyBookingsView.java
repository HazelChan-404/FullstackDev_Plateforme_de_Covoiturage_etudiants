package com.covoiturage.views;

import java.time.format.DateTimeFormatter;
import java.util.List;

import com.covoiturage.dto.BookingDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.service.BookingService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.H1;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.notification.Notification;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import com.vaadin.flow.server.VaadinSession;

@Route("my-bookings")
@PageTitle("Mes réservations - Covoiturage")
public class MyBookingsView extends VerticalLayout {
    
    private BookingService bookingService;
    private UserDTO currentUser;
    
    private Grid<BookingDTO> bookingsGrid;
    
    public MyBookingsView(BookingService bookingService) {
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
        
        H1 title = new H1("Mes réservations");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        header.add(title);
        
        // Grid
        bookingsGrid = createBookingsGrid();
        
        mainContainer.add(header, bookingsGrid);
        add(mainContainer);
        
        loadMyBookings();
    }
    
    private Grid<BookingDTO> createBookingsGrid() {
        Grid<BookingDTO> grid = new Grid<>(BookingDTO.class, false);
        grid.setHeightFull();
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
        
        grid.addColumn(booking -> 
            booking.getCreatedAt().format(DateTimeFormatter.ofPattern("dd/MM HH:mm"))
        ).setHeader("Réservé le").setWidth("120px").setFlexGrow(0);
        
        grid.addComponentColumn(booking -> {
            if (booking.getMessageToDriver() != null && !booking.getMessageToDriver().isEmpty()) {
                Div messageBadge = new Div("💬");
                messageBadge.getStyle()
                    .set("text-align", "center")
                    .set("font-size", "1.2rem");
                messageBadge.setTitle(booking.getMessageToDriver());
                return messageBadge;
            }
            return new Div();
        }).setHeader("Message").setWidth("80px").setFlexGrow(0);
        
        grid.addComponentColumn(booking -> {
            Button viewButton = new Button("Voir", VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_PRIMARY);
            viewButton.addClickListener(e -> {
                if (booking.getTripId() != null) {
                    getUI().ifPresent(ui -> ui.navigate(TripDetailsView.class, booking.getTripId()));
                }
            });
            return viewButton;
        }).setHeader("Action").setAutoWidth(true);
        
        return grid;
    }
    
    private void loadMyBookings() {
        try {
            List<BookingDTO> bookings = bookingService.getBookingsByPassengerId(currentUser.getId());
            bookingsGrid.setItems(bookings);
            
            if (bookings.isEmpty()) {
                Notification.show("Vous n'avez pas encore de réservation", 
                                3000, Notification.Position.MIDDLE);
            }
            
        } catch (Exception ex) {
            Notification.show("Erreur: " + ex.getMessage(), 
                            3000, Notification.Position.MIDDLE);
        }
    }
}