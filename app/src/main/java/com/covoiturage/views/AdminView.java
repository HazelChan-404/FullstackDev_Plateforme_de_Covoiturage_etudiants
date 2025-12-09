package com.covoiturage.views;

import com.covoiturage.dto.ReportDTO;
import com.covoiturage.dto.UserDTO;
import com.covoiturage.security.SecurityService;
import com.covoiturage.service.ReportService;
import com.covoiturage.service.UserService;
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
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

@Route(value = "admin", layout = MainLayout.class)
@PageTitle("Administration - Covoiturage")
public class AdminView extends VerticalLayout {
    
    private final SecurityService securityService;
    private final UserService userService;
    private final ReportService reportService;
    
    private Tabs tabs;
    private Grid<UserDTO> usersGrid;
    private Grid<ReportDTO> reportsGrid;
    private VerticalLayout contentLayout;
    
    public AdminView(SecurityService securityService, UserService userService, ReportService reportService) {
        this.securityService = securityService;
        this.userService = userService;
        this.reportService = reportService;
        
        if (!securityService.isAuthenticated() || !securityService.isAdmin()) {
            getUI().ifPresent(ui -> ui.navigate(""));
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
        mainContainer.getStyle().set("max-width", "1600px").set("margin", "0 auto");
        
        // Header
        Div header = createHeader();
        
        // Stats cards
        HorizontalLayout statsCards = createStatsCards();
        
        // Tabs
        tabs = createTabs();
        
        // Content area
        contentLayout = new VerticalLayout();
        contentLayout.setSizeFull();
        contentLayout.setPadding(false);
        contentLayout.setSpacing(true);
        
        // Create grids
        usersGrid = createUsersGrid();
        reportsGrid = createReportsGrid();
        
        contentLayout.add(usersGrid);
        
        tabs.addSelectedChangeListener(event -> {
            contentLayout.removeAll();
            if (tabs.getSelectedIndex() == 0) {
                contentLayout.add(usersGrid);
                loadUsers();
            } else {
                contentLayout.add(reportsGrid);
                loadReports();
            }
        });
        
        loadUsers();
        
        mainContainer.add(header, statsCards, tabs, contentLayout);
        add(mainContainer);
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
            .set("background", "linear-gradient(135deg, #ef4444 0%, #dc2626 100%)")
            .set("display", "flex")
            .set("align-items", "center")
            .set("justify-content", "center")
            .set("box-shadow", "0 8px 16px -4px rgba(239, 68, 68, 0.4)");
        
        Icon shieldIcon = VaadinIcon.SHIELD.create();
        shieldIcon.setSize("30px");
        shieldIcon.getStyle().set("color", "white");
        iconBox.add(shieldIcon);
        
        H1 title = new H1("Panneau d'Administration");
        title.getStyle()
            .set("margin", "0")
            .set("font-size", "2rem")
            .set("font-weight", "700")
            .set("color", "#1e293b");
        
        titleSection.add(iconBox, title);
        headerContent.add(titleSection);
        
        header.add(headerContent);
        return header;
    }
    
    private HorizontalLayout createStatsCards() {
        HorizontalLayout stats = new HorizontalLayout();
        stats.setWidthFull();
        stats.setSpacing(true);
        stats.getStyle().set("margin-bottom", "1.5rem");
        
        stats.add(
            createStatCard("👥", "0", "Utilisateurs", "#2563eb"),
            createStatCard("🚗", "0", "Trajets", "#10b981"),
            createStatCard("⚠", "0", "Signalements", "#f59e0b")
        );
        
        return stats;
    }
    
    private Div createStatCard(String emoji, String value, String label, String color) {
        Div card = new Div();
        card.getStyle()
            .set("background", "white")
            .set("padding", "1.5rem")
            .set("border-radius", "12px")
            .set("text-align", "center")
            .set("border", "1px solid #e2e8f0")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)")
            .set("flex", "1")
            .set("min-width", "150px");
        
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
        
        card.add(emojiText, valueText, labelText);
        return card;
    }
    
    private Tabs createTabs() {
        Tabs tabsComponent = new Tabs();
        tabsComponent.setWidthFull();
        tabsComponent.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)")
            .set("border", "1px solid #e2e8f0")
            .set("padding", "0.5rem");
        
        Tab usersTab = new Tab(VaadinIcon.USERS.create(), new Paragraph("Utilisateurs"));
        Tab reportsTab = new Tab(VaadinIcon.WARNING.create(), new Paragraph("Signalements"));
        
        usersTab.getElement().getStyle()
            .set("padding", "0.75rem 1.5rem")
            .set("border-radius", "8px");
        reportsTab.getElement().getStyle()
            .set("padding", "0.75rem 1.5rem")
            .set("border-radius", "8px");
        
