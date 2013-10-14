package org.milfont.bookmaker;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class Generate {

	private static Connection connect = null;
	private static Statement statement = null;
	private static PreparedStatement preparedStatement = null;
	private static ResultSet resultSet = null;

	public static void main(String[] args) throws IOException, SQLException, ClassNotFoundException {
		String path = "/Users/cmilfont/projetos/thoth/public/teste6.pdf";
		
		HashMap<String, String> config = new HashMap<String, String>();
		config.put("author", "Christiano Milfont");
		config.put("creator", "Editora Fortes");
		config.put("subject", "Curso Product On Rails");
		config.put("title", "Curso Product On Rails");
		
		Book book = new Book(path, config, true);
		buscarHTML(book);
		book.close();
	}

	public static String buscarHTML(Book book) throws SQLException,
			ClassNotFoundException {
		Class.forName("com.mysql.jdbc.Driver");
		connect = DriverManager
				.getConnection("jdbc:mysql://localhost/thoth_development?user=root&password=root");
		preparedStatement = connect
				.prepareStatement("SELECT * FROM topicos t where t.publicacao_id = 128");
		resultSet = preparedStatement.executeQuery();
		
		String cover = "/Users/cmilfont/projetos/thoth/public/system/publicacao_capas/imagens/000/000/018/full/capa.jpg";
		book.addCover(cover);
		
		while (resultSet.next()) {
			String html = resultSet.getString("corpo_texto_rico_editor");
			String titulo = resultSet.getString("titulo");
			book.add(titulo, html);
		}

		if (resultSet != null) {
			resultSet.close();
		}

		if (statement != null) {
			statement.close();
		}

		if (connect != null) {
			connect.close();
		}
		return "";
	}

}
