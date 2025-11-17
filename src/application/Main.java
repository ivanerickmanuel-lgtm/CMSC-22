/**
 * Java GUI application that..
 * @author
 * @created_date
 */

package application;

import javafx.application.Application;
import javafx.stage.Stage;

public class Main extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("ICS Registration Planner");
        primaryStage.setWidth(1280);
        primaryStage.setHeight(720);

        LoginView loginView = new LoginView();
        loginView.setStage(primaryStage);
    }

    public static void main(String[] args) {
        launch(args);
    }
}