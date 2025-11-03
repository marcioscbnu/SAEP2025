package br.com.saep.controller;

import br.com.saep.dao.ProdutoDAO;
import br.com.saep.model.Produto;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/ProdutoServlet")
public class ProdutoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        // Garante que o input (POST/GET) esteja em UTF-8
        request.setCharacterEncoding("UTF-8");
        super.service(request, response);
    }

    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("usuarioLogado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }

        String acao = request.getParameter("acao");
        if (acao == null) acao = "listar";

        try {
            String termoPesquisa = null;
            if ("buscar".equals(acao)) {
                termoPesquisa = request.getParameter("termo"); 
            }

            switch (acao) {
                case "listar":
                case "buscar":
                    listarProdutos(request, response, termoPesquisa); 
                    break;
                case "excluir":
                    // AÇÃO DE EXCLUIR CHAMA O MÉTODO EXCLUIR PRODUTO E JÁ REDIRECIONA
                    excluirProduto(request, response);
                    break;
                default:
                    listarProdutos(request, response, null);
            }
        } catch (Exception e) {
            System.err.println("Erro no doGet do ProdutoServlet:");
            e.printStackTrace();
            request.getSession().setAttribute("mensagemErro", "Erro interno ao processar a requisição de produtos: " + e.getMessage());
            response.sendRedirect("ProdutoServlet?acao=listar");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("usuarioLogado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String acao = request.getParameter("acao");
        String mensagem = null;

        try {
            if ("inserir".equals(acao)) {
                inserirProduto(request, response);
                mensagem = "Produto inserido com sucesso!";
            } else if ("editar".equals(acao)) {
                atualizarProduto(request, response);
                mensagem = "Produto atualizado com sucesso!";
            } else {
                mensagem = "Ação POST desconhecida.";
            }
        } catch (Exception e) {
            // Tratamento robusto para exceções no doPost
            System.err.println("ERRO DAO/Serviço no doPost do ProdutoServlet (Ação: " + acao + "):");
            e.printStackTrace(); 
            
            String erroDetalhado = e.getMessage();
            if (erroDetalhado == null || erroDetalhado.trim().isEmpty()) {
                erroDetalhado = "Erro de runtime/DAO não detalhado. Tipo: " + e.getClass().getSimpleName();
            }
            
            mensagem = "ERRO ao processar: " + erroDetalhado;
        }
        
        request.getSession().setAttribute("mensagemErro", mensagem);
        response.sendRedirect("ProdutoServlet?acao=listar"); 
    }

    private void listarProdutos(HttpServletRequest request, HttpServletResponse response, String termoPesquisa) 
            throws ServletException, IOException {
        
        response.setContentType("text/html;charset=UTF-8");
        
        try {
            ProdutoDAO produtoDAO = new ProdutoDAO();
            List<Produto> produtos;

            if (termoPesquisa != null && !termoPesquisa.trim().isEmpty()) {
                produtos = produtoDAO.buscarPorDescricao(termoPesquisa.trim());
            } else {
                produtos = produtoDAO.listarTodos(true); 
            }
            
            request.setAttribute("produtos", produtos);
            request.getRequestDispatcher("/cadastroProduto.jsp").forward(request, response); 
            
        } catch (Exception e) {
            throw new ServletException("Erro ao buscar dados de produtos.", e);
        }
    }
    
    private Produto extrairProdutoDosParametros(HttpServletRequest request) throws NumberFormatException {
        Produto p = new Produto();
        
        String idParam = request.getParameter("id");
        if (idParam != null && !idParam.isEmpty()) {
            p.setId(Integer.parseInt(idParam));
        }
        
        p.setDescproduto(request.getParameter("descproduto"));
        p.setTipoproduto(request.getParameter("tipoproduto").charAt(0));
        p.setUnidmedida(request.getParameter("unidmedida"));
        
        p.setEstoqueminimo(Double.parseDouble(request.getParameter("estoqueminimo")));
        p.setEstoqueatual(Double.parseDouble(request.getParameter("estoqueatual")));
        
        return p;
    }
    
    private void inserirProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Produto p = extrairProdutoDosParametros(request);
        ProdutoDAO produtoDAO = new ProdutoDAO();
        produtoDAO.inserir(p);
    }

    private void atualizarProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Produto p = extrairProdutoDosParametros(request);
        ProdutoDAO produtoDAO = new ProdutoDAO();
        produtoDAO.atualizar(p);
    }
    
    private void excluirProduto(HttpServletRequest request, HttpServletResponse response) throws IOException {
        try {
            int id = Integer.parseInt(request.getParameter("id"));
            ProdutoDAO produtoDAO = new ProdutoDAO();
            produtoDAO.excluirProduto(id);
            request.getSession().setAttribute("mensagemErro", "Produto excluído com sucesso!");
        } catch (NumberFormatException nfe) {
            request.getSession().setAttribute("mensagemErro", "ERRO: ID do produto inválido.");
        } catch (Exception e) {
            request.getSession().setAttribute("mensagemErro", "ERRO: Falha ao excluir produto. Detalhe: " + (e.getMessage() != null ? e.getMessage() : "Erro desconhecido."));
            System.err.println("Erro ao excluir produto:");
            e.printStackTrace();
        }
        
        // Redireciona para a listagem
        response.sendRedirect("ProdutoServlet?acao=listar");
    }
}