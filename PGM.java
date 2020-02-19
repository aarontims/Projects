/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package imagetoonifier;

import java.util.Scanner;
import java.io.*;
/**
 *
 * @author aaron
 */
public class PGM {
    // instance fields
    private String imageName;
    private int width, height;
    private int maxValue;
    private int[][] image;
    // Default constructor for creating a simple checker PGM image of 2 x 3
    public PGM() {
    imageName = "Simple";
    width = 2;
    height = 3;
    maxValue = 255; // value for white in default/given file
    image = new int[height][width]; // note: height as row, width as column
    image[0][0] = image[1][1] = 255; // white dots
    image[0][1] = image[2][0] = 127; // gray dots
    image[1][0] = image[2][1] = 0; // white dots
   }
    // Constructor for creating a "mid-gray" PGM image of w x h
    // All pixels shall carry value of 127
    public PGM(String name, int w, int h) {
    /*** student's work here to construct a gray box ***/
    imageName = name;
    width = w;
    height = h;
    maxValue = 255;
    image = new int[height][width]; // initializing a new int array with size hieght x width
    for (int i = 0; i < height; i++) {
        for (int j = 0; j < width; j++) {
            image[i][j] = 127; // gray dots 
        }
    }
    }
    // Constructor for reading a PGM image file
    public PGM(String filename) {
        this.imageName = filename;
        read(filename);
    }
    public int getWidth() { return width; } // function to get pgm's width
    public int getHeight() { return height; } // function to get pgm's height
    public int[][] getImage() { return image; } // function to get pgm's image value
    public int getMaxValue() { return maxValue; } // function to get pgm's max value
    public void read(String filename) {
        try {
            File f = new File(filename);
            Scanner reader = new Scanner(f);
            String header = reader.nextLine();
            
            if (header == null || header.length() < 2 || header.charAt(0) != 'P' || header.charAt(1) != '2') {
            throw new Exception("Wrong PGM header!");
            }
            
            do { // skip lines start with '#' (if any)
                header = reader.nextLine();
            } while (header.charAt(0) == '#');
        
            // get from last line instead of file
             Scanner readStr = new Scanner(header);
             width = readStr.nextInt();
             height = readStr.nextInt();
            // get the rest from file
             maxValue = reader.nextInt();
             /*** student's work here to read the image ***/
            image = new int[height][width]; // setting up the new int array
            int i = 0;
            int j = 0;
            String line;
            while(reader.hasNextLine()) { // while there is next line in the file
                line = reader.nextLine();
                Scanner readInt = new Scanner(line); 
                while(readInt.hasNextInt()) { // put the integer one by one to the file from each line
                    image[i][j] = readInt.nextInt();
                    if(j<width-1) // if the number is still within width range, keep adding horizontally
                        j++;
                    else { // if it exceeds, go to new line and reset to width = 0
                        j = 0;
                        i++;
                    }
                }
            }
            // catch the error and print the type of error
        }   catch (Exception e){
            /*** student's work here to handle exception(s) ***/
            System.out.println("Something error, error type is: " + e);
        }
    }
    // sorting the list and find the median value
    public static int sortMedian(int temp[]) {
        // the sorting algorithm
        for(int i = 0; i<temp.length-1; i++) {
            for(int j = i+1; j<temp.length; j++) {
                if(temp[j]<temp[i]) {
                    int swap = temp[i];
                    temp[i] = temp[j];
                    temp[j]= swap;
                }
            }
        }
        // finding the median
        int median = temp[(temp.length-1)/2];
        return median;
    }
    // function to do box blue filter
    public static double boxBlurFilter(int temp[]) {
        double total = 0;
        for(int i = 0; i < temp.length; i++) {
            total += temp[i];
        }
        // getting the average value (total divide by length)
        return (total/temp.length);
    }
    // function to do laplace filter
    public static int laplaceFilter(double temp[]) {
        double total = 0;
        // all values multiply by -1 except for the middle one, which would be multiplied by 8
        for(int i = 0; i < temp.length; i++) {
            if(i==4)
                total+=temp[i]*8;
            else
                total-=temp[i];
        }
        // return it in integer form
        return (int) Math.round(total);
    }
    // creating the palette
    public PGM palette() {
        PGM newPGM = new PGM();
        // make a new PGM and copy all the values (so it does not change the object value)
        newPGM.height = height;
        newPGM.imageName = "palette";
        newPGM.image = new int[height][width];
        newPGM.width = width;
        newPGM.maxValue = maxValue;
        // applying 3x3 filter to each value
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                int temp[] = new int[9]; // getting the 3x3 values surrounding it
                int z = 0;
                for(int k = 0; k < 3; k++) {
                    for(int l = 0; l < 3; l++) {
                        // if it exceeds the array, set value as 0
                        if(i+k-1<0 || i+k-1>=height || j+l-1<0 || j+l-1>=width) {
                            temp[z]=0;
                            z++;
                        }
                        else {
                            temp[z]=image[i+k-1][j+l-1];
                            z++;
                        }
                    }
                }
                // find the median for each value
                newPGM.image[i][j] = sortMedian(temp);
            }
        }
        return newPGM;
    }
    public PGM edge() {
        PGM edgePGM = new PGM();
        // make a new PGM and copy all the values (so it does not change the object value)
        edgePGM.height = height;
        edgePGM.imageName = "edge";
        edgePGM.image = new int[height][width];
        edgePGM.width = width;
        edgePGM.maxValue = maxValue;
        // the intermediate image array to store values after box blur filter
        double intermediateImage[][] = new double[height][width];
        // applying 3x3 filter to each value
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                int temp[] = new int[9]; // getting the 3x3 values surrounding it
                int z = 0;
                for(int k = 0; k < 3; k++) {
                    for(int l = 0; l < 3; l++) {
                        // if it exceeds the array, set value as 0
                        if(i+k-1<0 || i+k-1>=height || j+l-1<0 || j+l-1>=width) {
                            temp[z]=0;
                            z++;
                        }
                        else {
                            temp[z]=image[i+k-1][j+l-1];
                            z++;
                        }
                    }
                }
                // applying the box blur filter
                intermediateImage[i][j] = boxBlurFilter(temp);
            }
        }
        // applying 3x3 filter to each value
        for(int i = 0; i < height; i++) {
            for(int j = 0; j < width; j++) {
                double temp[] = new double[9]; // getting the 3x3 values surrounding it
                int z = 0;
                for(int k = 0; k < 3; k++) {
                    for(int l = 0; l < 3; l++) {
                        // if it exceeds the array, set value as 0
                        if(i+k-1<0 || i+k-1>=height || j+l-1<0 || j+l-1>=width) {
                            temp[z]=0; 
                            z++;
                        }
                        else {
                            temp[z]=intermediateImage[i+k-1][j+l-1];
                            z++;
                        }
                    }
                }
                // applying the laplace filter
                edgePGM.image[i][j] = laplaceFilter(temp);
                // clipping the value if it exceeds max value or less than 0
                if(edgePGM.image[i][j] > edgePGM.maxValue)
                    edgePGM.image[i][j] = edgePGM.maxValue;
                else if(edgePGM.image[i][j] < 0)
                    edgePGM.image[i][j] = 0;
                edgePGM.image[i][j]=Math.abs(edgePGM.image[i][j]-edgePGM.maxValue);
            }
        }
        return edgePGM;
    }
    // function to save the picture
    public void write(String filename) throws IOException {
    /*** student's work here to write the PGM image ***/
      PrintStream notepad = new PrintStream(filename + ".pgm"); // new PrintStream output on the filename in pgm format
      notepad.println("P2"); // starting with P2
      notepad.println(width + " " + height); // followed by width and height
      notepad.println(maxValue); // getting the max value for the pgm
      for(int i = 0 ; i < height; i++) { // printing the image value one by one to the file
          for(int j = 0; j < width; j++) {
              notepad.print(" " + image[i][j]);
          }
          notepad.print("\n");
      }
    }
    // function to blend two images
    public PGM blendPGM(PGM image2) {
        // creating new PGM and copy the values of the image
        PGM toonified = new PGM();
        toonified.height = height;
        toonified.imageName = "toonified";
        toonified.image = new int[height][width];
        toonified.width = width;
        toonified.maxValue = maxValue;
        // applying to all values
        for(int i = 0; i < toonified.height; i++) {
            for(int j = 0; j < toonified.width; j++) {
                // averaging from 1st and 2nd image
                toonified.image[i][j] = (image[i][j] + image2.image[i][j])/2;
            }
        }
        return toonified;
    }
}
