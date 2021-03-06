

package classification;

import actions.AppActions;
import algorithms.Classifier;
import data.DataSet;
import dataprocessors.AppData;
import javafx.application.Platform;
import javafx.scene.control.Label;
import ui.AppUI;
import vilij.templates.ApplicationTemplate;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * @author Ritwik Banerjee
 */
public class RandomClassifier extends Classifier {

    private static final Random RAND = new Random();

    @SuppressWarnings("FieldCanBeLocal")
    // this mock classifier doesn't actually use the data, but a real classifier will
    private DataSet dataset;

    private final int maxIterations;
    private final int updateInterval;
    //private static ApplicationTemplate applicationTemplate;


    // currently, this value does not change after instantiation
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

    public RandomClassifier(ApplicationTemplate applicationTemplate,DataSet dataset,
                            int maxIterations,
                            int updateInterval,
                            boolean tocontinue) {
        this.dataset = dataset;
        this.maxIterations = maxIterations;
        this.updateInterval = updateInterval;
        this.tocontinue = new AtomicBoolean(tocontinue);
        //this.applicationTemplate = applicationTemplate;
        this.applicationTemplate=applicationTemplate;
    }

    @Override
    public void run() {

        int a =AppActions.counter;

        if(!tocontinue()&& a<=maxIterations ) {
           // AppActions.alg_running=true;
            //System.out.print(a);
            int xCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 100).intValue();
            int constant     = new Double(RAND.nextDouble() * 100).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
            //((AppData)applicationTemplate.getDataComponent()).displayData();
            ((AppData)applicationTemplate.getDataComponent()).displayData();
            ((AppData)applicationTemplate.getDataComponent()).displayClassification(output);


            // everything below is just for internal viewing of how the output is changing
            // in the final project, such changes will be dynamically visible in the UI
            while (a % updateInterval !=0){
                a++;
                AppActions.counter++;

            }

            if (a % updateInterval == 0) {
                System.out.printf("Iteration number %d: ", a);
                ((Label)((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().get(
                        ((AppUI)applicationTemplate.getUIComponent()).itPane.getChildren().size()-1
                )).setText("Iteration number: "+a+"\nPlease click run button to continue.");
                //flush();
            }
            //if (a > maxIterations * .6 && RAND.nextDouble() < 0.05) {
              //  System.out.printf("Iteration number %d: ", a);
                //flush();
                //return;
            //}

            AppActions.counter++;
            //((AppUI)applicationTemplate.getUIComponent()).readonlybutton.setDisable(false);
            ((AppUI)applicationTemplate.getUIComponent()).scrnshot(false);
            return;
        }
        //AppActions.counter=1;

        //for (int i = 1; i <= maxIterations && tocontinue(); i++) {
        ((AppUI)applicationTemplate.getUIComponent()).runbutton.setDisable(true);

            int xCoefficient = new Double(RAND.nextDouble() * 10).intValue();
            int yCoefficient = new Double(RAND.nextDouble() * 10).intValue();
            int constant     = new Double(RAND.nextDouble() * 10).intValue();

            // this is the real output of the classifier
            output = Arrays.asList(xCoefficient, yCoefficient, constant);
            //((AppData)applicationTemplate.getDataComponent()).displayData();
            ((AppData)applicationTemplate.getDataComponent()).displayData();
            ((AppData)applicationTemplate.getDataComponent()).displayClassification(output);

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

    // for internal viewing only
    protected void flush() {
        System.out.printf("%d\t%d\t%d%n", output.get(0), output.get(1), output.get(2));
    }

    /** A placeholder main method to just make sure this code runs smoothly */
    public static void main(String... args) throws IOException {


        //ArrayList<String> l = ((AppData)applicationTemplate.getDataComponent()).lines;

       // DataSet dataset = DataSet.fromTextArea(l);
        //DataSet          dataset    = DataSet.fromTSDFile(Paths.get("/path/to/some-data.tsd"));
       // RandomClassifier classifier = new RandomClassifier(dataset, 100, 5, true);
        //classifier.run(); // no multithreading yet
    }
}