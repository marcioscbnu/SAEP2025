package br.com.saep.controller;

import br.com.saep.dao.MovimentoDAO;
import br.com.saep.dao.ProdutoDAO;
import br.com.saep.model.Movimento;
import br.com.saep.model.Produto;
import br.com.saep.model.Usuario;
import java.io.IOException;
import java.util.List;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/MovimentoServlet")
public class MovimentoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    
    @Override
    protected void service(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
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
        if ("listarEstoque".equals(acao)) {
            listarEstoqueParaMovimento(request, response);
        } else {
            response.sendRedirect("principal.jsp");
        }
    }
    
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        if (request.getSession().getAttribute("usuarioLogado") == null) {
            response.sendRedirect("login.jsp");
            return;
        }
        
        String acao = request.getParameter("acao");
        if ("registrar".equals(acao)) {
            registrarMovimentacao(request, response);
        } else {
            response.sendRedirect("principal.jsp");
        }
    }

    private void listarEstoqueParaMovimento(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        try {
            ProdutoDAO produtoDAO = new ProdutoDAO();
            // A chamada ao listarTodos corrigido
            List<Produto> produtos = produtoDAO.listarTodos(true); 
            
            request.setAttribute("produtos", produtos);
            // Encaminha para o JSP corrigido
            request.getRequestDispatcher("/gestaoMovimento.jsp").forward(request, response); 
            
        } catch (RuntimeException e) { 
            // Captura a exceção relançada pelo DAO (SQL ou Mapeamento)
            System.err.println("ERRO CRÍTICO no MovimentoServlet ao listar produtos:");
            e.printStackTrace();
            
            // Coloca a mensagem de erro na sessão para ser exibida na próxima tela
            String mensagemErro = "ERRO FATAL: Falha ao carregar produtos para movimentação. Detalhe: " + e.getMessage();
            request.getSession().setAttribute("mensagemMovimento", mensagemErro);
            // Redireciona para evitar um loop e mostrar a mensagem
            response.sendRedirect("principal.jsp"); 
        } catch (Exception e) {
            System.err.println("ERRO GENÉRICO ao listar produtos. Detalhe:");
            e.printStackTrace();
            request.getSession().setAttribute("mensagemMovimento", "ERRO DESCONHECIDO ao listar produtos. Consulte o administrador.");
            response.sendRedirect("principal.jsp"); 
        }
    }

    private void registrarMovimentacao(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        String mensagemAlerta = null;

        try {
            // 1. Receber e Validar Parâmetros
            int idProduto = Integer.parseInt(request.getParameter("idproduto"));
            char tipoMovto = request.getParameter("tipoMovto").charAt(0); // 'E' ou 'S'
            double qtdMovto = Double.parseDouble(request.getParameter("qtdMovto"));
            
            Usuario usuario = (Usuario) request.getSession().getAttribute("usuarioLogado");
            int idUsuario = usuario.getId();

            // 2. Processar a Transação
            if (qtdMovto <= 0) {
                mensagemAlerta = "ERRO: A quantidade de movimento deve ser maior que zero.";
            } else {
                ProdutoDAO produtoDAO = new ProdutoDAO();
                Produto produto = produtoDAO.buscarPorId(idProduto);

                if (produto == null) {
                    mensagemAlerta = "ERRO: Produto não encontrado para o ID " + idProduto;
                } else {
                    double estoqueAtual = produto.getEstoqueatual();
                    double novoEstoque = estoqueAtual;

                    // Lógica de Movimentação e Validação
                    if (tipoMovto == 'S') {
                        if (qtdMovto > estoqueAtual) {
                            mensagemAlerta = String.format("ERRO: Saída de %.2f %s não permitida. Estoque atual: %.2f %s.",
                                qtdMovto, produto.getUnidmedida(), estoqueAtual, produto.getUnidmedida());
                        } else {
                            novoEstoque = estoqueAtual - qtdMovto;
                        }
                    } else if (tipoMovto == 'E') {
                        novoEstoque = estoqueAtual + qtdMovto;
                    } else {
                         mensagemAlerta = "ERRO: Tipo de movimento inválido. Use 'E' ou 'S'.";
                    }
                    
                    // Se não houve erro de validação
                    if (mensagemAlerta == null) {
                        // 2a. Salvar Movimento
                        Movimento mov = new Movimento();
                        // ATENÇÃO: O model Movimento.java usa getIdmaterial(), o que equivale ao ID do produto
                        mov.setIdmaterial(idProduto); 
                        mov.setIdusuario(idUsuario);
                        mov.setTipomovto(tipoMovto);
                        mov.setQtdmovto(qtdMovto);
                        
                        MovimentoDAO movimentoDAO = new MovimentoDAO();
                        movimentoDAO.inserir(mov);

                        // 2b. Atualizar Estoque
                        produtoDAO.atualizarEstoque(idProduto, novoEstoque);

                        // Cria mensagem de SUCESSO ou ATENÇÃO
                        mensagemAlerta = String.format("Movimentação de %c (%.2f %s) registrada com sucesso para %s. Novo estoque: %.2f %s.",
                            tipoMovto, qtdMovto, produto.getUnidmedida(), produto.getDescproduto(), novoEstoque, produto.getUnidmedida());

                        if (novoEstoque < produto.getEstoqueminimo()) {
                            mensagemAlerta += "<br><strong class='text-danger'>🚨 ATENÇÃO: O estoque atual está ABAIXO do limite mínimo!</strong>";
                        }
                    }
                }
            }
        } catch (NumberFormatException e) {
            mensagemAlerta = "ERRO: O ID do produto ou a quantidade de movimento são inválidos.";
        } catch (RuntimeException e) {
            System.err.println("ERRO DAO/RUNTIME ao processar movimentação. Detalhe:");
            e.printStackTrace();
            mensagemAlerta = "ERRO INTERNO: Falha de conexão ou banco de dados. Verifique o log do servidor. Detalhe: " + e.getMessage();
        } catch (Exception e) {
            System.err.println("ERRO GENÉRICO ao processar movimentação. Detalhe:");
            e.printStackTrace();
            mensagemAlerta = "ERRO DESCONHECIDO: Falha na aplicação. Consulte o administrador.";
        }
        
        // 3. Ponto ÚNICO de Redirecionamento
        request.getSession().setAttribute("mensagemMovimento", mensagemAlerta);
        response.sendRedirect("MovimentoServlet?acao=listarEstoque");
    }
}