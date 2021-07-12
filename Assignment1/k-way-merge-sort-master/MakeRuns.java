import java.io.*;
import java.util.*;
import java.util.Comparator;
import java.util.PriorityQueue;

/**
 * Creating the main class that seperates a file into runs using
 * heap priority that takes an integer for the maximum size of the heap and
 * a filename with data
 * @authors Nick Humphries (1254869), Rida Zabad (1278354)
 *
 * @arg int k the maximum size of the heap
 * @arg string filename filename of the data including the extension
 */
class MakeRuns {
    private static final String END_OF_RUN = "*";
    private static final String ESCAPE_CHAR = "#";

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 2) {
            System.out.println("2 Arguement required: <Maximum size> <File Name>");
        } else {
            int max = Integer.parseInt(args[0]);
            String filename = args[1];
            //Calling File Reader
            FileReader(max, filename);
        }
    }

    //Read a given file and add it into the priority queue
    private static void FileReader(int max, String filename) {
        Tape runs = new Tape(0);

        try {
            MinHeap heap = new MinHeap(String.class, max);

            int runCount = 0;
            boolean initalized = false;
            String line = "";
            FileReader read = new FileReader(filename);
            BufferedReader bufferedReader = new BufferedReader(read);

            while ((line = bufferedReader.readLine()) != null) {
                //Initally populate the heap and don't heapify until its full
                if(heap.size() < max && !initalized){
                    heap.add(line);
                    continue;
                }

                if(!initalized){
                    heap.heapify();
                    initalized = true;
                }

                String root = (String) heap.root();
                runs.add(escape(root));

                if (root != null && line.compareTo(root) < 0) {
                    heap.store(line); //don't add the line as it can't be added to the current run
                } else {
                    heap.replace(line);
                }

                if(heap.storeCount() == max){
                    runs.add(END_OF_RUN);
                    heap.heapify();
                    runCount++;
                }
            }

            //output any remaining items from the current run to the tape
            while(heap.size() - heap.storeCount() > 0){
                String s = (String) heap.root();
                runs.add(escape(s));
                heap.replace(null);
            }
            runCount ++;

            //any stored items will be re-heaped and then added to a new run at the end of the tape
            if(heap.storeCount() > 0){
                runs.add(END_OF_RUN);
                heap.heapify();

                while(heap.size() - heap.storeCount() > 0){
                    String s = (String) heap.root();
                    runs.add(escape(s));
                    heap.replace(null);
                }
                runCount ++;
            }

            System.err.println("Number of Runs Created: " + runCount);

            //save the tape to a different filename
            runs.save(filename + ".runs");
            System.out.println("Runs saved to: " + filename + ".runs");
        }

        //Handling Both File not found and IO exception to notify user
        catch (FileNotFoundException ex) {
            System.err.println("The file does not exist");
        } catch (IOException ex) {
            System.err.println("Cannot read the file");
        }
    }

    /**
     * Escape the end of line special characters
     *
     * @param String val
     * @return String escaped string
     */
    private static String escape(String val) {
        val = val.replace(ESCAPE_CHAR, ESCAPE_CHAR + ESCAPE_CHAR);
        val = val.replace(END_OF_RUN, ESCAPE_CHAR + END_OF_RUN);
        return val;
    }
}