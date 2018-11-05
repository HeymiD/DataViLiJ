package dataprocessors;


import actions.AppActions;
import data.DataSet;
import javafx.scene.Node;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ConfirmationDialog;
import vilij.components.DataComponent;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;


import java.awt.image.AreaAveragingScaleFilter;
import java.io.File;
import java.io.FileNotFoundException;

import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

/**
 * This is the concrete application-specific implementation of the data component defined by the Vilij framework.
 *
 * @author Ritwik Banerjee
 * @see DataComponent
 */
public class AppData implements DataComponent {

    private TSDProcessor        processor;
    private ApplicationTemplate applicationTemplate;
    //private final String              separate="/";
    private final String              newline="\n";
    private final String                star="*";
    private final String                tab="\t";
    private final String                bar ="-";
    public ArrayList<String> names = new ArrayList<>();
    public ArrayList<String> lines = new ArrayList<>();
    public ArrayList<String> labels = new ArrayList<>();
    private boolean                     error=false;
    public DataSet                      dataSet;
    public ArrayList<String>            label_no_duplicate = new ArrayList<>();

    public AppData(ApplicationTemplate applicationTemplate) {
        //this.processor = new TSDProcessor(applicationTemplate);
        this.processor = new TSDProcessor();
        this.applicationTemplate = applicationTemplate;
        //names=new ArrayList<>();
        //lines=new ArrayList<>();
        //labels=new ArrayList<>();
    }

    public TSDProcessor getProcessor() {
        return processor;
    }



    @Override
    public void loadData(Path dataFilePath) {
        // TODO: NOT A PART OF HW 1
        clear();
        ((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().clear();
        PropertyManager manager = applicationTemplate.manager;
        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT_DESC.name()),
                        star+manager.getPropertyValue(AppPropertyTypes.DATA_FILE_EXT.name())
               ));

        if (dataFilePath.toFile().isDirectory()) {
            fileChooser.setInitialDirectory(dataFilePath.toFile());
        }
        File loaded_file = fileChooser.showOpenDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        if (loaded_file==null){return;}

        Scanner scanner = null;
        try {
            scanner = new Scanner(loaded_file);
            String data = "";
            while (scanner.hasNextLine()){
                data = data + scanner.nextLine()+newline;}
            checkData(data,loaded_file.getName(),loaded_file.getPath().substring(2));
            if(error){
                clear();
                ((AppUI)applicationTemplate.getUIComponent()).labelpane.getChildren().clear();
                ((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().clear();
                error=false;
                return;}

            dataSet = DataSet.fromTextArea(lines);
            int l=0;
            for(String line:lines){
                if(l<10){
                    ((AppUI)(applicationTemplate.getUIComponent())).getTextArea().appendText(line+newline);}
                l++;
            }

            ((AppUI)applicationTemplate.getUIComponent()).getTextArea().setVisible(true);
            ((AppUI)applicationTemplate.getUIComponent()).getTextArea().setDisable(true);
            ((AppUI)applicationTemplate.getUIComponent()).disableSaveButton(true);

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public void loadData(String dataString) throws IOException {
        // TODO for homework 1
       clear();
       checkData(dataString,
               applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NOTHING.name()),
               applicationTemplate.manager.getPropertyValue(AppPropertyTypes.NOTHING.name()));

       if(error){return;}
       //displayData();
        ((AppUI)applicationTemplate.getUIComponent()).disableSaveButton(false);
      // AddAverageLine();

        dataSet = DataSet.fromTextArea(lines);


    }


    @Override
    public void saveData(Path dataFilePath) {

    }

    @Override
    public void clear() {
        //((AppActions)applicationTemplate.getActionComponent()).counter=1;
        ((AppUI)applicationTemplate.getUIComponent()).labelpane.getChildren().clear();
        processor.clear();
        names.clear();
        lines.clear();
        labels.clear();
        label_no_duplicate.clear();
        ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
        )).setText("");
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        ((AppUI)applicationTemplate.getUIComponent()).scrnshot(true);
    }

