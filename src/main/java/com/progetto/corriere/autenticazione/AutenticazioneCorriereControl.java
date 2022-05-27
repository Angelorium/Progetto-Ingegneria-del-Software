package com.progetto.corriere.autenticazione;

import com.progetto.corriere.SchermataPrincipaleCorriere;
import com.progetto.interfacciaDatabase.InterfacciaAutenticazione;
import com.progetto.entity.Corriere;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import org.w3c.dom.Text;

import javax.security.auth.login.CredentialException;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Control che gestisce l'autenticazione della farmacia
 */
public class AutenticazioneCorriereControl {

    private TextField idCorriere;
    private PasswordField password;
    private ActionEvent event;

    /**
     * istanzia l'oggetto dati in input l'id del corriere e la password
     * @param idCorriere id del corriere
     * @param password password inserita dall'utente
     * @param event evento che rappresenta il click del tasto login
     */
    public AutenticazioneCorriereControl(TextField idCorriere, PasswordField password, ActionEvent event){
        this.setIdCorriere(idCorriere);
        this.setPassword(password);
        this.setEvent(event);
    }

    /**
     * Metodo di avvio di un oggetto di classe {@code AutenticazioneCorriereControl}
     */
    public void start(){
        try {
            String pwd = this.creaDigest(password.getText());
            try {
                int id = Integer.parseInt(idCorriere.getText());
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //ottiene stage corrente
                this.verificaCredenziali(this.getCredenziali(id, pwd));
                stage.close(); //chiudo scgermata autenticazione
            } catch (NumberFormatException e) { //id farmacia inserito in un formato non corretto
                Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //ottiene stage corrente
                ErroreAutenticazione errAut = new ErroreAutenticazione(0);
                errAut.start(stage);
            } catch (CredentialException e) { //credenziali non corrette
                if (e.getMessage().compareTo("idNonValido") == 0) {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //ottiene stage corrente
                    ErroreAutenticazione errAut = new ErroreAutenticazione(2);
                    errAut.start(stage);
                } else if (e.getMessage().compareTo("passwordNonValida") == 0) {
                    Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow(); //ottiene stage corrente
                    ErroreAutenticazione errAut = new ErroreAutenticazione(1);
                    errAut.start(stage);
                }
            }
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    private void setIdCorriere(TextField idCorriere) {
        if(idCorriere == null) {
            throw new NullPointerException("ID corriere = null");
        }
        this.idCorriere = idCorriere;
    }

    private void setPassword(PasswordField password) {
        if(password == null) {
            throw new NullPointerException("Password corriere = null");
        }
        this.password = password;
    }

    private void setEvent(ActionEvent event) {
        if(event == null) {
            throw new NullPointerException("Event = null");
        }
        this.event = event;
    }

    private String creaDigest(String password){
        MessageDigest md = null;
        try {
            md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        md.update(password.getBytes(StandardCharsets.UTF_8));
        byte[] digest = md.digest();
        String hex = String.format("%064x", new BigInteger(1, digest));
        return hex;  //la stringa ha 64 caratteri
    }

    private Corriere getCredenziali (int idCorriere, String password) throws CredentialException {
        InterfacciaAutenticazione intAut = new InterfacciaAutenticazione();
        return intAut.getCredenzialiCorriere(idCorriere, password);
    }

    private void verificaCredenziali(Corriere corriere) throws CredentialException{
        if(corriere != null){
            if(corriere.getNominativo().compareTo("passwordNonValida") == 0){
                throw new CredentialException("passwordNonValida");
            }
            try {
                SchermataPrincipaleCorriere schermataPrincipaleCorriere = new SchermataPrincipaleCorriere(corriere);
                Stage stage = new Stage();
                schermataPrincipaleCorriere.start(stage);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        else{
            throw new CredentialException("idNonValido");
        }
    }
}
