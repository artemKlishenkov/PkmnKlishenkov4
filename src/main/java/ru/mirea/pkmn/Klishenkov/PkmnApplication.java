package ru.mirea.pkmn.Klishenkov;

import com.fasterxml.jackson.databind.JsonNode;
import ru.mirea.pkmn.AttackSkill;
import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Klishenkov.web.http.PkmnHttpClient;
import ru.mirea.pkmn.Klishenkov.web.jdbc.DatabaseServiceImpl;

import java.io.IOException;
import java.sql.SQLException;
import java.util.stream.Collectors;

public class PkmnApplication {
    public static void main(String[] args) throws IOException, ClassNotFoundException, InterruptedException, SQLException {

        CardImport cardImport= new CardImport("src/main/resources/my_card.txt");
        Card my_card = cardImport.getCard();

        PkmnHttpClient pkmnHttpClient = new PkmnHttpClient();

        JsonNode card = pkmnHttpClient.getPokemonCard(my_card.getName(), my_card.getNumber());
        System.out.println(card.toPrettyString());

        System.out.println(card.findValues("attacks")
                .stream()
                .map(JsonNode::toPrettyString)
                .collect(Collectors.toSet()));

        JsonNode attacksArray = card.path("data").get(0).path("attacks");
        int i = 0;
        for (AttackSkill attackSkill : my_card.getSkills()) {
            attackSkill.setDescription(attacksArray.get(i).path("text").asText());
        }

        CardExport cardExport = new CardExport(my_card);
        cardExport.serilized(my_card);
        Card import_some_card = cardExport.deserialize("src/main/resources/Landorus.crd");
        System.out.println(import_some_card);


        DatabaseServiceImpl db = new DatabaseServiceImpl();
        System.out.println(db.getStudentFromDatabase(import_some_card.getPokemonOwner().getSurName()));
        System.out.println(db.getCardFromDatabase(import_some_card.getName()));
        db.getAllStudents();
        db.getAllCards();
    }
}
