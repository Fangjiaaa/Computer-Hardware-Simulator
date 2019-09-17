import java.io.*;
import java.util.Random;
import java.util.Scanner;

public class CPU {

    static int IR = 0; //store instruction
    static int PC = 0; //instruction counter
    static int SP = 1000; //address of memory
    static int AC = 0; //place to do calculations
    static int X = 0; //variable X
    static int Y = 0; //variable Y
    static int timer = 0; //timer to interupt 
    static int parameter = 0; //parameter line of the file (for timer)
    static boolean interruptHandler = false; //turn on when interrupt. turn off when intterrupt finsihes
    static boolean userMode = true; //only false when interrupt

    //Store instruction into IR. At the same time, increase PC for loading data in next step
    public static void IRstoreInstruction(OutputStream os, PrintWriter pw, InputStream is, Scanner rd) {

        IR = readMemory(os, pw, is, rd, PC);
        PC++;
    }

    //push elements on stack when interrupt
    public static void push(OutputStream os, PrintWriter pw, InputStream is, Scanner rd, int topush) {
        writeMemory(os, pw, is, rd, --SP, topush);
    }

    //pop elements off stack after interrupt
    public static int pop(OutputStream os, PrintWriter pw, InputStream is, Scanner rd) {
        return readMemory(os, pw, is, rd, SP++);
    }

    /*
        process instructions 
        since in main, we have already called IRstoreInstruction(),
        we have already stored instruction# into IR, and increaced the PC
        if we need the data under the instruction, we can just call IRstoreInstruction() again
     */
    public static void processInstruction(Runtime rt, Process proc, OutputStream os, PrintWriter pw, InputStream is, Scanner rd) {
        switch (IR) {
            case 1:
                IRstoreInstruction(os, pw, is, rd);
                AC = IR;
                break;
            case 2:
                IRstoreInstruction(os, pw, is, rd);
                AC = readMemory(os, pw, is, rd, IR);
                break;
            case 3:
                IRstoreInstruction(os, pw, is, rd);
                int temp_address;
                temp_address = readMemory(os, pw, is, rd, IR);
                AC = readMemory(os, pw, is, rd, temp_address);
                break;
            case 4:
                IRstoreInstruction(os, pw, is, rd);
                AC = readMemory(os, pw, is, rd, IR + X);
                break;
            case 5:
                IRstoreInstruction(os, pw, is, rd);
                AC = readMemory(os, pw, is, rd, IR + Y);
                break;
            case 6:
                AC = readMemory(os, pw, is, rd, SP + X);
                break;
            case 7:
                IRstoreInstruction(os, pw, is, rd);
                writeMemory(os, pw, is, rd, IR, AC);
                break;
            case 8: //rand 
                Random rand = new Random();
                AC = rand.nextInt(100) + 1;
                break;
            case 9:
                IRstoreInstruction(os, pw, is, rd);
                int port = IR;
                switch (port) {
                    case 1:
                        System.out.print(AC);
                        break;
                    case 2:
                        System.out.print((char) (AC));
                        break;
                    default:
                        System.out.print("Invalid port");
                        break;
                }
                break;
            case 10:
                AC += X;
                break;
            case 11:
                AC += Y;
                break;
            case 12:
                AC -= X;
                break;
            case 13:
                AC -= Y;
                break;
            case 14:
                X = AC;
                break;
            case 15:
                AC = X;
                break;
            case 16:
                Y = AC;
                break;
            case 17:
                AC = Y;
                break;
            case 18:
                SP = AC;
                break;
            case 19:
                AC = SP;
                break;
            case 20:
                IRstoreInstruction(os, pw, is, rd);
                PC = IR;
                break;
            case 21:
                IRstoreInstruction(os, pw, is, rd);
                if (AC == 0) {
                    PC = IR;
                }
                break;
            case 22:
                IRstoreInstruction(os, pw, is, rd);
                if (AC != 0) {
                    PC = IR;
                }
                break;
            case 23:
                IRstoreInstruction(os, pw, is, rd);
                push(os, pw, is, rd, PC);
                PC = IR;
                break;
            case 24:
                PC = pop(os, pw, is, rd);
                break;
            case 25:
                X++;
                break;
            case 26:
                X--;
                break;
            case 27:
                push(os, pw, is, rd, AC);
                break;
            case 28:
                AC = pop(os, pw, is, rd);
                break;
            case 29://interrupt 
                userMode = false;
                interruptHandler = true;

                int SPonStack = SP;
                SP = 2000;//or 1999?
                push(os, pw, is, rd, SPonStack);

                int PConStack = PC;
                PC = 1500;
                push(os, pw, is, rd, PConStack);

                break;

            case 30://end interrupt
                PC = pop(os, pw, is, rd);
                SP = pop(os, pw, is, rd);
                userMode = true;
                interruptHandler = false;
                break;

            case 50:
                pw.println("exit");
                pw.flush();
                System.exit(0);
                break;
            default:
                System.out.println("switch case parameter error");
                System.exit(0);
                break;

        }
    }

    //send memory write message, let memory saves the given data at given address
    public static void writeMemory(OutputStream os, PrintWriter pw, InputStream is, Scanner rd, int address, int data) {
        //if try to access address >=1000 in usermode, throw error.
        if (address >= 1000 && userMode) {
            System.out.println("Memory violation: accessing system address 1000 in user mode");
        }
        pw.print("write " + address + " " + data + "\n");
        pw.flush();
    }

    //send memory read message, get data at given address
    public static int readMemory(OutputStream os, PrintWriter pw, InputStream is, Scanner rd, int address) {
        //if try to access address >=1000 in usermode, throw error.
        if (address >= 1000 && userMode) {
            System.out.println("Memory violation: accessing system address 1000 in user mode");
        }
        pw.print("read " + address + "\n");
        pw.flush(); //send request to memory

        String line = rd.nextLine();
        
        return Integer.parseInt(line);
     
    }

    /*
    public static int pop(){
        
    }
    
    public static void push(int data){
        
    }
     */
    public static void main(String[] args) throws IOException {
        //pass a file name as a command line argument
        String fileName = args[0];
        timer = Integer.parseInt(args[1]);

        /*test 
        System.out.println(fileName + timer);
         */
        try {
            //Connect
            Runtime rt = Runtime.getRuntime();
            Process proc = rt.exec("java Memory");

            InputStream is = proc.getInputStream();
            OutputStream os = proc.getOutputStream();

            PrintWriter pw = new PrintWriter(os); //send to memory
            Scanner rd = new Scanner(is); //read from memory

            //send fine name to memory
            pw.println(fileName);
            pw.flush();

            while (true) {
                if (parameter < timer) {
                    //no interrupt
                    IRstoreInstruction(os, pw, is, rd);
                    processInstruction(rt, proc, os, pw, is, rd);

                    if (!interruptHandler) {
                        parameter++;
                    }
                } else {
                    parameter = 0; //refresh parameter line 
                    userMode = false;
                    interruptHandler = true;

                    int SP_on_Stack = SP;
                    SP = 2000;
                    push(os, pw, is, rd, SP_on_Stack); //SP= 1999

                    int PC_on_Stack = PC;
                    PC = 1000;
                    push(os, pw, is, rd, PC_on_Stack);//SP = 1998

                }

            }//while(true)

        }//try
        catch (Exception e) {
        }
    }//main

}//Cpua class
