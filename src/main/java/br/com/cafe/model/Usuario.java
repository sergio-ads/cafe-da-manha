package br.com.cafe.model;

import java.io.Serializable;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotEmpty;

import org.hibernate.validator.constraints.br.CPF;

@Entity // Mapeia a classe como uma entidade persistida no banco de dados
@Table(name = "usuario") // Define o nome da tabela como dados minúscula (para evitar problemas com o linux)
public class Usuario implements Serializable { // Cria a classe e permite a serialização da mesma
	private static final long serialVersionUID = 3259572963299822113L;
	
	@Id // Marca o atributo abaixo como chave primária
	@NotEmpty(message = "CPF é obrigatório") // Atributo não pode ser vazio
	@CPF(message = "CPF Inválido") // Valida o CPF 
	@Column(unique = true) // Atributo deve ser único no BD
	String cpf; // Atributo do BD
	@NotEmpty(message = "Nome é obrigatório") // Atributo não pode ser vazio
	String nome; // Atributo do BD
	@OneToMany(mappedBy = "usuario") // Marca o atributo com relação um para muitos e configura a chave estrangeira de opções
	private List<Opcao> opcoes; // Atributo do BD
	
	// Cria os getters and setters necessários abaixo
	public String getCpf() {
		return cpf;
	}
	public void setCpf(String cpf) {
		this.cpf = cpf;
	}
	public String getNome() {
		return nome;
	}
	public void setNome(String nome) {
		this.nome = nome;
	}
	public List<Opcao> getOpcoes() {
		return opcoes;
	}
	public void setOpcoes(List<Opcao> opcoes) {
		this.opcoes = opcoes;
	}
	
	
}
