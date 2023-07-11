package uk.ac.soton.seg15.view.events;

import uk.ac.soton.seg15.model.Calculate;
import javafx.event.ActionEvent;

/**
 * The Calculations Input button clicked listener
 * Passes the new calculated values to the scene
 */
public interface CalculateButtonListener {

    public void calculateButtonClicked(ActionEvent event);
}
