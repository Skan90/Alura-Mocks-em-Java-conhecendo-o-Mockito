package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.LeilaoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Testes para o service de finalização de leilões")
class FinalizarLeilaoServiceTest {
    @Mock
    private LeilaoDao leilaoDaoMock;

    @Mock
    private EnviadorDeEmails enviadorDeEmailsMock;

    @InjectMocks
    private FinalizarLeilaoService finalizarLeilaoService;


    @Test
    void deveriaFinalizarLeiloesExpirados() {
        List<Leilao> leiloes = leiloesFake();
        when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leiloes);
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();

        assertTrue(leilao.isFechado());
        assertEquals(new BigDecimal("900"), lanceVencedor.getValor());

        verify(leilaoDaoMock).salvar(leilao);
    }

    @Test
    void deveriaEnviarEmailParaVencedorDoLeilao() {
        List<Leilao> leiloes = leiloesFake();
        when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leiloes);
        finalizarLeilaoService.finalizarLeiloesExpirados();

        Leilao leilao = leiloes.get(0);
        Lance lanceVencedor = leilao.getLanceVencedor();

        verify(enviadorDeEmailsMock).enviarEmailVencedorLeilao(lanceVencedor);
    }

    @Test
    void naoDeveriaEnviarEmailParaVencedorDoLeilaoEmCasoDeErroAoEncerrarLeilao() {
        List<Leilao> leiloes = leiloesFake();
        when(leilaoDaoMock.buscarLeiloesExpirados()).thenReturn(leiloes);
        when(leilaoDaoMock.salvar(any())).thenThrow(RuntimeException.class);

        assertThrows(RuntimeException.class, () -> finalizarLeilaoService.finalizarLeiloesExpirados());

        verifyNoInteractions(enviadorDeEmailsMock);
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