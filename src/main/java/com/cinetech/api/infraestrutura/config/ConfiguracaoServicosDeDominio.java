package com.cinetech.api.infraestrutura.config;

import com.cinetech.api.dominio.repositorios.ReservaEventoRepositorio;
import com.cinetech.api.dominio.repositorios.SessaoRepositorio;
import com.cinetech.api.dominio.servicos.AgendamentoServico.AgendamentoServico;
import com.cinetech.api.dominio.servicos.AgendamentoServico.AgendamentoServicoImpl;
import com.cinetech.api.dominio.servicos.FiltroConteudoServico.FiltroConteudoServico;
import com.cinetech.api.dominio.servicos.FiltroConteudoServico.FiltroConteudoServicoImpl;
import com.cinetech.api.dominio.servicos.GestaoPontosFidelidadeServico.GestaoPontosFidelidadeServico;
import com.cinetech.api.dominio.servicos.GestaoPontosFidelidadeServico.GestaoPontosFidelidadeServicoImpl;
import com.cinetech.api.dominio.servicos.PrecificacaoServico.PrecificacaoServico;
import com.cinetech.api.dominio.servicos.PrecificacaoServico.PrecificacaoServicoImpl;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ConfiguracaoServicosDeDominio {

    @Bean
    public AgendamentoServico agendamentoService(
            SessaoRepositorio sessaoRepositorio, // Spring injetará SessaoRepositorioJpa aqui
            ReservaEventoRepositorio reservaEventoRepositorio) { // Spring injetará ReservaEventoRepositorioJpa aqui
        return new AgendamentoServicoImpl(sessaoRepositorio, reservaEventoRepositorio);
    }

    @Bean
    public PrecificacaoServico precificacaoService() {
        return new PrecificacaoServicoImpl();
    }

    @Bean
    public FiltroConteudoServico filtroConteudoService() {
        return new FiltroConteudoServicoImpl();
    }

    @Bean
    public GestaoPontosFidelidadeServico gestaoPontosFidelidadeService() {
        return new GestaoPontosFidelidadeServicoImpl();
    }
}
