package org.example.projeto.controller;

import static org.example.projeto.config.ApiPaths.API_V1;

import org.example.projeto.dto.ProdutoRequestDTO;
import org.example.projeto.dto.ProdutoResponseDTO;
import org.example.projeto.entity.Produto;
import org.example.projeto.service.ProdutoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import jakarta.validation.Valid;
import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping(API_V1 + "/produtos")
// REMOVIDO: @CrossOrigin(origins = "*", allowedHeaders = "*")
public class ProdutoController {

    @Autowired
    private ProdutoService produtoService;

    @PostMapping
    @PreAuthorize("hasRole('ADMIN')")  // só admin cria produtos
    public ResponseEntity<ProdutoResponseDTO> criar(@Valid @RequestBody ProdutoRequestDTO request) {
        Produto produto = Produto.fromRequest(request);
        Produto produtoSalvo = produtoService.salvar(produto);
        ProdutoResponseDTO response = produtoSalvo.toResponse();
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping
    // aberto a qualquer usuário autenticado (padrão do SecurityConfig)
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
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ProdutoResponseDTO> atualizar(
            @PathVariable Long id,
            @Valid @RequestBody ProdutoRequestDTO request) {
        Produto produto = Produto.fromRequest(request);
        Produto produtoAtualizado = produtoService.atualizar(id, produto);
        ProdutoResponseDTO response = produtoAtualizado.toResponse();
        return ResponseEntity.ok(response);
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deletar(@PathVariable Long id) {
        produtoService.deletar(id);
        return ResponseEntity.noContent().build();
    }

    // Métodos de busca – abertos a qualquer usuário autenticado
    @GetMapping("/categoria/{categoria}")
    public List<ProdutoResponseDTO> buscarPorCategoria(@PathVariable String categoria) {
        return produtoService.buscarPorCategoria(categoria)
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/buscar/nome")
    public List<ProdutoResponseDTO> buscarPorNome(@RequestParam String nome) {
        return produtoService.buscarPorNome(nome)
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/buscar/preco")
    public List<ProdutoResponseDTO> buscarPorPreco(@RequestParam BigDecimal min,
                                                   @RequestParam BigDecimal max) {
        return produtoService.buscarPorPrecoEntre(min, max)
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/buscar/estoque-baixo")
    public List<ProdutoResponseDTO> buscarEstoqueBaixo(@RequestParam(defaultValue = "5") Integer quantidade) {
        return produtoService.buscarEstoqueBaixo(quantidade)
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/buscar/categoria-preco")
    public List<ProdutoResponseDTO> buscarPorCategoriaEPrecoMax(@RequestParam String categoria,
                                                                @RequestParam BigDecimal precoMax) {
        return produtoService.buscarPorCategoriaEPrecoMax(categoria, precoMax)
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/ordenados/preco")
    public List<ProdutoResponseDTO> listarOrdenadosPorPreco() {
        return produtoService.listarOrdenadosPorPreco()
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }

    @GetMapping("/ordenados/nome")
    public List<ProdutoResponseDTO> listarOrdenadosPorNome() {
        return produtoService.listarOrdenadosPorNome()
                .stream().map(Produto::toResponse).collect(Collectors.toList());
    }
}
