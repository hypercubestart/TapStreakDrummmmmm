import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;

import java.io.BufferedReader;
import java.io.FileReader;

/**
 * Created by Andy on 11/11/2017.
 */
public class CsvToGraph extends Application {
    private String csvFile = "../datagetter/test10.csv";
    private BufferedReader br = null;
    private String line = "";
    private int positionBaseline;
    private int counter = 0;
    public static void main(String[] args) {
        launch(args);
    }
    @Override
    public void start(Stage primaryStage) throws Exception {
        br = new BufferedReader(new FileReader(csvFile));
        String tempo = br.readLine();
        br.readLine();
        positionBaseline = Integer.parseInt(br.readLine());
        init(primaryStage);
    }

    private void init(Stage primaryStage) {
        HBox root = new HBox();
        Scene scene = new Scene(root, 1000, 1000);

        NumberAxis xAxis = new NumberAxis();
        xAxis.setLabel("Tick Mark");

        NumberAxis yAxis = new NumberAxis();
        yAxis.setLabel("Position");

        LineChart<Number, Number> lineChart = new LineChart<Number, Number>(xAxis, yAxis);
        lineChart.setTitle("Drum Position vs Time");

        XYChart.Series<Number, Number> data = new XYChart.Series<>();
        data.getData().add(new XYChart.Data<>(counter, positionBaseline));
        try {
            while ((line = br.readLine()) != null) {
                Integer yPos = Integer.parseInt(line);
                if (Math.abs(yPos-positionBaseline) < 150) {
                    counter++;
                    data.getData().add(new XYChart.Data<>(counter, yPos));
                }
            }
        } catch (Exception e) {}

        lineChart.getData().add(data);
        root.getChildren().add(lineChart);

        lineChart.setCreateSymbols(false);
        scene.getStylesheets().add("linechart.css");
        lineChart.applyCss();

        primaryStage.setTitle("Line Chart");
        primaryStage.setScene(scene);
        primaryStage.show();
    }
}
