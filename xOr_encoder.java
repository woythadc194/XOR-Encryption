import java.io.*;
import java.util.*;
public class xOr_encoder{
    public static void main(String[]args) throws FileNotFoundException{
        String key = "";
        int keyLen = 0;
        try{
            key = args[0];
            keyLen = key.length();
        } catch(Exception e){ 
            System.out.println("No args[]");
        }

        PrintStream ps = new PrintStream(new File("cipher.txt"));
        Scanner sc = new Scanner(new File("cipher_ans.txt"));
        String cipherTxt = sc.nextLine();
        String cipherXOr = "";
        int pos = 0;
        for(int i=0; i<cipherTxt.length(); i++){
            char c = cipherTxt.charAt(i);
            int x = ( ((int)c) ^ ((int)key.charAt(pos)));
            ps.print(x + " " );
            
            pos ++;            
            if(pos>=keyLen)
                pos=0;

        }
        
    }   
}
