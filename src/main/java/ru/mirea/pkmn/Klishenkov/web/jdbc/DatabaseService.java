package ru.mirea.pkmn.Klishenkov.web.jdbc;

import ru.mirea.pkmn.Card;
import ru.mirea.pkmn.Student;

import java.sql.SQLException;

public interface DatabaseService {

    Card getCardFromDatabase(String cardName) throws SQLException;

    Student getStudentFromDatabase(String studentName);

    void saveCardToDatabase(Card card) throws SQLException;

    void createPokemonOwner(Student student) throws SQLException;
}
