package br.com.saep.dao;

import br.com.saep.model.Produto;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ProdutoDAO {
    
    // Método auxiliar para mapear o ResultSet para o objeto Produto
    private Produto mapResultSetToproduto(ResultSet rs) throws SQLException {
        Produto m = new Produto();
        m.setId(rs.getInt("id"));
        
        // CORREÇÃO CRÍTICA: Lendo 'descproduto' que estava faltando
        m.setDescproduto(rs.getString("descproduto"));
        
        // Mapeamento de outros campos, alinhados com a sua definição de tabela
        String tipoString = rs.getString("tipoproduto");
        m.setTipoproduto(tipoString != null && tipoString.length() > 0 ? tipoString.charAt(0) : ' ');
        m.setUnidmedida(rs.getString("unidmedida"));
        m.setEstoqueminimo(rs.getDouble("estoqueminimo"));
        m.setEstoqueatual(rs.getDouble("estoqueatual"));
        return m;
    }
    
    // ---------------------- C R U D BÁSICO ----------------------
    
    /**
     * Insere um novo produto no banco de dados.
     */
    public void inserir(Produto produto) {
        String sql = "INSERT INTO produto (descproduto, tipoproduto, unidmedida, estoqueminimo, estoqueatual) VALUES (?, ?, ?, ?, ?)";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, produto.getDescproduto());
                stmt.setString(2, String.valueOf(produto.getTipoproduto()));
                stmt.setString(3, produto.getUnidmedida());
                stmt.setDouble(4, produto.getEstoqueminimo());
                stmt.setDouble(5, produto.getEstoqueatual());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao inserir produto. " + e.getMessage());
            throw new RuntimeException("Erro ao inserir produto.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }
    
    /**
     * Atualiza os dados de um produto existente.
     */
    public void atualizar(Produto produto) {
        String sql = "UPDATE produto SET descproduto=?, tipoproduto=?, unidmedida=?, estoqueminimo=?, estoqueatual=? WHERE id=?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setString(1, produto.getDescproduto());
                stmt.setString(2, String.valueOf(produto.getTipoproduto()));
                stmt.setString(3, produto.getUnidmedida());
                stmt.setDouble(4, produto.getEstoqueminimo());
                stmt.setDouble(5, produto.getEstoqueatual());
                stmt.setInt(6, produto.getId());
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao atualizar produto (ID: " + produto.getId() + "). " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar produto.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }
    
    /**
     * Exclui um produto pelo ID.
     */
    public void excluirProduto(int id) {
        String sql = "DELETE FROM produto WHERE id=?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao excluir produto (ID: " + id + "). " + e.getMessage());
            throw new RuntimeException("Erro ao excluir produto. Pode haver movimentos associados.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }
    
    // ---------------------- MÉTODOS DE CONSULTA ----------------------
    
    /**
     * Lista todos os materiais, com opção de ordenação.
     */
    public List<Produto> listarTodos(boolean ordenarPorDescricao) {
        String sql = "SELECT * FROM produto";
        if (ordenarPorDescricao) { sql += " ORDER BY descproduto ASC"; }
        List<Produto> produtos = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql);
                 ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) { 
                    produtos.add(mapResultSetToproduto(rs)); 
                }
            }
            return produtos;
        } catch (SQLException e) {
            // Em caso de erro de SQL, imprime no console e RE-LANÇA
            System.err.println("ERRO DAO: Falha ao listar produtos. Verifique o log do servidor para detalhes da coluna ausente.");
            e.printStackTrace();
            // Lança uma exceção de tempo de execução que será capturada pelo Servlet
            throw new RuntimeException("Falha ao carregar lista de produtos. Verifique a coluna 'descproduto' no BD.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }
    
    /**
     * Busca um produto pelo ID.
     */
    public Produto buscarPorId(int id) {
        String sql = "SELECT * FROM produto WHERE id = ?";
        Connection conn = null;
        Produto produto = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setInt(1, id);
                try (ResultSet rs = stmt.executeQuery()) {
                    if (rs.next()) { produto = mapResultSetToproduto(rs); }
                }
            }
            return produto;
        } catch (SQLException e) {
             System.err.println("ERRO DAO: Falha ao buscar produto por ID " + id + ". " + e.getMessage());
            throw new RuntimeException("Erro ao buscar produto por ID.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }

    /**
     * Busca materiais por termo na descrição, tipo ou unidade de medida.
     */
    public List<Produto> buscarPorDescricao(String termo) {
        String sql = "SELECT * FROM produto WHERE descproduto LIKE ? OR tipoproduto LIKE ? OR unidmedida LIKE ? ORDER BY descproduto ASC";
        List<Produto> produtos = new ArrayList<>();
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                String termoSQL = "%" + termo + "%";
                stmt.setString(1, termoSQL); 
                stmt.setString(2, termoSQL); 
                stmt.setString(3, termoSQL); 
                
                try (ResultSet rs = stmt.executeQuery()) {
                    while (rs.next()) { produtos.add(mapResultSetToproduto(rs)); }
                }
            }
            return produtos;
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao buscar materiais por descrição, tipo ou unidade. " + e.getMessage());
            throw new RuntimeException("Erro ao buscar materiais.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }

    // ---------------------- MÉTODOS ESPECÍFICOS DE ESTOQUE ----------------------
    
    /**
     * Atualiza a coluna estoqueatual de um produto.
     */
    public void atualizarEstoque(int id, double novaQuantidade) {
        String sql = "UPDATE produto SET estoqueatual=? WHERE id=?";
        Connection conn = null;
        try {
            conn = ConnectionFactory.getConnection();
            try (PreparedStatement stmt = conn.prepareStatement(sql)) {
                stmt.setDouble(1, novaQuantidade);
                stmt.setInt(2, id);
                stmt.executeUpdate();
            }
        } catch (SQLException e) {
            System.err.println("ERRO DAO: Falha ao atualizar estoque do ID " + id + ". " + e.getMessage());
            throw new RuntimeException("Erro ao atualizar estoque.", e);
        } finally { ConnectionFactory.closeConnection(conn); }
    }
}