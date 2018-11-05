package ui;

import actions.AppActions;
import dataprocessors.AppData;
import javafx.beans.value.ObservableValue;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextArea;
import javafx.scene.layout.*;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.stage.Stage;
import settings.AppPropertyTypes;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;
import vilij.templates.UITemplate;
import java.io.IOException;
import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;
import static vilij.settings.PropertyTypes.EXIT_TOOLTIP;

/**
 * This is the application's user interface implementation.
 *
 * @author Ritwik Banerjee
 */
public final class AppUI extends UITemplate {

    /** The application to which this class of actions belongs. */
    ApplicationTemplate applicationTemplate;

    @SuppressWarnings("FieldCanBeLocal")
    private Button                       scrnshotButton; // toolbar button to take a screenshot of the data
    private LineChart<Number, Number> chart;          // the chart where data will be displayed
    //private Button                       displayButton;  // workspace button to display data on the chart
    private TextArea                     textArea;       // text area for new data input
    private boolean                      hasNewText;     // whether or not the text area has any new data since last display
    private String                       scrnshotpath;
    private Pane                          workspace;
    private final String                  separator="/";
    private boolean                       readonly;
    public  Pane                          left= new VBox();
    public Pane                           right = new VBox();
    public Pane                           labelpane = new Pane();
    public Pane                           buttonPane = new Pane();
    public Label                          iterations = new Label();
    public Pane                           itPane  = new Pane();
    public Button                        readonlybutton;
    public Pane                         textAreaPane = new Pane();
    public Button                       runbutton ;


    public Button getSaveButton(){
        return saveButton;
    }

    public String getSeparator() {
        return separator;
    }

    public void setReadonly(boolean b){
        readonly=b;
    }

    public LineChart<Number, Number> getChart() { return chart; }

    public boolean gethasNewText(){
        if (hasNewText) return true;
        else return false;
    }

    public void setHasNewText(boolean a){hasNewText=a;}

    public TextArea getTextArea() {
        return textArea;
    }


    public AppUI(Stage primaryStage, ApplicationTemplate applicationTemplate) {
        super(primaryStage, applicationTemplate);
        this.applicationTemplate = applicationTemplate;

        readonly=true;

    }

    @Override
    protected void setResourcePaths(ApplicationTemplate applicationTemplate) {
        super.setResourcePaths(applicationTemplate);
        PropertyManager manager = applicationTemplate.manager;
        String iconsPath = separator + String.join(separator,
             manager.getPropertyValue(GUI_RESOURCE_PATH.name()),
              manager.getPropertyValue(ICONS_RESOURCE_PATH.name()));
        scrnshotpath=String.join(separator, iconsPath, manager.getPropertyValue(SCREENSHOT_ICON.name()));

    }

    @Override
    protected void setToolBar(ApplicationTemplate applicationTemplate) {
        // TODO for homework 1
        PropertyManager manager = applicationTemplate.manager;
        newButton = setToolbarButton(newiconPath, manager.getPropertyValue(NEW_TOOLTIP.name()), true);
        saveButton = setToolbarButton(saveiconPath, manager.getPropertyValue(SAVE_TOOLTIP.name()), true);
        loadButton = setToolbarButton(loadiconPath, manager.getPropertyValue(LOAD_TOOLTIP.name()), false);
        printButton = setToolbarButton(printiconPath, manager.getPropertyValue(PRINT_TOOLTIP.name()), true);
        exitButton = setToolbarButton(exiticonPath, manager.getPropertyValue(EXIT_TOOLTIP.name()), false);
        scrnshotButton = setToolbarButton(scrnshotpath, manager.getPropertyValue(SCREENSHOT_TOOLTIP.name()),true);
        toolBar = new ToolBar(newButton, saveButton, loadButton, exitButton,scrnshotButton);

    }

    @Override
    protected void setToolbarHandlers(ApplicationTemplate applicationTemplate) {

        applicationTemplate.setActionComponent(new AppActions(applicationTemplate));
        newButton.setOnAction(e -> applicationTemplate.getActionComponent().handleNewRequest());
        saveButton.setOnAction(e -> applicationTemplate.getActionComponent().handleSaveRequest());
        loadButton.setOnAction(e -> applicationTemplate.getActionComponent().handleLoadRequest());
        exitButton.setOnAction(e -> applicationTemplate.getActionComponent().handleExitRequest());
        printButton.setOnAction(e -> applicationTemplate.getActionComponent().handlePrintRequest());

    }

    @Override
    public void initialize() {
        layout();
        setWorkspaceActions();
    }

    @Override
    public void clear() {
        // TODO for homework 1
       //AppData data = new AppData(applicationTemplate);
       //data.getProcessor().clear();
        //AppActions actions = new AppActions(applicationTemplate);


            AppData data = ((AppData)applicationTemplate.getDataComponent());
        data.clear();
        textArea.clear();
        chart.getData().clear();
        data.displayData();
        scrnshot(true);
         return;

    }




