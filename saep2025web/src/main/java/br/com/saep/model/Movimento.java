package br.com.saep.model;

import java.time.LocalDateTime;

public class Movimento {
	private int idtransacao;
	private int idmaterial;
	private int idusuario;
	private char tipomovto; // E ou S
	private double qtdmovto;
	private LocalDateTime datahoramovto;

	public Movimento() {
	}

	public int getIdtransacao() {
		return idtransacao;
	}

	public void setIdtransacao(int idtransacao) {
		this.idtransacao = idtransacao;
	}

	public int getIdmaterial() {
		return idmaterial;
	}

	public void setIdmaterial(int idmaterial) {
		this.idmaterial = idmaterial;
	}

	public int getIdusuario() {
		return idusuario;
	}

	public void setIdusuario(int idusuario) {
		this.idusuario = idusuario;
	}

	public char getTipomovto() {
		return tipomovto;
	}

	public void setTipomovto(char tipomovto) {
		this.tipomovto = tipomovto;
	}

	public double getQtdmovto() {
		return qtdmovto;
	}

	public void setQtdmovto(double qtdmovto) {
		this.qtdmovto = qtdmovto;
	}

	public LocalDateTime getDatahoramovto() {
		return datahoramovto;
	}

	public void setDatahoramovto(LocalDateTime datahoramovto) {
		this.datahoramovto = datahoramovto;
	}
}