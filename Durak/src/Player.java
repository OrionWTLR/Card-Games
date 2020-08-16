import java.util.ArrayList;

public class Player {
private ArrayList<Card> hand;
private int rank;

    public Player(ArrayList<Card> cards, int _tmp){
        hand = cards;
        rank = _tmp;
    }

    public int getRank() {
        return rank;
    }
    public ArrayList<Card> getHand(){
        return hand;
    }
    public void addToHand(Card c){
        hand.add(c);
    }
    public void removeFromHand(int i){
        hand.remove(i);
    }
    public Card drawCard(int i){
        Card playedCard = getHand().get(i);
        removeFromHand(i);
        return playedCard;

    }
}
