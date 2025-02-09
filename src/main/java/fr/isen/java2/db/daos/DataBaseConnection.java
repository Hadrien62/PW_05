package fr.isen.java2.db.daos;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * Classe utilitaire pour gérer la connexion à la base de données.
 *
 * Cette classe fournit une méthode statique permettant d'obtenir une connexion
 * à la base de données en utilisant {@link DriverManager}.
 */
public class DataBaseConnection {

    /**
     * Établit et retourne une connexion à la base de données.
     *
     * @return Une instance de {@link Connection} connectée à la base de données.
     * @throws SQLException Si une erreur survient lors de l'établissement de la connexion.
     */
    public static Connection getConnection() throws SQLException {
        String url = "jdbc:sqlite:./sqlite.db";  // URL de la base de données
        // DriverManager infère le driver à utiliser en fonction de l'URL
        return DriverManager.getConnection(url);
    }
}

