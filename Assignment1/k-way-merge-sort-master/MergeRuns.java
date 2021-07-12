import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * MergeRuns takes a file of runs created by the CreateRuns program and k-way
 * sort merges them to create a final fully sorted file
 * @authors Nick Humphries (1254869), Rida Zabad (1278354)
 *
 * @arg int k the number of splits for the k-way merge sort
 * @arg string filename filename of the runs including the extension
 */

class MergeRuns {
    //constants
    public static final String TMP_FILE_PREFIX = "tmp_";
    private static final String END_OF_RUN = "*";
    private static final String ESCAPE_CHAR = "#";

    private static Tape[] tapes;
    private static int finishedTapes;
    private static int runCount = 0;
    private static MinHeap heap;

    public static void main(String[] args) {
        int k = 0;
        String filename = "";

        if (args.length != 2) {
            System.err.println("Found " + args.length + " arguments");
            throw new IllegalArgumentException("Invalid number of arguments. Expects [(int) k] [(String) filename]");
        }

        if (!args[0].matches("-?\\d+")) {
            System.err.println("k=" + args[0]);
            throw new IllegalArgumentException("Parameter 1, k, must be an integer");
        }

        k = Integer.parseInt(args[0]);
        if (k <= 1) {  //TODO check that we have enough memory, might need to do some testing for this, put a max?
            System.err.println("k=" + k);
            throw new IllegalArgumentException("Parameter 1, k, must be greater than 1");
        }

        filename = args[1] + ".runs";
        if (!(new File(filename)).exists()) {
            System.err.println("Filename: " + args[1]);
            throw new IllegalArgumentException("Parameter 2, filename[.runs], must be a valid file that exists");
        }

        //create the tapes
        tapes = new Tape[k];
        heap = new MinHeap(HeapNode.class, k);

        distributeRuns(k, filename);

        int requiredPasses = (int) Math.ceil(Math.log(runCount) / Math.log(k));
        System.err.println("Required number of passes: " + requiredPasses);

        for (int i = 1; i <= requiredPasses; i++) {
            System.out.println("... running pass " + i);

            mergeTapes(k, i);

            if (finishedTapes == 1) {
                System.out.println("Merge Complete after " + i + " passes");
                break; //we can potentially finish before the number of required passes is reached
            }
        }

        if (tapes == null || tapes[0] == null) {
            throw new RuntimeException("The data could not be found");
        }

        tapes[0].save(args[1] + ".sorted");

        System.out.println("Sorted data saved to " + args[1] + ".sorted");
    }

    /**
     * Take the input runs and output them evenly into k-many files
     *
     * @param int   k of the k-way sort
     * @param input File input file of the initial runs
     */
    private static void distributeRuns(int k, String filename) {
        BufferedReader br = null;
        FileReader fr = null;

        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);

            String line;
            Tape t = new Tape(0);
            tapes[0] = t;

            int itemCount = 0;
            while ((line = br.readLine()) != null) {
                itemCount++;
                if (line.equals(END_OF_RUN)) {
                    //output each run into the (run % k)th tape
                    runCount++;

                    if (runCount < k) {
                        t = new Tape(runCount % k);
                        tapes[runCount] = t;
                    } else {
                        t = tapes[runCount % k];
                    }
                } else {
                    //add to the list of tapes
                    t.add(unescape(line));
                }
            }
            runCount++;

            //complete the tapes up after creating them
            for (int i = 0; i < k; i++) {
                if(tapes[i] == null) {
                    continue;
                }

                tapes[i].complete();
            }

        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to read input file");
        }

        //clean up the readers
        try {
            if (br != null)
                br.close();

            if (fr != null)
                fr.close();

        } catch (IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("Unable to close input file readers");
        }
    }

    /**
     * Recursively merges the tapes created by the distributeRuns method
     *
     * @param int k of the k-way sort, will create k many tapes
     * @param int pass id of the current pass
     */
    private static void mergeTapes(int k, int pass) {
        // create the new tapes
        Tape[] newTapes = new Tape[k];

        finishedTapes = 0;
        int runCount = 0;
        while (finishedTapes != k) { //keep going until all tapes are exhausted
            Tape output = newTapes[runCount % k];
            if (output == null) {
                output = new Tape((k * pass) + (runCount % k));
                newTapes[runCount % k] = output;
            }

            mergeRuns(k, output);
            runCount++;
        }

        //finish writing to the new tapes and destroy the old ones
        for (int i = 0; i < k; i++) {

            //only if the tape exits, complete it. It won't exist if there weren't enough runs
            Tape t = newTapes[i];
            if (t != null) {
                newTapes[i].complete();
            }

            tapes[i].destroy(); //clean up the old tapes
        }

        tapes = newTapes;
    }

    /**
     * Take k runs and merge them into a single run and store it on the current tape
     *
     * @param int  k the number of tapes used
     * @param Tape output the output tape
     */
    private static void mergeRuns(int k, Tape output) {
        if(!heap.hasStoredItems()) {
            for (int i = 0; i < k; i++) {
                if(tapes[i] == null) {
                    continue;
                }

                String s = tapes[i].next();

                if (s == null) {
                    continue;
                }

                HeapNode n = new HeapNode(s, tapes[i]);
                heap.add(n);
            }
        }
        heap.heapify();

        HeapNode root;
        while ((root = (HeapNode) heap.root()) != null) {
            output.add(root.getValue());    //write the root to the output tape

            Tape tape = root.getTape();
            String nextString = tape.next();

            //keep track of tapes that have nothing left to give
            if (nextString == null) {
                finishedTapes++;
            }

            HeapNode nextNode = new HeapNode(nextString, root.getTape());

            //handles end of runs
            if (nextString == null || nextString.compareTo(root.getValue()) < 0) {
                //only add the new node if it needs to be in the next pass
                if (nextString != null) {
                    heap.store(nextNode);
                } else {
                    heap.replace(null);
                }

                //return if all the tapes have finished or are too large to be added to the output
                if ((heap.storeCount() + finishedTapes) == k) {
                    return;
                }

            } else {
                heap.replace(nextNode);
            }
        }
    }

    /**
     * Unescape the strings
     *
     * @param String val string do unescape
     * @return String the unescaped string
     */
    private static String unescape(String val){
        val = val.replace(ESCAPE_CHAR + ESCAPE_CHAR, ESCAPE_CHAR);
        val = val.replace(ESCAPE_CHAR + END_OF_RUN, END_OF_RUN);
        return val;
    }
}
