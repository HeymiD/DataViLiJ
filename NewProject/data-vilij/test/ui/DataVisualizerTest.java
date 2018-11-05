package ui;

import dataprocessors.AppData;
import dataprocessors.TSDProcessor;
import javafx.geometry.Point2D;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import org.junit.Assert;
import org.junit.Test;
import vilij.components.Dialog;
import vilij.components.ErrorDialog;
import vilij.propertymanager.PropertyManager;
import vilij.templates.ApplicationTemplate;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.stream.Stream;

import static org.junit.Assert.*;
import static settings.AppPropertyTypes.*;
import static settings.AppPropertyTypes.RESOURCE_SUBDIR_NOT_FOUND;

public class DataVisualizerTest {

    @Test
    public void oneLineTSDValid() {
        String TSDline = "@a\ta\t1,2";
        TSDProcessor processor = new TSDProcessor();

         Map<String, String> dl=new HashMap<>();
         Map<String, Point2D> dp=new HashMap<>();
         dl.put("@a","a");
         dp.put("@a",new Point2D(Double.parseDouble("1"), Double.parseDouble("2")));

        try {
            processor.processString(TSDline);
        } catch (Exception e) {
            e.printStackTrace();
        }

        Assert.assertEquals(dp,processor.dataPoints);
        Assert.assertEquals(dl,processor.dataLabels);

    }

