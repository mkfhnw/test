package client.view;

import client.model.ToDo;
import javafx.collections.ObservableList;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;

public class SearchBarView extends MainBarView {

    // Constructor
    public SearchBarView(ObservableList<ToDo> searchList) {

        // Call super constructor to get defined elements
        super();

        // Individual icons and labels for this view
        this.icon = new ImageView("/common/resources/lupe.png");
        this.label = new Label("Suchergebnisse");
        this.icon.setFitHeight(50);
        this.icon.setFitWidth(53);
        this.header.getChildren().addAll(icon, label);

        // Populate tableView
        this.tableView.getItems().addAll(searchList);

        //Add css-styling
        this.getStylesheets().add(getClass().getResource("CategoryViewStyle.css").toExternalForm());
		this.label.getStyleClass().add("labelHeader");

    }

}
