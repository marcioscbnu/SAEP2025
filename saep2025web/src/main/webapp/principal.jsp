<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="br.com.saep.model.Usuario" %>
<%@ include file="includes/_header.jsp" %>
<% 
// Checa se o usuário está logado
Usuario usuario = (Usuario) session.getAttribute("usuarioLogado");
if (usuario == null) { response.sendRedirect("login.jsp"); return; }
%>
<h2>Bem-vindo(a), <%= usuario.getNome() %>!</h2>
<p class="lead">Selecione uma opção para gerenciar o sistema de estoque de beneficiamento.</p>

<div class="row mt-4">
    <div class="col-md-6 mb-4">
        <div class="card text-center shadow">
            <div class="card-body">
                <i class="fas fa-flask fa-4x text-info mb-3"></i>
                <h5 class="card-title">Cadastro de Materiais</h5>
                <p class="card-text">Incluir, alterar, buscar e excluir informações dos materiais.</p>
                <a href="ProdutoServlet?acao=listar" class="btn btn-info w-100"><i class="fas fa-edit"></i> Acessar</a>
            </div>
        </div>
    </div>
    
    <div class="col-md-6 mb-4">
        <div class="card text-center shadow">
            <div class="card-body">
                <i class="fas fa-exchange-alt fa-4x text-success mb-3"></i>
                <h5 class="card-title">Movimentação de Estoque</h5>
                <p class="card-text">Registrar entradas e saídas de materiais e monitorar o estoque.</p>
                
                <a href="MovimentoServlet?acao=listarEstoque" class="btn btn-success w-100"><i class="fas fa-arrow-right"></i> Acessar</a>
                
            </div>
        </div>
    </div>
</div>

<div class="mt-5 text-end">
    <a href="LogoutServlet" class="btn btn-danger"><i class="fas fa-sign-out-alt"></i> Sair do Sistema</a>
</div>

<%@ include file="includes/_footer.jsp" %>