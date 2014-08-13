package patterndocrepre;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.ListIterator;
import java.util.Vector;
import java.util.logging.Level;
import java.util.logging.Logger;
/*
 * @author n8107939
 */

public class PatternDocRepre {

    private static int SubTopicNumber = 20;
    //   private static int DocumentNumber = 23;// the document number 
    // combine associate rule table and stemmer
    //    public static void main (String [] args) throws IOException{
    private Vector WordsTopicInDoc;

    public void PatternMatr(ArrayList pnList, String statepath, String PatternPath) throws FileNotFoundException, IOException {
        Stemmer s = new Stemmer();
        String text = null;
        String[] SplitText = null;
        String word = null;
        String docCandidate;
        int topicIndex, documentIndex;
        int Number = pnList.size();
        String PatternMatrixPath = PatternPath + "\\";
        FileReader fr = new FileReader(statepath);// read result from LDA state
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        br.readLine();
        br.readLine();

        int t = 0;
        while (br.ready()) {
            if (t % 10000 == 0) {
                System.out.println(new SimpleDateFormat("hh:mm:ss").format(new Date()) + " Step 2/3: " + t);
            }
            t++;

            text = br.readLine();
            SplitText = text.split(" ");
            word = SplitText[4];
            docCandidate = SplitText[1].substring(SplitText[1].lastIndexOf("\\") + 1, SplitText[1].indexOf(".txt"));
            //System.out.println(docCandidate);
            if (pnList.contains(docCandidate)) {
                topicIndex = Integer.parseInt(SplitText[5]);            //convert string to integer
                documentIndex = pnList.indexOf(docCandidate);
                for (int i = 0; i < word.length(); i++) {
                    char ch = word.charAt(i);                   // convert string to character,()in it, the number is the index;
                    ch = Character.toLowerCase((char) ch);
                    s.add(ch);
                }
                s.stem();
                word = s.toString();// stem finished
                //System.out.println(word);
                ((Vector) ((Vector) WordsTopicInDoc.get(topicIndex)).get(documentIndex)).add(word);
            }

        }
        br.close();

        t = 0;
        int k;
        PrintWriter writer = null;

        while (t < SubTopicNumber) {
            File f = new File(PatternMatrixPath + "Topic" + t + ".names");
            System.out.println(new SimpleDateFormat("hh:mm:ss").format(new Date()) + " Step 3/3: " + (t + 1) + "/" + SubTopicNumber);
            if (f.exists()) {
                System.out.println("    already exists");
            } else {
                //  WordNumber = ((Vector) WordsTopicInDoc.get(t)).size();
                String[] temp = new String[1000000];

                for (int i = 0; i < 1000000; i++) {
                    temp[i] = "lol";

                }
                k = 0;
                for (int z = 0; z < Number; z++) {
                    ListIterator iter1 = ((Vector) ((Vector) WordsTopicInDoc.get(t)).get(z)).listIterator();

                    while (iter1.hasNext()) {
                        temp[k] = (String) iter1.next();
                        k++;
                    }
                }

                for (int i = 0; i < k; i++) {
                    for (int j = 0; j < k; j++) {
                        if (temp[i] != " " && i != j) {
                            if (temp[i].equals(temp[j])) {
                                temp[j] = " ";
                            }
                        }
                    }
                }

                ArrayList<String> list = new ArrayList<String>(Arrays.asList(temp));
                list.removeAll(Arrays.asList(" "));
                list.removeAll(Arrays.asList("lol"));
                temp = list.toArray(new String[list.size()]);
                int b = list.size();
                String[][] DecisionMatrix = new String[Number][b];
                for (int i = 0; i < DecisionMatrix.length; i++) {
                    for (int j = 0; j < DecisionMatrix[i].length; j++) {
                        DecisionMatrix[i][j] = "0";
                    }
                }
                for (int i = 0; i < Number; i++) {
                    ListIterator iter2 = ((Vector) ((Vector) WordsTopicInDoc.get(t)).get(i)).listIterator();
                    while (iter2.hasNext()) {
                        String CompareStr = (String) iter2.next();
                        for (int j = 0; j < b; j++) {
                            if (temp[j].equals(CompareStr)) {
                                DecisionMatrix[i][j] = "1";
                                break;
                            }
                        }
                    }
                }

                // remove the low frequency words and non-occure words.//

                //for (int i = 0; i < Number; i += 1000) {
                //    System.out.print("-");
                //}
                //System.out.print("\n");

                //end remove
                int count;
                writer = new PrintWriter(PatternMatrixPath + "Topic" + t + ".data");
                for (int i = 0; i < Number; i++) {                  //Start to write files
                    //if (i % 1000 == 0) {
                    //    System.out.print("=");
                    //}
                    count = 0;
                    for (int j = 0; j < b - 1; j++) {
                        if (DecisionMatrix[i][j] == "1") {
                            count++;
                        }
                    }
                    for (int j = 0; j < b - 1; j++) {
                        if (count > 1) {
                            //writeStringtoTxt(DecisionMatrix[i][j] + ",", "Topic" + t + ".data", PatternMatrixPath);
                            writer.print(DecisionMatrix[i][j] + ",");
                        }
                    }
                    if (count > 1) {
                        //writeStringtoTxt(DecisionMatrix[i][b - 1] + ".\r\n", "Topic" + t + ".data", PatternMatrixPath);
                        writer.print(DecisionMatrix[i][b - 1] + ".\r\n");
                    }
                }
                writer.close();
                writer = new PrintWriter(PatternMatrixPath + "Topic" + t + ".names");
                //int a = temp.length;
                //writeStringtoTxt("|\r\n|\r\n|\r\n\r\n", "Topic" + t + ".names", PatternMatrixPath);
                writer.print("|\r\n|\r\n|\r\n\r\n");
                for (int i = 0; i < b; i++) {
                    //writeStringtoTxt(temp[i] + ":\t0,1\r\n", "Topic" + t + ".names", PatternMatrixPath);
                    writer.print(temp[i] + ":\t0,1\r\n");
                }
                writer.close();
                //System.out.print("\n");
            }
            t++;
        }
    }

