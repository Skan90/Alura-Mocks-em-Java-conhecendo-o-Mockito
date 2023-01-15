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
import java.time.LocalDate;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class GeradorDePagamentoTest {

    @Mock
    private PagamentoDao pagamentoDaoMock;

    @Captor
    private ArgumentCaptor<Pagamento> captor;

    @InjectMocks
    private GeradorDePagamento geradorDePagamento;

    @BeforeEach
    void setUp() {
    }

    @Test
    void deveriaGerarPagamentoParaVencedorDoLeilao() {
        Leilao leilao = leilaoFake();
        Lance lanceVencedor = leilao.getLanceVencedor();
        geradorDePagamento.gerarPagamento(lanceVencedor);

        verify(pagamentoDaoMock).salvar(captor.capture());

        Pagamento pagamento = captor.getValue();

        assertEquals(LocalDate.now().plusDays(1), pagamento.getVencimento());
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