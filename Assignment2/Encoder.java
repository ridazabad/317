import java.io.*;
import java.util.ArrayList;


///get the file and converts the values to bytes for the Encoder
///till the end of the file
class Encoder {

    public static void main(String[] args) {
        if (args.length == 0 || args.length > 1) {
            System.err.println("Arguement required: <Maximum size>");
        } else {
            int max = Integer.parseInt(args[0]);
            //Calling File Reader
            FileReader(max);
            //Node test = initialize();
            //System.out.println(test);
        }
    }

    //Initializes the trie to be able to add new values
    private static Node initialize() {

        //First byte value used as int in order to add a reset
        int b = -128;

        //initial headnode
        Node headNode = new Node(0, 0, 0);

        //Add all the possible values as a chjild to head node
        for (int i = 1; i <= 256; i++) {

            Node tempNode = new Node(i, b, 0);
            headNode.addChild(tempNode);
            b = b + 1;
        }

        //Add the reset node
        Node reset = new Node(257, 200, 0);
        headNode.addChild(reset);

        //Return the headNode
        //ArrayList<Node> nodeList = new ArrayList<>();
        //nodeList = headNode.getChild();
        //System.out.println(nodeList.get(256).toString());
        return headNode;

    }

    ///Reads the cat file that has been given on the terminal taking a maximum amount of bits
    ///for the array to take.
    private static void FileReader(int max) {

        try {

            //To read for standard input
            String line = "";
            DataInputStream dis = new DataInputStream(System.in);

            //Take the value args for bits and change to bytes for max of array
            int arraymax = max / 8;
            byte[] byteArray = new byte[arraymax];

            //checks to see if can still be read
            int toRead;

            //Value to compare
            int x;

            //Get the array
            Node headNode = initialize();
            Node tempNode = headNode;

            //new index
            int ind = 258;
            int parentindex = 0;

            //Read to the end of the file
            while ((toRead = dis.read(byteArray, 0, arraymax)) != -1) {

                //Loop through all values in the byte array
                for (int i = 0; i < toRead; i++) {

                    //Store value to compare
                    x = byteArray[i];

                    //If byte value read is not null
                    if (x != 0) {

                        //Check if the value in the byte array
                        //exists as a child of teh current node
                        if (tempNode.hasChild(x) == true) {

                            //Move to next
                            tempNode = tempNode.getNext(x);
                            //System.out.println(tempNode);
                        }

                        //Get the parent index to output
                        //Add new node as a child
                        //Point to the head to restart
                        //increment the index and go back to the last added value
                        else {
                            System.out.println(tempNode);
                            parentindex = tempNode.getIndex();
                            Node newNode = new Node(ind, x, parentindex);
                            tempNode.addChild(newNode);
                            tempNode = headNode;
                            ind++;
                            i--;
                        }

                        //Check the last value being read is within the trie and not at the end
                        //add it ass a phrase
                        //else check if it is the same as last added phrase
                        if (i == (arraymax - 1)) {
                            if (parentindex != tempNode.getIndex()) {
                                System.out.println(tempNode.getIndex());
                            }
                        }
                    }
                }
                //Reset after loop done
                //Reset index and print reset symbol
                headNode.reset();
                System.out.println(0);
                ind = 258;
                tempNode = headNode;
                //break;
            }

            dis.close();
        }

        //Catch the IO excpetion
        catch (IOException ex) {
            System.err.println("Cannot read the file");
        }

    }

}

class Node {

    //Node variables
    private int index;
    private int value;
    private int parent;
    private ArrayList<Node> nodeList = new ArrayList<>();


    //Node constructor
    public Node(int ind, int val, int par) {

        this.index = ind;
        this.parent = par;
        this.value = val;
    }

    //Gets for all the variables that the node
    //has stored
    public int getIndex() {
        return this.index;
    }

    public int getValue() {
        return this.value;
    }

    public int getParent() {
        return this.parent;
    }

    public ArrayList<Node> getChild() {
        return this.nodeList;
    }

    public void addChild(Node node) {

        nodeList.add(node);
    }

    public void removeChildren() {
        nodeList = new ArrayList<>();
    }

    public boolean hasChild(int val) {

        for (int i = 0; i < nodeList.size(); i++) {
            int x = nodeList.get(i).getValue();
            if (val == x) {
                return true;
            }
        }

        return false;
    }

    public Node getNext(int val) {
        int pos = 0;

        for (int i = 0; i < nodeList.size(); i++) {
            int x = nodeList.get(i).getValue();
            if (val == x) {
                pos = i;
            }
        }

        return nodeList.get(pos);
    }

    public void reset() {
        for(int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).removeChildren();
        }
    }

    @Override
    public String toString() {
        return Integer.toString(index);
    }
}
