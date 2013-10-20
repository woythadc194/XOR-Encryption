import java.util.*;
import java.io.*;
import java.math.*;
public class xOr_decoder{

    private static String cipherCode;
    private static Map<Integer, ArrayList<Integer>> keys = new TreeMap<Integer, ArrayList<Integer>>();
    private static int currentScore;
    private static ArrayList<Integer> keyChars;
    
    public static void main(String[] args) throws FileNotFoundException {

        long start = System.currentTimeMillis();
        makeKeyChars();

        Scanner sc = new Scanner(new File("cipher.txt"));
        cipherCode = sc.nextLine().replace(',',' ');                            //removes any commas from string (for euler file)
        buildMap(Integer.parseInt(args[0]));                                    //(1 - args[0]) of possible key lengths
        int keyScore = 0;                                                       //best score for each key length
        for(int x : keys.keySet())                                              //find the key with highest score
            if(x>keyScore)
                keyScore = x;
        ArrayList<Integer> targetList = keys.get(keyScore);                     //use best key
        int counter = 0;                                                        //hold position in key, wraps back to 0 once == keylength
        sc = new Scanner(cipherCode);                                           //new scanner on ciphercode
        while(sc.hasNext()){            
            int xOr = Integer.parseInt("" + sc.next());                         
            int key = targetList.get(counter);
            System.out.print((char)(xOr^key));                                  //prints char by char
            counter++;
            if(counter>=targetList.size())
                counter = 0;
        }

        System.out.println("\n");
        for(int i=0; i<targetList.size(); i++){                                 //print key char by char
            int x = targetList.get(i);
            System.out.print((char)(x));                                        
        }
        System.out.println();

        long end = System.currentTimeMillis();
        System.out.println();
        printTime(end-start); 
    }
    
    private static void buildMap(int maxKeyLen){
        for(int i=maxKeyLen; i>0; i--){
            ArrayList<Integer> list = new ArrayList<Integer>();
            for(int j=0; j<i; j++){
                int listKey = getKey(j,i);
                if (listKey!=-1)
                    list.add(listKey);
            }
            keys.put(currentScore, list);
            System.out.println();                 //for seperating key index results bewteen rounds  //FIXME
        }
    }
    
    
    /*  
     *  Takes all possible lowercase characters and XOR them with the 
     *  corresponding encrypted letter. Whichever letter produces the most 
     *  amount or the characters 'e', 't', 'a', 'o', 'i', 'n' must be the 
     *  correct key letter
     */
     
    //Parameters: keyIndex - the offset to start the xOr wrapping with
    //Returns: key - char that generated the most common letters in english
    private static int getKey(int keyIndex, int keyLen){
        if(keyIndex==0)
            currentScore = 0;
        int larger = 0;                                                         //Largest number of letters found
        int key = 0;                                                            //key that found those letters
        for(int i : keyChars){                                                  //chars (a->z)&(A->Z)&(" ") in ascii
            Scanner cs = new Scanner(cipherCode);                                     //scan the text
            for(int j=0; j<keyIndex; j++)                                       //provides offset to start if character isn't first in key
                cs.next();                                                      // "ditto"
            int counter = 0;                                                    //number of times letter was found with current possible key
            LetterValueGen valGen = new LetterValueGen();
            while(cs.hasNext()){                                                //while items still in line
                int xOr = Integer.parseInt(""+cs.next());                       //xOr value 
                int y = (xOr^i);      
                int charPoints = valGen.getVal((char)y);                        //xOr value XOR with key
                if(charPoints<0)
                    return -1;
                counter += charPoints;                                           //increment counter
                for(int j=1; j<keyLen; j++){                                    //depending on keyLen, skips appropriate amount of chars to wrap key
                    try{
                        cs.next();
                    }catch(Exception e){}
                }
            }
            if(larger<=counter){                                                //saves key with best results
                larger = counter;
                key = i;
            }
        }
        currentScore += larger;
        ///*System.out.print("keyIndex: " + keyIndex + " = " + key + " = '" + ((char)key) + "' with a score of " + larger);       //prints key index, int value, char value //FIXME
        if(keyIndex==keyLen-1){
            System.out.print(keyLen + " Total: " + currentScore);
            System.out.println();
        }//*/
        return key;
    }
    
    private static void makeKeyChars(){
        keyChars = new ArrayList<Integer> ();
        keyChars.add(32);
        for(int i=97; i<=122; i++)
            keyChars.add(i);
        for(int i=65; i<=90; i++)
            keyChars.add(i);
        for(int i=48; i<=57; i++)
            keyChars.add(i);
        
    }

    public static void printTime(long ms){
        long s=0, m=0, h=0;
        if(ms>1000){
            s = ms/1000;
            if(s>60){
                m = s/60;
                if(m>60)
                    h = m/60;
            }
        }
        m%=60;
        s%=60;
        ms%=1000;
        String a=""+m, b=""+s, c=""+ms;
        if(a.length()!=2)
            a = "0" + a;
        if(b.length()!=2)
            b = "0" + b;
        while(c.length()!=3)
            c = "0" + c;
        System.out.println("T: " + h + ":" + a + ":" + b + "." + c);
    }
}
