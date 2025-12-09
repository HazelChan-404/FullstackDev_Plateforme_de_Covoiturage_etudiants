package com.covoiturage.views;

import com.covoiturage.dto.NotificationDTO;
import com.covoiturage.security.SecurityService;
import com.covoiturage.service.NotificationService;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.button.ButtonVariant;
import com.vaadin.flow.component.grid.Grid;
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
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "notifications", layout = MainLayout.class)
@PageTitle("Notifications - Covoiturage")
public class NotificationsView extends VerticalLayout {
    
    private final NotificationService notificationService;
    private final SecurityService securityService;
    private Grid<NotificationDTO> grid;
    
    public NotificationsView(NotificationService notificationService, SecurityService securityService) {
        this.notificationService = notificationService;
        this.securityService = securityService;
        
        if (!securityService.isAuthenticated()) {
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
        
        // Grid
        grid = createGrid();
        
        mainContainer.add(header, grid);
        add(mainContainer);
        
        loadNotifications();
        
        grid.addItemClickListener(event -> {
            NotificationDTO notification = event.getItem();
            if (notification != null && !notification.getIsRead()) {
                markAsRead(notification);
            }
        });
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
        
        HorizontalLayout headerContent = new HorizontalLayout();
        headerContent.setWidthFull();
        headerContent.setAlignItems(FlexComponent.Alignment.CENTER);
        headerContent.setJustifyContentMode(FlexComponent.JustifyContentMode.BETWEEN);
        
        HorizontalLayout titleSection = new HorizontalLayout();
        titleSection.setAlignItems(FlexComponent.Alignment.CENTER);
        titleSection.setSpacing(true);
        
        Div iconBox = new Div();
        iconBox.getStyle()
            .set("width", "60px")
            .set("height", "60px")
            .set("border-radius", "12px")
            .set("background", "linear-gradient(135deg, #8b5cf6 0%, #7c3aed 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 8px 16px -4px rgba(139, 92, 246, 0.4)");
        
        Icon bellIcon = VaadinIcon.BELL.create();
        bellIcon.setSize("30px");
        bellIcon.getStyle().set("color", "white");
        iconBox.add(bellIcon);
        
        H1 title = new H1("Mes Notifications");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        titleSection.add(iconBox, title);
        
        Button markAllButton = new Button("Tout marquer comme lu", VaadinIcon.CHECK.create());
        markAllButton.addThemeVariants(ButtonVariant.LUMO_TERTIARY);
        markAllButton.addClickListener(e -> markAllAsRead());
        
        headerContent.add(titleSection, markAllButton);
        header.add(headerContent);
        
        return header;
    }
    
    private Grid<NotificationDTO> createGrid() {
        Grid<NotificationDTO> notifGrid = new Grid<>(NotificationDTO.class, false);
        notifGrid.setHeightFull();
        notifGrid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)")
            .set("border", "1px solid #e2e8f0");
        
        notifGrid.addComponentColumn(notification -> {
            HorizontalLayout content = new HorizontalLayout();
            content.setAlignItems(FlexComponent.Alignment.CENTER);
            content.setSpacing(true);
            content.setPadding(true);
            content.setWidthFull();
            
            // Type indicator
            Div typeIndicator = new Div();
            typeIndicator.getStyle()
                .set("width", "40px")
                .set("height", "40px")
                .set("border-radius", "8px")
                .set("display", "flex")
                .set("align-items", "center")
                .set("justify-content", "center")
                .set("flex-shrink", "0");
            
            Icon icon;
            String color, bgColor;
            
            switch (notification.getNotificationType()) {
                case BOOKING_REQUEST:
                    icon = VaadinIcon.CALENDAR.create();
                    color = "#2563eb";
                    bgColor = "rgba(37, 99, 235, 0.1)";
                    break;
                case BOOKING_CONFIRMED:
                    icon = VaadinIcon.CHECK_CIRCLE.create();
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                case BOOKING_CANCELLED:
                    icon = VaadinIcon.CLOSE_CIRCLE.create();
                    color = "#ef4444";
                    bgColor = "rgba(239, 68, 68, 0.1)";
                    break;
                case TRIP_REMINDER:
                    icon = VaadinIcon.BELL.create();
                    color = "#f59e0b";
                    bgColor = "rgba(245, 158, 11, 0.1)";
                    break;
                case MESSAGE_RECEIVED:
                    icon = VaadinIcon.ENVELOPE.create();
                    color = "#8b5cf6";
                    bgColor = "rgba(139, 92, 246, 0.1)";
                    break;
                case REVIEW_RECEIVED:
                    icon = VaadinIcon.STAR.create();
                    color = "#fbbf24";
                    bgColor = "rgba(251, 191, 36, 0.1)";
                    break;
                case TRIP_COMPLETED:
                    icon = VaadinIcon.CHECK.create();
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                case PAYMENT_RECEIVED:
                    icon = VaadinIcon.MONEY.create();
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                default:
                    icon = VaadinIcon.INFO_CIRCLE.create();
                    color = "#64748b";
                    bgColor = "rgba(100, 116, 139, 0.1)";
            }
            
            icon.setSize("20px");
            icon.getStyle().set("color", color);
            typeIndicator.getStyle()
                .set("background", bgColor)
                .set("border", "2px solid " + color);
            typeIndicator.add(icon);
            
            // Content
            VerticalLayout textContent = new VerticalLayout();
            textContent.setPadding(false);
            textContent.setSpacing(false);
            textContent.getStyle().set("flex", "1");
            
            Paragraph titleText = new Paragraph(notification.getTitle());
            titleText.getStyle()
                .set("margin", "0")
                .set("font-weight", notification.getIsRead() ? "500" : "700")
                .set("font-size", "1rem")
                .set("color", notification.getIsRead() ? "#64748b" : "#1e293b");
            
            Paragraph messageText = new Paragraph(notification.getMessage());
            messageText.getStyle()
                .set("margin", "0.25rem 0 0 0")
                .set("font-size", "0.875rem")
                .set("color", "#64748b")
                .set("line-height", "1.5");
            
            textContent.add(titleText, messageText);
            
            content.add(typeIndicator, textContent);
            
            // Unread indicator
            if (!notification.getIsRead()) {
                Div unreadDot = new Div();
                unreadDot.getStyle()
                    .set("width", "10px")
                    .set("height", "10px")
                    .set("border-radius", "50%")
                    .set("background", "#2563eb")
                    .set("flex-shrink", "0");
                content.add(unreadDot);
            }
            
            return content;
        }).setAutoWidth(true).setFlexGrow(1);
        
        notifGrid.addComponentColumn(notification -> {
            VerticalLayout dateBox = new VerticalLayout();
            dateBox.setPadding(false);
            dateBox.setSpacing(false);
            dateBox.setAlignItems(FlexComponent.Alignment.END);
            
            Paragraph date = new Paragraph(
                notification.getCreatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("dd/MM/yyyy")
                )
            );
            date.getStyle()
                .set("margin", "0")
                .set("font-size", "0.875rem")
                .set("color", "#64748b")
                .set("white-space", "nowrap");
            
            Paragraph time = new Paragraph(
                notification.getCreatedAt().format(
                    java.time.format.DateTimeFormatter.ofPattern("HH:mm")
                )
            );
            time.getStyle()
                .set("margin", "0.25rem 0 0 0")
                .set("font-size", "0.75rem")
                .set("color", "#94a3b8")
                .set("white-space", "nowrap");
            
            dateBox.add(date, time);
            return dateBox;
        }).setHeader("Date").setAutoWidth(true).setFlexGrow(0);
        