    @Test(expected = Exception.class)
    public void oneLineTSDInValidName() throws Exception {
        String TSDline = "a\ta\t1,2";
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);

    }

    @Test
    public void oneLineTSDEmptyLabel() throws Exception {
        String TSDline = "@a\t\t1,2";
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);
        Map<String, String> dl=new HashMap<>();
        Map<String, Point2D> dp=new HashMap<>();
        dl.put("@a","");
        dp.put("@a",new Point2D(Double.parseDouble("1"), Double.parseDouble("2")));
        Assert.assertEquals(dl,processor.dataLabels);
        Assert.assertEquals(dp,processor.dataPoints);

    }

    @Test(expected = Exception.class)
    public void oneLineTSDInValidFormat() throws Exception {
        String TSDline = "aa1,2";
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);

    }
    @Test(expected = Exception.class)
    public void oneLineTSDNullData() throws Exception {
        String TSDline = "@a\ta\t";
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);

    }
    @Test(expected = Exception.class)
    public void oneLineTSDinValidData() throws Exception {
        String TSDline = "@a\ta\tasdadad";
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);

    }

    @Test(expected = Exception.class)
    public void oneLineTSDNullLine() throws Exception {
        String TSDline = null;
        TSDProcessor processor = new TSDProcessor();
        processor.processString(TSDline);

    }

    @Test(expected = Exception.class)
    public void saveDatatoNonExistantFilePath()throws Exception{
        Path unvalid = Paths.get("asdiadadalsdassd");
        save(unvalid,"aiojsjdaksdjlakd");

    }

    @Test
    public void saveDatatoExistantFilePath()throws Exception{


        Path valid = Paths.get("hw1/out/production/cse219homework/user_data");
        save(valid,"aiojsjdaksdjlakd");
        File f = new File(valid.toString()+"\\a.tsd");
        boolean exist = f.exists();
        Assert.assertTrue(exist);


    }
    @Test
    public void configTestNonNumericalInput(){
        Random rand = new Random();
        ArrayList<Integer> maxit = new ArrayList<>();
        ArrayList<Integer> upd = new ArrayList<>();
        ArrayList<Integer> nc = new ArrayList<>();
        for(int i=0;i<6;i++){maxit.add(0);upd.add(0);nc.add(0);}
        int select = rand.nextInt(6);

        run_config(maxit,upd,"asdas","askdad",select,nc,"asjdasdad");
        int def =1;
        Assert.assertTrue(maxit.get(select)==def);
        Assert.assertTrue(upd.get(select)==def);
        Assert.assertTrue(nc.get(select)==def);

    }

    @Test
    public void configTestNumericalInput(){
        Random rand = new Random();
        ArrayList<Integer> maxit = new ArrayList<>();
        ArrayList<Integer> upd = new ArrayList<>();
        ArrayList<Integer> nc = new ArrayList<>();
        for(int i=0;i<6;i++){maxit.add(0);upd.add(0);nc.add(0);}int select = rand.nextInt(6);

        run_config(maxit,upd,"5","4",select,nc,"23");
        int m =5;
        int u=4;
        Assert.assertTrue(maxit.get(select)==m);
        Assert.assertTrue(upd.get(select)==u);
        Assert.assertTrue(nc.get(select)==23);

    }

    @Test
    public void configTestNumericalInputwithUpdtBiggerMax(){
        Random rand = new Random();
        ArrayList<Integer> maxit = new ArrayList<>();
        ArrayList<Integer> upd = new ArrayList<>();
        ArrayList<Integer> nc = new ArrayList<>();
        for(int i=0;i<6;i++){maxit.add(0);upd.add(0);nc.add(0);}
        int select = rand.nextInt(6);

        run_config(maxit,upd,"2","4",select,nc,"7");
        int d =0;

        Assert.assertTrue(maxit.get(select)==d);
        Assert.assertTrue(upd.get(select)==d);
        Assert.assertTrue(nc.get(select)==d);

    }

    @Test
    public void configTestNumericalInputwithNegative(){
        Random rand = new Random();
        ArrayList<Integer> maxit = new ArrayList<>();
        ArrayList<Integer> upd = new ArrayList<>();
        ArrayList<Integer> nc = new ArrayList<>();
        for(int i=0;i<6;i++){maxit.add(0);upd.add(0);nc.add(0);}
        int select = rand.nextInt(6);

        run_config(maxit,upd,"-2","-4",select,nc,"-4");
        int d =0;

        Assert.assertTrue(maxit.get(select)==d);
        Assert.assertTrue(upd.get(select)==d);
        Assert.assertTrue(nc.get(select)==d);

    }







    public void save(Path dataFilePath,String txt)throws NoSuchFileException {

        ArrayList<String> lines = new ArrayList<>();
        String[] l = txt.split("\n");
        for(int i=0;i<l.length;i++){
            lines.add(l[i]);
        }

        if(!dataFilePath.toFile().isDirectory()){throw new NoSuchFileException(null);}

        //PropertyManager manager = PropertyManager.getManager();

        //FileChooser filechooser = new FileChooser();
        //filechooser.getExtensionFilters().addAll(
          //      new FileChooser.ExtensionFilter(manager.getPropertyValue(DATA_FILE_EXT_DESC.name()),
              //          "*" + manager.getPropertyValue(DATA_FILE_EXT.name())
            //    ));
        //URL resource = getClass().getResource("/" + manager.getPropertyValue(DATA_RESOURCE_PATH.name()));
        //File file = new File(resource.getFile());

        //if (file.isDirectory()) {
        //    filechooser.setInitialDirectory(file);
        //}

        //URL resource = getClass().getResource("/" + manager.getPropertyValue(DATA_RESOURCE_PATH.name()));

        File file = new File(String.valueOf(dataFilePath)+"/a.tsd");
        File file_to_save = file;

        if (file_to_save != null) {
            FileWriter fileWriter = null;
            try {
                fileWriter = new FileWriter(file_to_save);
            } catch (IOException e) {
                e.printStackTrace();
            }
            for(String line: lines) {
                try {

                    fileWriter.append(line+"\n");

                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            try {
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //try {
              //  dataFilePath = Paths.get(resource.toURI());
            //} catch (URISyntaxException e) {
                //Dialog urierror = applicationTemplate.getDialog(Dialog.DialogType.ERROR);
                //ErrorDialog u = ((ErrorDialog) urierror);
                //u.show(manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()),
                  //      manager.getPropertyValue(RESOURCE_SUBDIR_NOT_FOUND.name()));
            }
            //lastfile = file_to_save.toPath();
            //((AppUI) applicationTemplate.getUIComponent()).disableSaveButton(true);
        }

        public void run_config(ArrayList<Integer> max_iterations,ArrayList<Integer> update_interval
        ,String maxit,String updateint,int selection,ArrayList<Integer> number_clusters, String clusters){
            try {
                if (maxit.isEmpty() || updateint.isEmpty()) {
                    if(maxit.isEmpty()&&!updateint.isEmpty()){
                        update_interval.set(selection, Integer.valueOf(updateint));}
                    if(updateint.isEmpty()&&!maxit.isEmpty()){
                        max_iterations.set(selection, Integer.valueOf(maxit));}
                    number_clusters.set(selection,Integer.valueOf(clusters));
                        //continuous_run.set(selection, checkBox.isSelected());
                    //config_window.close();
                    //return;
                }

                if (!(Integer.valueOf(maxit) < 1 || Integer.valueOf(updateint) < 1)) {
                    if(Integer.valueOf(updateint)<=Integer.valueOf(maxit)){
                        max_iterations.set(selection, Integer.valueOf(maxit));
                        update_interval.set(selection, Integer.valueOf(updateint));
                        number_clusters.set(selection,Integer.valueOf(clusters));
                        //continuous_run.set(selection, checkBox.isSelected());
                       // config_window.close();
                    }

                }

            }
            catch(NumberFormatException n){
                max_iterations.set(selection,1);
                update_interval.set(selection,1);
                number_clusters.set(selection,1);
                //continuous_run.set(selection,checkBox.isSelected());
               // config_window.close();
            }
        }


    }










