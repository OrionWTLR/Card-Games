import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;



public class GameSpace {
    private ArrayList<Card> Deck  = new ArrayList<>();
    private ArrayList<Card> Field = new ArrayList<>();

    private Random r = new Random();
    private Scanner sc = new Scanner(System.in);
    private String cozidSuit;
    private final int PERM_HAND_SIZE = 6;
    private int roundCount = 1;
    private boolean pickUp;
    private boolean firstGoesFirst = true;
    private Card BlankFace = new Card("BlankFace", 0);

    public GameSpace(){

        for(int i = 0; i <= 3; i++){
            for(int j = 1; j <= 13; j++) {
                Deck.add(new Card(i, j));
            }
        }

        //remove all cards between 2 and 5
        for(int i = 0; i < Deck.size(); i++){
            Card tmp = Deck.get(i);
            if(2<=tmp.value() && tmp.value()<=5){
                Deck.remove(tmp);
            }
        }

        shuffle();
        shuffle();
        shuffle();

        //set each ace to a the highest value
        final int ACE_VALUE = 100;
        for(Card c : Deck){
            if(c.value() == 1){
                c.setValue(ACE_VALUE);
            }
        }

    }

    public void play(){

        //establish the trump suit
        int rand = r.nextInt(Deck.size()-1);

        //get trump card
        Card cozid = Deck.get(rand);

        //remove it from original deck
        Deck.remove(rand);

        //establish trump suit
        cozidSuit = cozid.suit();
        System.out.println("Trump Card: "+cozid.string());

        ArrayList<Card> handP = randomTransfer(PERM_HAND_SIZE);
        ArrayList<Card> handQ = randomTransfer(PERM_HAND_SIZE);

        Player p1 = new Player(handP, (1));
        Player p2 = new Player(handQ, (2));

        while(Deck.size() != 0) {

            //first going first is true when player 2 picks up the cards bc he cannot beat what is on the field
            //it is false when player 1 ends the round after all his cards have been beaten in a round

            battleGround(p1, p2);

            if(firstGoesFirst) {
                //battleGround(p1, p2);
            }else{
                //battleGround(p2, p1);
            }

            System.out.println("\nNumber of Cards Left: "+Deck.size()+"\n");
        }
    }

    private void battleGround(Player first, Player second){

        System.out.println("player "+first.getRank()+" here are your cards:");
        printCards(first.getHand());

        Card firstCard = playCard(first);
        firstCard.println();
        Field.add(firstCard);

        //A blankface card means the bottom of a hand has been reached and the round is over
        if(firstCard == BlankFace){
            return;
        }

        System.out.println("player "+second.getRank()+" here are your cards:");
        printCards(second.getHand());

        System.out.println("player "+second.getRank()+" can either beat card(1) or pick up card(2)");

        int b = sc.nextInt();
        boolean beatTheCard = (b == 1);

        //If the play wants to beat 1st card this will see if it is possible to beat with the cards available
        if(b == 1) {
            beatTheCard = beatCardPossibility(second, firstCard);
        }

        if(beatTheCard){

            Card secondCard = playCard(second);

            //while second card doesn't beat first card OR while second card isn't a trump card
            //ask second player to try again
            while( !( secondCard.trounces(firstCard) || cozidSuit.equals(secondCard.suit()) )){
                System.out.println(secondCard.string()+" cannot beat "+firstCard.string());

                second.addToHand(secondCard);
                printCards(second.getHand());
                secondCard = playCard(second);
            }

            secondCard.println();

            Field.add(secondCard);

            //A blankface card means the bottom of a hand has been reached and the round is over
            if(secondCard == BlankFace){
                return;
            }

        }else{

            //if second player cannot beat cards then he must take all of them into his hand
            for(Card f : Field){
                second.addToHand(f);
            }

            System.out.println("player "+second.getRank()+" has picked up card from field:");


        }

        printField();

        //check if it is possible for first player to match any cards on the field
        boolean matchTheCard = matchCardPossibility(first);

        //if it is possible then the round will continue and if it isn't then the round will end
        if(matchTheCard) {
            System.out.println("Player "+first.getRank()+" can match the cards on the field");
            recursiveBattle(beatTheCard, first, second);

            printField();

        }else{

            System.out.println("Player " + first.getRank() + " cannot match the cards on the field ");


        }

        //refill both players' hands until both have at least six cards
        refill(first);
        refill(second);

        //clear the field for the next round
        Field.clear();
    }

