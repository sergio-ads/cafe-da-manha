package br.com.cafe.repository;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.Query;

import org.springframework.context.annotation.Configuration;

import br.com.cafe.model.Usuario;
import br.com.cafe.model.Opcao;
import javassist.NotFoundException;

@Configuration // Classe de configuração
public class CafeBD {
	@PersistenceContext // Injeta a dependência via contexto de persistência
	private EntityManager entityManager; // Busca o gerenciador de BD
	
	@SuppressWarnings("unchecked") // O compilador java não consegue verificar a conversão de List para List<Usuario> por isso desativamos o aviso
	public List<Usuario> listarUsuario() { // Localiza todos os usuários
		Query query = entityManager.createNativeQuery("select * from usuario", Usuario.class); // Cria query SQL
		return query.getResultList(); // Retorna os resultados
	}
	
	public Usuario listarUsuarioById(String cpf) { // Localiza usuário por CPF
		try {
			Query query = entityManager.createNativeQuery("select * from usuario where cpf = ?", Usuario.class); // Cria query SQL
			query.setParameter(1, cpf); // Define o primeiro parâmetro ? da query acima
			return (Usuario) query.getSingleResult(); // Retorna o resultado único ou lança exceção em outros casos
		} catch (NoResultException e) { // Exceção caso não encontre nenhum usuário
			throw new NoResultException("CPF não localizado!"); // Lança exceção com a mensagem a ser retornada via API
		} catch (NonUniqueResultException e) { // Exceção caso haja mais de um usuário 
			throw new NonUniqueResultException("Há mais de um CPF com os dados informados"); // Lança exceção com a mensagem a ser retornada via API
		}
	}
	
	public boolean existUsuarioById(String cpf) { // Verifica se existe usuário por CPF
		try {
			Query query = entityManager.createNativeQuery("select * from usuario where cpf = ?", Usuario.class); // Cria query SQL
			query.setParameter(1, cpf); // Define o primeiro parâmetro ? da query acima
			query.getSingleResult(); // Testa se o resultado é único ou lança exceção em outros casos
			return true; // Retorna verdadeiro caso exista apenas um usuário com esse CPF
		} catch (NoResultException e) { // Exceção caso não encontre nenhum usuário
			return false; // Retorna falso caso não exista usuário com esse CPF
		} catch (NonUniqueResultException e) { // Exceção caso haja mais de um usuário
			return true; // Retorna falso caso exista mais de um usuário com esse CPF
		}
	}
	
	public int countUsuarioById(String cpf) { // Contar usuário por CPF
		Query query = entityManager.createNativeQuery("select * from usuario where cpf = ?", Usuario.class); // Cria query SQL
		query.setParameter(1, cpf); // Define o primeiro parâmetro ? da query acima
		return query.getResultList().size(); // Retorna o tamanho dos resultados
	}

	public Usuario inserirUsuario(Usuario usuario) { // Adiciona usuário no banco de dados
		try {
			if(!existUsuarioById(usuario.getCpf())) { //café não existe
				Query usuarioQuery = entityManager.createNativeQuery("insert into usuario (cpf, nome) values (?,?)", Usuario.class); // Cria query SQL
				usuarioQuery.setParameter(1, usuario.getCpf()); // Define o primeiro parâmetro ? da query acima
				usuarioQuery.setParameter(2, usuario.getNome()); // Define o segundo parâmetro ? da query acima
				usuarioQuery.executeUpdate(); // Executa a query
			}			
			if(!usuario.getOpcoes().isEmpty()) { // Verifica se o usuário possui opções de lanche
				for(Opcao opcao : usuario.getOpcoes()) { // Itera sobre cada opção
					if(!existOpcao(opcao.getOpcao())) { // Verifica se não existe essa opção para qualquer usuário
						Query opcaoQuery = entityManager.createNativeQuery("insert into opcao (opcao,usuario_cpf) values (?,?)", Opcao.class); // Cria query SQL
						opcaoQuery.setParameter(1, opcao); // Define o primeiro parâmetro ? da query acima
						opcaoQuery.setParameter(2, usuario.getCpf()); // Define o segundo parâmetro ? da query acima
						opcaoQuery.executeUpdate(); // Executa a query
					} else { // Se já existir essa opção para qualquer usuário então execute...
						if(!OpcaoIsForUsuario(opcao.getOpcao(), usuario.getCpf())) { // Verificar se a opção existente não é do usuário
							throw new IllegalArgumentException("Já há essa opção de café da manhã com outro usuário"); // Lança exceção com a mensagem a ser retornada via API
						}					
					}
				}
			}
			return usuario; // Retorna o usuário adicionado
		} catch (IllegalArgumentException e) { // Exceção lançada quando um ou alguns dos dados estejam incorretos
			throw e; // Lança exceção com a mensagem a ser retornada via API
		}
	}
	
