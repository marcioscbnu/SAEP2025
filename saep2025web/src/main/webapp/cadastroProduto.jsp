<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="br.com.saep.model.Produto, java.util.List" %>
<% 
if (session.getAttribute("usuarioLogado") == null) { response.sendRedirect("login.jsp"); return; } 
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SAEP - Cadastro de Produtos</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <style>
        .text-danger-custom { color: #dc3545; font-weight: bold; }
    </style>
</head>
<body>
<div class="container mt-5">

<a href="principal.jsp" class="btn btn-secondary mb-3"><i class="fas fa-arrow-left"></i> Voltar à Principal</a>

<h2>Cadastro e Gestão de Produtos</h2>

<% 
String msgErro = (String) session.getAttribute("mensagemErro"); 
if (msgErro != null) { 
    session.removeAttribute("mensagemErro");
    // Determina a classe de alerta
    String alertClass = msgErro.toLowerCase().contains("erro") || msgErro.toLowerCase().contains("falha") ? "alert-danger" : "alert-success";
%>
    <div class="alert <%= alertClass %> alert-dismissible fade show" role="alert">
        <%= msgErro %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<div class="row mb-4">
    <div class="col-md-4">
        <button type="button" class="btn btn-primary w-100" data-bs-toggle="modal" data-bs-target="#modalProduto" onclick="prepararModal('inserir')">
            <i class="fas fa-plus"></i> Novo Produto
        </button>
    </div>
    <div class="col-md-8">
        <form action="ProdutoServlet" method="get" class="d-flex">
            <input type="hidden" name="acao" value="buscar">
            <input type="search" name="termo" class="form-control me-2" placeholder="Pesquisar por Nome ou Unidade de Medida..." aria-label="Pesquisar">
            <button class="btn btn-outline-success" type="submit"><i class="fas fa-search"></i> Buscar</button>
            <a href="ProdutoServlet?acao=listar" class="btn btn-outline-secondary ms-2" title="Limpar Pesquisa"><i class="fas fa-redo"></i></a>
        </form>
    </div>
</div>

<div class="table-responsive">
    <table class="table table-striped table-hover">
        <thead>
            <tr>
                <th>ID</th>
                <th>Nome do Produto</th>
                <th>Tipo</th>
                <th>Unidade</th>
                <th>Estoque Mínimo</th>
                <th>Estoque Atual</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <% 
            @SuppressWarnings("unchecked")
            List<Produto> produtos = (List<Produto>) request.getAttribute("produtos");
            
            if (produtos != null && !produtos.isEmpty()) { 
                for (Produto p : produtos) {
                    // Mapeamento dos tipos para exibição
                    String tipoExibicao = "Desconhecido";
                    if (p.getTipoproduto() == 'A' || p.getTipoproduto() == 'a') {
                        tipoExibicao = "Acabado";
                    } else if (p.getTipoproduto() == 'P' || p.getTipoproduto() == 'p') {
                        tipoExibicao = "Em Processo";
                    } else if (p.getTipoproduto() == 'M' || p.getTipoproduto() == 'm') {
                        tipoExibicao = "Matéria Prima";
                    }
            %>
                <tr>
                    <td><%= p.getId() %></td>
                    <td><%= p.getDescproduto() %></td>
                    <td><%= tipoExibicao %></td>
                    <td><%= p.getUnidmedida() %></td>
                    <td><%= String.format("%.2f", p.getEstoqueminimo()) %></td>
                    <td class="<%= (p.getEstoqueatual() < p.getEstoqueminimo()) ? "text-danger-custom" : "" %>">
                        <%= String.format("%.2f", p.getEstoqueatual()) %>
                    </td>
                    <td>
                        <button class="btn btn-sm btn-warning me-1" data-bs-toggle="modal" data-bs-target="#modalProduto"
                            onclick="prepararModal('editar', 
                                <%= p.getId() %>, 
                                '<%= p.getDescproduto().replace("'", "\\'") %>', 
                                '<%= p.getTipoproduto() %>', 
                                '<%= p.getUnidmedida() %>',  
                                <%= p.getEstoqueminimo() %>, 
                                <%= p.getEstoqueatual() %>)">
                            <i class="fas fa-edit"></i>
                        </button>
                        <a href="ProdutoServlet?acao=excluir&id=<%= p.getId() %>" class="btn btn-sm btn-danger" 
                            onclick="return confirm('Tem certeza que deseja excluir o produto <%= p.getDescproduto() %>? Esta ação é irreversível.')">
                            <i class="fas fa-trash"></i>
                        </a>
                    </td>
                </tr>
            <% } } else { %>
                <tr>
                    <td colspan="6" class="text-center">Nenhum produto cadastrado ou encontrado.</td>
                </tr>
            <% } %>
        </tbody>
    </table>
</div>

<div class="modal fade" id="modalProduto" tabindex="-1" aria-labelledby="modalProdutoLabel" aria-hidden="true">
    <div class="modal-dialog">
        <div class="modal-content">
            <div class="modal-header">
                <h5 class="modal-title" id="modalProdutoLabel">Gestão de Produto</h5>
                <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
            </div>
            <form id="formProduto" action="ProdutoServlet" method="post" onsubmit="return validarFormulario()">
                <div class="modal-body">
                    
                    <input type="hidden" name="acao" id="acaoForm">
                    <input type="hidden" name="id" id="ProdutoId">
                    
                    <div class="mb-3">
                        <label for="descproduto" class="form-label">Nome do Produto:</label>
                        <input type="text" class="form-control" id="descproduto" name="descproduto" required maxlength="100">
                    </div>
                    
                    <div class="mb-3">
                        <label for="tipoproduto" class="form-label">Tipo de Produto:</label>
                        <select class="form-select" id="tipoproduto" name="tipoproduto" required>
                            <option value="">Selecione...</option>
                            <option value="A">A - Acabado</option>
                            <option value="P">P - Em Processo</option>
                            <option value="M">M - Matéria Prima</option>
                        </select>
                    </div>
                    
                    <div class="mb-3">
                        <label for="unidmedida" class="form-label">Unidade de Medida (Ex: KG, L, UN):</label>
                        <input type="text" class="form-control" id="unidmedida" name="unidmedida" required maxlength="2">
                    </div>
                    
                    <div class="mb-3">
                        <label for="estoqueminimo" class="form-label">Estoque Mínimo (Ex: 10.00):</label>
                        <input type="number" step="0.01" min="0" class="form-control" id="estoqueminimo" name="estoqueminimo" required value="0.00">
                    </div>

                    <div class="mb-3">
                        <label for="estoqueatual" class="form-label">Estoque Inicial/Atual (Ex: 50.00):</label>
                        <input type="number" step="0.01" min="0" class="form-control" id="estoqueatual" name="estoqueatual" required value="0.00">
                        <small class="form-text text-muted">A quantidade será ajustada na Movimentação.</small>
                    </div>
                    
                </div>
                <div class="modal-footer">
                    <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
                    <button type="submit" class="btn btn-primary">Salvar</button>
                </div>
            </form>
        </div>
    </div>
</div>

<script>
    function validarFormulario() {
        var desc = document.getElementById('descproduto').value;
        var estMin = parseFloat(document.getElementById('estoqueminimo').value);
        var qtdAtual = parseFloat(document.getElementById('estoqueatual').value);
        
        if (desc.trim() === "") { alert("O nome do produto é obrigatório."); return false; }
        if (estMin < 0 || isNaN(estMin)) { alert("Estoque Mínimo deve ser um número positivo ou zero."); return false; }
        if (qtdAtual < 0 || isNaN(qtdAtual)) { alert("Quantidade Atual deve ser um número positivo ou zero."); return false; }
        return true;
    }

    function prepararModal(acao, id, descProduto, tipoProduto, unidmedida, estoqueminimo, estoqueatual) {
        document.getElementById('acaoForm').value = acao;
        document.getElementById('modalProdutoLabel').innerText = acao === 'inserir' ? 'Inserir Novo Produto' : 'Editar Produto';
        
        if (acao === 'editar') {
            document.getElementById('ProdutoId').value = id;
            document.getElementById('descproduto').value = descProduto;
            document.getElementById('tipoproduto').value = tipoProduto;
            document.getElementById('unidmedida').value = unidmedida;
            document.getElementById('estoqueminimo').value = estoqueminimo;
            document.getElementById('estoqueatual').value = estoqueatual;
        } else {
            document.getElementById('formProduto').reset();
            document.getElementById('ProdutoId').value = '';
            document.getElementById('estoqueminimo').value = '0.00';
            document.getElementById('estoqueatual').value = '0.00';
        }
    }
</script>

</div>
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
</body>
</html>