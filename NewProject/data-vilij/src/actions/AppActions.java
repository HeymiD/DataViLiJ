package actions;

import classification.RandomClassifier;
import clusterer.KMeansClusterer;
import clusterer.RandomClusterer;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.embed.swing.SwingFXUtils;
import javafx.scene.Scene;
import javafx.scene.SnapshotParameters;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.image.WritableImage;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import jdk.nashorn.internal.objects.annotations.Property;
import settings.AppPropertyTypes;
import ui.AppUI;
import vilij.components.ActionComponent;
import vilij.components.ConfirmationDialog;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.settings.PropertyTypes;
import vilij.templates.ApplicationTemplate;
import javax.imageio.ImageIO;
import javax.xml.crypto.Data;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.List;

import static settings.AppPropertyTypes.*;
import static vilij.settings.PropertyTypes.*;

/**
 * This is the concrete implementation of the action handlers required by the application.
 *
 * @author Ritwik Banerjee
 */
public final class AppActions implements ActionComponent {

    /**
     * The application to which this class of actions belongs.
     */
    private ApplicationTemplate applicationTemplate;
    private final String sep = "/";
    private final String star = "*";
    private final String tab = "\t";
    private final String newline = "\n";
    private ArrayList<Integer> max_iterations = new ArrayList<>();
    private ArrayList<Integer> update_interval = new ArrayList<>();
    private ArrayList<Boolean> continuous_run = new ArrayList<>();
    //private ArrayList<Integer> max_iterations_cluster = new ArrayList<>();
    //private ArrayList<Integer> update_interval_cluster = new ArrayList<>();
    //private ArrayList<Boolean> continuous_run_cluster = new ArrayList<>();
    private ArrayList<Integer>  number_clusters = new ArrayList<>();
    public Integer            selected=-1;
    //public Integer            selected_clustering=-1;
    public Integer            selected_al=-1;
    public static int counter=1;
    public static boolean alg_running = false;





    /**
     * Path to the data file currently active.
     */
    Path dataFilePath;
    Path lastfile;

    public AppActions(ApplicationTemplate applicationTemplate) {
        this.applicationTemplate = applicationTemplate;
        for(int i=0;i<6;i++){max_iterations.add(0);}
        for(int i=0;i<6;i++){update_interval.add(0);}
        //for(int i=0;i<6;i++){max_iterations_cluster.add(0);}
        //for(int i=0;i<6;i++){update_interval_cluster.add(0);}
        for(int i=0;i<6;i++){continuous_run.add(false);}
        //for(int i=0;i<6;i++){continuous_run_cluster.add(false);}
        for(int i=0;i<6;i++){number_clusters.add(0);}


    }


    @Override
    public void handleNewRequest() {
        //counter=1;
        if (!((AppUI) applicationTemplate.getUIComponent()).getTextArea().getText().isEmpty()) {
            //try {
                //promptToSave();
                applicationTemplate.getUIComponent().clear();
                ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(false);
                ((AppUI) applicationTemplate.getUIComponent()).readonlybutton.setVisible(true);
                ((AppUI) applicationTemplate.getUIComponent()).setReadonly(false);
                ((AppUI) applicationTemplate.getUIComponent()).labelpane.getChildren().clear();
                ((AppUI) applicationTemplate.getUIComponent()).buttonPane.getChildren().clear();
                //((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().clear();
                ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                        ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1)).setText("");


