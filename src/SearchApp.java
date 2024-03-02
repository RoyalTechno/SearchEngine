import javafx.application.Application;
import javafx.event.*;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;

public class SearchApp extends Application{
    private static final int TopXSearchResults = 10;
    private SearchQuery model;
    public SearchApp(){
        //create a new search instance
        model = new SearchQuery();
    }

    public static void main(String[] args) {
        launch(args);
    }

    public void start(Stage stage) throws Exception {
        Pane mainPanel = new Pane();
        //create the view class GUI layout
        SearchAppView1 view = new SearchAppView1();
        //instruct how to handle user button click
        view.getSearchButton().setOnAction(new EventHandler<ActionEvent>() {
            public void handle(ActionEvent actionEvent) {
                //check that there is input in the text box
                if(!view.getQuery().getText().isEmpty()){
                    //update list views after search is done
                    view.update(model.searchFor(view.getQuery().getText(),
                            view.getBoostButton().isSelected(),
                            TopXSearchResults));
                    //reset the text box and the radio button after every search
                    view.getQuery().setText("");
                    view.getBoostButton().setSelected(false);
                }
            }
        });
        //add view class layout onto the main window
        mainPanel.getChildren().add(view);
        stage.setTitle("Search Engine");
        stage.setResizable(false);
        stage.setScene(new Scene(mainPanel));
        stage.show();
    }
}
