import java.io.*;
import java.util.*;

class REsearch{

	//Main
	public static void main(String[] args){
		
		//Check if correct arguement
		if (args.length == 0 || args.length > 1) {
			System.err.println("Arguement required: <filename>");
		} else {

			//Create a pattern searcher
			Pattern p = new Pattern();
			//Set the array that read from standard input
			p.setarrays();
			//Get the file name and begin pattern searching
			p.read(args[0]);
			
		}
	}
}


class Node{
	
	//Node variables
	private String value;
	private Node next;
	private Node prev;

	//Node constructor
	public Node(String v) {
		this.value = v;
	}

	//Three methods to return the required varaibles
	public String getV(){
		return this.value;
	}

	public int getIntV(){
		return Integer.parseInt(this.value);
	}
	
	public Node getN(){
		return this.next;
	}

	public Node getP(){
		return this.prev;
	}

	//Methods to change the required variables
	public void setV(String v){
		this.value = v;
	}

	public void setN(Node n){
		this.next = n;
	}
	
	public void setP(Node p){
		this.prev = p;
	}
}

class Deque<Item>{

	//Variables for the deque
	Node first = new Node("-1");
	Node last = new Node("-1");
	Node scan = new Node("scan");

	//Constructor
	public Deque(){
		scan.setP(first);
		scan.setN(last);
	}
	//get the required values
	public Node getFirst(){
		return this.first;
	}
	
	public Node getLast(){
		return this.last;
	}
	
	
	//Add the first value
	public void addFirst(Node f){
		
		//Check if there is no first value
		if(first.getV().compareTo("-1") == 0){
		
			//Set the first
			first = f;
			//Connect to scan
			first.setN(scan);
			scan.setP(first);
		}
		
		//If there is already a first value
		else{

			//Old first saved
			Node oldFirst = first;
			//Make first the new value
			first = f;
			//connect the new first to the oldfirst
			first.setN(oldFirst);
			//connect oldfirst to the new first
			oldFirst.setP(first);
		}
	}

	//Remove the first value
	public Node removeFirst(){
		
		//pop the first 
		Node popFirst = first;

		//Check if there is a first value
		if(first.getV().compareTo("-1") != 0){
	
			//Check if there in a next
			if(first.getN() != null){
	
				//if first has taken the value of scan meaning no possible curr
				if(first.getV().compareTo("scan") == 0){
					first = first;
				}
				
				//check if the next is scan
				else if(first.getN().getV().compareTo("scan") == 0){
					first = first.getN();
					first.setP(new Node("-1"));
					scan.setP(new Node("-1"));
				}
				//if next is not scan
				else{
					//set the first value the next
					first = first.getN();
					//Make next previous point to null
					first.setP(new Node("-1"));
				}
			}
		}

		return popFirst;
	}


	//Add a last value
	public void addLast(Node l){

		//Check if there is no last value
		if(scan.getN().getV().compareTo("-1") == 0){
			//set the last
			last = l;
			//Make it point to scan
			last.setP(scan);
			scan.setN(last);
		}
		//if there is already a last value
		else{
			//save old last
			Node oldLast = last;
			//set new last
			last = l;
			//make point to the correct value
			oldLast.setN(last);
			//make the last prev old last
			last.setP(oldLast);
		}
	}

	//Switch next possible to current possible
	public void switchpossible(){
	
		if(scan.getN().getV().compareTo("-1") != 0){
			//new first is first last value
			first = scan.getN();
		
			//set previous scan to the current last
			scan.setP(last);
	
			//set last next to scan
			last.setN(scan);
		
			//set scan next to null
			scan.setN(new Node("-1"));

			//set new first previous to null
			first.setP(new Node("-1"));
		}
	}
		
	//test to print the deque and all its states
	public void test(){
		//String n = first.getN().getV();
		Node next = first;
		while(next != null){
			System.out.println(next.getV());
			next = next.getN();
		}
	}

	//checks if the deque is empty
	public boolean Empty(){
		if(scan.getP().getV().compareTo("-1") == 0 && scan.getN().getV().compareTo("-1") == 0){
			return true;
		}
		else{
			return false;
		}
	}	

	//Reset the deque by reseting all the values in it
	public void reset(){
		first.setV(null);
		first.setP(new Node(null));
		first.setN(scan);
		scan.setP(first);
		scan.setN(last);
		last.setV(null);
		last.setP(scan);
		last.setN(new Node(null));	
	}
}

