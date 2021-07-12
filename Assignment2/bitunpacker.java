import java.nio.ByteBuffer;
import java.io.IOException;

/**
 * Takes a stream of integers (line by line
 */
class BitUnpacker {

    static final int RESET_PHRASE = 0;
    static final int INIT_BITPACK_SIZE = 9;
    static final int INIT_PHRASE_COUNT = 256;

    public static void main(String[] args) {
        int phraseCount = INIT_PHRASE_COUNT;
        int bitPackSize = INIT_BITPACK_SIZE; //it starts at 9 because of the reset symbol
        int currentPack = 0;
        int bitOffset = 0;
        int halfPhrase = 0;

        try {
            byte[] bytes = new byte[4];
            while (System.in.read(bytes, 0, 4) != -1) {
                int step = ByteBuffer.wrap(bytes).getInt();

                if (bitOffset != 0) {
                    phraseCount++;
                    //increase the pack size to hold more phrase numbers
                    if (phraseCount > Math.pow(2, bitPackSize)) {
                        bitPackSize++;
                    }

                    int tailSize = bitPackSize - (32 - bitOffset);

                    //get the other half from the prev step and add the rest of the bits onto the end
                    int phrase = step >>> (32 - tailSize);
                    halfPhrase = halfPhrase >>> (32 - tailSize - (32 - bitOffset));
                    phrase = phrase | halfPhrase;
                    System.out.println(phrase);
                    bitOffset = tailSize;

                    if (phrase == RESET_PHRASE) {
                        phraseCount = INIT_PHRASE_COUNT;
                        bitPackSize = INIT_BITPACK_SIZE;
                    }

                }

                while (32 - bitOffset >= bitPackSize) {
                    phraseCount++;
                    //increase the pack size to hold more phrase numbers
                    if (phraseCount > Math.pow(2, bitPackSize)) {
                        bitPackSize++;
                    }

                    int phrase = step << bitOffset; //clear the most significant bits
                    phrase = phrase >>> (32 - bitPackSize); //clear the least significant bits
                    System.out.println(phrase);

                    bitOffset += bitPackSize;

                    if (phrase == RESET_PHRASE) {
                        phraseCount = INIT_PHRASE_COUNT;
                        bitPackSize = INIT_BITPACK_SIZE;
                    }
                }

                //handle a half phrase which dangles off the end of the int and overflows onto the next
                if (bitOffset != 32) {
                    halfPhrase = step << bitOffset;
                } else {
                    bitOffset = 0;
                }
            }
        } catch (IOException e){
            System.err.println("There was a problem reading the input");
        }
    }
}