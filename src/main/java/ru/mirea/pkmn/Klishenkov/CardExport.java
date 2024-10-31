package ru.mirea.pkmn.Klishenkov;

import ru.mirea.pkmn.Card;

import java.io.*;

public class CardExport {
    public static final long serialVersionUID = 1L;
    Card card;

    public CardExport(Card card){

    }

    public Card serilized(Card card) throws IOException {
        File myFile = new File(card.getName() + ".crd");
        FileOutputStream fileOutputStream = new FileOutputStream(myFile);
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(fileOutputStream);
        objectOutputStream.writeObject(card);
        objectOutputStream.close();
        fileOutputStream.close();
        return card;
    }

    public Card deserialize(String filename) throws IOException, ClassNotFoundException {
        FileInputStream fileInputStream = new FileInputStream(filename);
        ObjectInputStream objectInputStream = new ObjectInputStream(fileInputStream);
        Card card = (Card) objectInputStream.readObject();
        objectInputStream.close();
        fileInputStream.close();
        return card;
    }

    public Card getCard() {
        return card;
    }
}
