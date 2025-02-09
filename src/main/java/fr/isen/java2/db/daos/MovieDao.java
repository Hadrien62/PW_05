package fr.isen.java2.db.daos;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

import fr.isen.java2.db.entities.Genre;
import fr.isen.java2.db.entities.Movie;

public class MovieDao {

	/**
	 * Récupère la liste de tous les films avec leur genre depuis la base de données.
	 *
	 * @return Une liste d'objets {@link Movie} contenant les films et leurs genres.
	 * @throws RuntimeException En cas d'erreur SQL.
	 */
	public List<Movie> listMovies() {
		List<Movie> movies = new ArrayList<>();

		// Connexion à la base de données
		try (Connection cnx = DataBaseConnection.getConnection()) {
			// Création d'un statement pour exécuter la requête SQL
			try (Statement st = cnx.createStatement()) {

				// Exécution de la requête pour récupérer les films et leurs genres
				ResultSet rs = st.executeQuery("SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre");

				// Parcours des résultats
				while (rs.next()) {
					Movie movie = new Movie(
							rs.getInt("idmovie"),                         // ID du film
							rs.getString("title"),                        // Titre du film
							rs.getDate("release_date").toLocalDate(),     // Date de sortie du film
							new Genre(rs.getInt("genre_id"), rs.getString("name")),  // Genre du film
							rs.getInt("duration"),                        // Durée du film en minutes
							rs.getString("director"),                     // Nom du réalisateur
							rs.getString("summary")                       // Résumé du film
					);

					// Ajout du film à la liste
					movies.add(movie);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error: ", e);
		}

		// Retourne la liste des films
		return movies;
	}

	/**
	 * Récupère la liste des films appartenant à un genre spécifique.
	 *
	 * @param genreName Le nom du genre pour lequel on veut récupérer les films.
	 * @return Une liste d'objets {@link Movie} contenant les films du genre spécifié.
	 *         Si aucun film n'est trouvé, la liste sera vide.
	 * @throws RuntimeException En cas d'erreur SQL.
	 */
	public List<Movie> listMoviesByGenre(String genreName) {
		List<Movie> movies = new ArrayList<>();
		try (Connection cnx = DataBaseConnection.getConnection()) {
			// Préparation de la requête pour récupérer les films du genre spécifié
			try (PreparedStatement st = cnx.prepareStatement(
					"SELECT * FROM movie JOIN genre ON movie.genre_id = genre.idgenre WHERE genre.name=?")) {

				// Remplacement du paramètre dans la requête préparée
				st.setString(1, genreName);

				// Exécution de la requête
				try (ResultSet rs = st.executeQuery()) {
					if (rs.next()) { // Vérifie que des résultats existent
						do {
							Movie movie = new Movie(
									rs.getInt("idmovie"),                        // ID du film
									rs.getString("title"),                       // Titre du film
									rs.getDate("release_date").toLocalDate(),    // Date de sortie
									new Genre(rs.getInt("genre_id"), rs.getString("name")), // Genre
									rs.getInt("duration"),                       // Durée du film
									rs.getString("director"),                    // Réalisateur
									rs.getString("summary")                      // Résumé
							);
							movies.add(movie);
						} while (rs.next()); // Continue à parcourir les résultats
					} else {
						System.out.println("Aucun film trouvé !");
					}
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error: ", e);
		}
		return movies;
	}


	/**
	 * Ajoute un nouveau film dans la base de données et retourne l'objet {@link Movie} avec son ID généré.
	 *
	 * @param movie L'objet {@link Movie} à ajouter dans la base de données.
	 * @return L'objet {@link Movie} avec l'ID généré par la base de données.
	 *         Retourne {@code null} si l'insertion échoue.
	 * @throws RuntimeException En cas d'erreur SQL lors de l'insertion du film.
	 */
	public Movie addMovie(Movie movie) {
		try (Connection cnx = DataBaseConnection.getConnection()) {
			// Requête SQL d'insertion avec valeurs paramétrées
			String sqlQuery = "INSERT INTO movie (title, release_date, genre_id, duration, director, summary) VALUES (?, ?, ?, ?, ?, ?)";

			// Préparation de la requête pour récupérer les clés générées
			try (PreparedStatement st = cnx.prepareStatement(sqlQuery, Statement.RETURN_GENERATED_KEYS)) {

				// Remplissage des paramètres de la requête
				st.setString(1, movie.getTitle());                  // Titre du film
				st.setDate(2, Date.valueOf(movie.getReleaseDate())); // Date de sortie
				st.setInt(3, movie.getGenre().getId());             // ID du genre
				st.setInt(4, movie.getDuration());                  // Durée du film
				st.setString(5, movie.getDirector());               // Réalisateur
				st.setString(6, movie.getSummary());                // Résumé

				// Exécution de la requête d'insertion
				st.executeUpdate();

				// Récupération de l'ID généré
				ResultSet ids = st.getGeneratedKeys();
				if (ids.next()) {
					return new Movie(
							ids.getInt(1),           // ID généré
							movie.getTitle(),
							movie.getReleaseDate(),
							movie.getGenre(),
							movie.getDuration(),
							movie.getDirector(),
							movie.getSummary()
					);
				}
			}
		} catch (SQLException e) {
			throw new RuntimeException("Error inserting movie: ", e);
		}
		return null;
	}

}
