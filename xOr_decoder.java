import java.util.*;
import java.io.*;
import java.math.*;

public class xOr_decoder{

    private static int THREADS;
    
    public static void main(String[] args) throws FileNotFoundException {
        long start = System.currentTimeMillis();
        try{
            THREADS = Integer.parseInt(args[0]);
        }catch(Exception e){
            THREADS = 1;
        }
        Scanner sc = new Scanner(new File("cipher.txt"));
        String cipherCode = sc.nextLine().replace(',',' ');
        
        ArrayList<Integer> keyChars = makeKeyChars();
        
        for(int i=1; i<=THREADS; i++){
            xOr_decoder_thread t = new xOr_decoder_thread(i, cipherCode, start, keyChars, THREADS);
            t.start();
        }
    }
    
    private static ArrayList<Integer> makeKeyChars(){
        ArrayList<Integer> keyChars = new ArrayList<Integer> ();
        keyChars.add(32);
        for(int i=97; i<=122; i++)
            keyChars.add(i);
        for(int i=65; i<=90; i++)
            keyChars.add(i);
        for(int i=48; i<=57; i++)
            keyChars.add(i);
        return keyChars;
    }
}


class xOr_decoder_thread extends Thread{
    
    private static ArrayList<Integer> keyChars;
    private static boolean keyFound;
    private static int THREADS;
    private static long start;
    
    private String code;
    private int PID;
    private int keyLength;
    private boolean keyFinder;
    private int currentScore;
    private ArrayList<Integer> keyArray = new ArrayList<Integer>();
        
    public xOr_decoder_thread(int keyLength, String code, long start, ArrayList<Integer> keyChars, int THREADS){
        this.keyLength = keyLength;
        this.PID = keyLength;
        this.code = code;
        this.start = start;
        this.keyChars = keyChars;
        this.keyFound = false;
        this.keyFinder = false;
        this.THREADS = THREADS;
    }
    
    public void printPassage(){
        int counter = 0;                                                        //hold position in key, wraps back to 0 once == keylength
        Scanner sc = new Scanner(code);                                           //new scanner on ciphercode
        while(sc.hasNext()){            
            int xOr = Integer.parseInt("" + sc.next());                         
            int key = keyArray.get(counter);
            System.out.print((char)(xOr^key));                                  //prints char by char
            counter++;
            if(counter>=keyArray.size())
                counter = 0;
        }

        System.out.println("\n");
        for(int i=0; i<keyArray.size(); i++){                                 //print key char by char
            int x = keyArray.get(i);
            System.out.print((char)(x));                                        
        }
        System.out.println();
    }
    
    public void run(){
        while(!keyFound)
            findKey();
        if(keyFinder){
//            printPassage();//Comment out if printing passage is unneccesary
            long end = System.currentTimeMillis();
            printTime(end-start);             
            System.exit(0);
        }
    }
    
    private void findKey(){
        ArrayList<Integer> list = new ArrayList<Integer>();
        for(int j=0; j<keyLength; j++){
            int listKey = getKey(j, keyLength);
            if (listKey!=-1)
                list.add(listKey);
        }
        for(int i =0; i<list.size(); i++){
            int x = list.get(i);
        }
        if(engTest(list)){
            keyFinder = true; 
            keyFound = true;
            keyArray = list;
        }else{
            keyLength += THREADS;
        }
        System.out.println();
    }

    private boolean engTest(ArrayList<Integer> keyList){
        Scanner sc = new Scanner(code);
        String key = "";
        for(int i=0; i<keyList.size(); i++)
            key+=((char)(int)(keyList.get(i)));
        System.out.print("Process " + PID + ": Best of length " + key.length() + " is \"" + key + "\"");

        String token = "";
        int counter = 0;
        while(sc.hasNext()){
            int xOr = Integer.parseInt("" + sc.next());
            int keyIndex = keyList.get(counter);
            char c = (char)(xOr^keyIndex);
            token += c;
            if(c==' '){
                token = token.substring(0,token.length()-1);
                if(tests(token)){
//                    System.out.println(token + " FAILED");
                    return false;
                }
                token = "";
            }
            counter++;
            if(counter>=key.length())
                counter = 0;
        }
        if(token!=""){
            if(tests(token)){
//                System.out.println(token + " FAILED");
                return false;
            }
            token = "";
        }
        System.out.println(" and it PASSED all tests");
        return true;
    }
    
    private static boolean tests(String token){
        if(punctTest(token) || qTest(token) || noTripleLetter(token))
            return true;
        return false;
    }
    
    private static boolean qTest(String token){
        for(int i=0; i<token.length(); i++){
            if(token.charAt(i)=='q' || token.charAt(i)=='Q')
                if(token.charAt(i+1)!='u' && token.charAt(i+1)!='U' )
                    return true;
        }
        return false;
    }
    
    private static boolean punctTest(String token){
        for(int i=0; i<token.length(); i++){
            char c = token.charAt(i);
            if((c=='.' || c=='!' || c=='?') && i!=token.length()-1 && i!=token.length()-2) return true;
            if(c=='"' && i!=0 && i!=token.length()-1 && i!=token.length()-2) return true;
            if(c=='&' && token.length()!=1) return true;
        }
        return false;
    }
    
    private static boolean noTripleLetter(String token){
        for(int i=0; i<token.length(); i++){
            try{
                if(token.charAt(i) == token.charAt(i+1) && token.charAt(i+1) == token.charAt(i+2))
                    return true;
            }catch(Exception e){}
        }        
        return false;
    }
    
    
    /*  
     *  Takes all possible lowercase characters and XOR them with the 
     *  corresponding encrypted letter. Whichever letter produces the most 
     *  amount or the characters 'e', 't', 'a', 'o', 'i', 'n' must be the 
     *  correct key letter
     */
     
    //Parameters: keyIndex - the offset to start the xOr wrapping with
    //Returns: key - char that generated the most common letters in english
    private int getKey(int keyIndex, int keyLen){
        if(keyIndex==0)
            currentScore = 0;
        int larger = 0;                                                         //Largest number of letters found
        int key = 0;                                                            //key that found those letters
        for(int i : keyChars){                                                  //chars (a->z)&(A->Z)&(" ") in ascii
            Scanner sc = new Scanner(code);                                     //scan the text
            for(int j=0; j<keyIndex; j++)                                       //provides offset to start if character isn't first in key
                sc.next();                                                      // "ditto"
            int counter = 0;                                                    //number of times letter was found with current possible key
            LetterValueGen valGen = new LetterValueGen();
            while(sc.hasNext()){                                                //while items still in line
                int xOr = Integer.parseInt(""+sc.next());                       //xOr value 
                int y = (xOr^i);      
                int charPoints = valGen.getVal((char)y);                        //xOr value XOR with key
                if(charPoints<0)
                    return -1;
                counter += charPoints;                                           //increment counter
                for(int j=1; j<keyLen; j++){                                 //depending on keyLen, skips appropriate amount of chars to wrap key
                    try{
                        sc.next();
                    }catch(Exception e){}
                }
            }
            if(larger<=counter){                                                //saves key with best results
                larger = counter;
                key = i;
            }
        }
        currentScore += larger;
        return key;
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
