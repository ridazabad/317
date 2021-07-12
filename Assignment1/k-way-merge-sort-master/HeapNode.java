/**
 * Class Node
 * Used internally for the MinHeap
 * @authors Nick Humphries (1254869), Rida Zabad (1278354)
 *
 * @param value String Value of the node
 * @param tape  Tape tape reference of where the node came from
 */

class HeapNode implements Comparable<HeapNode> {
    private String value;
    private Tape tape;

    /**
     * Create the node
     *
     * @param String value string value of the heap node
     * @param Tape   tape The tape the string came from
     */
    public HeapNode(String value, Tape tape) {
        this.value = value;
        this.tape = tape;
    }

    /**
     * Compares the nodes using String.compareTo(String) method by the nodes values
     */
    @Override
    public int compareTo(HeapNode anotherNode) {
        return value.compareTo(anotherNode.getValue());
    }

    /**
     * Returns the node in a string format
     */
    @Override
    public String toString() {
        return "'" + value + "' , '" + tape.toString() + "'";
    }

    /**
     * Get the value of the node
     */
    public String getValue() {
        return this.value;
    }

    /**
     * Gets the tape of which the node came from
     */
    public Tape getTape() {
        return this.tape;
    }
}
