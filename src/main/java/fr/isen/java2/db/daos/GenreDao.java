package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;

public class GenreDao {

	/**
	 * Récupère la liste de tous les genres de films présents dans la base de données.
	 *
	 * @return Une liste d'objets {@link Genre} contenant les informations des genres.
	 *         Retourne une liste vide si aucun genre n'est trouvé.
	 * @throws RuntimeException En cas d'erreur SQL lors de l'exécution de la requête.
	 */
	public List<Genre> listGenres() {
		List<Genre> genres = new ArrayList<>();
		try (Connection cnx = DataBaseConnection.getConnection()) {
			try (Statement st = cnx.createStatement()) {
				try (ResultSet rs = st.executeQuery("SELECT * FROM genre")) {
					while (rs.next()) {
						Genre genre = new Genre(rs.getInt("idgenre"), rs.getString("name"));
						genres.add(genre);
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error: ", e);
		}
		return genres;
	}

	/**
	 * Récupère un genre spécifique à partir de son nom.
	 *
	 * @param name Le nom du genre recherché.
	 * @return Un objet {@link Genre} correspondant au nom donné, ou {@code null} si aucun genre n'est trouvé.
	 * @throws RuntimeException En cas d'erreur SQL lors de l'exécution de la requête.
	 */
	public Genre getGenre(String name) {
		try (Connection cnx = DataBaseConnection.getConnection()) {
			try (PreparedStatement st = cnx.prepareStatement("SELECT * FROM genre WHERE name=?")) {
				st.setString(1, name);
				try (ResultSet rs = st.executeQuery()) {
					if (rs.next()) {
						return new Genre(rs.getInt("idgenre"), rs.getString("name"));
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error: ", e);
		}
		return null;
	}

	/**
	 * Ajoute un nouveau genre dans la base de données.
	 *
	 * @param name Le nom du genre à ajouter.
	 * @throws RuntimeException En cas d'erreur SQL lors de l'insertion du genre.
	 */
	public void addGenre(String name) {
		try (Connection cnx = DataBaseConnection.getConnection()) {
			// Définition de la requête SQL pour insérer un genre dans la base
			String sqlQuery = "INSERT INTO genre(name) VALUES(?)";

			// Création d'un PreparedStatement pour éviter les injections SQL et exécuter la requête
			try (PreparedStatement ps = cnx.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {
				// Attribution du paramètre à la requête (nom du genre)
				ps.setString(1, name);

				// Exécution de la requête d'insertion
				ps.executeUpdate();
			}
		} catch (SQLException e) {
			// Capture et conversion des erreurs SQL en RuntimeException pour simplifier la gestion des erreurs
			throw new RuntimeException("Error inserting genre: ", e);
		}
	}

}
