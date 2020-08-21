package demos.GSPGuiDemo.code.ui;

import demos.GSPGuiDemo.code.constant.SystemConstant;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;

import java.net.URL;

public class Format extends Application {

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle(String.join(" ", SystemConstant.name,
                SystemConstant.version));
        URL resource = getClass().getClassLoader().getResource("build/demos/GSPGuiDemo/xml/Format.fxml");
        Parent root = FXMLLoader.load(resource);
        Scene scene = new Scene(root);
        primaryStage.setScene(scene);
        primaryStage.setResizable(false);
        URL url = getClass().getClassLoader().getResource("build/demos/GSPGuiDemo/icon/database.png");
        primaryStage.getIcons().add(new Image(url.openStream()));
        primaryStage.show();
    }

    @Override
    public void stop() throws Exception {
        super.stop();
        System.exit(1);
    }
}