class Pattern{
	//Arrays for the FSM 
	private ArrayList<String> chararr = new ArrayList<String>();
	private ArrayList<String> n1 = new ArrayList<String>();
	private ArrayList<String> n2 = new ArrayList<String>();
	//Deque
	private Deque<Node> de = new Deque<Node>();

	public Pattern(){
	}
	
	public void setarrays(){

		try{
			//get the input
			String line = "";
		    	BufferedReader br = new BufferedReader(new InputStreamReader(System.in));
			String[] arr = new String[5];

			//while not the end
			while((line = br.readLine()) != null){
			
				//Split the line by "," and save values into the arraylists
				arr = line.split(",");
				chararr.add(arr[1]);
				n1.add(arr[2]);
				n2.add(arr[3]);
			}
	
			//initialize the deque with the first value
			de.addFirst(new Node("0"));
			
		}
		
		//Catch the IO excpetion
		catch (IOException ex) {
			System.err.println("Cannot read the file");
		}
	}

	public void read(String f){
		try{
			//get the input from the file
			String line = "";
			String filename = f;
			FileReader fileReader = new FileReader(filename);
		    	BufferedReader br = new BufferedReader(new BufferedReader(fileReader));

			//the string that we are comparing
			String comp = "";
			String machine = "";
			int machineInt = 0;
			
			//integer to keep track of where we are in the machine
			int a = 0;

			//While not the end of the file
			while((line = br.readLine()) != null){

				//Loop through all the characters in the line
				for(int x = 0; x < line.length(); x++){
					//get the currect string
					comp = Character.toString(line.charAt(x));					
										
					//Check if the deque is empty
					if(de.Empty() == false){
						
						//save the machine's location
						Node temp = de.removeFirst();
						machine = temp.getV();
						
						//Loop through the deque to check for matches
						while(machine.compareTo("scan") != 0){

							//get the int of the value to search through each array list
							machineInt = Integer.parseInt(machine);
		
							//checks if the string is a "."
							if(chararr.get(machineInt).compareTo(".") == 0){

								//checks if it is at the final state
								if(machineInt == Integer.parseInt(n1.get(n1.size() - 1)) - 1){
										
										//Match found! display line
										System.out.println(line);
									
										//move to the next line
										a++;
										x = line.length() - 1;
										temp = de.removeFirst();
										machine = temp.getV();
										
									}
									else{

										//checks if the x value for the loop is at the last
										if(x < line.length() - 1){

											//adds next possible
											de.addLast(new Node(n1.get(machineInt)));
	
											//switch possible and move to next char
											de.switchpossible();
											x++;
											comp = Character.toString(line.charAt(x)); 
											temp = de.removeFirst();
											machine = temp.getV();
										}
										else{
											//End of line go to next line
											temp = de.removeFirst();
											machine = temp.getV();	
											x++;	
										}
										
										
									}
								
							}
							
							//check if popped value is a branching state
							//if it is not a branching state
							else if(chararr.get(machineInt).compareTo("~") != 0){
	
								//Check if there is a match
								//if match
								if(comp.compareTo(chararr.get(machineInt)) == 0){

									//Checks if at final state
									if(machineInt == Integer.parseInt(n1.get(n1.size() - 1)) - 1){
										
										//Match found! print the ling
										System.out.println(line);

										//move to the next line
										a++;
										x = line.length() - 1;
										temp = de.removeFirst();
										machine = temp.getV();
										
									}
									else{
										if(x < line.length() - 1){
											de.addLast(new Node(n1.get(machineInt)));
											de.switchpossible();
											x++;
											comp = Character.toString(line.charAt(x)); 
											temp = de.removeFirst();
											machine = temp.getV();
										}
										else{
											temp = de.removeFirst();
											machine = temp.getV();	
											x++;	
										}
										
										
									}
									
									
								}
								//if not match
								else{
									temp = de.removeFirst();
									machine = temp.getV();
									if(machine.compareTo("scan") == 0){
										de.addFirst(new Node("0"));
									}

								}
							}
							//if it is a branching state
							else{

								//add to first the two it points to as possible current
								de.addFirst(new Node(n1.get(machineInt)));
								de.addFirst(new Node(n2.get(machineInt)));

								temp = de.removeFirst();
								machine = temp.getV();
								

							}
							
						}
					}
					//if it is empty
					else{
						//de.test();
						a++;
						x = a;
						de.addFirst(new Node("0"));
					}

					//System.out.println(x);

					
				}
		
				//de.reset();
				//de.test();
				de.addFirst(new Node("0"));
				a = 0;
			}
		}
		
		//Catch the IO excpetion
		catch (IOException ex) {
			System.err.println("Cannot read the file");
		}

	}

}
