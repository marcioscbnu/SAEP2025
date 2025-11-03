package br.com.saep.dao;

import br.com.saep.model.Movimento;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class MovimentoDAO {
    
    public void inserir(Movimento mov) {
        // CORREÇÃO: Ajustada a query para usar 'idproduto' conforme o seu DDL
        String sql = "INSERT INTO movimento (idproduto, idusuario, tipomovto, qtdmovto) VALUES (?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                // O valor passado é o ID do produto, mesmo o getter se chamando getIdmaterial()
                stmt.setInt(1, mov.getIdmaterial()); 
                stmt.setInt(2, mov.getIdusuario());
                stmt.setString(3, String.valueOf(mov.getTipomovto()));
                stmt.setDouble(4, mov.getQtdmovto());
                // Datahoramovto usa o default CURRENT_TIMESTAMP do banco, omitido do INSERT.
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao registrar movimento. " + e.getMessage());
            throw new RuntimeException("Erro ao registrar movimento de estoque.", e);
        } finally {
            ConnectionFactory.closeConnection(conn);
        }
    }
}