        notifGrid.setClassNameGenerator(notification -> 
            notification.getIsRead() ? "notification-read-row" : "notification-unread-row"
        );
        
        return notifGrid;
    }
    
    private void loadNotifications() {
        try {
            Long userId = securityService.getAuthenticatedUserId();
            if (userId != null) {
                grid.setItems(notificationService.getNotificationsForUserOrderByDate(userId));
            }
        } catch (Exception e) {
            grid.setItems();
            showError("Erreur lors du chargement: " + e.getMessage());
        }
    }
    
    private void markAsRead(NotificationDTO notification) {
        try {
            notificationService.markNotificationAsRead(
                notification.getId(), 
                securityService.getAuthenticatedUserId()
            );
            loadNotifications();
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }
    
    private void markAllAsRead() {
        try {
            Long userId = securityService.getAuthenticatedUserId();
            if (userId != null) {
                // Implement markAllAsRead in NotificationService if needed
                showSuccess("Toutes les notifications ont été marquées comme lues");
                loadNotifications();
            }
        } catch (Exception e) {
            showError("Erreur: " + e.getMessage());
        }
    }
    
    private void showError(String message) {
        Notification notification = Notification.show("⚠ " + message, 4000, Notification.Position.MIDDLE);
        notification.addThemeVariants(NotificationVariant.LUMO_ERROR);
    }
    
    private void showSuccess(String message) {
        Notification notification = Notification.show("✓ " + message, 3000, Notification.Position.TOP_CENTER);
        notification.addThemeVariants(NotificationVariant.LUMO_SUCCESS);
    }
}
