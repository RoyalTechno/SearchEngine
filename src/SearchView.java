import javafx.scene.control.*;

import java.util.List;

public interface SearchView {
    Button getSearchButton();
    RadioButton getBoostButton();
    TextField getQuery();
    void update(List<SearchResult> results);
}
