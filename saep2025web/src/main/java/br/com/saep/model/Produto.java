package br.com.saep.model;

public class Produto {
	private int id;
	private String descproduto;
	private char   tipoproduto; // C, A, U
	private String unidmedida; // kg ou L
	private double estoqueminimo;
	private double estoqueatual;

	public Produto() {
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getDescproduto() {
		return descproduto;
	}

	public void setDescproduto(String descproduto) {
		this.descproduto = descproduto;
	}

	public char getTipoproduto() {
		return tipoproduto;
	}

	public void setTipoproduto(char tipoproduto) {
		this.tipoproduto = tipoproduto;
	}

	public String getUnidmedida() {
		return unidmedida;
	}

	public void setUnidmedida(String unidmedida) {
		this.unidmedida = unidmedida;
	}

	public double getEstoqueminimo() {
		return estoqueminimo;
	}

	public void setEstoqueminimo(double estoqueminimo) {
		this.estoqueminimo = estoqueminimo;
	}

	public double getEstoqueatual() {
		return estoqueatual;
	}

	public void setEstoqueatual(double estoqueatual) {
		this.estoqueatual = estoqueatual;
	}
}