    private void layout() {
        // TODO for homework 1
        PropertyManager manager =  applicationTemplate.manager;
        NumberAxis x = new NumberAxis();
        //x.setLowerBound(-100);
        //x.setUpperBound(100);
        x.setAnimated(false);
        //x.setAutoRanging(false);
        NumberAxis y = new NumberAxis();
        //y.setLowerBound(-100);
        //y.setUpperBound(100);
        y.setAnimated(false);
        //y.setAutoRanging(false);
        LineChart<Number,Number> sc = new
                LineChart<Number, Number>(x,y);
        sc.setTitle(manager.getPropertyValue(DATA_VISAULISATION.name()));
        chart=sc;
        chart.setPrefSize(600,450);
        chart.setTranslateX(90);
        chart.getStylesheets().add(manager.getPropertyValue(AppPropertyTypes.PROPERTIES_CSS_DIR.name()));
        chart.setHorizontalGridLinesVisible(false);
        chart.setVerticalGridLinesVisible(false);
        chart.setHorizontalZeroLineVisible(false);
        chart.setVerticalZeroLineVisible(false);



        this.workspace=new HBox();
        TextArea databox = new TextArea();
        textArea=databox;
        textArea.setVisible(false);
        textArea.textProperty().addListener(new javafx.beans.value.ChangeListener<String>() {
            @Override
            public void changed(ObservableValue<? extends String> observableValue, String oldValue, String newValue) {
                if(!oldValue.equals(newValue)&&!observableValue.getValue().isEmpty()){
                    setHasNewText(true);
                   // displayButton.setDisable(false);
                    newButton.setDisable(false);
                    saveButton.setDisable(false);
                }
                if(observableValue.getValue().isEmpty()){
                    //displayButton.setDisable(true);
                    newButton.setDisable(true);
                    saveButton.setDisable(true);
                }
                if(oldValue.equals(newValue)){
                    saveButton.setDisable(true);

                }
                 return;
               }
           }
        );
        textArea.setTranslateX(20);
        textArea.setPrefSize(300,150);

        //
        //Button displaybutton= new Button(manager.getPropertyValue(DISPLAY.name()));
        //displayButton=displaybutton;
        //displaybutton.setVisible(false);

        //displayButton.setDisable(false);
        //displayButton.setTranslateX(20);
        //displayButton.setTranslateY(10);


         readonlybutton = new Button(manager.getPropertyValue(READ_ONLY.name()));
         runbutton = new Button("Run");


        readonlybutton.setTranslateX(240);
        readonlybutton.setTranslateY(150);
        readonlybutton.setVisible(false);


        runbutton.setTranslateY(120);
        runbutton.setTranslateX(20);
        runbutton.setVisible(false);
        runbutton.setDisable(true);


        //iterations.setTranslateY(160);
        //iterations.setTranslateX(20);
        //((AppUI)applicationTemplate.getUIComponent()).buttonPane.getChildren().add(iterations);
        itPane.getChildren().add(iterations);

        //Button newstagecreator = new Button("StageCreator");
        //newstagecreator.setOnAction(e -> createNewWindow());
        textAreaPane.getChildren().add(textArea);
        textAreaPane.getChildren().add(readonlybutton);
        labelpane.setTranslateY(20);
        labelpane.setMaxSize(30,120);
        buttonPane.setTranslateY(20);
        itPane.setMaxSize(30,20);
        itPane.setTranslateY(150);
        itPane.setTranslateX(20);
        workspace.getChildren().add(left);
        workspace.getChildren().add(right);
        right.getChildren().add(chart);
        left.getChildren().add(textAreaPane);
        left.getChildren().add(labelpane);
        left.getChildren().add(buttonPane);
        left.getChildren().add(itPane);
        //left.getChildren().add(displayButton);
        //left.getChildren().add(readonlybutton);
        //left.getChildren().add(newstagecreator);
        this.workspace.getChildren().add(chart);
        appPane.getChildren().add(workspace);






    }

    private void setWorkspaceActions()  {
        // TODO for homework 1
       AppActions actions = new AppActions(applicationTemplate);
        readonlybutton.setOnAction(e ->Read_Only_Enable_Disable());
        scrnshotButton.setOnAction(e -> {
            try {
                actions.handleScreenshotRequest();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        });

        //int s = ((AppActions)applicationTemplate.getActionComponent()).selected;
        //runbutton.setOnAction(e->actions.app_Run());

    }

    //public void createNewWindow(){
      //  Stage newWindow = new Stage();
       // newWindow.setScene(new javafx.scene.Scene(new Button("New Stage"), 100, 100));
        //newWindow.show();
    //}

    public void disableSaveButton(boolean val){
        saveButton.setDisable(val);
    }

    public void Read_Only_Enable_Disable(){
        if(readonly){
            textArea.setDisable(false);
            saveButton.setDisable(true);
            readonly=false;
        }
        else {
            //textArea.clear();
            buttonPane.getChildren().clear();
            ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                    ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
            )).setText("");            textArea.setDisable(true);
            //saveButton.setDisable(false);
            readonly=true;
            //left.getChildren().remove(left.getChildren().size()-1);
            if(labelpane.getChildren().size()>0){
                labelpane.getChildren().remove(labelpane.getChildren().size()-1);}
            AppData data = ((AppData)applicationTemplate.getDataComponent());
            try {
                data.loadData(textArea.getText());
            } catch (IOException e) {
                e.printStackTrace();
            }
            //data.clear();
        }
    }

    public void scrnshot(boolean value){
        scrnshotButton.setDisable(value);
    }







}
