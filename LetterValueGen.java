import java.util.*;

public class LetterValueGen{

    private Map<Character, Integer> map;

    public LetterValueGen(){
        map = new HashMap<Character, Integer>();
        map.put('e', 10);
        map.put('t', 9);
        map.put('a', 8);
        map.put('o', 7);
        map.put('i', 6);
        map.put('n', 5);
        map.put('s', 4);
        map.put('h', 3);
        map.put('r', 2);
        map.put('d', 1);
    }
    
    public int getVal(char c){
        for(char ch : map.keySet()){
            if(c==ch)
                return map.get(c);
            if(c==ch+32)
                return map.get(ch);
        }
        return 0;
    }

    public int getVal(int x){
        if(97<=x && x<=122)
            return x-96;
        else if(65<= x && x<=90)
            return x-64;
        else if(x>126||x<32)
            return -1;
        return 0;
    }
}
