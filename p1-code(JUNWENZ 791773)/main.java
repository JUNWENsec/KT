/* author JUNWEN ZHANG 791773
*/
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class main {
	
	public static int InsertionCost;
	public static int DeletionCost;
	public static int ReplacementCost;
	public static int MatchCost;
	public static double Replacement[][] = new double [26][26];
	public final static int MAXDISTANCE = 9000000;

	public static void main(String[] args) {
		Scanner keyboard = new Scanner (System.in);
		System.out.println("Please enter the root for train text: ");
		String trainPath = keyboard.nextLine();
		System.out.println("Please enterh the root for name text: ");
		String namesPath = keyboard.nextLine();
		boolean run = true;
		while(run){
			run = false;
			System.out.println("choose the method you want to use: "+ "\n"
								+"1. basic Global Edit Distance(0,1,1,1)" + "\n"
								+"2. Global Edit Distance with replacement matrix(0,1,3,matrix)" +"\n"
								+"3. Local Edit Distance(1,-1,-1,-1)" + "\n"
								+"4. N-gram(N=2)"+ "\n"
								+"5. Soundex" + "\n"
								+"the method you want to use is : ");
			int methodNum =0;
			methodNum = keyboard.nextInt();
			switch(methodNum){
			case 1:
				globalEditDistance(trainPath,namesPath);
				break;
			case 2:
				globalEDwithReplace(trainPath,namesPath);
				break;
			case 3:
				localEditDistance(trainPath,namesPath);
				break;
			case 4:
				Ngram(trainPath,namesPath);
				break;
			case 5:
				soundex(trainPath,namesPath);
				break;
			default:
				System.out.println("Wrong method number!");
				break;
			}
			System.out.println("Do you want to change the method?(Y/N) ");
			String flag = keyboard.nextLine();
			flag = keyboard.nextLine();
			if(flag.equals("Y")){run = true;}
			else {System.out.println("end of the system");}
		}
	}
	
	// get the precision and recall through Global Edit Distance method
	public static void globalEditDistance(String trainPath, String namesPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		//String pathNames = "/Users/junwenz/Documents/project/kt/src/kt/names.txt";
		String pathTrain = trainPath;
		String pathNames = namesPath;
		File trainFile=new File(pathTrain);
        if(!trainFile.exists()){ 
            System.out.println("no train file: " + pathTrain);
        }
        File libraryFile=new File(pathNames);
        if(!libraryFile.exists()){ 
            System.out.println("no train file: " + pathNames);
        }
        try {
            BufferedReader train = new BufferedReader(new FileReader(trainFile));
            String trainStr;
            float result = 0;
            float correct = 0;
            int count =0;   
            
            while (( trainStr = train.readLine()) != null) {
            	String[] ft=trainStr.split("\\s+");
                String trainStrf = ft[0];
                String trainStrt = ft[1];
                BufferedReader library = new BufferedReader(new FileReader(libraryFile));
                String libraryStr;
                int minScore = MAXDISTANCE;
                String matchStr = "matchstr";
                setCost(0,1,1,1);
                while ((libraryStr = library.readLine()) != null){
                	int tempScore = getGlobalDistance(trainStrf, libraryStr);
                	if(tempScore < minScore){
                		minScore = tempScore;
                	}
                }
                BufferedReader tie = new BufferedReader(new FileReader(libraryFile));
                while ((libraryStr = tie.readLine()) != null){
                	int tempScore = getGlobalDistance(trainStrf, libraryStr);
                	if(tempScore == minScore){
                		count = count +1;
                		if(libraryStr.equals(trainStrt)){
                			correct = correct +1;
                			matchStr = libraryStr;
                		}
                	}
                }
                result = result+1;
                //System.out.println("original:"+trainStrf+"+"+"test:"+trainStrt+"+"+"dic:"+matchStr);
                //System.out.println("c:"+correct+"result"+result+"recall"+count);
                library.close();            	
            }
            float precision = (correct/count)*100;
            float recall = (correct/result)*100;
            System.out.println("Global Edit Distance is: preision is: "+precision  +  " recall is: " + recall );
            train.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }     
	}
	
	// get the precision and recall through Global Edit Distance with replacement matrix method
	public static void globalEDwithReplace(String trainPath, String namesPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		//String pathNames = "/Users/junwenz/Documents/project/kt/src/kt/names.txt";
		String pathTrain = trainPath;
		String pathNames = namesPath;
		File trainFile=new File(pathTrain);
        if(!trainFile.exists()){ 
            System.out.println("no train file: " + pathTrain);
        }
        File libraryFile=new File(pathNames);
        if(!libraryFile.exists()){ 
            System.out.println("no train file: " + pathNames);
        }
        try {
            BufferedReader train = new BufferedReader(new FileReader(trainFile));
            String trainStr;
            float result = 0;
            float correct = 0;
            int count =0;   
            getReplacementMartixFrequency(pathTrain);
            while (( trainStr = train.readLine()) != null) {
            	String[] ft=trainStr.split("\\s+");
                String trainStrf = ft[0];
                String trainStrt = ft[1];
                BufferedReader library = new BufferedReader(new FileReader(libraryFile));
                String libraryStr;
                int minScore = MAXDISTANCE;
                //String matchStr = "matchstr";
                while ((libraryStr = library.readLine()) != null){
                	int tempScore = getGlobalMartixScore(trainStrf, libraryStr);
                	if(tempScore < minScore){
                		minScore = tempScore;
                	}
                }
                BufferedReader tie = new BufferedReader(new FileReader(libraryFile));
                while ((libraryStr = tie.readLine()) != null){
                	int tempScore = getGlobalMartixScore(trainStrf, libraryStr);
                	if(tempScore == minScore){
                		count = count +1;
                		//System.out.println(trainStrf+"  "+libraryStr+"   "+trainStrt);
                		//System.out.println(minScore+"  "+ getGlobalMartixScore(trainStrf, trainStrt));
                		if(libraryStr.equals(trainStrt)){
                			correct = correct +1;
                			//matchStr = libraryStr;
                		}
                	}
                }
                result = result+1;
                //System.out.println(trainStrf+" "+trainStrt+" "+minScore+" "+);
                //System.out.println("c:"+correct+"result"+result+"tie"+count);
                library.close();            	
            }
            float precision = (correct/count)*100;
            float recall = (correct/result)*100;
            System.out.println("Global Edit Distance with replacement matrix: precision is: "+precision+" recall is: "+recall);
            train.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }     
	}
	
	//get precision and recall through Local Edit Distance method
	public static void localEditDistance(String trainPath, String namesPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		//String pathNames = "/Users/junwenz/Documents/project/kt/src/kt/names.txt";
		String pathTrain = trainPath;
		String pathNames = namesPath;
		File trainFile=new File(pathTrain);
        if(!trainFile.exists()){ 
            System.out.println("no train file: " + pathTrain);
        }
        File libraryFile=new File(pathNames);
        if(!libraryFile.exists()){ 
            System.out.println("no train file: " + pathNames);
        }
        try {
            BufferedReader train = new BufferedReader(new FileReader(trainFile));
            String trainStr;
            float result = 0;
            float correct = 0;
            int count =0; 
            while (( trainStr = train.readLine()) != null) {
            	String[] ft=trainStr.split("\\s+");
                String trainStrf = ft[0];
                String trainStrt = ft[1];
                BufferedReader library = new BufferedReader(new FileReader(libraryFile));
                String libraryStr;
                int maxScore = 0;
                //String matchStr = "matchstr";
                while ((libraryStr = library.readLine()) != null){
                	int tempScore = getLocalDistance(trainStrf, libraryStr);
                	if(tempScore > maxScore){
                		maxScore = tempScore;
                	}
                }
                BufferedReader tie = new BufferedReader(new FileReader(libraryFile));
                while ((libraryStr = tie.readLine()) != null){
                	int tempScore = getLocalDistance(trainStrf, libraryStr);
                	if(tempScore == maxScore){
                		count = count +1;
                		if(libraryStr.equals(trainStrt)){
                			correct = correct +1;
                			//matchStr = libraryStr;
                		}
                	}
                }
                result = result+1;
                //System.out.println("original:"+trainStrf+"+"+"test:"+trainStrt+"+"+"dic:"+matchStr);
                //System.out.println("c:"+correct+"result"+result+"recall"+count);
                library.close();            	
            }
            float precision = (correct/count)*100;
            float recall = (correct/result)*100;
            System.out.println("Local Edit Distance: precision is: "+precision + " recall is: " + recall);
            train.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }  
	}
	
	//get the precision and recall through N-gram method
	public static void Ngram(String trainPath, String namesPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		//String pathNames = "/Users/junwenz/Documents/project/kt/src/kt/names.txt";
		String pathTrain = trainPath;
		String pathNames = namesPath;
		File trainFile=new File(pathTrain);
        if(!trainFile.exists()){ 
            System.out.println("no train file: " + pathTrain);
        }
        File libraryFile=new File(pathNames);
        if(!libraryFile.exists()){ 
            System.out.println("no train file: " + pathNames);
        }
        try {
            BufferedReader train = new BufferedReader(new FileReader(trainFile));
            String trainStr;
            float result = 0;
            float correct = 0;
            int count =0;   
            while (( trainStr = train.readLine()) != null) {
            	String[] ft=trainStr.split("\\s+");
                String trainStrf = ft[0];
                String trainStrt = ft[1];
                BufferedReader library = new BufferedReader(new FileReader(libraryFile));
                String libraryStr;
                int minScore = 90000000;
                while ((libraryStr = library.readLine()) != null){
                	int tempScore = ngram(2,trainStrf.toLowerCase(), libraryStr);
                	if(tempScore < minScore){
                		minScore = tempScore;
                	}
                }
                BufferedReader tie = new BufferedReader(new FileReader(libraryFile));
                while ((libraryStr = tie.readLine()) != null){
                	int tempScore = ngram(2,trainStrf.toLowerCase(), libraryStr);
                	if(tempScore == minScore){
                		count = count +1;
                		if(libraryStr.equals(trainStrt)){
                			correct = correct +1;
                		}
                	}
                }
                result = result+1;
                //System.out.println("original:"+trainStrf+"+"+"test:"+trainStrt+"+"+"dic:");
                //System.out.println("c:"+correct+"result"+result+"recall"+count);
                library.close();            	
            }
            float precision = (correct/count)*100;
            float recall = (correct/result)*100;
            System.out.println("ngram: "+"precision is: "+ precision +" recall is: " + recall );
            train.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }  
	
	}
	
	// get the precision and recall through Soundex method
	public static void soundex(String trainPath, String namesPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		//String pathNames = "/Users/junwenz/Documents/project/kt/src/kt/names.txt";
		String pathTrain = trainPath;
		String pathNames = namesPath;
		File trainFile=new File(pathTrain);
        if(!trainFile.exists()){ 
            System.out.println("no train file: " + pathTrain);
        }
        File libraryFile=new File(pathNames);
        if(!libraryFile.exists()){ 
            System.out.println("no train file: " + pathNames);
        }
        try {
            BufferedReader train = new BufferedReader(new FileReader(trainFile));
            String trainStr;
            float result = 0;
            float correct = 0;
            int count =0;   
            while (( trainStr = train.readLine()) != null) {
            	String[] ft=trainStr.split("\\s+");
                String trainStrf = ft[0];
                String trainStrt = ft[1];
                String f = soundexScore(trainStrf);
                String t;
                BufferedReader library = new BufferedReader(new FileReader(libraryFile));
                String libraryStr;
                int maxScore = 0;
                while ((libraryStr = library.readLine()) != null){
                	t = soundexScore(libraryStr);
                	if(f.equals(t)){
                		count ++;
                		//System.out.println(trainStrf+" "+libraryStr+"\n");
                		if(libraryStr.equals(trainStrt)){
                			correct++;
                		}
                	}
                }
                result = result+1;
                //System.out.println("original:"+trainStrf+"+"+"test:"+trainStrt+"+"+"dic:"+matchStr);
                //System.out.println("c:"+correct+"result"+result+"recall"+count);
                library.close();            	
            }
            float recall = (correct/result)*100;
            float precision = (correct/count)*100;
            System.out.println("soundex: precision is: "+ precision +" recall is: " + recall );
            train.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }  
	}
	
	//global edit distance and trace the best way to change string f to string t
	//and get the replacement martix
	//from string f to string t, t from the dictionary and return distance
	public static int getGlobalDistance(String f,String t){
		//setCost(0,1,1,1);
		f = f.toLowerCase();
		int lt = t.length();
		int lf = f.length();
		char t_i; // ith character of t 
	    char f_j; // jth character of f
		int Score[][] = new int [lt+1][lf+1];
		int cost;//element in array Score
		char Trace[][]= new char[lt+1][lf+1];
		//setCost(0,2,2,1);
		Trace[0][0] = 'm';
		Score[0][0] = 0;
		for(int i = 1;i<=lt;i++){
			Score[i][0] = i*InsertionCost;
			Trace[i][0] = 'i';
		}
		for(int j = 1;j<=lf;j++){
			Score[0][j] = j*DeletionCost;
			Trace[0][j] = 'd';
		}
		
		int i,j=0;
		for (i = 1; i <= lt; i++) {
			t_i = t.charAt(i - 1); 
	        for (j = 1; j <= lf; j++) {
	        	f_j = f.charAt(j - 1);   
	        	if(t_i == f_j){cost = MatchCost;}
                else{cost =  ReplacementCost;}  
	            Score[i][j] = Minimum(Score[i - 1][j] + InsertionCost,
	            		Score[i][j - 1] + DeletionCost,Score[i - 1][j - 1] + cost);  
	            if(Score[i][j]==Score[i - 1][j - 1] + MatchCost){
	                Trace[i][j]='m';
	            } else if (Score[i][j]==Score[i - 1][j] + InsertionCost){
	                Trace[i][j]='i';
	            } else if (Score[i][j]==Score[i][j - 1] + DeletionCost){
	                Trace[i][j]='d';
	            } else {
	                Trace[i][j]='r';
	            }
	                
	        }  
	    }
	    i=lt;
	    j=lf;
		while(i>0&&j>0){
			if(Trace[i][j]=='r'){
				f_j = f.charAt(j - 1);
				t_i = t.charAt(i - 1);
				boolean bool = true;
				if(f_j == '\''){bool = false;}
				if(bool){
					int n = f_j -97;
					int m = t_i -97;
					
					Replacement[n][m] = Replacement[n][m]+1;
				}
				i=i-1;
				j=j-1;
			} else if (Trace[i][j]=='m'){
				i=i-1;
				j=j-1;
			} else if (Trace[i][j]=='i'){j=j-1;
			} else if (Trace[i][j]=='d'){i=i-1;}
		}
		int finalScore = Score[lt][lf] ;
		//System.out.println(finalScore);
		/*
		for (int p = 0; p <= lt; p++) {  
            for (int q = 0; q <= lf; q++) {  
                System.out.print(Trace[p][q]+" ");  
            } 
            System.out.println();
		}
		*/
		
	    return finalScore;		
    }
	
	public static void getReplacementMartixSpread(){
		String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		File myFile=new File(pathTrain);
        if(!myFile.exists()){ 
            System.out.println("no file: " + pathTrain);
        }
        try {
            BufferedReader txt = new BufferedReader(new FileReader(myFile));
            String str;
            while ((str = txt.readLine()) != null) {
            	String[] ft=str.split("\\s+");
                String f = ft[0];
                String t = ft[1];
                setCost(0,1,1,1);
                int score = getGlobalDistance(f, t);
                //System.out.println(score);
            	//System.out.println("f:"+f + "+" +"t:"+t);
            }
            //print out replacement martix
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		System.out.print(Replacement[i][j]+"  ");
            	}
            	System.out.println("");
            }
            //
            //get the frequency of replacement
            setCost(0,1,2,1);
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		if(Replacement[i][j] == 0){Replacement[i][j]=DeletionCost;}
            		else if(Replacement[i][j]>50){Replacement[i][j]=InsertionCost-1;}
            		else {Replacement[i][j]=InsertionCost;}
            	}
            }
            System.out.println(DeletionCost);
            System.out.print("replace");
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		System.out.print(Replacement[i][j]+"  ");
            	}
            	System.out.println("");
            }
          
            txt.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }
        
	}
	
	// get the replacement matrix and turn it into the replacmentScore matrix
	public static void getReplacementMartixFrequency(String trainPath){
		//String pathTrain = "/Users/junwenz/Documents/project/kt/src/kt/train.txt";
		String pathTrain = trainPath;
		File myFile=new File(pathTrain);
        if(!myFile.exists()){ 
            System.out.println("no file: " + pathTrain);
        }
        try {
            BufferedReader txt = new BufferedReader(new FileReader(myFile));
            String str;
            while ((str = txt.readLine()) != null) {
            	String[] ft=str.split("\\s+");
                String f = ft[0];
                String t = ft[1];
                setCost(0,1,1,1);
                int score = getGlobalDistance(f, t);
            }
            // print out replacement martix
            /*
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		
            		System.out.print(Replacement[i][j]+"  ");
            	}
            	System.out.println("");
            }
            */
            
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		if(Replacement[i][j] == 0){Replacement[i][j]=3;}
            		else if(Replacement[i][j]>70) 
            		{Replacement[i][j]=0;}
            		else{Replacement[i][j]=2;}
            	    }
            }
            
           /*
            System.out.println("replace");
            for(int i=0;i<26;i++){
            	for(int j=0;j<26;j++){
            		System.out.print(Replacement[i][j]+"  ");
            	}
            	System.out.println("");
            }
            //System.out.println(MatchCost,InsertionCost,DeletionCost);
             * 
             */
            txt.close();
        } 
        catch (IOException e) 
        {
            e.getStackTrace();
        }
        
	}
	
	//get global edit distance with replacement martix score
	public static int getGlobalMartixScore(String f,String t){
		setCost(0,1,3,1);
		f = f.toLowerCase();
		int lt = t.length();
		int lf = f.length();
		char t_i; // ith character of t 
        char f_j; // jth character of f
		int Score[][] = new int [lt+1][lf+1];
		int cost;//element in array Score
		double replacementCost = 0;
		Score[0][0] = 0;
		for(int i = 1;i<=lt;i++){
			Score[i][0] = i*InsertionCost;
		}
		for(int j = 1;j<=lf;j++){
			Score[0][j] = j*DeletionCost;
		}
		int i,j=0;
		for (i = 1; i <= lt; i++) {  
            t_i = t.charAt(i - 1); 
            for (j = 1; j <= lf; j++) {  
                f_j = f.charAt(j - 1);
                boolean bool = f_j == '\''?false:true;
                if(bool){
                	replacementCost = Replacement[f_j -97][t_i-97];
                } else {replacementCost = InsertionCost;}
                if(t_i == f_j){cost = MatchCost;}
                else{cost =  (int) replacementCost;} 
                Score[i][j] = Minimum(Score[i - 1][j] + InsertionCost, Score[i][j - 1] + DeletionCost,  
                		Score[i - 1][j - 1] + cost);  
                //boolean bool = f_j == '\''?false:true;
                
            }  
        }
		int finalScore = Score[lt][lf] ;
		//System.out.println(finalScore);
		/*
		for (int p = 0; p <= lt; p++) {  
            for (int q = 0; q <= lf; q++) {  
                System.out.print(Score[p][q]+" ");  
            } 
            System.out.println();
		}
		*/
        return finalScore;
			
	}
	
	//use local edit distance to get the score between two strings
	public static int getLocalDistance(String f, String t){
		f = f.toLowerCase();
		int lt = t.length();
		int lf = f.length();
		char t_i; // ith character of t 
        char f_j; // jth character of f
		int Score[][] = new int [lt+1][lf+1];
		int maxScore = 0;
		setCost(1,-1,-1,-1);
		int cost;//element in array Score
		Score[0][0] = 0;
		for(int i = 1;i<=lt;i++){
			Score[i][0] = 0;
		}
		for(int j = 1;j<=lf;j++){
			Score[0][j] = 0;
		}
		int i,j=0;
		for (i = 1; i <= lt; i++) {  
            t_i = t.charAt(i - 1); 
            for (j = 1; j <= lf; j++) {  
                f_j = f.charAt(j - 1);   
                cost = (t_i == f_j) ? MatchCost : ReplacementCost;  
                Score[i][j] = Maximum(Score[i - 1][j] + InsertionCost, Score[i][j - 1] + DeletionCost,  
                		Score[i - 1][j - 1] + cost);
                Score[i][j] = Score[i][j] > 0 ? Score[i][j]:0;
                maxScore = Score[i][j] > maxScore ? Score[i][j]:maxScore; 
                }
            }  
		int finalScore = maxScore;
		//System.out.println(finalScore);
		/*
		for (int p = 0; p <= lt; p++) {  
            for (int q = 0; q <= lf; q++) {  
                System.out.print(Score[p][q]+" ");  
            } 
            System.out.println();
		}
		*/
        return finalScore;
	}
	
	// set the parameter
	public static void setCost(int m,int i,int d, int r ){
		InsertionCost = i;
		DeletionCost = d;
		ReplacementCost = r;
		MatchCost = m;
	}
	
	//get the Soundex string of the string s 
	public static String soundexScore(String s){
        char[] x = s.toUpperCase().toCharArray();
        char ca = x[0];
        String firstChar = String.valueOf(ca);

        for (int i = 0; i < x.length; ++i) {
            switch (x[i]) {
                case 'B':
                case 'F':
                case 'P':
                case 'V': {
                    x[i] = '1';
                    break;
                }

                case 'C':
                case 'G':
                case 'J':
                case 'K':
                case 'Q':
                case 'S':
                case 'X':
                case 'Z': {
                    x[i] = '2';
                    break;
                }
                case 'D':
                case 'T': {
                    x[i] = '3';
                    break;
                }

                case 'L': {
                    x[i] = '4';
                    break;
                }

                case 'M':
                case 'N': {
                    x[i] = '5';
                    break;
                }

                case 'R': {
                    x[i] = '6';
                    break;
                }

                default: {
                    x[i] = '0';
                    break;
                }
            }

        }
        String soundex= firstChar;
        //remove duplicates and 0 
        for(int i = 1 ; i< x.length;++i){
            if(x[i]!=x[i-1]&&x[i]!= '0'){
                soundex = soundex +x[i];
            }
        }
        soundex = soundex + "0000";
        return soundex.substring(0,4);
    }
	
	// use N-gram to get the score between two strings
	public static int ngram(int k, String f, String t) {
		f = f.toLowerCase();
        f = "#"+ f +"#";
        t = "#"+ t +"#";
        int count = 0;
        int[] flag = new int[t.length()-k+1];
        for(int i = 0; i< f.length()-k+1; ++i ){
            for(int j = 0; j<t.length()-k+1 ; ++j) {
                if (f.substring(i, i + k).equals(t.substring(j, j + k)) && flag[j]!=1){
                    count++;
                    flag[j] = 1;
                }
            }
        }
        int score = f.length()-k+1+t.length()-k+1-2*count;
        return score;
	}
	
	
	//get minimum of a,b,c
	public static int Minimum(int a, int b, int c) {  
	    int min =  a<b ? a : b;  
	    return  min<c ? min : c;  
	}
	
	// get maximum of a,b,c
	public static int Maximum(int a ,int b,int c){
		int max = a<b ? b:a;
		return max>c ? max:c;
	}
	
	
	
}
