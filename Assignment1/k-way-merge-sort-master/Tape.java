import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Class Tape
 * Controls the tape and provides easy access methods for iterating through the tape
 * @authors Nick Humphries (1254869), Rida Zabad (1278354)
 *
 * @param int i tape id used for the unique filename
 */

class Tape {
    private static final int BUFFER_SIZE = 2000; //defines the buffer size for the reader and writer

    private BufferedWriter bw = null;
    private BufferedReader br = null;
    private String filename;
    private String last = null;
    private FileWriter fw = null;
    private FileReader fr = null;
    private boolean writeable = true;

    public Tape(int i) {
        filename = MergeRuns.TMP_FILE_PREFIX + i;

        //create the buffer writer for populating the tape
        try {
            fw = new FileWriter(filename);
            bw = new BufferedWriter(fw, 2000);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Tape: " + filename + " => The Tape could not be created");
        }
    }

    /**
     * Ensuring proper garbage collection
     */
    @Override
    protected void finalize() throws Throwable {
        //ensure that the buggered writer and buffered reader are both closed
        this.destroy();
        super.finalize();
    }

    /**
     * Better string representation of the class
     *
     * @return filename of the tape
     */
    @Override
    public String toString() {
        return filename;
    }

    /**
     * Adds an item to the end of the tape
     *
     * @param String value The string value of the node
     */
    public void add(String value) {

        if (!writeable) {
            throw new RuntimeException(filename + ": Tape is not currently writable. complete() has already been called");
        }


        try {
            last = value;
            bw.write(value);
            bw.newLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(filename + ": The Tape is not writable");
        }
    }

    /**
     * Gets the last added string
     */
    public String last() {
        return last;
    }

    /**
     * Will return the next value on the tape
     */
    public String next() {
        if (writeable) {
            throw new RuntimeException(filename + ": Tape is not currently readable. Call complete() before reading from the tape");
        }



        try {
            return br.readLine();
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(filename + ": The Tape is not readable");
        }
    }

    /**
     * Completes the writing to the tape and sets up the buffered reader
     */
    public void complete() {
        //close the writers
        try {
            if (bw != null) {
                bw.close();
                bw = null;
            }

            if (fw != null) {
                fw.close();
                fw = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(filename + ": The Tape writers could not be closed");
        }

        //open the readers
        try {
            fr = new FileReader(filename);
            br = new BufferedReader(fr);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(filename + ": The Tape is not readable");
        }

        writeable = false;
    }

    /**
     * Save the tape to a non-temporary file
     *
     * @param String destination The file location of where to save the tape
     */
    public void save(String dest) {
        closeReadersWriters();

        //get the last remaining tape and rename it to {filename}.sorted
        try {
            File sorted = new File(filename);
            File destination = new File(dest);
            Files.move(sorted.toPath(), destination.toPath(), java.nio.file.StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Unable to move the data from the temporary file " + filename + " to final destination " + dest);
        }
    }

    /**
     * Destroys the tape and the file associated to it
     */
    public void destroy() {
        closeReadersWriters();

        //clean up and delete the file if it exists
        File f = new File(filename);
        if (f.exists() && !(f.delete())) {
            throw new RuntimeException(filename + ": The Tape could not be destroyed");
        }
    }

    /**
     * Close all readers and writers
     */
    private void closeReadersWriters() {
        try {
            //close the readers and writers
            if (bw != null) {
                bw.close();
                bw = null;
            }

            if (fw != null) {
                fw.close();
                fw = null;
            }

            if (br != null) {
                br.close();
                br = null;
            }

            if (fr != null) {
                fr.close();
                fr = null;
            }
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException(filename + ": The readers or writers are not close-able");
        }
    }
}
