package org.example.projeto.controller;

import org.example.projeto.dto.ProdutoRequestDTO;
import org.example.projeto.dto.ProdutoResponseDTO;
import org.example.projeto.entity.Produto;
import org.example.projeto.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal; // NOVO: para parâmetros de preço
import java.util.List;
import java.util.stream.Collectors; // NOVO: para converter listas

@RestController
@RequestMapping("/api/produtos")
@CrossOrigin(origins = "*", allowedHeaders = "*")

public class ProdutoController {




    @Autowired
    private ProdutoService produtoService;

    // =============================================
    // MÉTODOS QUE JÁ EXISTIAM (ADAPTADOS PARA DTOs)
    // =============================================

    @PostMapping
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO request) {
        // 1. Converte DTO para entidade
        Produto produto = Produto.fromRequest(request);

        // 2. Salva usando o Service
        Produto produtoSalvo = produtoService.salvar(produto);

        // 3. Converte entidade salva para DTO de resposta
        ProdutoResponseDTO response = produtoSalvo.toResponse();

        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    public List<ProdutoResponseDTO> listar() {
        return produtoService.listar()
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> buscarPorId(@PathVariable Long id) {
        return produtoService.buscarPorId(id)
                .map(produto -> ResponseEntity.ok(produto.toResponse()))
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO request) {

        // 1. Converte DTO para entidade
        Produto produto = Produto.fromRequest(request);

        // 2. Atualiza usando o Service
        Produto produtoAtualizado = produtoService.atualizar(id, produto);

        // 3. Converte para DTO de resposta
        ProdutoResponseDTO response = produtoAtualizado.toResponse();

        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // =============================================
    // NOVOS ENDPOINTS CUSTOMIZADOS (O QUE FOI ADICIONADO)
    // =============================================

    // 1. Buscar por categoria
    // GET /api/produtos/categoria/Eletrônicos
    @GetMapping("/categoria/{categoria}")
    public List<ProdutoResponseDTO> buscarPorCategoria(@PathVariable String categoria) {
        return produtoService.buscarPorCategoria(categoria)
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 2. Buscar por nome (contém, ignorando maiúsculas/minúsculas)
    // GET /api/produtos/buscar/nome?nome=note
    @GetMapping("/buscar/nome")
    public List<ProdutoResponseDTO> buscarPorNome(@RequestParam String nome) {
        return produtoService.buscarPorNome(nome)
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 3. Buscar por faixa de preço
    // GET /api/produtos/buscar/preco?min=100&max=1000
    @GetMapping("/buscar/preco")
    public List<ProdutoResponseDTO> buscarPorPreco(
            @RequestParam BigDecimal min,
            @RequestParam BigDecimal max) {
        return produtoService.buscarPorPrecoEntre(min, max)
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 4. Buscar produtos com estoque baixo
    // GET /api/produtos/buscar/estoque-baixo?quantidade=5
    @GetMapping("/buscar/estoque-baixo")
    public List<ProdutoResponseDTO> buscarEstoqueBaixo(
            @RequestParam(defaultValue = "5") Integer quantidade) {
        return produtoService.buscarEstoqueBaixo(quantidade)
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 5. Buscar por categoria e preço máximo
    // GET /api/produtos/buscar/categoria-preco?categoria=Eletrônicos&precoMax=2000
    @GetMapping("/buscar/categoria-preco")
    public List<ProdutoResponseDTO> buscarPorCategoriaEPrecoMax(
            @RequestParam String categoria,
            @RequestParam BigDecimal precoMax) {
        return produtoService.buscarPorCategoriaEPrecoMax(categoria, precoMax)
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 6. Listar ordenados por preço (crescente)
    // GET /api/produtos/ordenados/preco
    @GetMapping("/ordenados/preco")
    public List<ProdutoResponseDTO> listarOrdenadosPorPreco() {
        return produtoService.listarOrdenadosPorPreco()
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }

    // 7. Listar ordenados por nome (crescente)
    // GET /api/produtos/ordenados/nome
    @GetMapping("/ordenados/nome")
    public List<ProdutoResponseDTO> listarOrdenadosPorNome() {
        return produtoService.listarOrdenadosPorNome()
                .stream()
                .map(Produto::toResponse)
                .collect(Collectors.toList());
    }
}