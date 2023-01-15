package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FinalizarLeilaoServiceTest {
    @Mock
    private LeilaoDao leilaoDaoMock;

//    @InjectMocks
    private FinalizarLeilaoService finalizarLeilaoService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.initMocks(this);
        this.finalizarLeilaoService = new FinalizarLeilaoService(leilaoDaoMock);
    }

    @Test
    void deveriaFinalizarLeiloesExpirados() {
        List<Leilao> leiloes = leiloesFake();
        when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leiloes);
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("900"), leilao.getLanceVencedor().getValor());

        verify(leilaoDaoMock).salvar(leilao);
    }

    private List<Leilao> leiloesFake() {
        List<Leilao> listaDeLeiloes = new ArrayList<>();

        Leilao leilao = new Leilao();
        leilao.setNome("Celular");
        leilao.setUsuario(new Usuario("Fulano"));
        leilao.setValorInicial(new BigDecimal("500"));

        Lance primeiroLance = new Lance();
        primeiroLance.setUsuario(new Usuario("Beltrano"));
        primeiroLance.setValor(new BigDecimal("600"));

        Lance segundoLance = new Lance();
        segundoLance.setUsuario(new Usuario("Ciclano"));
        segundoLance.setValor(new BigDecimal("900"));

        leilao.propoe(primeiroLance);
        leilao.propoe(segundoLance);

        listaDeLeiloes.add(leilao);

        return listaDeLeiloes;
    }
}