    /*    private void writeStringtoTxt(String text, String filename, String path) {
     PrintWriter writer = null;
     try {
     //writer = new PrintWriter(path + filename, "UTF-8");
     writer = new PrintWriter(new BufferedWriter(new FileWriter(path + filename, true)));
     writer.print(text);
     writer.close();
     } catch (Exception ex) {
     Logger.getLogger(PatternDocRepre.class.getName()).log(Level.SEVERE, null, ex);
     } finally {
     writer.close();
     }
     }
     */
    // statepath 	path to extracted file from .gz
    // poslist		names of all the text files imported to mallet
    // PatternPath	
    public void pattern(String statepath, ArrayList poslist, String PatternPath) throws FileNotFoundException, IOException {

        Stemmer s = new Stemmer();
        String text = null;
        String[] SplitText = null;
        String word = null;
        String docCandidate;
        int topicIndex, documentIndex;
        int posNumber = poslist.size();// should choose the positive document in another step
        int negNumber = 0;//neglist.size();

        FileReader fr = new FileReader(statepath);// read result from LDA state
        BufferedReader br = new BufferedReader(fr);
        br.readLine();
        br.readLine();
        br.readLine();

        WordsTopicInDoc = new Vector();       // Initialize 3 dimensional vector!
        for (int i = 0; i < SubTopicNumber; i++) {
            //if (i % 20 == 0) {
            System.out.println(new SimpleDateFormat("hh:mm:ss").format(new Date()) + " Step 1/3: " + (i + 1) + "/" + SubTopicNumber);
            //}
            WordsTopicInDoc.add(new Vector());

            for (int j = 0; j < posNumber; j++) {// change to posList and negList
                ((Vector) WordsTopicInDoc.get(i)).add(new Vector());
            }

        }

        PatternMatr(poslist, statepath, PatternPath);// 1 is positive pattern, 0 is for negative
    }
}
