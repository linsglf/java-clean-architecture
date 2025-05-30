package com.cinetech.api.infraestrutura.web.controller;


import com.cinetech.api.aplicacao.CompraAplicacao;
import com.cinetech.api.dominio.modelos.ingresso.Ingresso;
import com.cinetech.api.infraestrutura.web.dto.compra.IngressoResponseDTO;
import com.cinetech.api.infraestrutura.web.dto.compra.IniciarCompraRequestDTO;
import com.cinetech.api.infraestrutura.web.dto.compra.PrecoCalculadoResponseDTO;
import com.cinetech.api.infraestrutura.web.dto.compra.ConfirmarPagamentoRequestDTO;
// Importar um hipotético IngressoApiMapper para converter Ingresso (domínio) para IngressoResponseDTO
// import com.cinetech.api.infraestrutura.web.mapper.IngressoApiMapper;


import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/compras") // Endpoint base para o fluxo de compra
public class CompraController {

    private final CompraAplicacao compraAplicacao;
    // private final IngressoApiMapper ingressoApiMapper; // Para converter Ingresso para DTO

    public CompraController(CompraAplicacao compraAplicacao /*, IngressoApiMapper ingressoApiMapper */) {
        this.compraAplicacao = compraAplicacao;
        // this.ingressoApiMapper = ingressoApiMapper;
    }

    @PostMapping("/iniciar-selecao")
    public ResponseEntity<PrecoCalculadoResponseDTO> iniciarSelecaoEPreco(
            @Valid @RequestBody IniciarCompraRequestDTO requestDTO) {
        try {
            PrecoCalculadoResponseDTO response = compraAplicacao.iniciarSelecaoIngressoECalcularPreco(requestDTO);
            return ResponseEntity.ok(response);
        } catch (IllegalArgumentException e) {
            // Em um sistema real, usar @ControllerAdvice para tratar exceções e retornar DTOs de erro
            return ResponseEntity.badRequest().body(null); // Simplesmente null para o erro de DTO
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).body(null); // Ex: Assento já ocupado, sessão não permite
        }
    }

    @PostMapping("/finalizar")
    public ResponseEntity<IngressoResponseDTO> finalizarCompra(
            @Valid @RequestBody ConfirmarPagamentoRequestDTO requestDTO) {
        try {
            Ingresso ingressoDominio = compraAplicacao.finalizarCompraIngresso(requestDTO);
            // Converter Ingresso (domínio) para IngressoResponseDTO
            // IngressoResponseDTO responseDTO = ingressoApiMapper.toResponseDTO(ingressoDominio);
            // Simulação do mapeamento:
            IngressoResponseDTO responseDTO = new IngressoResponseDTO(
                    ingressoDominio.getId().toString(),
                    ingressoDominio.getCliente().getNome(),
                    ingressoDominio.getCliente().getCpf(),
                    ingressoDominio.getSessao().getFilme().getTitulo(),
                    ingressoDominio.getSessao().getSala().getNome(),
                    ingressoDominio.getAssento().getIdentificadorPosicao(),
                    ingressoDominio.getSessao().getDataHoraInicio(),
                    ingressoDominio.getValorPago(),
                    ingressoDominio.isMeiaEntradaAplicada(),
                    ingressoDominio.getPromocaoAplicadaId() != null ? "Promoção Aplicada ID: " + ingressoDominio.getPromocaoAplicadaId() : null,
                    ingressoDominio.getCodigoValidacao(),
                    ingressoDominio.getDataCompra()
            );
            return ResponseEntity.status(HttpStatus.CREATED).body(responseDTO);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest().build(); //body(new ErroDTO(e.getMessage()));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build(); //body(new ErroDTO(e.getMessage()));
        }
    }
}
