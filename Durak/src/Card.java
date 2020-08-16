public class Card {
    //1(A) 2 3 4 5 6 7 8 9 10 11(J) 12(Q) 13(K)
    private String suit;
    private int value;
    private String[] face = new String[]{"Jack", "Queen", "King"};

    public Card(int s, int v){

        if(s > 3 || v <=0 || v > 13){
            return;
        }

        String[] suits = new String[] {"Spades", "Hearts", "Clubs", "Diamonds"};

        for(int i = 0; i < suits.length; i++){
            if(i == s){
                suit = suits[i];
            }
        }

        value = v;

    }

    public Card(String s, int v){
        suit = s;
        value = v;
    }

    public String suit() {
        return suit;
    }

    public int value(){
        return value;
    }

    public void setValue(int _v){
        value = _v;
    }

    public void setSuit(String _s){
        suit = _s;
    }

    public String string(){
        if(value == 1 || value == 100){
            return "[Ace" +" of "+suit+"]";
        }

        if(1 < value && value < 11){
            return "["+value +" of "+suit+"]";
        }

        int j = 0;
        for(int i = 11; i <= 13; i++){
            if(value == i){
                return "["+face[j]+" of "+suit+"]";
            }
            j++;
        }
        return "";
    }

    public boolean trounces(Card c){
        return suit.equals(c.suit()) && (value > c.value());
    }

    public void println(){
        System.out.println(string());
    }

    public void print(){
        System.out.print(string());
    }


}
