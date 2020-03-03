package cz.muni.fi.pa165.currency;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.math.BigDecimal;
import java.math.MathContext;
import java.math.RoundingMode;
import java.util.Currency;


/**
 * This is base implementation of {@link CurrencyConvertor}.
 *
 * @author petr.adamek@embedit.cz
 */
public class CurrencyConvertorImpl implements CurrencyConvertor {

    private final ExchangeRateTable exchangeRateTable;
    private final Logger logger = LoggerFactory.getLogger(CurrencyConvertorImpl.class);

    public CurrencyConvertorImpl(ExchangeRateTable exchangeRateTable) {
        this.exchangeRateTable = exchangeRateTable;
    }

    @Override
    public BigDecimal convert(Currency sourceCurrency, Currency targetCurrency, BigDecimal sourceAmount) {
        logger.trace("Calling convert({}, {}, {})", sourceCurrency, targetCurrency, sourceAmount);
        if (sourceCurrency == null || targetCurrency == null || sourceAmount == null) {
            throw new IllegalArgumentException("All arguments are required");
        }
        BigDecimal rate;

        try {
            rate = exchangeRateTable.getExchangeRate(sourceCurrency, targetCurrency);
        } catch (ExternalServiceFailureException e) {
            logger.error("Convert failed due to external service failure.", e);
            throw new UnknownExchangeRateException("Unknown exchange rate.", e);
        }

        if (rate == null) {
            logger.warn("Convert failed due to missing exchange rate.");
            throw new UnknownExchangeRateException("Unknown exchange rate.");
        }
        BigDecimal result = sourceAmount.multiply(rate);
        //result.setScale(2, RoundingMode.HALF_EVEN);
        return result.setScale(2, RoundingMode.HALF_EVEN);
    }

}
