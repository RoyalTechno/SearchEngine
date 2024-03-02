import javafx.collections.FXCollections;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import java.util.*;

public class SearchAppView1 extends Pane implements SearchView{
    private ListView<String> nameList;
    private ListView<Double> scoreList;
    private TextField query;
    private Button searchButton;
    private RadioButton boostButton;
    public Button getSearchButton() { return searchButton; }
    public RadioButton getBoostButton() { return boostButton; }
    public TextField getQuery() { return query; }

    public SearchAppView1(){
        //Create the labels
        Label l1 = new Label("Titles");
        l1.relocate(20,20);
        Label l2 = new Label("Scores");
        l2.relocate(210,20);

        //create the titles and scores lists
        nameList = new ListView<>();
        nameList.relocate(20,40);
        nameList.setPrefSize(180,150);

        scoreList = new ListView<>();
        scoreList.relocate(210,40);
        scoreList.setPrefSize(120,150);

        // Create the button and radio panes
        searchButton = new Button("Search");
        searchButton.relocate(180,240);
        searchButton.setPrefSize(90,30);

        boostButton = new RadioButton("Boost");
        boostButton.relocate(90,240);
        boostButton.setPrefSize(90,30);

        //create the text field
        query = new TextField();
        query.relocate(20,200);
        query.setPrefSize(310,10);

        //Add all the components to the Pane
        getChildren().addAll(l1,l2,nameList,scoreList,searchButton,boostButton,query);
        setPrefSize(350,280);
    }

    public void update(List<SearchResult> results){
        String[] titles = new String[results.size()];
        Double[] scores = new Double[results.size()];
        for (int i=0; i<results.size(); i++){
            titles[i] = results.get(i).getTitle();
            scores[i] = results.get(i).getScore();
        }
        nameList.setItems(FXCollections.observableArrayList(titles));
        scoreList.setItems(FXCollections.observableArrayList(scores));
    }
}
