package br.com.cafe.rest;

import java.util.List;

import javax.transaction.Transactional;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import br.com.cafe.model.Usuario;
import br.com.cafe.repository.CafeBD;

@Transactional // Torna a classe capaz de gerenciar transações do BD
@RestController // Mapeia o controlador como uma API Rest
public class CafeRestController { // Classe responsável por receber os dados, efetuar o controle e retornar via JSON
	@Autowired
	private CafeBD cafeBD; // Injeta a fábrica do gerenciador de dados

	@GetMapping("/cafe") // Endpoint que lista todos os dados
	public List<Usuario> Get() {		
		return cafeBD.listarUsuario();
	}
	
	@GetMapping("/cafe/{cpf}") // Endpoint que lista os dados baseado no cpf
	public Usuario GetById(@PathVariable String cpf) {
		return cafeBD.listarUsuarioById(cpf);
	}
	
	@PostMapping("/cafe") // Endpoint que recebe e salva os dados
	public Usuario Post(@Valid @RequestBody(required = false) Usuario usuario) {
		return cafeBD.inserirUsuario(usuario);
	}
	
	@PutMapping("/cafe/{cpf}") // Endpoint que altera os dados do café do usuário
	public Usuario PutCpf(@Valid @RequestBody(required = false) Usuario usuario, @PathVariable String cpf) {
		return cafeBD.alterarUsuario(usuario, cpf, null);
	}
	
	@PutMapping("/cafe/{cpf}/{opcao}") // Endpoint altera as opções do café do usuário
	public Usuario PutOpcao(@Valid @RequestBody(required = false) Usuario usuario, @PathVariable String cpf, @PathVariable String opcao) {
		return cafeBD.alterarUsuario(usuario, cpf, opcao);
	}

	@DeleteMapping("/cafe/{cpf}") // Endpoint que apaga os dados baseado no cpf
	public void Delete(@PathVariable String cpf) throws Exception {
		cafeBD.excluirUsuario(cpf);
	}

}
