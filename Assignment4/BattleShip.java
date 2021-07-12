import java.io.*;
import java.util.*;

class BattleShip{
	
	public static void main (String[] args){
		try{
			//create object that reads file and stores them into 3 arrays for ship, x coordinates, and y coordinates
			Readsfile rf = new Readsfile(args[0]);
			//rf.printit();
			//create map of ships at certain dimensions
			Mainmap mm = new Mainmap(rf.getMaxpos(),rf.getShips(),rf.getXcoor(),rf.getYcoor(), rf.getSn(), Integer.parseInt(args[1]));
			//print map
			mm.printmap();
			//check how well the map scores
			mm.Solve();
		}catch(Exception e){
			System.err.println(e);
		}
	}
}
//reads a file and prints the contents to 3 int arrays
class Readsfile{
	private int[] ships;
	private int[] xcoor;
	private int[] ycoor;
	private String file;
	//the current position in the array we are storing a number in
	private int currpos;
	//grid size
	private int maxpos;
	//number of ships in grid
	private int shipnum = 0;
	//creates arrays and calls to fill array
	public Readsfile(String f){
		file = f;
		ships = new int[30];
		xcoor = new int[30];
		ycoor = new int[30];
		currpos = 0;
		maxpos = 0;
		MakeArrays();
	}
	
	public void MakeArrays(){
		try{
			//read linhes from file
			BufferedReader br = new BufferedReader(new FileReader(file));
			String line = br.readLine();
			//current loop so know what line we are reading
			int currloop = 0;
			while(line != null){
				for(int i = 0; i < line.length(); i++){
					//as long as character is a number save it in the array
					if(Character.isDigit(line.charAt(i))){
						if(currloop == 0){
						ships[currpos] = Character.getNumericValue(line.charAt(i));
						shipnum++;
						} else if(currloop == 1){
						xcoor[currpos] = Character.getNumericValue(line.charAt(i));
						} else {
						ycoor[currpos] = Character.getNumericValue(line.charAt(i));
						}
						currpos++;
					}
				}
				maxpos = currpos;
				currloop++;
				//get new line from file
				line = br.readLine();
				currpos = 0;
			}
			br.close();
		}catch(Exception e){
			System.err.println(e);
		}
	}
	//get the size of the grid
	public int getMaxpos(){
		return maxpos;
	}
	//get the number of ships in the grid
	public int getSn(){
		return shipnum;
	}
	//get the array of ship sizes
	public int[] getShips(){
		return ships;
	}
	//get array of x coordinates(numbers on side x)
	public int[] getXcoor(){
		return xcoor;
	}
	//get array of y coordinates(numbers on side y)
	public int[] getYcoor(){
		return ycoor;
	}
	//print the arrays
	public void printit(){
		String prints = "";
		for(int i = 0; i < maxpos; i++){
			prints = prints +" "+ ships[i];
		}
		System.out.println(prints);
		prints = "";
		for(int i = 0; i < maxpos; i++){
			prints = prints +" "+ xcoor[i];
		}
		System.out.println(prints);
		prints = "";
		for(int i = 0; i < maxpos; i++){
			prints = prints +" "+ ycoor[i];
		}
		System.out.println(prints);
	}
}

class HillClimb{

}
//creates a ship that is made of coordinates in an array, a size and a orientation
class Ship{
	private ArrayList<Integer> surrpos = new ArrayList<Integer>();
	private int size;
	private int gsize;
	private int[] coor;
	private boolean vert;
	//initialises ship
	public Ship(int s, int[] c, boolean v){
		size = s;
		coor = c;
		vert = v;
		
	}
	//return size of ship
	public int getSize(){
		return size;
	}
	//return ships coordiantes
	public int[] getCoor(){
		return coor;
	}
	//set size of grid
	public void setg(int i){
		gsize = i;
	}
	//set the coordinates of ship
	public void setCoor(int[] i){
		coor = i;
	}
	//get orientation
	public boolean getVert(){
		return vert;
	}
	
