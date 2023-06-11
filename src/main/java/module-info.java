module pwr.project.snake {
    requires javafx.controls;
    requires javafx.fxml;
            
            requires com.dlsc.formsfx;
                requires org.kordamp.ikonli.javafx;
                    requires com.almasb.fxgl.all;
    
    opens pwr.project.snake to javafx.fxml;
    exports pwr.project.snake;
}