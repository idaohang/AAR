/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package patterndocrepre;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * 1: path to txt files
 * 2: path to ungzipped txt file
 * 3: path to output folder
 * @author Admin
 */
public class Main {
    public static void main(String[] args) {
        ArrayList<String> posList = new ArrayList<String>();
        
        File file = new File(args[0]);
        File[] files = file.listFiles();
        for (int i = 0; i < files.length; i++) {
            //System.out.println(files[i].toString());
            //posList.add(files[i].toString());
            posList.add(files[i].getName().replace(".txt",""));
        }
        
        PatternDocRepre repre = new PatternDocRepre();
        
        try {
            repre.pattern(args[1], posList, args[2]);
        } catch (Exception ex) {
            Logger.getLogger(PatternDocRepre.class.getName()).log(Level.SEVERE, null, ex);
        }
        //*/
    }
    
}