	public Usuario alterarUsuario(Usuario usuario, String cpf, String opcao) { // Altera usuário no BD
		try {
			if(existUsuarioById(usuario.getCpf())) { // Verifica se existe usuário com esse CPF
				Query usuarioQuery = entityManager.createNativeQuery("update usuario set nome = ? where cpf = ?", Usuario.class); // Cria query SQL
				usuarioQuery.setParameter(1, usuario.getNome()); // Define o primeiro parâmetro ? da query acima
				usuarioQuery.setParameter(2, cpf); // Define o segundo parâmetro ? da query acima
				usuarioQuery.executeUpdate(); // Executa a query
				if(opcao != null && !opcao.equals("")) { // Verifica se foi informada alguma opção
					if(!existOpcao(opcao)) // Verifica se não existe essa opção para qualquer usuário
						throw new IllegalArgumentException("Opção não localizada"); // Lança exceção com a mensagem a ser retornada via API
					if(usuario.getOpcoes().size() < 1 || usuario.getOpcoes().size() > 1) // Verifica se foi informada apenas uma opção para atualizar
						throw new IllegalArgumentException("Deve ser indicada apenas uma opção para atualizar"); // Lança exceção com a mensagem a ser retornada via API
					if(existOpcao(usuario.getOpcoes().get(0).getOpcao())) // Verifica se existe essa opção para qualquer usuário
						throw new IllegalArgumentException("Já há essa opção de café da manhã com outro usuário"); // Lança exceção com a mensagem a ser retornada via API
					Query opcaoQuery = entityManager.createNativeQuery("update opcao set opcao = ? where opcao = ?", Usuario.class); // Cria query SQL
					opcaoQuery.setParameter(1, usuario.getOpcoes().get(0)); // Define o primeiro parâmetro ? da query acima
					opcaoQuery.setParameter(2, opcao); // Define o segundo parâmetro ? da query acima
					opcaoQuery.executeUpdate(); // Executa a query
				}			
				entityManager.flush(); // Sincroniza o contexto de persistência
				entityManager.clear(); // Limpa o contexto de persistência para retornar os dados atualizados
				return listarUsuarioById(cpf); // Retorna o usuário alterado
			} else
				throw new IllegalArgumentException("Usuário não localizado"); // Lança exceção com a mensagem a ser retornada via API
			
		} catch (IllegalStateException e) { // Exceção lançada caso haja algum erro nos dados 
			throw new IllegalStateException("Falha ao salvar os dados"); // Lança exceção com a mensagem a ser retornada via API
		}
	}
	
	public void excluirUsuario(String cpf) throws NotFoundException { // Exclui usuário no BD
		try {
			listarUsuarioById(cpf);
			
			Query queryOpcao = entityManager.createNativeQuery("delete from opcao where usuario_cpf = ?", Opcao.class); // Cria query SQL
			queryOpcao.setParameter(1, cpf); // Define o primeiro parâmetro ? da query acima
			queryOpcao.executeUpdate(); // Executa a query
			
			Query queryUsuario = entityManager.createNativeQuery("delete from usuario where cpf = ?", Usuario.class); // Cria query SQL
			queryUsuario.setParameter(1, cpf); // Define o primeiro parâmetro ? da query acima
			queryUsuario.executeUpdate(); // Executa a query
		} catch (NoResultException e) { // Exceção caso não encontre nenhum usuário
			throw new NoResultException("CPF não localizado"); // Lança exceção com a mensagem a ser retornada via API
		} catch (NonUniqueResultException e) { // Exceção caso haja mais de um usuário
			throw new NonUniqueResultException("Há mais de um CPF com os dados informados"); // Lança exceção com a mensagem a ser retornada via API
		}
	}
	
	public boolean existOpcao(String opcao) { // Verifica se existe opção
		try {
			Query query = entityManager.createNativeQuery("select * from opcao where opcao = ?", Opcao.class); // Cria query SQL
			query.setParameter(1, opcao); // Define o primeiro parâmetro ? da query acima
			query.getSingleResult(); // Testa se o resultado é único ou lança exceção em outros casos
			return true; // Retorna verdadeiro
		} catch (NoResultException e) { // Exceção caso não encontre nenhuma opção
			return false; // Retorna falso
		} catch (NonUniqueResultException e) { // Exceção caso haja mais de uma opção
			return true; // Retorna verdadeiro
		}
	}
	
	private boolean OpcaoIsForUsuario(String opcao, String usuario_cpf) { // Verifica se a opção é do usuário
		Query query = entityManager.createNativeQuery("select * from opcao where opcao = ?", Opcao.class); // Cria query SQL
		query.setParameter(1, opcao); // Define o primeiro parâmetro ? da query acima
		Opcao opcaoObj = (Opcao) query.getSingleResult();  // Tenta buscar resultado único
		if(opcaoObj.obterUsuario().getCpf().equals(usuario_cpf)) { // Se a opção for do usuário então...
			return true; // Retorna verdadeiro
		} else // Caso contrário...
			return false; // Retorna falso
	}

}
