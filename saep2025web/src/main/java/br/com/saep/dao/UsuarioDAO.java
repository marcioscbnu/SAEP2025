package br.com.saep.dao;

import br.com.saep.model.Usuario;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class UsuarioDAO {
    public Usuario autenticar(String email, String senha) {
        String sql = "SELECT id, nome, email FROM Usuario WHERE email = ? AND senha = ?";
        Connection conn = null;
        Usuario usuario = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, email);
                stmt.setString(2, senha);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) {
                        usuario = new Usuario();
                        usuario.setId(rs.getInt("id"));
                        usuario.setNome(rs.getString("nome"));
                        usuario.setEmail(rs.getString("email"));
                    }
                }
            }
            return usuario;
        } catch (SQLException e) {
            throw new RuntimeException("Erro na autenticação. Detalhes: " + e.getMessage(), e);
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }
}