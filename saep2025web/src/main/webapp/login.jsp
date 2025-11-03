<!DOCTYPE html>
<html>
<head>
<title>Login - SAEP</title>
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
</head>
<body class="bg-light">
    <div class="container">
        <div class="row justify-content-center mt-5">
            <div class="col-md-4">
                <div class="card shadow">
                    <div class="card-body">
                        <h3 class="card-title text-center">Login - |Estoques de Beneficiamento - Ação</h3>
                        
                        <% 
                        // 4.1: Mensagem de falha de autenticação
                        String erro = (String) request.getAttribute("mensagemErro");
                        if (erro != null) {
                        %>
                            <div class="alert alert-danger text-center"><%= erro %></div>
                        <% } %>
                        
                        <form action="LoginServlet" method="post">
                            <div class="mb-3">
                                <label for="email" class="form-label">Email:</label>
                                <input type="email" class="form-control" id="email" name="email" required>
                            </div>
                            <div class="mb-3">
                                <label for="senha" class="form-label">Senha:</label>
                                <input type="password" class="form-control" id="senha" name="senha" required>
                            </div>
                            <button type="submit" class="btn btn-primary w-100">Entrar</button>
                        </form>
                    </div>
                </div>
            </div>
        </div>
    </div>
</body>
</html>