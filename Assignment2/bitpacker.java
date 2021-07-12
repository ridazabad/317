import java.util.Scanner;
import java.nio.ByteBuffer;
import java.util.*;
import java.io.*;

/**
 * Takes a stream of integers (line by line
 */
class BitPacker {

    static final int RESET_PHRASE = 0;
    static final int INIT_BITPACK_SIZE = 9;
    static final int INIT_PHRASE_COUNT = 256;

    public static void main(String[] args) {
        int phraseCount = INIT_PHRASE_COUNT;
        int bitPackSize = INIT_BITPACK_SIZE; //it starts at 9 because of the reset symbol
        int currentPack = 0;
        int bitOffset = 0;

        DataOutputStream output = new DataOutputStream(System.out);

        //read system.in until it is null
        Scanner sc = new Scanner(System.in);
        while(sc.hasNextLine()) {

            //increase the pack size to hold more phrase numbers
            phraseCount ++;
            if(phraseCount > Math.pow(2, bitPackSize)) {
                bitPackSize ++;
            }

            int phrase;
            try {
                phrase = Integer.parseInt(sc.nextLine());
            } catch(Exception e){
                System.err.println("There was an error reading from the input stream");
                continue;
            }

            //Pack the bits onto the end of the current phrase
            if(bitPackSize + bitOffset > 32){
                // put as many bits on as possible
                int rightShift = bitPackSize - (32 - bitOffset);
                int partial = phrase >>> rightShift;
                currentPack = currentPack | partial;

                //output the current pack and reset
                try {
                    output.writeInt(currentPack);
                } catch(Exception e){
                    System.err.println("There was an error writing to the output stream");
                }

                currentPack = 0;
                bitOffset = 0;

                // put the rest onto the next pack
                int tail = phrase << (32 - rightShift);
                currentPack = currentPack | tail;
                bitOffset = rightShift;

            } else { //add to the current pack
                int add = phrase << (32 - bitPackSize - bitOffset);
                currentPack = currentPack | add;
                bitOffset += bitPackSize;
            }

            //if the pack is full, output it and reset
            if(bitOffset == 32){
                try {
                    output.writeInt(currentPack);
                } catch(Exception e){
                    System.err.println("There was an error writing to the output stream");
                }
                currentPack = 0;
                bitOffset = 0;
            }

            //handle the reset
            if(phrase == RESET_PHRASE) {
                phraseCount = INIT_PHRASE_COUNT;
                bitPackSize = INIT_BITPACK_SIZE;
            }
        }

        try {
            //final clean up
            if(currentPack != 0){
                output.writeInt(currentPack);
            }
            output.close();
        } catch(Exception e){
            System.err.println("There was an error writing to the output stream");
        }
    }
}