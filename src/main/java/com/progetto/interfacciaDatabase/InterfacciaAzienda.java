package com.progetto.interfacciaDatabase;
import com.progetto.entity.Lotto;
import com.progetto.entity.LottoOrdinato;
import com.progetto.entity.Ordine;

import java.sql.*;
import java.time.LocalDate;
import java.util.ArrayList;

public class InterfacciaAzienda {

    /**
     * Metodo che ritorna tutti i Lotti contenuti nel database
     *
     * @return ArrayList di Lotto contenente tutti i Lotti del database
     */
    public ArrayList<Lotto> getLotti() {

        ArrayList<Lotto> lotti = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            Statement statement = connection.createStatement();
            ResultSet resultLotti = statement.executeQuery("SELECT * " +
                    "FROM lotto " +
                    "WHERE data_scadenza IS NOT NULL ");
            while (resultLotti.next()) {
                lotti.add(new Lotto(resultLotti));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lotti;
    }

    /**
     * Rimuove un particolare Lotto dal database. Il Lotto è indicato tramite il suo id.
     *
     * @param id intero identificativo del Lotto da rimuovere
     */
    public void rimuoviLotto(int id) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            PreparedStatement statement = connection.prepareStatement("update lotto set data_scadenza = null where id_lotto = ?");
            statement.setInt(1,id);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Aggiunge un Lotto passato come parametro al database.
     *
     * @param lotto riferimento al Lotto da inserire
     */
    public void addLotto(Lotto lotto) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            PreparedStatement statement = connection.prepareStatement("insert into lotto values (null,?,?,?,?)");
            statement.setDate(1,Date.valueOf(lotto.getDataScadenza()));
            statement.setInt(2,lotto.getQuantitaContenuta());
            statement.setInt(3,lotto.getQuantitaOrdinata());
            statement.setString(4,lotto.getNomeFarmaco());
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Modifica la quantità di Farmaci contenuti e ordinati di tutti i Lotti conenuti in un Ordine.
     * Le nuove quantità coincidono esattamente con la quantità di Farmaci presenti nell'Ordine.
     * Modifica la data di scadenza in base a quando sono stati prodotti i Farmaci.
     *
     * @param ordine Ordine prenotato del quale si vogliono riempire i Lotti
     */
    public void updateQuantitaLotti(Ordine ordine) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            PreparedStatement statement = connection.prepareStatement("update lotto set n_contenuti = ?, n_ordinati = ?, data_scadenza = ? where id_lotto = ?");
            ArrayList<LottoOrdinato> lotti = ordine.getLottiContenuti();
            for (LottoOrdinato lotto : lotti) {
                statement.setInt(1,lotto.getQuantitaOrdine());
                statement.setInt(2,lotto.getQuantitaOrdine());
                statement.setDate(3,Date.valueOf(LocalDate.now().plusYears(2)));
                statement.setInt(4,lotto.getIdLotto());
                statement.executeUpdate();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Ritorna tutti gli Ordini in stato di Prenotazione.
     *
     * @return ArrayList contenente tutti gli Ordini prenotati
     */
    public ArrayList<Ordine> getOrdiniPrenotati() {

        ArrayList<Ordine> ordini = new ArrayList<>();
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            Statement statement = connection.createStatement();
            ResultSet resultOrdini = statement.executeQuery("select * from ordine,composizione,lotto,farmacia where stato = 3 AND id_ordine = ordine_id_ordine AND id_lotto = lotto_id_lotto AND id_farmacia = farmacia_id_farmacia ORDER BY id_ordine");
            int previousID = -1;
            while(resultOrdini.next()) {
                if (previousID == resultOrdini.getInt("id_ordine")) {
                    ordini.get(ordini.size()-1).addLotto(new LottoOrdinato(resultOrdini.getInt("id_lotto"),resultOrdini.getInt("n_farmaci")));
                } else {
                    ordini.add(new Ordine(resultOrdini));
                }
                previousID = resultOrdini.getInt("id_ordine");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ordini;
    }

    /**
     * Modifica lo stato di un qualunque Ordine in Elaborazione
     *
     * @param id_ordine identificativo dell'Ordine da mandare in elaborazione
     */
    public void cambiaStatoInElaborazione(int id_ordine) {
        try (Connection connection = DriverManager.getConnection("jdbc:mysql://127.0.0.1:3306/dbAzienda", "root","password")){
            PreparedStatement statement = connection.prepareStatement("update ordine set stato = 1 where id_ordine = ?");
            statement.setInt(1,id_ordine);
            statement.executeUpdate();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}