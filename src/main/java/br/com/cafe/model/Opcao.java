package br.com.cafe.model;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

@Entity // Mapeia a classe como uma entidade persistida no banco de dados
@Table(name = "opcao") // Define o nome da tabela como dados minúscula (para evitar problemas com o linux)
public class Opcao implements Serializable { // Cria a classe e permite a serialização da mesma
	private static final long serialVersionUID = -7447808137813367572L;
	
	@Id // Marca o atributo abaixo como chave primária
	@NotEmpty(message = "Opção é obrigatório") // Atributo não pode ser vazio
	@Column(unique = true) // Atributo deve ser único no BD
	String opcao; // Atributo do BD
	@ManyToOne // Cria uma relação de muitos para um
	Usuario usuario; // Atributo da tabela usuário
	
	// Cria os getters and setters necessários abaixo
	public String getOpcao() {
		return opcao;
	}
	public void setOpcao(String opcao) {
		this.opcao = opcao;
	}
	public Usuario obterUsuario() { // Modifica o nome para não retornar via JSON
		return usuario;
	}
	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	
}