    private void recursiveBattle(boolean beatTheCard, Player first, Player second){

        printField();

        if(beatTheCard){
            System.out.println("player "+first.getRank()+" here are your cards:");
            printCards(first.getHand());
            System.out.println("player "+first.getRank()+" can either match(1) or end round(2)");

            boolean match = (sc.nextInt() == 1);
            //not matching implies 1st ended the round

            if(!matchCardPossibility(first)){
                match = false;
            }

            if(match){

                Card firstCard = playCard(first);

                while(!doesItMatch(firstCard)){
                    System.out.println(firstCard.string()+" doesn't match any cards in the field");

                    first.addToHand(firstCard);
                    printCards(first.getHand());
                    firstCard = playCard(first);
                }

                firstCard.println();
                Field.add(firstCard);

                //A blankface card means the bottom of a hand has been reached and the round is over
                if(firstCard == BlankFace){
                    return;
                }

                System.out.println("player "+second.getRank()+" here are your cards:");
                printCards(second.getHand());
                System.out.println("player "+second.getRank()+" can either beat card(1) or pick up card(2)");

                int b = sc.nextInt();
                beatTheCard = (b == 1);

                //If the play wants to beat 1st card this will see if it is possible to beat with the cards available
                if(b == 1) {
                    beatTheCard = beatCardPossibility(second, firstCard);
                }

                if(beatTheCard){

                    Card secondCard = playCard(second);
                    secondCard.println();
                    Field.add(secondCard);

                    //A blankface card means the bottom of a hand has been reached and the round is over
                    if(secondCard == BlankFace){
                        return;
                    }

                }

                recursiveBattle(beatTheCard, first, second);

            }else{
                System.out.println("player "+first.getRank()+" has ended the round ");

            }
        }else{

            for(Card f : Field){
                second.addToHand(f);
            }

            System.out.println("player "+second.getRank()+" has picked up the cards");

        }

        Field.clear();
    }

    private Card playCard(Player p){

        System.out.println("\nplayer " + p.getRank() + " choose a card by its index number:");

        int i = sc.nextInt() - 1;
        if(i+1 <= p.getHand().size()){
            return p.drawCard(i);
        }
        return BlankFace;

    }

    private void refill(Player p){
        if(Deck.size() == 0){
            return;
        }

        int i = 0;
        while(p.getHand().size() < PERM_HAND_SIZE){

            //This will prevent i from going out of bounds
            if(i < Deck.size()) {
                Card c = Deck.get(i);
                Deck.remove(i);
                p.addToHand(c);
            }

            //this will prevent an infinite loop from occurring
            if(i == 2*PERM_HAND_SIZE){
                return;
            }

            i++;
        }
    }

    private void shuffle(){
        //generate a list of random pairs
        for(int i = 0; i <= Deck.size()/2; i++){
            int a = r.nextInt(Deck.size());
            int b  =r.nextInt(Deck.size());

            //standing swap
            Card tmp = Deck.get(a);
            Deck.set(a, Deck.get(b));
            Deck.set(b, tmp);
        }
    }

    private ArrayList<Card> randomTransfer(int amount){
        ArrayList<Card> hand = new ArrayList<>();
        for(int i = 0; i < amount; i++){
            int a = r.nextInt(Deck.size());
            hand.add(Deck.get(a));
            Deck.remove(a);
        }
        return hand;
    }

    private void printCards(ArrayList<Card> cards){
        int i = 1;
        for(Card c : cards){
            System.out.print(i+":");
            c.println();
            i++;
        }

    }

    private boolean beatCardPossibility(Player second, Card firstCard){
        boolean beatTheCard = false;

        //check if it is possible for second player to beat firstCard with normal cards he has
        for (Card c : second.getHand()) {
            if (c.trounces(firstCard)) {
                beatTheCard = true;
                //System.out.print(c.string()+" ");
            }

        }


        //check if there are any cozid
        for (Card c : second.getHand()) {
            if (cozidSuit.equals(c.suit())) {
                beatTheCard = true;
                //System.out.print(c.string()+" ");
            }

        }


        return beatTheCard;
    }

    private boolean matchCardPossibility(Player p){
        boolean matchTheCard = false;

        for(Card c : p.getHand()){
            for(Card f : Field){
                if(c.value() == f.value()){
                    matchTheCard = true;
                }
            }
        }

        return matchTheCard;
    }

    private boolean doesItMatch(Card c){
        for(Card f : Field){
            if(f.value() == c.value()){
                return true;
            }
        }
        return false;
    }

    private void printField(){
        if(Field.size() == 0){
            return;
        }
        System.out.println("\nThe Field:");
        for(Card f : Field){
            f.println();
        }
        System.out.println();
    }

}
