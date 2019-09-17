

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Memory {

    final static int[] memory = new int[2000];

    //main 
    public static void main(String[] args) throws FileNotFoundException {
        Scanner rdm = new Scanner(System.in);

        //read file name
        File file = new File(rdm.nextLine());

        readFile(file);

        processRequest(rdm);

    } //main

    //1. read file from the file name CPU send
    public static void readFile(File file) throws FileNotFoundException {
        Scanner sc = new Scanner(file);

        int address = 0;
        while (sc.hasNext()) {

            if (sc.hasNextInt()) {
                memory[address++] = sc.nextInt();

            } else {
                String line = sc.next();
                if (line.charAt(0) == '.') {
                    address = Integer.parseInt(line.substring(1));
                } 
                else {
                    sc.nextLine();

                }
            }

        }

    }//readFile finish

    //2. send data to CPU at given address 
    public static int read(int address) {
        return memory[address];
    }

    //3.write the data from CPU to memroy at given address 
    public static void write(int address, int data) {
        memory[address] = data;
    }

    //4.read or write? "read address" "write address data"
    public static void processRequest(Scanner rd) {

        while (rd.hasNextLine()) {
            String line = rd.nextLine();

            String[] word = line.trim().split("\\s+");
            
            //read write or exit current process 
            if (word[0].equals("read")) {  //"read address"
                System.out.println(read(Integer.parseInt(word[1])));
            } else if (word[0].equals("write")) { //"write address data"
                write(Integer.parseInt(word[1]), Integer.parseInt(word[2]));
            } else {
                System.exit(0);
            }

        }//while

    }//readRequest

}//Memorya

