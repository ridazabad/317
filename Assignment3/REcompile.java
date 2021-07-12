import java.io.*;
import java.util.*;

// E -> T
// E -> TE
// T -> F*
// T -> F+
// T -> F?
// T -> F|T
// F -> v
// F -> .
// F -> (E)
// F -> [E]
// F -> ![E]!

//Calls a Finite state machine to be made from the regex pattern entered if it is valid regex pattern
class REcompile{
	public static void main(String [] args){
		try{
			//calls finite state machine to be made and regex to be parsed and then printed
			FSM fsm = new FSM(args[0]);
			fsm.parser();
			//fsm.printit();
		}catch(Exception e){
			System.err.println(e);
		}
	}
}
//Takes a regex pattern and turns it into a Finite state machine
class FSM{
	private int state;
	private char [] chc;
	private char [] fsmc;
	private char [] notchc;
	private char [] unfill;
	private int [] next1;
	private int [] next2;
	private int scurr = 0;
	private int curr = 0;
	private int saved = 0;
	private int diff = 0;
	private String regex;
	private boolean isregex = true;
	private boolean issb = false;
	private boolean cont = false;
	//Constructor that takes a regex string as an parameter
	public FSM(String regex){
		state = 0;
		chc = new char[10000];
		fsmc = new char[10000];
		unfill = new char[10];
		next1 = new int [10000];
		next2 = new int [10000];
		//Initialises an array of special characters
		notchc = new char[]{'|','*','+','?','.','(',')','[',']','\\'};
		this.regex = regex; 
		FillArray();
	}
	//Fills a char array with the characters of the regex string
	public void FillArray(){
		for(int i = 0; i < regex.length(); i++){
			chc[i] = regex.charAt(i);
		}
	}
	//Checks if character is a special character
	public boolean isLit(char c){
		for(int i = 1; i < 10; i++){
			if(c == notchc[i]){
				return false;
			}
		}
		return false;
	}
	//Check if character is apart of literal vocab
	public boolean isvocab(char c){
		char tmp;
		tmp = c;
		//System.out.println(Character.isLetter(tmp.charAt(0)));
		if(Character.isLetter(tmp)){
			return true;
		}else if(isLit(tmp)){
			return true;
		}else{
			return false;
		}
	}
	//Sets the index of each array to the specified value
	public void setState(int cstate, char ch, int n1, int n2){
		fsmc[cstate] = ch;
		next1[cstate] = n1;
		next2[cstate] = n2;
	}
	//parse an expression into an finite state machine
	public void parser(){
		int r = 0;
		setState(state,'~',(state+1),(state+1));
		state++;
		r = expression();
		//if the expression is a valid regex print it out
		if((curr == (regex.length())) && (isregex)){
			//System.err.println("Valid regex");
			printit();
		//if the expression is a valid regex print it out 
		}else if ((curr == (regex.length()+diff-1)) && (isregex)){
			//System.err.println("Valid regex");
			printit();
		//if the expression is not a valid regex dont print it out
		}else{
			error();
			isregex = true;
			System.err.println("Problem 7");
		}
	}
	//Takes the expression and tries to put it into a fsm as long as there are valid characters in it
	public int expression(){
		int r = term();
		if((isvocab(chc[curr])) || (chc[curr] == '(') || (chc[curr] == '.') || (chc[curr] == '[') || (chc[curr] == '\\') ||(chc[curr] == '!') || issb){
			expression();
		}
		return r;
	}
	//Takes an expression and tries to put it in to an fsm
	public int term(){
		int r = 0;
		int t1 = 0;
		int t2 = 0;
		int f = 0;
		f = state - 1; 
		//if it is inside a square bracket
		if(issb){
			r = t1 = factorv2();
		}else{
			r = t1 = factor();
		}
		//if the current char being looked at is * then make it so the fsm has the correct branching states
		if(chc[curr] == '*'){
			setState(state,'~',(state+1),t1);
			curr++;
			r = state;
			if(next1[f] == next2[f]){
				next2[f] = state;
			}
			next1[f] = state;
			//if the char ? was seen less than 2 spaces ago then but more than the current then make the previous next states branch to the correct state 
			if((f == saved) && (f > 0)){
				if(next1[saved-1] == next2[saved-1]){
					next2[saved-1] = state;
				}
				next1[saved-1] = state;
				saved = 0;
			}
			state++;
		//if the current char being looked at is + then make it so the fsm has the correct branching states
		}else if(chc[curr] == '+'){
			setState(state,'~',(state+1),t1);
			curr++;
			r = state;
			state++;
		//if the current char being looked at is ? then make it so the fsm has the correct branching states
		}else if(chc[curr] == '?'){
			setState(state,'~',(state+1),t1);
			curr++;
			r = state;
			if(next1[f] == next2[f]){
				next2[f] = state;
			}
			next1[f] = state;
			if(next1[t1] == next2[t1]){
				next2[t1] = state+1;
			}
			next1[t1] = state+1;
			//if the char ? was seen less than 2 spaces ago then but more than the current then make the previous next states branch to the correct state 
			if((f == saved) && (f > 0)){
				if(next1[saved-1] == next2[saved-1]){
					next2[saved-1] = state;
				}
				next1[saved-1] = state;
				saved = 0;
			}
			saved = state;
			state++;
		//if the current char being looked at is | then make it so the fsm has the correct branching states
		}else if(chc[curr] == '|'){
			if(next1[f] == next2[f]){
				next2[f] = state;
			}
			next1[f] = state;
			f = state - 1;
			curr++;
			r = state;
			state++;
			t2 = term();
			setState(r,'~',t1,t2);
			if(next1[f] == next2[f]){
				next2[f] = state;
			}
			next1[f] = state;
		}
		return r;
	}
	//If the current chars are inside a [] then every symbol is treated as a literal 
	public int factorv2(){
		int r = 0;
		if(chc[curr] != '|'){
			setState(state,chc[curr],(state+1),(state+1));
			curr++;
			r = state;
			state++;
		}
		if(chc[curr] == ']'){
			//System.err.println("The current: " + chc[curr] + "the state is" + state);
			issb = false;
		}
		return r;
	}
	//Checks if the item being looked at is a literal,wildcard, bracket or escape sybmol.
	public int factor(){
		int r = 0;
		//if the char is a literal then set state
		if((isvocab(chc[curr]))){
			setState(state,chc[curr],(state+1),(state+1));
			curr++;
			r = state;
			state++;
		//if the char is a wildcard then setstate with the . as the curr rather than a branching statement
		}else if (chc[curr] == '.'){
			setState(state,'.',(state + 1),(state + 1));
			curr++;
			r = state;
			state++;
		//if the char is a backslash then make sure the following symbol losses meaning and is a literal
		}else if (chc[curr] == '\\'){
			curr++;
			if(!isvocab(chc[curr])){
				setState(state,chc[curr],(state+1),(state+1));
				r = state;
				state++;
				curr++;
			//if the following char is not a special symbol then there this is not a valid regex
			}else{
				error();
				System.err.println("Problem 6");
			}
		}else{
			//If the char is a ( then call expression for everything inside the bracket 
			if(chc[curr] == '('){
				curr++;
				r = expression();
				//if bracket does not close then there is an error otherwise continue
				if(chc[curr] == ')'){
					curr++;
				}else{
					error();
					System.err.println("Problem 5");
				}
			//If the char is a [ then call expression for everything inside the bracket 
			}else if(chc[curr] == '['){
				curr++;
				//change inside expression to alternations between literals
				sbrack();
				issb = true;
				r = expression();
				issb = false;
				//if bracket does not close then there is an error otherwise continue
				if(chc[curr] == ']'){
					curr++;
				}else{
					error();
					System.err.println("Problem 4");
				}
			//if the char is a ! then check if the next statement is [, if so then everything inside the brackets are an expression
			}else if(chc[curr] == '!'){
				curr++;
				if(chc[curr] == '['){
					curr++;
					r = expression();
					//if expression doesnt end with ]! then there is an error otherwise continue
					if(chc[curr] == ']'){
						curr++;
						if(chc[curr] == '!'){
							curr++;
						}else{
							error();
							System.err.println("Problem 3");
						}
					}else{
						error();
						System.err.println("Problem 2");
					}
				}
			}else{
				error();
				System.err.println("Problem 1");
			}
		}
		return r;
	}
	//loops thru literals in [] and makes it so the space between each literal(this includes special characters) has an | symbol meaning alteration between each literal 
	public void sbrack(){
		scurr = curr + 1;
		int k = curr;
		int l = 0;
		int check = curr;
		int end = regex.length();
		//if the first char in the [] expression is ] then take it as a literal, otherwise move forward
		if(chc[check] == ']'){
			check++;
		}
		while(chc[check] != ']'){
			if(check < end){
				check++;
			}else{
				error();
				System.err.println("hi");
				break;
			}
		}
		diff = check - curr;
		if(isregex){
			//for each char that isnt the last in the [] expression set a | between each literal
			for(int j = 0; j < (diff - 1); j++){
				for(int i = 0; i < (end - k); i++){
					chc[(end-i+1)] = chc[(end-i)]; 
				}
				k = k + 2;
				end++;
				l = (curr + 1) + (j*2);
				chc[l] = '|';
			}
			for(int i = curr; i < (regex.length() + diff);i++){
				//System.err.println(chc[i]);
			}
		}else{
			System.err.println("Problem 9");
		}
	}
	//if there is an error and the regex is not valid then show it
	public void error(){
		System.err.println("Not a valid regex");
		isregex = false;
	}
	//print the finite state machine
	public void printit(){
		for (int i = 0; i < state; i++){
			System.out.println(i +","+ fsmc[i]+","+ next1[i]+","+""+ next2[i]);
		}
	}
}