	public void setVert(boolean b){
		vert = b;
	}
	//set orientation
	public void Left(){
		for(int i = 0; i < size; i++){
			if(i%size != 0){
				int[] tmp;
				tmp = getCoor();
				tmp[i] = tmp[i] - 1;
				setCoor(tmp);	
			}else{
				break;
			}
		}
	}
	//tbd
	public void Right(){
		for(int i = 0; i < size; i++){
			if(i%size != (size - 1)){
				int[] tmp;
				tmp = getCoor();
				tmp[i] = tmp[i] + 1;
				setCoor(tmp);	
			}else{
				break;
			}
		}
	}
	//tbd
	public void Up(){
		for(int i = 0; i < size; i++){
			if((i - size) > 0){
				int[] tmp;
				tmp = getCoor();
				tmp[i] = tmp[i] - size;
				setCoor(tmp);	
			}else{
				break;
			}	
		}

	}
	//tbd
	public void Down(){
		for(int i = 0; i < size; i++){
			if((i + size) < (size*size)){
				int[] tmp;
				tmp = getCoor();
				tmp[i] = tmp[i] + size;
				setCoor(tmp);	
			}else{
				break;
			}	
		}
	
	}
	//tbd
	public void Hori(){
		for(int i = 0; i < size; i++){
		}

	}

	public void empty(){
		surrpos = new ArrayList<Integer>();
	}
	

