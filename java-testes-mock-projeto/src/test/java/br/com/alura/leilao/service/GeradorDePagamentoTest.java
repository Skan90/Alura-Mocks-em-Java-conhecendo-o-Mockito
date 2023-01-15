package br.com.alura.leilao.service;

import br.com.alura.leilao.dao.PagamentoDao;
import br.com.alura.leilao.model.Lance;
import br.com.alura.leilao.model.Leilao;
import br.com.alura.leilao.model.Pagamento;
import br.com.alura.leilao.model.Usuario;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDaoMock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @Mock
    private Clock clock;

    @InjectMocks
    private GeradorDePagamento geradorDePagamento;

    @Test
    void deveriaGerarPagamentoParaVencedorDoLeilaoDiaDeSemana() {
        Leilao leilao = leilaoFake();
        Lance lanceVencedor = leilao.getLanceVencedor();
        LocalDate data = LocalDate.of(2023, 1, 11);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        verify(pagamentoDaoMock).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.of(2023, 1, 11).plusDays(1), pagamento.getVencimento());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());

    }

    @Test
    void deveriaGerarPagamentoParaVencedorDoLeilaoQuandoProximoDiaUtilForSabado() {
        Leilao leilao = leilaoFake();
        Lance lanceVencedor = leilao.getLanceVencedor();
        LocalDate data = LocalDate.of(2023, 1, 13);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        verify(pagamentoDaoMock).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.of(2023, 1, 13).plusDays(3), pagamento.getVencimento());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());

    }
    @Test
    void deveriaGerarPagamentoParaVencedorDoLeilaoQuandoProximoDiaUtilForDomingo() {
        Leilao leilao = leilaoFake();
        Lance lanceVencedor = leilao.getLanceVencedor();
        LocalDate data = LocalDate.of(2023, 1, 14);

        Instant instant = data.atStartOfDay(ZoneId.systemDefault()).toInstant();

        when(clock.instant()).thenReturn(instant);
        when(clock.getZone()).thenReturn(ZoneId.systemDefault());

        geradorDePagamento.gerarPagamento(lanceVencedor);

        verify(pagamentoDaoMock).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.of(2023, 1, 14).plusDays(2), pagamento.getVencimento());
        assertEquals(lanceVencedor.getValor(), pagamento.getValor());
        assertFalse(pagamento.getPago());
        assertEquals(lanceVencedor.getUsuario(), pagamento.getUsuario());
        assertEquals(leilao, pagamento.getLeilao());

    }


    private Leilao leilaoFake() {
        Leilao leilao = new Leilao();
        leilao.setNome("Celular");
        leilao.setUsuario(new Usuario("Fulano"));
        leilao.setValorInicial(new BigDecimal("500"));

        Lance lanceUnico = new Lance();
        lanceUnico.setUsuario(new Usuario("Beltrano"));
        lanceUnico.setValor(new BigDecimal("600"));

        leilao.setLanceVencedor(lanceUnico);
        leilao.propoe(lanceUnico);

        return leilao;
    }
}