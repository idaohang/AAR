/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package readindat;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

/**
 *
 * @author Clarky
 */
public class ReadInDat {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        try {
            readData("user_ratedmovies-timestamps.dat");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Read in data from file, split on tab character into array and print all as independent pieces
     *
     * @param location The location of the file to be read
     * @throws java.io.IOException
     */
    public static void readData(String location) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(location));
        String line;
        
        while ((line = reader.readLine()) != null) {
            String[] parts = line.split("\t");

            for (int i = 0; i < parts.length; i++) {
                System.out.print(i + ":" + parts[i] + " ");
                if (i == parts.length - 1) {
                    System.out.println();
                }
            }
        }
    }
}