           // } catch (IOException e) {
             //   e.printStackTrace();
            //}
                  }
        else {
            applicationTemplate.getUIComponent().clear();
            ((AppUI) applicationTemplate.getUIComponent()).getTextArea().setDisable(false);
            ((AppUI) applicationTemplate.getUIComponent()).readonlybutton.setVisible(true);
            ((AppUI) applicationTemplate.getUIComponent()).setReadonly(false);
        }
        lastfile = null;

    }


    @Override
    public void handleSaveRequest() {
        // TODO: NOT A PART OF HW 1

        PropertyManager manager =applicationTemplate.manager;
        AppData data = ((AppData)applicationTemplate.getDataComponent());

        if (lastfile != null) {


            FileWriter writer = null;
            try {
                writer = new FileWriter(lastfile.toFile());
            } catch (IOException e) {
                e.printStackTrace();
            }
            for (String line:data.lines) {
                // try {
                try {
                    writer.append(line+newline);

                } catch (IOException e) {
                    e.printStackTrace();
                }


            }

            try {
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return;
        }


            else {


            FileChooser filechooser = new FileChooser();
            filechooser.getExtensionFilters().addAll(
                    new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
                            star + manager.getPropertyValue(DATA_FILE_EXT.name())
                    ));
            URL resource = getClass().getResource(sep + manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
            File file = new File(resource.getFile());

            if (file.isDirectory()) {
                filechooser.setInitialDirectory(file);
            }
            File file_to_save = filechooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());

            if (file_to_save != null) {
                FileWriter fileWriter = null;
                try {
                    fileWriter = new FileWriter(file_to_save);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                for(String line: data.lines) {
                    try {

                        fileWriter.append(line+newline);

                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                try {
                    fileWriter.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    dataFilePath = Paths.get(resource.toURI());
                } catch (URISyntaxException e) {
                    Dialog urierror = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                    ErrorDialog u = ((ErrorDialog) urierror);
                    u.show(manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()),
                            manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
                }
                lastfile = file_to_save.toPath();
                ((AppUI) applicationTemplate.getUIComponent()).disableSaveButton(true);
            }
        }
    }

    @Override
    public void handleLoadRequest() {
        // TODO: NOT A PART OF HW 1
        counter=1;
        ((AppUI) applicationTemplate.getUIComponent()).getTextArea().clear();
        PropertyManager manager = applicationTemplate.manager;
        AppData data = ((AppData) applicationTemplate.getDataComponent());
        URL r = getClass().getResource(sep + manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        try {
            dataFilePath = Paths.get(r.toURI());
            data.loadData(dataFilePath);
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void handleExitRequest() {
        // TODO for homework 1
        //System.out.print(counter);
        PropertyManager manager = applicationTemplate.manager;
        if(selected>-1&&continuous_run.get(selected)==false&&alg_running){
            Dialog al_running = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            al_running.show(manager.getPropertyValue(AppPropertyTypes.EXIT_WHILE_RUNNING_WARNING.name()),
                    manager.getPropertyValue(AppPropertyTypes.EXIT_WHILE_RUNNING_WARNING.name()));

            ConfirmationDialog a = ((ConfirmationDialog) al_running);
            if(a.getSelectedOption()==null){
                return;
            }
            else if(a.getSelectedOption().equals(ConfirmationDialog.Option.NO)){
                a.close();
                return;
            }
            else{
                applicationTemplate.getUIComponent().getPrimaryWindow().close();
                return;
            }

        }

        if(alg_running&&((AppUI)applicationTemplate.getUIComponent()).runbutton.isDisabled()){

            Dialog al_running = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
            al_running.show(manager.getPropertyValue(AppPropertyTypes.EXIT_WHILE_RUNNING_WARNING.name()),
                    manager.getPropertyValue(AppPropertyTypes.EXIT_WHILE_RUNNING_WARNING.name()));

            ConfirmationDialog a = ((ConfirmationDialog) al_running);
            if(a.getSelectedOption()==null){
                return;
            }
            else if(a.getSelectedOption().equals(ConfirmationDialog.Option.NO)){
                a.close();
                return;
            }
            else{
                applicationTemplate.getUIComponent().getPrimaryWindow().close();
                System.exit(0);
                return;
            }
        }


        if(((AppUI)applicationTemplate.getUIComponent()).getSaveButton().isDisabled()){
            applicationTemplate.getUIComponent().getPrimaryWindow().close();
            System.exit(0);
        }
        else{
            try {
                promptToSave();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    @Override
    public void handlePrintRequest() {
        // TODO: NOT A PART OF HW 1
    }

    public void handleScreenshotRequest() throws IOException {
        // TODO: NOT A PART OF HW 1
        PropertyManager manager = applicationTemplate.manager;

        FileChooser fileChooser = new FileChooser();
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter(manager.getPropertyValue(IMAGE_EXT_DESC.name()),
                        star + manager.getPropertyValue(IMAGE_EXT.name())
                ));
        SnapshotParameters parameters = new SnapshotParameters();


        WritableImage chart_image = ((AppUI) applicationTemplate.getUIComponent()).getChart()
                .snapshot(parameters, null);

        BufferedImage bufferedImageARGB = SwingFXUtils.fromFXImage(chart_image, null);
        BufferedImage bufferedImageRGB = new BufferedImage(bufferedImageARGB.getWidth(), bufferedImageARGB.getHeight(), BufferedImage.OPAQUE);

        Graphics2D graphics = bufferedImageRGB.createGraphics();
        graphics.drawImage(bufferedImageARGB, 0, 0, null);

        URL resource = getClass().getResource(sep + manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        File dir = new File(resource.getFile());
        fileChooser.setInitialDirectory(dir);
        File image_file = fileChooser.showSaveDialog(applicationTemplate.getUIComponent().getPrimaryWindow());
        //ImageIO.write(SwingFXUtils.fromFXImage(chart_image,null),"jpeg",image_file);
        ImageIO.write(bufferedImageRGB, "jpeg", image_file);

        graphics.dispose();
        return;

    }


    /**
     * This helper method verifies that the user really wants to save their unsaved work, which they might not want to
     * do. The user will be presented with three options:
     * <ol>
     * <li><code>yes</code>, indicating that the user wants to save the work and continue with the action,</li>
     * <li><code>no</code>, indicating that the user wants to continue with the action without saving the work, and</li>
     * <li><code>cancel</code>, to indicate that the user does not want to continue with the action, but also does not
     * want to save the work at this point.</li>
     * </ol>
     *
     * @return <code>false</code> if the user presses the <i>cancel</i>, and <code>true</code> otherwise.
     */
    public boolean promptToSave() throws IOException {
        // TODO for homework 1
        // TODO remove the placeholder line below after you have implemented this method
        PropertyManager manager = applicationTemplate.manager;

        Dialog dialog = applicationTemplate.getDialog(Dialog.DialogType.CONFIRMATION);
        dialog.show(
                manager.getPropertyValue(SAVE_UNSAVED_WORK_TITLE.name()),
                manager.getPropertyValue(SAVE_UNSAVED_WORK.name()).substring(0,22)+newline+
        manager.getPropertyValue(SAVE_UNSAVED_WORK.name()).substring(22));

        ConfirmationDialog d = ((ConfirmationDialog) dialog);
        if (d.getSelectedOption() == null) {
            return false;
        }
        if (d.getSelectedOption().equals(ConfirmationDialog.Option.NO)) {
            //((AppUI)applicationTemplate.getUIComponent()).getChart().getData().clear();
            d.getSelectedOption();
            //applicationTemplate.getUIComponent().clear();
            applicationTemplate.getUIComponent().getPrimaryWindow().close();


        } else if (d.getSelectedOption().equals(ConfirmationDialog.Option.CANCEL)) {
            d.getSelectedOption();
            d.close();
            applicationTemplate.getUIComponent().getPrimaryWindow().close();
        } else {
            handleSaveRequest();

        }

        return false;

    }

    public void handleClassification() {
        //selected_al=0;
        AppData data = ((AppData) applicationTemplate.getDataComponent());
        int counter = 0;
        for (String name : data.label_no_duplicate) {
            if (!name.equals("null")) {
                counter++;
            }
        }
        //System.out.print(counter);
        if (counter == 2) {
            ((AppUI) applicationTemplate.getUIComponent()).buttonPane.getChildren().clear();
            ToggleGroup algorithms = new ToggleGroup();
            RadioButton al_a = new RadioButton(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name())+
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_A.name()));
            al_a.setToggleGroup(algorithms);
            al_a.setTranslateX(20);
            al_a.setTranslateY(20);
            al_a.setUserData(0);

            RadioButton al_b = new RadioButton(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name())+
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_B.name()));
            al_b.setToggleGroup(algorithms);
            al_b.setTranslateX(20);
            al_b.setTranslateY(50);
            al_b.setUserData(1);

            RadioButton al_c = new RadioButton(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLASSIFICATION.name())+
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_C.name()));
            al_c.setToggleGroup(algorithms);
            al_c.setTranslateX(20);
            al_c.setTranslateY(80);
            al_c.setUserData(2);

            Button settings_a = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_a.setOnAction(e->handleSettingsClassification(0));
            settings_a.setTranslateX(150);
            settings_a.setTranslateY(20);
            Button settings_b = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_b.setOnAction(e->handleSettingsClassification(1));
            settings_b.setTranslateX(150);
            settings_b.setTranslateY(50);
            Button settings_c = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_c.setOnAction(e->handleSettingsClassification(2));
            settings_c.setTranslateX(150);
            settings_c.setTranslateY(80);

            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setVisible(true);


            algorithms.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle){

                    if(algorithms.getSelectedToggle()!=null){
                        selected=((int)algorithms.getSelectedToggle().getUserData());
                        if(max_iterations.get(selected)>0
                                &&update_interval.get(selected)>0){
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setOnAction(e->run_Classification());
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(false);
                        }
                        else{((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);}
                    }
                }
            });

            ((AppUI) applicationTemplate.getUIComponent()).buttonPane.getChildren()
                    .addAll(al_a, al_b, al_c, settings_a, settings_b, settings_c,
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton);

        }
        else{
            return;
        }
    }

    public void handleClustering() {
        //selected_al=1;
        AppData data = ((AppData) applicationTemplate.getDataComponent());


            ((AppUI) applicationTemplate.getUIComponent()).buttonPane.getChildren().clear();
            ToggleGroup algorithms = new ToggleGroup();
            RadioButton al_a = new RadioButton(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RAND_CLUST.name()));
               //     applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name())+
                 //   applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_A.name()));
            al_a.setToggleGroup(algorithms);
            al_a.setTranslateX(20);
            al_a.setTranslateY(20);
            al_a.setUserData(3);

            RadioButton al_b = new RadioButton(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.KMEANS.name()));
                 //   applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name())+
                   // applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_B.name()));
            al_b.setToggleGroup(algorithms);
            al_b.setTranslateX(20);
            al_b.setTranslateY(50);
            al_b.setUserData(4);

            RadioButton al_c = new RadioButton(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERING.name())+
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.ALGORITHM_C.name()));
            al_c.setToggleGroup(algorithms);
            al_c.setTranslateX(20);
            al_c.setTranslateY(80);
            al_c.setUserData(5);

            Button settings_a = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_a.setOnAction(e->handleSettingsClustering(3));
            settings_a.setTranslateX(150);
            settings_a.setTranslateY(20);
            Button settings_b = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_b.setOnAction(e->handleSettingsClustering(4));
            settings_b.setTranslateX(150);
            settings_b.setTranslateY(50);
            Button settings_c = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.SETTINGS.name()));
            settings_c.setOnAction(e->handleSettingsClustering(5));
            settings_c.setTranslateX(150);
            settings_c.setTranslateY(80);

            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setVisible(true);

            algorithms.selectedToggleProperty().addListener(new ChangeListener<Toggle>(){
                public void changed(ObservableValue<? extends Toggle> ov, Toggle old_toggle, Toggle new_toggle){

                    if(algorithms.getSelectedToggle()!=null){
                        selected=((int)algorithms.getSelectedToggle().getUserData());
                        //System.out.print(max_iterations.get(selected));
                        if(max_iterations.get(selected)>0
                                &&update_interval.get(selected)>0){
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setOnAction(e-> {
                                try {
                                    run_Cluster(selected);
                                } catch (ClassNotFoundException e1) {
                                    e1.printStackTrace();
                                } catch (IllegalAccessException e1) {
                                    e1.printStackTrace();
                                } catch (InvocationTargetException e1) {
                                    e1.printStackTrace();
                                } catch (InstantiationException e1) {
                                    e1.printStackTrace();
                                }
                            });
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(false);
                        }
                        else{((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);}
                    }
                }
            });


            ((AppUI) applicationTemplate.getUIComponent()).buttonPane.getChildren()
                    .addAll(al_a, al_b, al_c, settings_a, settings_b, settings_c,
                            ((AppUI)applicationTemplate.getUIComponent()).runbutton);



    }

    public void handleSettingsClassification(int selection){

        //selected_class=selection;

        Stage config_window = new Stage();

        HBox configbox = new HBox();
        VBox left = new VBox();
        VBox right = new VBox();
        right.setTranslateX(20);
        TextField maxit = new TextField();
        maxit.setPromptText(max_iterations.get(selection)+"");
        TextField updateint = new TextField();
        updateint.setPromptText(update_interval.get(selection)+"");
        CheckBox  checkBox = new CheckBox();
        checkBox.setSelected(continuous_run.get(selection));

        Button submit = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name()));
        submit.setOnAction(e-> {
                try {
            if (maxit.getText().isEmpty() || updateint.getText().isEmpty()) {
                if(maxit.getText().isEmpty()&&!updateint.getText().isEmpty()){
                    update_interval.set(selection, Integer.valueOf(updateint.getText()));}
                if(updateint.getText().isEmpty()&&!maxit.getText().isEmpty()){
                    max_iterations.set(selection, Integer.valueOf(maxit.getText()));}
                continuous_run.set(selection, checkBox.isSelected());
                config_window.close();
                //return;
            }

            if (!(Integer.valueOf(maxit.getText()) < 1 || Integer.valueOf(updateint.getText()) < 1)) {
                if(Integer.valueOf(updateint.getText())<=Integer.valueOf(maxit.getText())){
                    max_iterations.set(selection, Integer.valueOf(maxit.getText()));
                    update_interval.set(selection, Integer.valueOf(updateint.getText()));
                    continuous_run.set(selection, checkBox.isSelected());
                    config_window.close();
                }

            }

                  }
                  catch(NumberFormatException n){
                    max_iterations.set(selection,1);
                    update_interval.set(selection,1);
                    continuous_run.set(selection,checkBox.isSelected());
                    config_window.close();
                }



        });


        Label maxit_label = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.MAXIMUM_IT.name()));
        Label update_int = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.UPDATE_INT.name()));
        Label continuous_run = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONT_RUN.name()));


        left.getChildren().addAll(maxit_label,update_int,continuous_run,submit);
        left.setSpacing(20);
        right.getChildren().addAll(maxit,updateint,checkBox);
        right.setSpacing(10);

        configbox.getChildren().addAll(left,right);

        Scene config_scene = new Scene(configbox);

        config_window.setScene(config_scene);

        config_window.show();


    }

    public void handleSettingsClustering(int selection){

        //selected_clustering = selection;

        Stage config_window = new Stage();

        HBox configbox = new HBox();
        VBox left = new VBox();
        VBox right = new VBox();
        right.setTranslateX(20);
        TextField maxit = new TextField();
        maxit.setPromptText(max_iterations.get(selection)+"");
        TextField updateint = new TextField();
        updateint.setPromptText(update_interval.get(selection)+"");
        TextField clusters = new TextField();
        clusters.setPromptText(number_clusters.get(selection)+"");
        CheckBox  checkBox = new CheckBox();
        checkBox.setSelected(continuous_run.get(selection));

        Button submit = new Button(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.DONE.name()));
        submit.setOnAction(e-> {try {
            if (maxit.getText().isEmpty() || updateint.getText().isEmpty()) {
                if(maxit.getText().isEmpty()&&!updateint.getText().isEmpty()){
                    update_interval.set(selection, Integer.valueOf(updateint.getText()));}
                if(updateint.getText().isEmpty()&&!maxit.getText().isEmpty()){
                    max_iterations.set(selection, Integer.valueOf(maxit.getText()));}
                continuous_run.set(selection, checkBox.isSelected());
                number_clusters.set(selection,Integer.valueOf(clusters.getText()));
                config_window.close();
                //return;
            }

            if (!(Integer.valueOf(maxit.getText()) < 1 || Integer.valueOf(updateint.getText()) < 1)) {
                if(Integer.valueOf(updateint.getText())<=Integer.valueOf(maxit.getText())){
                    max_iterations.set(selection, Integer.valueOf(maxit.getText()));
                    update_interval.set(selection, Integer.valueOf(updateint.getText()));
                    continuous_run.set(selection, checkBox.isSelected());
                    number_clusters.set(selection,Integer.valueOf(clusters.getText()));
                    config_window.close();
                }

            }

        }
        catch(NumberFormatException n){
            max_iterations.set(selection,1);
            update_interval.set(selection,1);
            continuous_run.set(selection,checkBox.isSelected());
            number_clusters.set(selection,1);
            config_window.close();
        }


    });


        Label maxit_label = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.MAXIMUM_IT.name()));
        Label update_int = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.UPDATE_INT.name()));
        Label continuous_run = new Label(applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CONT_RUN.name()));
        Label clusters_label = new Label(
                applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUSTERS.name())
        );


        left.getChildren().addAll(maxit_label,update_int,clusters_label,continuous_run,submit);
        left.setSpacing(20);
        right.getChildren().addAll(maxit,updateint,clusters, checkBox);
        right.setSpacing(10);

        configbox.getChildren().addAll(left,right);

        Scene config_scene = new Scene(configbox);

        config_window.setScene(config_scene);

        config_window.show();


    }

    public void run_Classification(){

        //System.out.println(selected + " " + selected_al);

        alg_running = true;
        ((AppUI)applicationTemplate.getUIComponent()).scrnshot(true);
        ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(true);
        ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
        )).setText("");
        DataSet dataset = ((AppData)applicationTemplate.getDataComponent()).dataSet;

        RandomClassifier randomClassifier = new RandomClassifier(applicationTemplate,dataset,
                max_iterations.get(selected),update_interval.get(selected),continuous_run.get(selected));



        if(!continuous_run.get(selected)){
            randomClassifier.run();
            ((AppData)applicationTemplate.getDataComponent()).displayData();
            ((AppData)applicationTemplate.getDataComponent()).displayClassification(randomClassifier.getOutput());
            if(counter>max_iterations.get(selected)){
                ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                counter=1;


                ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
            }
            alg_running=false;
            return;
        }

        Task task = new Task(){

            int i;

            @Override
            protected Void call() throws Exception {

                for(int j=0;j<max_iterations.get(selected);j++) {
                    i = j;
                    if (i % update_interval.get(selected) == 0) {
                        System.out.printf("Iteration number %d: ", i); //
                        // flush();
                        System.out.println();
                    }

                    Platform.runLater(randomClassifier);
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ie) {
                        ie.printStackTrace();
                    }
                }
                alg_running=false;
                ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(false);
                ((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
                //((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                    return null;
                }};

        Thread thread = new Thread(task);
        thread.start();


    }

    public void run_Cluster(int selected) throws ClassNotFoundException, IllegalAccessException, InvocationTargetException, InstantiationException {
        //System.out.println(selected + " " + selected_al);



        alg_running = true;
        ((AppUI)applicationTemplate.getUIComponent()).scrnshot(true);
        ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(true);
        ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
        )).setText("");
        DataSet dataset = ((AppData)applicationTemplate.getDataComponent()).dataSet;

        if(selected==3){



            //RandomClusterer randomClusterer = new RandomClusterer(applicationTemplate,dataset,
              //      max_iterations.get(selected),update_interval.get(selected),continuous_run.get(selected),
                //    number_clusters.get(selected));

            Class<?>    klass       = Class.forName(
                   applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUS.name())+
                   applicationTemplate.manager.getPropertyValue(AppPropertyTypes.RAND_CLUST.name())
            );


            Constructor konstructor = klass.getConstructors()[0];

            RandomClusterer rand =(RandomClusterer) konstructor.newInstance(applicationTemplate,dataset,
                    max_iterations.get(selected),update_interval.get(selected),continuous_run.get(selected),
                    number_clusters.get(selected));

            DataSet ds = dataset;

            if(!continuous_run.get(selected)){
                //randomClusterer.run();
                    rand.run();
                if(counter>max_iterations.get(selected)){
                    ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                    counter=1;

                    try {
                        dataset=DataSet.fromTextArea(((AppData)applicationTemplate.getDataComponent()).lines);
                        System.out.println(dataset.getLabels());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
                }
                alg_running=false;
                return;
            }

            Task task = new Task(){
                DataSet d =ds;
                int i;

                @Override
                protected Void call() throws Exception {

                    for(int j=0;j<max_iterations.get(selected);j++) {
                        i = j;
                        alg_running=true;
                        if (i % update_interval.get(selected) == 0) {
                            System.out.printf("Iteration number %d: ", i); //
                            // flush();
                            System.out.println();
                        }

                        //Platform.runLater(randomClusterer);
                        Platform.runLater(rand);
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            ie.printStackTrace();
                        }
                    }
                    try {
                        d=DataSet.fromTextArea(((AppData)applicationTemplate.getDataComponent()).lines);
                        //System.out.println(d.getLabels());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
                    ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(false);
                    ((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
                    //((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                    return null;
                }};

            Thread thread = new Thread(task);
            thread.start();

        }
        if(selected==4){
            ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
            Class<?>    klass       = Class.forName(
                    applicationTemplate.manager.getPropertyValue(AppPropertyTypes.CLUS.name())+
                            applicationTemplate.manager.getPropertyValue(AppPropertyTypes.KMEANS.name())
            );
            Constructor konstructor = klass.getConstructors()[0];

            KMeansClusterer kMeansClusterer =(KMeansClusterer) konstructor.newInstance(
                    dataset, max_iterations.get(selected), update_interval.get(selected),
                    number_clusters.get(selected),applicationTemplate);



            if(continuous_run.get(selected)) {

                DataSet ds = dataset;
                Task task = new Task(){

                    int i;
                    DataSet d =ds;
                    @Override
                    protected Void call() throws Exception {

                        for(int j=0;j<max_iterations.get(selected);j++) {
                            i = j;
                            alg_running=true;
                            if (i % update_interval.get(selected) == 0) {
                                System.out.printf("Iteration number %d: ", i); //
                                // flush();
                                System.out.println(alg_running);
                                System.out.println();
                            }

                            //Platform.runLater(randomClusterer);
                            Platform.runLater(kMeansClusterer);
                            try {
                                Thread.sleep(1000);
                            } catch (InterruptedException ie) {
                                ie.printStackTrace();
                            }
                        }


                        try {
                            d=DataSet.fromTextArea(((AppData)applicationTemplate.getDataComponent()).lines);
                            //System.out.println(d.getLabels());
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
                        ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(false);
                        ((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
                        //((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                        return null;
                    }};

                Thread thread = new Thread(task);
                thread.start();

                //System.out.print(dataset.getLabels());

            }
            else{
                kMeansClusterer.cont=continuous_run.get(selected);
                kMeansClusterer.run();
                System.out.println("Counter:"+counter);
                if(counter>max_iterations.get(selected)){
                    ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);
                    counter=1;

                    try {
                        dataset=DataSet.fromTextArea(((AppData)applicationTemplate.getDataComponent()).lines);
                        System.out.println(dataset.getLabels());
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                    ((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
                }

                alg_running=false;

                return;
            }



            //kMeansClusterer.run();
            //((AppData)applicationTemplate.getDataComponent()).displayClustering(dataset);
        }
        alg_running=false;

    }

}






























