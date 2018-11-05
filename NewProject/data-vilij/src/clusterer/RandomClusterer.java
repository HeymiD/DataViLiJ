package clusterer;

import actions.AppActions;
import algorithms.Clusterer;
import data.DataSet;
import dataprocessors.AppData;
import javafx.scene.control.Label;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.atomic.AtomicBoolean;

public class RandomClusterer extends Clusterer {
    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    private final AtomicBoolean tocontinue;
    private ApplicationTemplate applicationTemplate;

    @Override
    public int getMaxIterations() {
        return maxIterations;
    }

    @Override
    public int getUpdateInterval() {
        return updateInterval;
    }

    @Override
    public boolean tocontinue() {
        return tocontinue.get();
    }

    public RandomClusterer(ApplicationTemplate applicationTemplate,DataSet dataset,
                           int maxIterations,
                           int updateInterval,
                           boolean tocontinue,
                           int number_of_labels) {
        super(number_of_labels);
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        this.applicationTemplate=applicationTemplate;
    }

    @Override
    public void run() {

        int a =AppActions.counter;
        AppData appData = ((AppData)applicationTemplate.getDataComponent());

        if(!tocontinue()&& a<=maxIterations ) {


            for(String key:dataset.getLabels().keySet()){
                dataset.updateLabel(key,"Cluster"+RAND.nextInt(numberOfClusters));
            }


            ((AppData)applicationTemplate.getDataComponent()).displayClustering(dataset);




            while (a % updateInterval !=0){
                a++;
                AppActions.counter++;

            }

            if (a % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", a);
                ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                        ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
                )).setText("Iteration number: "+a+"\nPlease click run button to continue.");
                System.out.println();

            }

            AppActions.counter++;

            ((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
            return;
        }
        ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);


        for(String key:dataset.getLabels().keySet()){
            dataset.updateLabel(key,"Cluster"+RAND.nextInt(numberOfClusters));
        }

        ((AppData)applicationTemplate.getDataComponent()).displayClustering(dataset);



        //try {
        //  Thread.sleep(10);
        //} catch (InterruptedException ie) {
        //  ie.printStackTrace();
        //}
        // everything below is just for internal viewing of how the output is changing
        // in the final project, such changes will be dynamically visible in the UI
        // if (i % updateInterval == 0) {
        //   System.out.printf("Iteration number %d: ", i); //
        // flush();
        //}
        //if (i > maxIterations * .6 && RAND.nextDouble() < 0.05) {
        //  System.out.printf("Iteration number %d: ", i);
        //flush();
        //break;
        //}
        //}
    }



}
