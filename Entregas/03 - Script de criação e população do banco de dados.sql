
-- ----------------------------------------------------------------------
-- Script de Criação e População do Banco de Dados - SAEP FioTinta
-- ----------------------------------------------------------------------
-- Script de redefinição, criação e população completa do banco de dados SAEP

-- 1. LIMPEZA TOTAL (RESET)
-- --------------------------------------------------------------------------

-- Remove o banco de dados se ele existir, garantindo que não haja resquícios.
-- Este comando remove também todas as tabelas e dados contidos no banco.
-- RESET DO BANCO E USUÁRIO
DROP DATABASE IF EXISTS saep_db1;
DROP USER IF EXISTS 'saep_db1'@'localhost';

-- CRIAÇÃO DO BANCO E USUÁRIO
CREATE DATABASE saep_db1;
CREATE USER 'saep_db1'@'localhost' IDENTIFIED BY 'saep_db1';
GRANT ALL PRIVILEGES ON *.* TO 'saep_db1'@'localhost';
FLUSH PRIVILEGES;
select * from mysql.user;
USE saep_db1;

-- TABELA USUARIO
DROP TABLE IF EXISTS usuario;
CREATE TABLE usuario (
    id INT AUTO_INCREMENT PRIMARY KEY,
    nome VARCHAR(40) NOT NULL,
    email VARCHAR(60) NOT NULL UNIQUE, -- Adicionado UNIQUE para consistência
    senha VARCHAR(20) NOT NULL
);

-- TABELA MATERIAL
DROP TABLE IF EXISTS produto;
CREATE TABLE produto (
    id INT AUTO_INCREMENT PRIMARY KEY,
    descproduto VARCHAR(60) NOT NULL,
    tipoproduto CHAR(1) NOT NULL COMMENT 'C=Corante, A=Alvejante, U=Auxiliar',
    unidmedida CHAR(2) NOT NULL COMMENT 'kg ou L',
    estoqueminimo DECIMAL(8,4) NOT NULL,
    estoqueatual DECIMAL(8,4) NOT NULL
);

-- TABELA MOVIMENTO
DROP TABLE IF EXISTS movimento;
CREATE TABLE movimento (
    idtransacao INT AUTO_INCREMENT PRIMARY KEY,
    idproduto INT NOT NULL,
    idusuario INT NOT NULL,
    tipomovto CHAR(1) NOT NULL COMMENT 'E=Entrada, S=Saída',
    qtdmovto DECIMAL(8,4) NOT NULL,
    datahoramovto TIMESTAMP DEFAULT CURRENT_TIMESTAMP NOT NULL,
    
    FOREIGN KEY (idproduto) REFERENCES produto(id) ON DELETE RESTRICT,
    FOREIGN KEY (idusuario) REFERENCES usuario(id) ON DELETE RESTRICT
);

-- POPULAÇÃO DE DADOS

-- Usuário
INSERT INTO usuario (nome, email, senha)
VALUES ('Administrador', 'admin@email.com', '12345');

-- Materiais
INSERT INTO produto (descproduto, tipoproduto, unidmedida, estoqueminimo, estoqueatual)
VALUES 
    ('Remazol Blue R', 'C', 'kg', 10.00, 50.00),
    ('Peróxido de Hidrogênio', 'A', 'L', 50.00, 20.00),
    ('Hidróxido de Sódio/NaOH', 'A', 'kg', 5.00, 15.00),
    ('Foron Yellow', 'C', 'kg', 5.00, 1.00);

-- Movimentos (ajustado para usar idusuario = 1 existente)
INSERT INTO movimento (idproduto, idusuario, tipomovto, qtdmovto)
VALUES 
    (1, 1, 'E', 10.00),
    (3, 1, 'S', 5.00);
