package cz.muni.fi.pa165.currency;

import org.junit.Rule;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.junit.MockitoJUnit;
import org.mockito.junit.MockitoRule;

import java.math.BigDecimal;
import java.util.Currency;

//import static org.junit.Assert.*;
import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

public class CurrencyConvertorImplTest {

    @Test
    public void testConvert() throws ExternalServiceFailureException {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        when(mockTable.getExchangeRate(Currency.getInstance("EUR"), Currency.getInstance("CZK"))).thenReturn(new BigDecimal("25.0525"));
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //act
        BigDecimal res = cc.convert(Currency.getInstance("EUR"), Currency.getInstance("CZK"), new BigDecimal("10.00"));
        //assert
        assertThat(res).isEqualTo("250.52");
        // Don't forget to test border values and proper rounding.
        //fail("Test is not implemented yet.");
    }

    @Test
    public void testConvertWithNullSourceCurrency() {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        //when(mockTable.getExchangeRate(Currency.getInstance("EUR"), Currency.getInstance("CZK"))).thenReturn(new BigDecimal("25.0525"));
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //assert
        assertThatThrownBy(() -> {cc.convert(null, Currency.getInstance("CZK"), new BigDecimal("10.00"));}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithNullTargetCurrency() {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        //when(mockTable.getExchangeRate(Currency.getInstance("EUR"), Currency.getInstance("CZK"))).thenReturn(new BigDecimal("25.0525"));
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //assert
        assertThatThrownBy(() -> {cc.convert(Currency.getInstance("EUR"), null, new BigDecimal("10.00"));}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithNullSourceAmount() {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        //when(mockTable.getExchangeRate(Currency.getInstance("EUR"), Currency.getInstance("CZK"))).thenReturn(new BigDecimal("25.0525"));
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //assert
        assertThatThrownBy(() -> {cc.convert(Currency.getInstance("EUR"), Currency.getInstance("CZK"), null);}).isInstanceOf(IllegalArgumentException.class);
    }

    @Test
    public void testConvertWithUnknownCurrency() throws ExternalServiceFailureException {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        when(mockTable.getExchangeRate(Currency.getInstance("JPY"), Currency.getInstance("CZK"))).thenReturn(null);
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //assert
        assertThatThrownBy(() -> {cc.convert(Currency.getInstance("JPY"), Currency.getInstance("CZK"), new BigDecimal("10.00"));}).isInstanceOf(UnknownExchangeRateException.class);
    }

    @Test
    public void testConvertWithExternalServiceFailure() throws ExternalServiceFailureException {
        //arrange
        ExchangeRateTable mockTable = mock(ExchangeRateTable.class);
        when(mockTable.getExchangeRate(Currency.getInstance("EUR"), Currency.getInstance("CZK"))).thenThrow(ExternalServiceFailureException.class);
        CurrencyConvertor cc = new CurrencyConvertorImpl(mockTable);
        //assert
        assertThatThrownBy(() -> {cc.convert(Currency.getInstance("EUR"), Currency.getInstance("CZK"), new BigDecimal("10.00"));}).isInstanceOf(UnknownExchangeRateException.class);
    }

}
