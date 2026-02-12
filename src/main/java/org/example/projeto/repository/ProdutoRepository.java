package org.example.projeto.repository;

import org.example.projeto.entity.Produto;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository // Indica que esta interface é um componente Spring (bean) da camada de persistência
public interface ProdutoRepository extends JpaRepository<Produto, Long> {
    // JpaRepository já fornece métodos prontos:
    // - save()    -> salvar/atualizar
    // - findAll() -> listar todos
    // - findById()-> buscar por ID
    // - delete()  -> deletar
    // - count()   -> contar registros
}