import java.io.*;
import java.util.*;
import java.lang.Object;

class Decoder {

    public static void main(String[] args) {
        FileReader();
    }

    //Initializes the trie to be able to add new values
    private static Hashtable<Integer, Node> initialize() {

        //Hashtable
        Hashtable<Integer, Node> h = new Hashtable<Integer, Node>();

        //First byte value used as int in order to add a reset
        int b = -128;

        //initial headnode
        Node headNode = new Node(0, 0, 0);
        h.put(0, headNode);

        //Add all the possible values as a chjild to head node
        for (int i = 1; i <= 256; i++) {

            Node tempNode = new Node(i, b, 0);
            headNode.addChild(tempNode);
            b = b + 1;
            h.put(i, tempNode);
        }

        //Add the reset node
        Node reset = new Node(257, 200, 0);
        headNode.addChild(reset);
        h.put(257, reset);


        return h;

    }


    //Initializes the trie to be able to add new values
    private static Node initialize2() {

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

        return headNode;

    }

    private static void FileReader() {

        try {

            //To read for standard input
            String line = "";
            //DataInputStream dis = new DataInputStream(System.in);
            BufferedReader d = new BufferedReader(new InputStreamReader(System.in));

            //value being read
            int x;

            //Main nodes
            Hashtable<Integer, Node> htable = initialize();

            //headnode
            Node headNode = initialize2();

            Node nodeToPrint = new Node(0, 0, 0);


            //index
            int ind = 258;
            int val = 1;
            int par = 1;
            int iterator = 0;

            //to print
            int toprint = 0;


            while ((line = d.readLine()) != null) {

                x = Integer.parseInt(line);

                //array
                ArrayList<Integer> outputBuffer = new ArrayList<>();

		//Checks if it not a reset
                if (x != 0) {

		    //gets the node to print at current x value
                    nodeToPrint = htable.get(x);
		   
		    //adds the the output buffer array
                    outputBuffer.add(nodeToPrint.getValue());

		    //gets next possible parent
                    toprint = nodeToPrint.getParent();

		    //Checks if next possible is not head
                    while (toprint != 0) {

			//Save at new position
                        nodeToPrint = htable.get(toprint);

   			//add it the output buffer
                        outputBuffer.add(nodeToPrint.getValue());

			//get the next parent and check if not head
                        toprint = nodeToPrint.getParent();
                    }
                    
                    //output the string from the trie in the correct order
                    for (int m = (outputBuffer.size() - 1); m >= 0; m--) {
                        System.out.write((0xFF & outputBuffer.get(m)));
                    }

		    //Check if first step
                    if (iterator == 0) {
		
			//Get new node
                        Node tempNode = new Node(ind, val, x);

			//Add new node to the hashtable
                        htable.put(ind, tempNode);
  			//Add new node to its associated parent
                        htable.get(x).addChild(tempNode);
                        //inc both iterator and index
                        ind++;
                        iterator++;

			//Next steps
                    } else {

			//get new node value
                        Node tempNode = new Node(ind, val, x);

			//add it tot he hashtable
                        htable.put(ind, tempNode);

			//associate it to the correct parent
                        htable.get(x).addChild(tempNode);

			//get the parent and check if it is the first layer
                        par = tempNode.getParent();
                        tempNode = htable.get(par);

			//Loop till it is in the first layer
                        while (par >= 257) {

                            par = tempNode.getParent();
                            tempNode = htable.get(par);

                        }

			//Save the value to store in last added node
                        val = tempNode.getValue();
                        //System.out.println(val);

			//Save it to the last added node
                        htable.get((ind - 1)).setValue(val);
                        //System.out.println(htable.get(ind));
                        ind++;

                    }
		//If needs to reset
                } else {
                    headNode.reset();
                    ind = 258;
                    iterator = 0;
                    headNode = initialize2();
                    htable = initialize();
                    //System.out.println("reset");

                }


            }

            d.close();
        } catch (IOException ex) {
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

    public void removeChildren(){
        nodeList = new ArrayList<>();
    }

    public void setValue(int v) {

        this.value = v;
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
        for (int i = 0; i < nodeList.size(); i++) {
            nodeList.get(i).removeChildren();
        }
    }

    @Override
    public String toString() {
        return Integer.toString(index) + " " + Integer.toString(value) + " " + Integer.toString(parent);
    }
}
