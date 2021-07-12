
/**
 * MinHeap (Minimum Priority Queue) Implementation
 * @authors Nick Humphries (1254869), Rida Zabad (1278354)
 *
 * Note: This class generates compiler warnings due to unchecked generic constraints
 *
 * @param int maxSize maximum size of the heap
 */

class MinHeap<E extends Comparable<? super E>> {
    private int maxSize; //maximum size of the entire heap
    private int curSize = 0; //current priority queue size
    private int stoSize = 0; //storage size
    private E[] heap;

    /**
     * Create the MinHeap
     *
     * @param int maxSize the maximum size of the min heap
     */
    public MinHeap(Class<E> c, int maxSize) {
        final E[] heap = (E[]) new Comparable[maxSize];
        this.heap = heap;

        this.maxSize = maxSize;
    }

    /**
     * Outputs the heap to a JSON encoded array in string format
     */
    @Override
    public String toString() {
        String output = "[";
        for (int i = 0; i < stoSize; i++) {
            output += i + ": {" + heap[i].toString() + "}";

            if (i < stoSize - 1) {
                output += " , ";
            }
        }
        output += "]";

        return output;
    }

    /**
     * Get the root
     *
     * @return E the root node
     */
    public E root() {
        return heap[0];
    }

    /**
     * Checks if the heap is full or not
     *
     * @return boolean True if the heap is full
     */
    public boolean isFull(){
        return (stoSize == maxSize);
    }

    /**
     * Checks if the heap has stored, unordered items
     *
     * @return boolean True if there are unordered items in the heap
     */
    public boolean hasStoredItems(){
        return (stoSize != curSize);
    }

    /**
     * Gets the total number of non-null items
     *
     * @return int number of items
     */
    public int size(){
        return stoSize;
    }

    /**
     * Gets the number of items in the stored state
     *
     * @return int returns the number of items in the stored state
     */
    public int storeCount(){
        return stoSize - curSize;
    }

    /**
     * Adds a node to the end of the heap
     *
     * @param E n The node to add to the end of the heap
     */
    public void add(E n) {
        if (stoSize == maxSize) {
            throw new RuntimeException("Maximum heap size reached");
        }

        heap[stoSize] = n;
        stoSize++;
    }

    /**
     * Stores the node outside of the priority queue, waiting for the next heapify call
     * This will always destory the root node
     *
     * @param E n the node to be stored
     */
    public void store(E n) {
        curSize --; //reduce the currentSize so that the stored node is not compared to
        E last = heap[curSize];
        heap[curSize] = n;

        if(curSize != 0) {
            heap[0] = last;
            downheap();
        }
    }

    /**
     * Returns the highest priority (lowest value) from the heap
     *
     * @param E n The node to replace the root with
     */
    public void replace(E n) {
        //if there is nothing to replace the root, then use the last element in the heap
        if (n == null) {
            curSize--;
            stoSize--;
            n = heap[curSize];

            //move the last stored item into the removed items slot, and remove the duplicated pointer
            heap[curSize] = heap[stoSize];
            heap[stoSize] = null;
        }

        //replace the node with the new node
        if(curSize != 0){
            heap[0] = n;
        }

        downheap();
    }

    /**
     * Resort the entire heap. This will sort any stored nodes into priority order also
     */
    public void heapify() {
        curSize = stoSize;

        for(int i = (((int)Math.round(Math.ceil(maxSize/2)))-1); i >= 0; i--) {
            downheap(i);
        }
    }

    /**
     * Up heaps until the priority is restored
     */
    private void upheap() {
        int i = curSize - 1;

        while (i > 0) {
            E node = heap[i];
            E parent = getParent(i);

            if (parent == null) {
                return; //finished up heaping
            }

            if (parent.compareTo(node) < 0) {
                heap[i] = parent;
                heap[(i - (i % 2)) / 2] = node;
                i = (i - (i % 2)) / 2;
                continue; //swap and check again
            }

            return; //also finished
        }
    }

    /**
     * Down heaps until the priority is restored
     *
     * @param int index (default: 0) The index to downheap from
     */
    private void downheap() {
        downheap(0);
    }

    private void downheap(int index) {
        int i = index;

        if (heap[0] == null) {
            return;
        }

        while (i < curSize) {
            E node = heap[i];
            E left = getLeftChild(i);
            E right = getRightChild(i);


            if (left == null) {
                return; //finished down heaping
            }

            //if the left is lower, and is lower than the right then swap
            if (left.compareTo(node) < 0 && (right == null || left.compareTo(right) < 0)) {
                heap[i] = left;
                heap[i * 2 + 1] = node;
                i = i * 2 + 1;
                continue; //swap and check again
            }

            if (right == null) {
                return; //finished down heaping
            }

            if (right.compareTo(node) < 0) {
                heap[i] = right;
                heap[i * 2 + 2] = node;
                i = i * 2 + 2;
                continue; //swap and check again
            }

            return; //also finished
        }
    }

    /**
     * Get the left child of the index node i
     *
     * @param int i index of node
     */
    private E getLeftChild(int i) {
        int child = i * 2 + 1;
        if (child > curSize - 1) {
            return null;
        }

        return heap[child];
    }

    /**
     * Get the right child of the index node i
     *
     * @param int i index of node
     */
    private E getRightChild(int i) {
        int child = i * 2 + 2;
        if (child > curSize - 1) {
            return null;
        }

        return heap[child];
    }

    /**
     * Get the parent of the index node i
     *
     * @param int i index of node
     */
    private E getParent(int i) {
        //the root has no parents, like batman
        if (i == 0) {
            return null;
        }

        return heap[(i - (i % 2)) / 2];
    }
}
