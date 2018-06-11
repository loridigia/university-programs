package Charts;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.stage.Stage;

public class ChartBarSample extends Application {
    
    
    static String avv = "Avventurieri";
    static String mod = "Moderati";
    static String spre = "Spregiudicate";
    static String prud = "Prudenti";
    
    static XYChart.Series series1;
    
    @Override public void start(Stage stage) {
        
        stage.setTitle("Simulazione Battaglia dei Sessi");
        
        final CategoryAxis xAxis = new CategoryAxis();
        final NumberAxis yAxis = new NumberAxis();
        final BarChart<String,Number> bc = new BarChart<String,Number>(xAxis,yAxis);
        
        bc.setTitle("Riepilogo Simulazione");
        yAxis.setLabel("Numero di Umani");
    
        series1 = new XYChart.Series();
        series1.setName("Umani");
        series1.getData().add(new XYChart.Data(avv, 20));
        series1.getData().add(new XYChart.Data(mod, 10));
        series1.getData().add(new XYChart.Data(spre, 30));
        series1.getData().add(new XYChart.Data(prud, 20));
        
        Scene scene  = new Scene(bc,800,600);
        bc.getData().add(series1);
        stage.setScene(scene);
        stage.show();
    }
    
    public static void main(String[] args) {
        launch(args);
    }
    public static void load(int a, int m, int s, int p) {
    
        series1.getData().add(new XYChart.Data(avv, a));
        series1.getData().add(new XYChart.Data(mod, m));
        series1.getData().add(new XYChart.Data(spre, s));
        series1.getData().add(new XYChart.Data(prud, p));
    
    
    }
}