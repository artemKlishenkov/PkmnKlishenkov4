package ru.mirea.pkmn.Klishenkov.web.jdbc;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import ru.mirea.pkmn.*;
import java.io.FileInputStream;
import java.io.IOException;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

public class DatabaseServiceImpl implements DatabaseService {

    private final Connection connection;
    private final Properties databaseProperties;

    public DatabaseServiceImpl() throws SQLException, IOException {


        databaseProperties = new Properties();
        databaseProperties.load(new FileInputStream("src/main/resources/database.properties"));


        connection = DriverManager.getConnection(
                databaseProperties.getProperty("database.url"),
                databaseProperties.getProperty("database.user"),
                databaseProperties.getProperty("database.password")
        );
        System.out.println("Connection is "+(connection.isValid(0) ? "up" : "down"));

        try {
            Statement statement = connection.createStatement();

            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private PokemonStage setStage(String s){
        if(s.equals("BASIC"))
            return PokemonStage.BASIC;
        else if(s.equals("STAGE1"))
            return PokemonStage.STAGE1;
        else if(s.equals("STAGE2"))
            return PokemonStage.STAGE2;
        else if(s.equals("VSTAR"))
            return PokemonStage.VSTAR;
        else if(s.equals("VMAX"))
            return PokemonStage.VMAX;
        return null;
    }
    private EnergyType setType(String s){
        if(s.equals("FIRE"))
            return EnergyType.FIRE;
        else if(s.equals("GRASS"))
            return EnergyType.GRASS;
        else if(s.equals("WATER"))
            return EnergyType.WATER;
        else if(s.equals("LIGHTNING"))
            return EnergyType.LIGHTNING;
        else if(s.equals("PSYCHIC"))
            return EnergyType.PSYCHIC;
        else if(s.equals("FIGHTING"))
            return EnergyType.FIGHTING;
        else if(s.equals("DARKNESS"))
            return EnergyType.DARKNESS;
        else if(s.equals("METAL"))
            return EnergyType.METAL;
        else if(s.equals("FAIRY"))
            return EnergyType.FAIRY;
        else if(s.equals("DRAGON"))
            return EnergyType.DRAGON;
        else if(s.equals("COLORLESS"))
            return EnergyType.COLORLESS;
        return null;
    }

    @Override
    public Card getCardFromDatabase(String cardName) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM card WHERE name = '" + cardName + "' AND pokemon_owner IS NOT NULL;");

            Card card = null;
            while (resultSet.next()) {
                String name = resultSet.getString("name");
                int hp = Integer.parseInt(resultSet.getString("hp"));
                String game_set = resultSet.getString("game_set");
                String stage = resultSet.getString("stage");
                String retreat_cost = resultSet.getString("retreat_cost");
                String weakness_type = resultSet.getString("weakness_type");
                String resistance_type = resultSet.getString("resistance_type");
                String attack_skill = resultSet.getString("attack_skills");
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(attack_skill);
                List<AttackSkill> attacksList = new ArrayList<>();
                for (JsonNode attackNode : jsonNode) {
                    AttackSkill attack = new AttackSkill(
                            attackNode.path("name").asText(),
                            attackNode.path("description").asText(),
                            attackNode.path("cost").asText(),
                            attackNode.path("damage").asInt()
                    );
                    attacksList.add(attack);
                }

                String pokemon_type = resultSet.getString("pokemon_type");
                char regulation_mark = resultSet.getString("regulation_mark").charAt(0);
                String card_number = resultSet.getString("card_number");

                ResultSet studentResultSet = statement.executeQuery(
                        "SELECT * FROM student WHERE id = '" + resultSet.getString("pokemon_owner") + "';");
                studentResultSet.next();
                Student pokemon_owner = new Student(studentResultSet.getString("familyName"),
                        studentResultSet.getString("firstName"),
                        studentResultSet.getString("patronicName"),
                        studentResultSet.getString("group"));

                card = new Card(setStage(stage), name, hp, setType(pokemon_type), null,
                        attacksList, setType(weakness_type), setType(resistance_type),
                        retreat_cost, game_set, regulation_mark, pokemon_owner, card_number);
                break;
            }

            statement.close();
            return card;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
    public void getAllStudents() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM student;");
        ResultSetMetaData rsmd = resultSet.getMetaData();
        while (resultSet.next()) {
            for (int i = 1; i <= 5; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    public void getAllCards() throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "SELECT * FROM card;");
        ResultSetMetaData rsmd = resultSet.getMetaData();
        while (resultSet.next()) {
            for (int i = 1; i <= 14; i++) {
                if (i > 1) System.out.print(",  ");
                String columnValue = resultSet.getString(i);
                System.out.print(columnValue + " " + rsmd.getColumnName(i));
            }
            System.out.println("");
        }
    }

    public void editStudent(String studentSurname, String studentName) throws SQLException {
        Statement statement = connection.createStatement();
        ResultSet resultSet = statement.executeQuery(
                "UPDATE student SET \"familyName\" = '" + studentSurname + "', \"firstName\" = '" + studentName + "' WHERE \"familyName\" = '" + studentSurname + "';");
    }

    @Override
    public Student getStudentFromDatabase(String studentSurname) {
        try {
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery(
                    "SELECT * FROM student WHERE \"familyName\" = '" + studentSurname + "';");

            Student student = null;
            while (resultSet.next()) {
                String familyName = resultSet.getString("familyName");
                String firstName = resultSet.getString("firstName");
                String patronicName = resultSet.getString("patronicName");
                String group = resultSet.getString("group");

                student = new Student(familyName, firstName, patronicName, group);
                break;
            }

            statement.close();
            return student;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public void saveCardToDatabase(Card card) throws SQLException {
        Statement statement;
        try {
            statement = connection.createStatement();
            Gson gson = new GsonBuilder().create();
            try {
                statement.execute("INSERT INTO card VALUES(gen_random_uuid(), '" +
                        card.getName() + "', '" +
                        card.getHp() + "', " +
                        "(SELECT id FROM card WHERE name = '" + card.getEvolvesFrom().getName() + "'  limit 1), '" +
                        card.getGameSet() + "', " +
                        "(SELECT id FROM student WHERE \"familyName\" = '" + card.getPokemonOwner().getSurName() + "'  limit 1), '" +
                        card.getPokemonStage() + "', '" +
                        card.getRetreatCost() + "', '" +
                        card.getWeaknessType() + "', '" +
                        card.getResistanceType() + "', '" +
                        gson.toJson(card.getSkills()) + "', '" +
                        card.getRegulationMark() +
                        card.getNumber() + "');");
            } catch (NullPointerException e) {
                System.out.println(e.toString());
                statement.execute("INSERT INTO card(id, name, hp, game_set, pokemon_owner, stage, retreat_cost, " +
                        "weakness_type, resistance_type, attack_skills, pokemon_type, regulation_mark,  card_number)" +
                        " VALUES(gen_random_uuid(), '" +
                        card.getName() + "', '" +
                        card.getHp() + "', '" +
                        card.getGameSet() + "', " +
                        "(SELECT id FROM student WHERE \"familyName\" = '" + card.getPokemonOwner().getSurName() + "'  limit 1), '" +
                        card.getPokemonStage() + "', '" +
                        card.getRetreatCost() + "', '" +
                        card.getWeaknessType() + "', '" +
                        card.getResistanceType() + "', '" +
                        gson.toJson(card.getSkills()) + "', '" +
                        card.getPokemonType() + "', '" +
                        card.getRegulationMark() + "', '" +
                        card.getNumber() + "');");
                System.out.println("success");
            }
        statement.close();
    } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void createPokemonOwner(Student student) throws SQLException {
        try {
            Statement statement = connection.createStatement();
            statement.execute("INSERT INTO student VALUES(gen_random_uuid(), '" +
                    student.getSurName() + "', '" +
                    student.getFirstName() + "', '" +
                    student.getPatronicName() + "', '" +
                    student.getGroup() + "');");
            statement.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("Success " + getStudentFromDatabase(student.getSurName()));
    }

}