    public void displayData() {
        ((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
        processor.toChartData(((AppUI) applicationTemplate.getUIComponent()).getChart());
        //((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
        //displayClassification();
    }

    public void displayClassification(List<Integer> output){
        /*//Double y_2 = ((output.get(0).doubleValue()*100.0)+output.get(2).doubleValue())/output.get(1).doubleValue();

        //((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setAutoRanging(false);
        //((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setLowerBound(MinXValue()*0.96);
        //((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setUpperBound(MaxXValue()*1.04);
        //((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setTickUnit(
                ( MaxXValue()-MinXValue())/5
        );
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setAutoRanging(false);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setLowerBound(MinYValue()*0.96);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setUpperBound(MaxYValue()*1.04);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setTickUnit(
                ( MaxXValue()-MinXValue())/5
        );
        if(y_2<MinYValue()){
            ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setLowerBound(y_2*0.96);
        }
        if(y_2>MaxYValue()){
            ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setUpperBound(y_2*1.04);

        }
        */

        processor.processClassification(output,lines,((AppUI)applicationTemplate.getUIComponent()).getChart(),
                MaxXValue(),MinYValue(),MinXValue());
    }

    public void displayClustering(DataSet dataSet){
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setAutoRanging(false);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setLowerBound(MinXValue()*0.96);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setUpperBound(MaxXValue()*1.04);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getXAxis()).setTickUnit(
                ( MaxXValue()-MinXValue())/5
        );
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setAutoRanging(false);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setLowerBound(MinYValue()*0.96);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setUpperBound(MaxYValue()*1.04);
        ((NumberAxis)((AppUI)applicationTemplate.getUIComponent()).getChart().getYAxis()).setTickUnit(
                ( MaxXValue()-MinXValue())/5
        );
        processor.processClustering(dataSet,((AppUI)applicationTemplate.getUIComponent()).getChart());
    }



    public void checkData(String txt, String filename, String filepath){

        if(txt.isEmpty()){return;}

        String[] a = txt.split(newline);
        for(String str:a){
            lines.add(str);
        }
        PropertyManager manager =applicationTemplate.manager;

        int line_counter = 0;
        for(String line:lines){names.add(line.split(tab)[0]);}

        try {
            processor.processString(txt);
        } catch (Exception e1) {
            Dialog unvalid_data = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
            ErrorDialog unvalid_Data = ((ErrorDialog)unvalid_data);
            unvalid_Data.show(manager.getPropertyValue(PropertyTypes.LOAD_ERROR_TITLE.name()),
                    manager.getPropertyValue(AppPropertyTypes.UNVALID_DATA.name())+line_counter);
            error=true;
            return;
            //e1.printStackTrace();
        }



        for(int i=0;i<names.size();i++){
            for(int j=0;j<names.size();j++){
                if(i!=j){
                    if(names.get(i).equals(names.get(j))){
                        Dialog duplicate_dialog = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                        duplicate_dialog.show(
                                manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_TITLE.name()),
                                manager.getPropertyValue(AppPropertyTypes.DUPLICATE_ERROR_MSG.name())+
                                        names.get(j)+newline+
                                        manager.getPropertyValue(AppPropertyTypes.LINE_NO.name())+(j+1));
                        //((AppUI)(applicationTemplate.getUIComponent())).getTextArea().clear();
                        error=true;
                        return;


                    }
                }
            }
        }

        for(String line:lines){labels.add(line.split(tab)[1]);}

        //ArrayList<String> label_no_duplicate = new ArrayList<>();
        for(String label:labels){
            if(!label_no_duplicate.contains(label)){label_no_duplicate.add(label);}
        }
        String datastatistics = "";

        if(filepath.equals(manager.getPropertyValue(AppPropertyTypes.NOTHING.name()))){
             datastatistics = lines.size()+manager.getPropertyValue(AppPropertyTypes.INSTANCES_WITH.name())
                    +label_no_duplicate.size()+
                    manager.getPropertyValue(AppPropertyTypes.LABELS_LOADED_FROM.name())
                    + newline + filename +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.AND_THE_PATH.name())
                    +newline+ filepath+
                    newline+manager.getPropertyValue(AppPropertyTypes.LABELS_ARE.name()) + newline;
        }
        else{
             datastatistics = lines.size()+manager.getPropertyValue(AppPropertyTypes.INSTANCES_WITH.name())
                    +label_no_duplicate.size()+
                    manager.getPropertyValue(AppPropertyTypes.LABELS_LOADED_FROM.name())
                    + newline + filename +
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.AND_THE_PATH.name())
                    +newline+ filepath.substring(0,filepath.length()-30)+newline+
                    filepath.substring(filepath.length()-30,filepath.length())+
                    newline+manager.getPropertyValue(AppPropertyTypes.LABELS_ARE.name()) + newline;
        }


        for(String label:label_no_duplicate){
            datastatistics=datastatistics+(bar+label+newline);
        }

        Label label = new Label(datastatistics);
        label.setTranslateX(20);
        //label.setTranslateY(30);
        ((AppUI)applicationTemplate.getUIComponent()).labelpane.getChildren().add(label);

        Label algortype = new Label(manager.getPropertyValue(AppPropertyTypes.ALGORITHM_TYPE.name()));
        algortype.setTranslateX(20);
        algortype.setTranslateY(20);


        Button classbutton = new Button(manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name()));
        //classbutton.setDisable(true);
        classbutton.setTranslateY(50);
        classbutton.setTranslateX(20);
        AppActions actions = ((AppActions)applicationTemplate.getActionComponent());
        classbutton.setOnAction(e->actions.handleClassification());
        Button clusterbuttoo = new Button(manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name()));
        clusterbuttoo.setTranslateY(90);
        clusterbuttoo.setTranslateX(20);
        //AppActions actions = ((AppActions)applicationTemplate.getActionComponent());
        clusterbuttoo.setOnAction(e->actions.handleClustering());
        ((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().add(algortype);
        ((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().add(classbutton);
        ((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().add(clusterbuttoo);


    }
    public Double MaxXValue(){
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

    public Double MaxYValue(){
        double max = -1000000000;
        for(XYChart.Series series:((AppUI)applicationTemplate.getUIComponent()).getChart().getData()){
            for(int i=0;i<series.getData().size();i++){
                Double yvalue =(Double) ((XYChart.Data)series.getData().get(i)).getYValue();
                if(yvalue>max){max=yvalue;}
            }
        }
        return max;
    }

    }