        tabsComponent.add(usersTab, reportsTab);
        return tabsComponent;
    }
    
    private Grid<UserDTO> createUsersGrid() {
        Grid<UserDTO> grid = new Grid<>(UserDTO.class, false);
        grid.setHeightFull();
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(UserDTO::getFirstName).setHeader("Prénom").setAutoWidth(true);
        grid.addColumn(UserDTO::getLastName).setHeader("Nom").setAutoWidth(true);
        grid.addColumn(UserDTO::getEmail).setHeader("Email").setFlexGrow(1);
        
        grid.addComponentColumn(user -> {
            Div badge = new Div();
            if (user.getIsVerified()) {
                badge.setText("✓ Vérifié");
                badge.getStyle()
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("background", "rgba(16, 185, 129, 0.1)")
                    .set("color", "#10b981")
                    .set("font-weight", "600")
                    .set("font-size", "0.875rem");
            } else {
                badge.setText("⚠ Non vérifié");
                badge.getStyle()
                    .set("padding", "4px 12px")
                    .set("border-radius", "12px")
                    .set("background", "rgba(245, 158, 11, 0.1)")
                    .set("color", "#f59e0b")
                    .set("font-weight", "600")
                    .set("font-size", "0.875rem");
            }
            return badge;
        }).setHeader("Statut").setAutoWidth(true);
        
        grid.addComponentColumn(user -> {
            Button viewButton = new Button("Voir", VaadinIcon.EYE.create());
            viewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            viewButton.addClickListener(e -> 
                getUI().ifPresent(ui -> ui.navigate(ProfileView.class, user.getId()))
            );
            return viewButton;
        }).setHeader("Actions").setAutoWidth(true);
        
        return grid;
    }
    
    private Grid<ReportDTO> createReportsGrid() {
        Grid<ReportDTO> grid = new Grid<>(ReportDTO.class, false);
        grid.setHeightFull();
        grid.getStyle()
            .set("background", "white")
            .set("border-radius", "12px")
            .set("box-shadow", "0 2px 4px rgba(0, 0, 0, 0.05)")
            .set("border", "1px solid #e2e8f0");
        
        grid.addColumn(ReportDTO::getId).setHeader("ID").setWidth("80px").setFlexGrow(0);
        
        grid.addColumn(report -> 
            report.getReporter() != null ? report.getReporter().getEmail() : "N/A"
        ).setHeader("Signaleur").setAutoWidth(true);
        
        grid.addColumn(ReportDTO::getReportType).setHeader("Type").setAutoWidth(true);
        grid.addColumn(ReportDTO::getReportReason).setHeader("Raison").setFlexGrow(1);
        
        grid.addComponentColumn(report -> {
            Div badge = new Div();
            String text, color, bgColor;
            
            switch (report.getReportStatus()) {
                case PENDING:
                    text = "⏳ En attente";
                    color = "#f59e0b";
                    bgColor = "rgba(245, 158, 11, 0.1)";
                    break;
                case RESOLVED:
                    text = "✓ Résolu";
                    color = "#10b981";
                    bgColor = "rgba(16, 185, 129, 0.1)";
                    break;
                case DISMISSED:
                    text = "✗ Rejeté";
                    color = "#ef4444";
                    bgColor = "rgba(239, 68, 68, 0.1)";
                    break;
                default:
                    text = "? Inconnu";
                    color = "#64748b";
                    bgColor = "rgba(100, 116, 139, 0.1)";
            }
            
            badge.setText(text);
            badge.getStyle()
                .set("padding", "4px 12px")
                .set("border-radius", "12px")
                .set("background", bgColor)
                .set("color", color)
                .set("font-weight", "600")
                .set("font-size", "0.875rem")
                .set("white-space", "nowrap");
            
            return badge;
        }).setHeader("Statut").setAutoWidth(true);
        
        grid.addColumn(ReportDTO::getCreatedAt).setHeader("Date").setAutoWidth(true);
        
        grid.addComponentColumn(report -> {
            HorizontalLayout actions = new HorizontalLayout();
            actions.setSpacing(true);
            
            Button reviewButton = new Button("Examiner", VaadinIcon.EYE.create());
            reviewButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_TERTIARY);
            reviewButton.addClickListener(e -> {
                showInfo("Examiner le signalement #" + report.getId());
            });
            
            actions.add(reviewButton);
            
            if (report.getReportStatus() == com.covoiturage.model.Report.ReportStatus.PENDING) {
                Button resolveButton = new Button("Résoudre", VaadinIcon.CHECK.create());
                resolveButton.addThemeVariants(ButtonVariant.LUMO_SMALL, ButtonVariant.LUMO_SUCCESS);
                resolveButton.addClickListener(e -> {
                    try {
                        com.covoiturage.dto.UpdateReportDTO updateDTO = new com.covoiturage.dto.UpdateReportDTO();
                        updateDTO.setReportStatus(com.covoiturage.model.Report.ReportStatus.RESOLVED);
                        updateDTO.setAdminNotes("Résolu par l'administrateur");
                        
                        reportService.updateReportStatus(report.getId(), updateDTO, securityService.getAuthenticatedUserId());
                        loadReports();
                        showSuccess("Signalement marqué comme résolu");
                    } catch (Exception ex) {
                        showError("Erreur: " + ex.getMessage());
                    }
                });
                actions.add(resolveButton);
            }
            
            return actions;
        }).setHeader("Actions").setAutoWidth(true);
        
        return grid;
    }
    
    private void loadUsers() {
        try {
            // Load users (implement getAllUsers in UserService if needed)
            showInfo("Chargement des utilisateurs...");
        } catch (Exception e) {
            usersGrid.setItems();
            showError("Erreur: " + e.getMessage());
        }
    }
    
    private void loadReports() {
        try {
            reportsGrid.setItems(reportService.getPendingReports());
        } catch (Exception e) {
            reportsGrid.setItems();
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
    
    private void showInfo(String message) {
        Notification.show("ℹ " + message, 2000, Notification.Position.MIDDLE);
    }
}