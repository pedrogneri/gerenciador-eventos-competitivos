package controllers;

import application.Session;
import com.jfoenix.controls.JFXComboBox;
import com.jfoenix.controls.JFXToggleNode;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.ToggleGroup;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import models.Event;
import services.EventService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class EventListController {
    public GridPane gridEvents;
    public GridPane gridNavigation;
    public JFXComboBox cbxEventsPerPage;
    public HBox paneNavigation;
    public HBox hboxEventList;
    public HBox hboxEventsPerPage;

    private List<List<Event>> eventPages;
    private ToggleGroup navigationButtons;
    private int eventsPerPage;
    private int numberOfPages;
    private int pageIndex;

    static List<Event> institutionEvents;
    static Event event;

    @FXML
    public void initialize() {
        List<String> eventsId = Session.getInstance().getInstitution().getEvents_id();
        cbxEventsPerPage.getItems().addAll(6, 9, 12, 15, "Todos");
        cbxEventsPerPage.setValue(9);

        institutionEvents = new EventService().requestAllEventsOfInstitution(eventsId);
        navigationButtons = new ToggleGroup();
        eventsPerPage = Integer.parseInt(cbxEventsPerPage.getValue().toString());
        numberOfPages = getNumberOfPages();
        pageIndex = 0;

        if(eventsId.size() > 0) showEventsOnView();
        else showNoEventsMessage();
    }

    private void showNoEventsMessage(){
        hboxEventsPerPage.setVisible(false);
        paneNavigation.setVisible(false);

        Label message = new Label("Você ainda não tem eventos!");
        message.getStyleClass().add("no-events-message");
        hboxEventList.getChildren().clear();
        hboxEventList.getChildren().add(message);
    }

    private void showEventsOnView(){
        paginationEvents();
        listEventsPage();
    }

    @FXML
    public void changeEventsPerPage(){
        eventsPerPage = cbxEventsPerPage.getValue().equals("Todos") ? institutionEvents.size() :
                Integer.parseInt(cbxEventsPerPage.getValue().toString());
        numberOfPages = getNumberOfPages();
        pageIndex = 0;

        navigationButtons.getToggles().clear();
        gridNavigation.getChildren().clear();
        showEventsOnView();
    }

    private void listEventsPage() {
        List<Event> eventListPage = eventPages.get(pageIndex);
        gridEvents.getChildren().clear();

        for (int i = 0; i < eventListPage.size(); i++) {
            event = eventListPage.get(i);
            addNewEventItem(i);
        }
    }

    private void paginationEvents(){
        eventPages = new ArrayList<>();
        boolean haveOnlyOnePage = numberOfPages == 1;

        if(haveOnlyOnePage)
            generateOnePageNavigation();
        else
            generateMultiplePageNavigation();
    }

    private void generateOnePageNavigation(){
        eventPages.add(appendEventsOnPageAndReturn(0));
        paneNavigation.setVisible(false);
    }

    private void generateMultiplePageNavigation(){
        for(int pageIndex = 0; pageIndex < numberOfPages; pageIndex++){
            eventPages.add(appendEventsOnPageAndReturn(pageIndex));
            generateNavigationButton(pageIndex);
        }
        navigationButtons.getToggles().get(0).setSelected(true);
        paneNavigation.setVisible(true);
    }

    private void generateNavigationButton(int pageIndex){
        JFXToggleNode toggleNode = new JFXToggleNode();
        toggleNode.setText(String.valueOf(pageIndex + 1));
        toggleNode.setOnAction(this::navigateThroughThePages);
        toggleNode.setToggleGroup(navigationButtons);
        gridNavigation.add(toggleNode, pageIndex, 0);
    }

    private void navigateThroughThePages(ActionEvent event){
        JFXToggleNode toggleNode = (JFXToggleNode) event.getSource();
        if(!toggleNode.isSelected()) toggleNode.setSelected(true);

        pageIndex = Integer.parseInt(toggleNode.getText()) - 1;
        listEventsPage();
    }

    private List<Event> appendEventsOnPageAndReturn(int pageIndex){
        List<Event> page = new ArrayList<>();
        boolean lastPage = pageIndex == numberOfPages-1;
        int eventIndex = eventsPerPage * pageIndex;
        int eventLimit = lastPage ? institutionEvents.size() - eventIndex : eventsPerPage;

        for(int eventCount = 0; eventCount < eventLimit; eventCount++){
            page.add(institutionEvents.get(eventIndex));
            eventIndex++;
        }
        return page;
    }

    private int getNumberOfPages(){
        return (institutionEvents.size() / eventsPerPage) + (institutionEvents.size() % eventsPerPage == 0 ? 0 : 1);
    }

    @FXML
    protected void navigateToPreviousPage(){
        if(pageIndex > 0){
            pageIndex--;
            navigationButtons.getToggles().get(pageIndex).setSelected(true);
            listEventsPage();
        }
    }

    @FXML
    protected void navigateToNextPage(){
        if(pageIndex < eventPages.size()-1){
            pageIndex++;
            navigationButtons.getToggles().get(pageIndex).setSelected(true);
            listEventsPage();
        }
    }

    private void addNewEventItem(int eventCount){
        try{
            AnchorPane eventItem = FXMLLoader.load(getClass().getResource("/views/home/partials/event-item.fxml"));
            gridEvents.add(eventItem, eventCount % 3,eventCount / 3);
        } catch (Exception e){ e.printStackTrace(); }
    }

    @FXML
    protected void openEventCreateView(){
        try {
            Scene scene = new Scene(FXMLLoader.load(getClass().getResource("/views/external-forms/event-create.fxml")));
            Stage stage = new Stage();
            stage.setScene(scene);
            stage.setResizable(false);
            stage.centerOnScreen();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
