<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="br.com.saep.model.Produto, java.util.List" %>
<% 
// Checa se o usuário está logado
if (session.getAttribute("usuarioLogado") == null) { response.sendRedirect("login.jsp"); return; } 

// Pega a lista de produtos (estoque) enviada pelo MovimentoServlet
List<Produto> produtos = (List<Produto>) request.getAttribute("produtos");
if (produtos == null) { produtos = java.util.Collections.emptyList(); }

// Recupera a mensagem de alerta/sucesso/erro da sessão e a remove
String msgAlerta = (String) session.getAttribute("mensagemMovimento"); 
session.removeAttribute("mensagemMovimento");
%>
<!DOCTYPE html>
<html lang="pt-br">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1">
    <title>SAEP - Movimentação de Estoque</title>
    <link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.5.2/css/all.min.css">
    <style>
        .table-min-width { min-width: 800px; }
        .text-danger-custom { color: #dc3545; font-weight: bold; }
    </style>
</head>
<body>
<div class="container mt-5">

<a href="principal.jsp" class="btn btn-secondary mb-3"><i class="fas fa-arrow-left"></i> Voltar à Principal</a>

<h2>Movimentação de Estoque - Gestão do Inventário</h2>

<% // Exibe a mensagem de alerta/sucesso/erro
if (msgAlerta != null) { 
    // Detecta se é erro para usar alert-danger
    boolean isError = msgAlerta.toUpperCase().contains("ERRO") || msgAlerta.toUpperCase().contains("FALHA");
%>
    <div class="alert alert-<%= isError ? "danger" : "success" %> alert-dismissible fade show" role="alert">
        <%= msgAlerta %>
        <button type="button" class="btn-close" data-bs-dismiss="alert" aria-label="Close"></button>
    </div>
<% } %>

<p class="lead">Lista de produtos com estoque atual. Clique em Entrada ou Saída para registrar um movimento.</p>

<div class="table-responsive">
    <table class="table table-striped table-hover table-min-width">
        <thead class="table-dark">
            <tr>
                <th>ID</th>
                <th>Descrição do Produto</th>
                <th>Tipo</th>
                <th>Estoque Mín.</th>
                <th>Estoque Atual</th>
                <th>Unid.</th>
                <th>Ações</th>
            </tr>
        </thead>
        <tbody>
            <% if (produtos.isEmpty()) { %>
                <tr>
                    <td colspan="7" class="text-center">Nenhum produto cadastrado ou em estoque para movimentação.</td>
                </tr>
            <% } else { %>
                <% for (Produto p : produtos) { %>
                    <% 
                        // Cor da linha: Amarelo se o estoque estiver abaixo do mínimo
                        String rowClass = p.getEstoqueatual() < p.getEstoqueminimo() ? "table-warning" : ""; 
                    %>
                    <tr class="<%= rowClass %>">
                        <td><%= p.getId() %></td>
                        <td><%= p.getDescproduto() %></td>
                        <td>
                            <% 
                                char tipo = p.getTipoproduto();
                                if (tipo == 'C') out.print("Corante");
                                else if (tipo == 'A') out.print("Alvejante");
                                else if (tipo == 'U') out.print("Auxiliar");
                                else out.print("Outro");
                            %>
                        </td>
                        <td><%= String.format("%.2f", p.getEstoqueminimo()) %></td>
                        <td>
                            <strong><%= String.format("%.2f", p.getEstoqueatual()) %></strong>
                            <% if (p.getEstoqueatual() < p.getEstoqueminimo()) { %>
                                <span class="badge bg-danger">ABAIXO DO MÍN.</span>
                            <% } %>
                        </td>
                        <td><%= p.getUnidmedida() %></td>
                        <td>
                            <button type="button" class="btn btn-sm btn-success" 
                                data-bs-toggle="modal" data-bs-target="#movimentoModal" 
                                onclick="prepararMovimento('entrada', <%= p.getId() %>, '<%= p.getDescproduto() %>', '<%= p.getUnidmedida() %>', <%= p.getEstoqueatual() %>)">
                                <i class="fas fa-plus"></i> Entrada
                            </button>
                            <button type="button" class="btn btn-sm btn-danger" 
                                data-bs-toggle="modal" data-bs-target="#movimentoModal" 
                                onclick="prepararMovimento('saida', <%= p.getId() %>, '<%= p.getDescproduto() %>', '<%= p.getUnidmedida() %>', <%= p.getEstoqueatual() %>)">
                                <i class="fas fa-minus"></i> Saída
                            </button>
                        </td>
                    </tr>
                <% } %>
            <% } %>
        </tbody>
    </table>
</div>


<div class="modal fade" id="movimentoModal" tabindex="-1" aria-labelledby="movimentoModalLabel" aria-hidden="true">
  <div class="modal-dialog">
    <div class="modal-content">
      <form action="MovimentoServlet" method="POST" onsubmit="return validarMovimento()">
        <div class="modal-header">
          <h5 class="modal-title" id="movimentoModalLabel">Registrar Movimentação</h5>
          <button type="button" class="btn-close" data-bs-dismiss="modal" aria-label="Close"></button>
        </div>
        <div class="modal-body">
            <input type="hidden" name="acao" value="registrar">
            <input type="hidden" name="idproduto" id="idProdutoMovimento">
            
            <p>Produto: <strong id="nomeProdutoMovimento"></strong></p>
            <p>Estoque Atual: <strong id="estoqueAtualDisplay"></strong></p>
            
            <div class="mb-3">
                <label for="tipoMovto" class="form-label">Tipo de Movimento</label>
                <select class="form-select" name="tipoMovto" id="tipoMovto" required>
                    <option value="E">Entrada</option>
                    <option value="S">Saída</option>
                </select>
            </div>
            
            <div class="mb-3">
                <label for="qtdMovto" class="form-label">Quantidade (<span id="unidMedidaDisplay"></span>)</label>
                <input type="number" step="0.01" class="form-control" name="qtdMovto" id="qtdMovto" required min="0.01">
                <small class="form-text text-muted" id="avisoSaida" style="display: none;">A quantidade de saída não pode ser maior que o estoque atual.</small>
            </div>
        </div>
        <div class="modal-footer">
          <button type="button" class="btn btn-secondary" data-bs-dismiss="modal">Cancelar</button>
          <button type="submit" class="btn btn-primary">Registrar</button>
        </div>
      </form>
    </div>
  </div>
</div>

<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<script>
    let estoqueAtual = 0; // Variável global para rastrear o estoque

    function prepararMovimento(tipo, id, nome, unidade, atual) {
        // Correção para sanitizar string nome no JavaScript
        nome = nome.replace(/'/g, "\\'");
        
        estoqueAtual = atual;
        
        document.getElementById('idProdutoMovimento').value = id;
        document.getElementById('nomeProdutoMovimento').innerText = nome;
        document.getElementById('estoqueAtualDisplay').innerText = atual.toFixed(2) + ' ' + unidade;
        document.getElementById('unidMedidaDisplay').innerText = unidade;
        
        document.getElementById('tipoMovto').value = tipo === 'entrada' ? 'E' : 'S';
        
        // Configurações específicas para Saída
        const avisoSaida = document.getElementById('avisoSaida');
        if (tipo === 'saida') {
            avisoSaida.style.display = 'block';
        } else {
            avisoSaida.style.display = 'none';
        }
        
        document.getElementById('movimentoModalLabel').innerText = 
            tipo === 'entrada' ? 'Registrar ENTRADA' : 'Registrar SAÍDA';
        
        // Limpa e foca no campo de quantidade
        document.getElementById('qtdMovto').value = '';
        setTimeout(() => { document.getElementById('qtdMovto').focus(); }, 300);
    }
    
    function validarMovimento() {
        const tipoMovto = document.getElementById('tipoMovto').value;
        const qtdMovto = parseFloat(document.getElementById('qtdMovto').value);
        
        if (isNaN(qtdMovto) || qtdMovto <= 0) {
            alert("A quantidade de movimento deve ser um número positivo.");
            return false;
        }
        
        if (tipoMovto === 'S' && qtdMovto > estoqueAtual) {
            alert(`A quantidade de saída (${qtdMovto.toFixed(2)}) não pode ser maior que o estoque atual (${estoqueAtual.toFixed(2)}).`);
            return false;
        }
        
        return true;
    }
</script>

</div>
</body>
</html>