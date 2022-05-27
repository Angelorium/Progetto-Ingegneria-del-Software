package com.progetto.interfacciaDatabase;

import com.progetto.entity.EntryListaSegnalazioni;
import com.progetto.entity.Farmaco;

import java.sql.*;
import java.util.ArrayList;

/**
 * Classe che contiene i metodi necessari per richiedere dati al database dell'Azienda e della Catena Farmaceutica
 * per conto di un {@code Farmacista}
 */
public class InterfacciaFarmacia {
    /**
     * Getter per ottenere un lista di oggetti della classe {@code Farmaco} che l'Azienda può produrre
     * @return lista di segnalazioni
     */
    public ArrayList<Farmaco> getFarmaci() {
        ArrayList<Farmaco> lista = new ArrayList<>();
        try(Connection connection = DriverManager.getConnection("jdbc:mysql://localhost:3306/dbazienda", "root","password")){
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("select * from farmaco");
            while(resultSet.next()) {
                String nome = resultSet.getString("nome");
                String principio_attivo = resultSet.getString("principio_attivo");
                int tipo = resultSet.getInt("tipo");
                lista.add(new Farmaco(nome, principio_attivo, tipo));
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return lista;
    }
}