	//Calculates the sujrrounding
	public ArrayList<Integer> getSurr(){
       
		//Loops for all the coordinates
		for(int i = 0; i < coor.length; i++){
		   
		    int x = coor[i];
	   
		    int y = 0;

			//if it is a first value in the row of a  map do no compute the 3 values
		    if((x % gsize) != 0){

		       
			//Check if the value we want to add is greater than or eq to zero
			//check that it is within the grid
			//check that it is not already ion the array of surrounding
			//Apllies to all the if statements
		        if((y = (x - gsize - 1)) >= 0 && (y = (x - gsize - 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }
		   
		        if((y = (x - 1)) >= 0 && (y = (x - 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }

		        if((y = (x + gsize - 1)) > 0 && (y = (x + gsize - 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }

		    }

			//if it the last value in teh row do not compute the values
		    if((x % gsize) != (gsize - 1)){

		       
		        if((y = (x - gsize + 1)) > 0 && (y = (x - gsize + 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }

		        if((y = (x + 1)) > 0 && (y = (x + 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }

		        if((y = (x + gsize + 1)) > 0 && (y = (x + gsize + 1)) < gsize*gsize && surrpos.indexOf(y) == -1){
		            surrpos.add(y);
		        }

		    }

			//Main values that are always computed
		    if((y = (x - gsize)) >= 0 && (y = (x - gsize)) < gsize*gsize && surrpos.indexOf(y) == -1){
		        surrpos.add(y);
		    }

		    if((y = (x)) >= 0 && (y = (x)) < gsize*gsize && surrpos.indexOf(y) == -1){
		        surrpos.add(y);
		    }

		    if((y = (x + gsize)) > 0 && (y = (x + gsize)) < gsize*gsize && surrpos.indexOf(y) == -1){
		        surrpos.add(y);
		    }
		   
		}

	return this.surrpos;
    }
}


//Makes an array of a grid with a certain size and with 1's representing ships on the grid
class Mainmap{
	private int gridsize;
	private int shipnum;
	private int best;
	//array of current grid
	private int[] grid;
	//array of best solution grid
	private int[] soln;
	//array of number of 1's in a certain column
	private int[] gridx;
	//array of number  of 1's in a certain row
	private int[] gridy;
	//array of x's from file
	private int[] filex;
	//array of y's from file
	private int[] filey;
	//array of ship sizes from file
	private int[] fs;
	//array of ships
	private Ship[] ships;
	//arrays where no ships can be
	private int[] nox;
	private int[] noy;
	private int[] noships;
	private int currno = 0;
	private int numnox = 0;
	private int numnoy = 0;
	private int loopnum = 0;
	//create map and make it the best solution grid so far
	public Mainmap(int i, int[] ship, int[] x, int[] y, int sn, int max){
		loopnum = max;
		int j = (i*i);
		shipnum = sn;
		gridsize = i;
		gridx = new int[i];
		gridy = new int[i];
		grid = new int[j];
		ships = new Ship[sn];
		nox = new int[j];
		noy = new int[j];
		noships = new int[(j + j)];
		filex = x;
		filey = y;
		fs = ship;
		getZero();
		test();
		Makemap();
		soln = grid;
		best = 1000;
		
	}
	//fills map with ships, making sure the are spaced at least one vertical space away(need to implement horizontal space)
	public void Makemap(){
		int currp = 0;
		int count = 0;
		//for each ship in number of ships, initialise the ship with its coordinates,size, orientation, and also let ship know gridsize
		for (int i = 0; i < shipnum; i++){
			int size = 0;
			size = fs[i];
			int[] tmp = new int[size];

			//Get a random value
			Random random = new Random();
			//Value to make point at
			int ran = random.nextInt((gridsize*gridsize));
			//Horizontal or vertical
			int allign = random.nextInt(2);

	

			//&& anyatend(size, ran) == false
			//&& (ran + size) < 36 
			

			//Check if the random exists in the Noship array
			//No ship array keeps track of all points that cannot containa  ship
			if(exists(ran) == false){

				//Check if horizontal
				//Check if it within the grifd
				//Check if it will go to the next line
				//Check if all values are available points
				if(allign == 0 && (ran + size) < gridsize*gridsize && anyatend(size, ran) == false && existsH(ran, size) == false){

					//Loop for the size of the ship and add it to the grid
					for(int j = 0; j < size; j++){
						tmp[j] = ran + j;
						grid[ran + j] = 1;
						currp++;
					}

					//create ship
					Ship ship = new Ship(size,tmp,false);
					ship.setg(gridsize);

					//Update the noships array to hold all the surr of the ship
					update(ship);

					//add ship to main array
					ships[i] = ship;

					//next position	
					currp++;
				}
				
				//Check if vertical
				//check if within the grid
				//Check if all vertical points are available points
				else if(allign == 1 && (ran + (gridsize*size)) < gridsize*gridsize && existsV(ran, size) == false){

					//Loop for the size and create the ship in the grid
					for(int j = 0; j < size; j++){
						tmp[j] = (ran + (j*gridsize));
						grid[ran + (j*gridsize)] = 1;
						currp++;
					}

					//create ship
					Ship ship = new Ship(size,tmp,true);
					ship.setg(gridsize);
					update(ship);
					ships[i] = ship;
					currp++;
				}


				//If it is not possib;le to add  the point 
				else{

					//stay in this loop
					i--;

					//inc count
					count++;

					//if count reaches a certain point
					if(count > 100){

						//Reset the map
						count = 0;
						i = -1;
						reset();
						noships = new int[gridsize*gridsize*gridsize];
						currno = 0;
						getZero();
					}
				
				}
				
			}


			//Same as above to reset the map
			else{
				i--;
				count++;
				if(count > 100){
					count = 0;
					i = -1;
					reset();
					noships = new int[gridsize*gridsize*gridsize];
					currno = 0;
					getZero();
					
				}
				
			}

		}
	}

	//is at end
	public boolean anyatend(int size, int random){

		boolean val = false;		
		
		for(int i = 0; i < size; i++){
			if((random + i) % gridsize == 0){
				val = true;
			}
			else{
				val = false;
			}
		}

		return val;
	}
	//checks ship is away from other ships
	public boolean checkship(Ship ship){
		for(int i = 0; i < ship.getSize(); i++){
			if(ship.getVert()){
				//for(int i = 0; )
				return true;
			}else{
				return false;
			}
		}
		return false;

	}


	//Adds all the zero rows and zero columns to the no ship array
	public void getZero(){

		int currloop = 0;
		for(int i = 0; i < gridsize; i++){
			//System.out.println(filex[i]);
			if(filex[i] == 0){
				for(int j = 0; j < (gridsize*gridsize); j=j+gridsize){
					nox[currloop] = i + j;
					noships[currno] = nox[currloop];
					currno++;
					currloop++;
					numnox++;
				}
				currloop = 0;	
			}
			if(filey[i] == 0){
				for(int j = 0; j < gridsize; j++){
					noy[j] = (i*gridsize) + j;
					noships[currno] = noy[j];
					currno++;
					numnoy++;
				}	
			}
		}


	}
	//checks the current score of the map and how close to the solution it is
	public void checkMap(){
		int currX = 0;
		int currY = 0;
		int currloop = 0;
		int matches = 0;
		int score = 0;
		//for each 1 in a certain column, add it together and print total
		for(int j = 0; j < gridsize; j++){
			for(int i = 0 + j; i < grid.length; i = i+gridsize){
				if(grid[i] == 1){
					currX++;
				}
			}
			//System.out.println("x " +j+ " has: " + currX + " many hits, we are looking for: " +filex[j]);
			score = score + Math.abs(currX-filex[j]);
			gridx[j] = currX;
			currX = 0;
		}
		//for each 1 in a certain row, add it together and print total
		for(int i = 0; i < grid.length; i = i+gridsize){
			for(int j = 0+i; j < gridsize+i; j++){
				if(grid[j] == 1){
					currY++;
				}
			}
			//System.out.println("Y " +currloop+ " has: " + currY + " many hits, we are looking for: " +filey[currloop]);
			score = score + Math.abs(currY-filey[currloop]);
			gridy[currloop] = currY;
			currY = 0;
			currloop++;
		}
		//if a row/column matches that from the file then state it
		for(int i = 0; i < gridsize; i++){
			if(gridx[i] == filex[i]){
				matches++;
			}
			if(gridy[i] == filey[i]){
				matches++;
			}
		}
		//if solution found
		if(matches == (gridsize+gridsize)){
			System.err.print("well done");
		}
		//else if the current score is better than the last saved score, replace it. 
		if(score < best){
			soln = grid;
			best = score;
			System.err.print("Best so far");
		}
		System.out.println(" The score is:" + score);
		
	}

	//Gets the best solution
	public void Solve(){

		for(int i = 0; i < loopnum; i++){
			Makemap();
			checkMap();
			System.out.println("current Best: " + best);
		}
		
		printmap();
	}
	//prints the current map
	public void printmap(){
		String line = "";
		for(int i = 0; i < gridsize; i++){
			for (int j = 0; j < gridsize; j++){
				line = line +" "+soln[j+(i*gridsize)];
			}
			System.out.println(line);
			line = "";
		}
	}


	//resets the map
	public void reset(){
		for(int i = 0; i < grid.length; i++){
			grid[i] = 0;
		}
	}


	//Checks if the  parameter exists in the noship array
	public boolean exists(int val){
		
		boolean checker = false;
		
		for(int i = 0; i < currno; i++){
			if(noships[i] == val){
				checker = true;
				break;
			}
			else{
				checker = false;
			}
		}

		return checker;
	}


	//Checks if any of the Horizontal points are in the noshiparray
	public boolean existsH(int val, int size){
		
		boolean checker = false;

		for(int x = 0; x < size; x++){
			for(int i = 0; i < currno; i++){
				if(noships[i] == (val + x)){
					checker = true;
					break;
				}
			}
		}

		return checker;
	}


	//Check if any of the Vertical exists in the no ship array
	public boolean existsV(int val, int size){
		
		boolean checker = false;

		for(int x = 0; x < size; x++){
			for(int i = 0; i < currno; i++){
				if(noships[i] == (val + (x*gridsize))){
					checker = true;
					break;
				}
			}
		}

		return checker;
	}

	//Update the noshiparray to hold the surrounding of a  certain ship
	public void update(Ship ship){

		ArrayList<Integer> surrpos = ship.getSurr();

			for(int i = 0; i < surrpos.size(); i++){

				if(exists(surrpos.get(i)) == false){
					noships[currno] = surrpos.get(i);
					currno++;
				}
			}
	}


	//Print noship array for testing purposes
	public void test(){
		for(int i = 0; i < currno; i++){
			System.out.println(noships[i]);
		}
	}





}

















