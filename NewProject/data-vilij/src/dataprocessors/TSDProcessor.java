package dataprocessors;

import data.DataSet;
import javafx.event.EventHandler;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

/**
 * The data files used by this data visualization applications follow a tab-separated format, where each data point is
 * named, labeled, and has a specific location in the 2-dimensional X-Y plane. This class handles the parsing and
 * processing of such data. It also handles exporting the data to a 2-D plot.
 * <p>
 * A sample file in this format has been provided in the application's <code>resources/data</code> folder.
 *
 * @author Ritwik Banerjee
 * @see XYChart
 */
public final class TSDProcessor {

    //ApplicationTemplate applicationTemplate;



    public static class InvalidDataNameException extends Exception {

        private static final String NAME_ERROR_MSG = "All data instance names must start with the @ character.";

        public InvalidDataNameException(String name) {
            super(String.format("Invalid name '%s'." + NAME_ERROR_MSG, name));
        }
    }

    public Map<String, String>  dataLabels;
    public Map<String, Point2D> dataPoints;
    //private Map<String, String> names;

    public TSDProcessor() {
        dataLabels = new HashMap<>();
        dataPoints = new HashMap<>();
        //ApplicationTemplate applicationTemplate = new ApplicationTemplate();
        //this.applicationTemplate = applicationTemplate;

        //names= new HashMap<>();
    }

    /**
     * Processes the data and populated two {@link Map} objects with the data.
     *
     * @param tsdString the input data provided as a single {@link String}
     * @throws Exception if the input string does not follow the <code>.tsd</code> data format
     */
    public void processString(String tsdString) throws Exception {
        AtomicBoolean hadAnError   = new AtomicBoolean(false);
        StringBuilder errorMessage = new StringBuilder();
        Stream.of(tsdString.split("\n"))
                .map(line -> Arrays.asList(line.split("\t")))
                .forEach(list -> {
                    try {
                        String   name  = checkedname(list.get(0));
                        String   label = list.get(1);
                        String[] pair  = list.get(2).split(",");
                        Point2D  point = new Point2D(Double.parseDouble(pair[0]), Double.parseDouble(pair[1]));
                        dataLabels.put(name, label);
                        dataPoints.put(name, point);
                       // names.put(label,name);
                    } catch (Exception e) {
                        errorMessage.setLength(0);
                        errorMessage.append(e.getClass().getSimpleName()).append(": ").append(e.getMessage());
                        hadAnError.set(true);
                    }
                });
        if (errorMessage.length() > 0)
            throw new Exception(errorMessage.toString());

    }

    public void processClassification(List<Integer> output,ArrayList<String> lines, XYChart chart,
                                      Double maxX,Double minY,Double minX){


        Double y_1 = output.get(2).doubleValue()/output.get(1).doubleValue();
        Double y_2 = ((output.get(0).doubleValue()*100.0)+output.get(2).doubleValue())/output.get(1).doubleValue();
        XYChart.Series<Number, Number> class_series = new XYChart.Series<>();
        class_series.setName("classification_line");
        //class_series.getData().add(new XYChart.Data<>(MaxXValue(),MinYValue()));
        class_series.getData().add(new XYChart.Data<>(maxX,minY));
        //class_series.getData().add(new XYChart.Data<>(MinXValue(),y_1));
        class_series.getData().add(new XYChart.Data<>(minX,y_1));
        //class_series.getData().add(new XYChart.Data<>(MaxXValue(),y_2));
        chart.getData().add(class_series);
        Node line =class_series.getNode().lookup(".chart-series-line");
        line.setStyle("-fx-stroke: red;");



    }

    public void processClustering(DataSet dataSet,XYChart chart){
        //((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        chart.getData().clear();
        dataLabels=dataSet.getLabels();
        //toChartData(((AppUI)applicationTemplate.getUIComponent()).getChart());
        toChartData(chart);
    }

    /*public Double MaxXValue(XYChart chart){
        double max = -1000000000;
        for(XYChart.Series series:((AppUI)applicationTemplate.getUIComponent()).getChart().getData()){
            for(int i=0;i<series.getData().size();i++){
                Double xvalue =(Double) ((XYChart.Data)series.getData().get(i)).getXValue();
                if(xvalue>max){max=xvalue;}
            }
        }
        return max;
    }
    public Double MinXValue(){
        double min = 100000000;
        for(XYChart.Series series:((AppUI)applicationTemplate.getUIComponent()).getChart().getData()){
            for(int i=0;i<series.getData().size();i++){
                Double xvalue =(Double) ((XYChart.Data)series.getData().get(i)).getXValue();
                if(xvalue<min){min=xvalue;}
            }
        }
        return min;
    }

    public Double MinYValue(){
        double min = 100000000;
        for(XYChart.Series series:((AppUI)applicationTemplate.getUIComponent()).getChart().getData()){
            for(int i=0;i<series.getData().size();i++){
                Double yvalue =(Double) ((XYChart.Data)series.getData().get(i)).getYValue();
                if(yvalue<min){min=yvalue;}
            }
        }
        return min;
    }
       */


    /**
     * Exports the data to the specified 2-D chart.
     *
     * @param chart the specified chart
     */
    public void toChartData(XYChart<Number, Number> chart) {
        Set<String> labels = new HashSet<>(dataLabels.values());

        for (String label : labels) {
            XYChart.Series<Number, Number> series = new XYChart.Series<>();
            series.setName(label);


            dataLabels.entrySet().stream().filter(entry -> entry.getValue().equals(label)).forEach(entry -> {
                Point2D point = dataPoints.get(entry.getKey());
                series.getData().add(new XYChart.Data<>(point.getX(), point.getY()));
                XYChart.Data<Number,Number> graph_plot = new XYChart.Data<>(point.getX(), point.getY());

                //PropertyManager manager = applicationTemplate.manager;
                PropertyManager manager = PropertyManager.getManager();

                StackPane hoverboard = new StackPane();
                hoverboard.setPrefSize(100,15);
                hoverboard.getStyleClass().addAll();
                hoverboard.setStyle(manager.getPropertyValue(AppPropertyTypes.FX_BACKGROUND_TRANSPARENT.name()));


                Label data_name = new Label(entry.getKey());

                data_name.setPrefSize(80,20);
                data_name.getStyleClass().addAll(
                        manager.getPropertyValue(AppPropertyTypes.CHART_LINE_SYMBOL.name()));
                data_name.setStyle(manager.getPropertyValue(AppPropertyTypes.FX_FONT_SIZE.name()+15));

                hoverboard.setOnMouseEntered(new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent mouseEvent) {
                                hoverboard.getChildren().setAll(data_name);
                                //data_name.toFront();
                                hoverboard.setCursor(Cursor.NONE);
                                hoverboard.toFront();
                            }
                        });
                        hoverboard.setOnMouseExited(new EventHandler<MouseEvent>() {
                            @Override public void handle(MouseEvent mouseEvent) {
                                hoverboard.getChildren().clear();
                                hoverboard.setCursor(Cursor.DEFAULT);
                            }
                        });

                graph_plot.setNode(hoverboard);
                series.getData().add(graph_plot);
            });

            chart.getData().add(series);
        }
        }



    public void clear() {
        dataPoints.clear();
        dataLabels.clear();
    }

    private String checkedname(String name) throws InvalidDataNameException {
        if (!name.startsWith("@"))
            throw new InvalidDataNameException(name);
        return name;
    }





}
