/*
 * CREDIT SUISSE IS WILLING TO LICENSE THIS SPECIFICATION TO YOU ONLY UPON THE
 * CONDITION THAT YOU ACCEPT ALL OF THE TERMS CONTAINED IN THIS AGREEMENT.
 * PLEASE READ THE TERMS AND CONDITIONS OF THIS AGREEMENT CAREFULLY. BY
 * DOWNLOADING THIS SPECIFICATION, YOU ACCEPT THE TERMS AND CONDITIONS OF THE
 * AGREEMENT. IF YOU ARE NOT WILLING TO BE BOUND BY IT, SELECT THE "DECLINE"
 * BUTTON AT THE BOTTOM OF THIS PAGE. Specification: JSR-354 Money and Currency
 * API ("Specification") Copyright (c) 2012-2013, Credit Suisse All rights
 * reserved.
 */
package javax.money.spi;

import javax.money.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.Set;

/**
 * This class models the accessor for rounding instances, modeled as
 * {@link javax.money.MonetaryOperator}.
 * <p>
 * This class is thread-safe.
 *
 * @author Anatole Tresch
 * @author Werner Keil
 */
public interface MonetaryRoundingsSingletonSpi{

    /**
     * Allows to access the names of the current defined roundings.
     *
     * @param providers the providers and ordering to be used. By default providers and ordering as defined in
     *                  #getDefaultProviders is used.
     * @return the set of custom rounding ids, never {@code null}.
     */
    Set<String> getRoundingNames(String... providers);

    /**
     * Allows to access the names of the current registered rounding providers.
     *
     * @return the set of provider names, never {@code null}.
     */
    Set<String> getProviderNames();

    /**
     * Allows to access the list and ordering of the default providers.
     *
     * @return the ordered provider names, never {@code null}.
     */
    List<String> getDefaultProviders();

    /**
     * Execute a query for {@link javax.money.MonetaryRounding}. This allows to model more complex used cases,
     * such as historic or special roundings.
     *
     * @param query the query to be exected, not null.
     * @return the roundings found, never null.
     */
    Collection<MonetaryRounding> getRoundings(RoundingQuery query);


    /**
     * Creates a rounding that can be added as {@link javax.money.MonetaryOperator} to
     * chained calculations. The instance must lookup the concrete
     * {@link javax.money.MonetaryRounding} instance from the {@link javax.money.spi.MonetaryRoundingsSingletonSpi}
     * based on the input {@link javax.money.MonetaryAmount}'s {@link javax.money.CurrencyUnit}.
     *
     * @return the (shared) default rounding instance.
     */
    MonetaryRounding getDefaultRounding();


    /**
     * Creates an {@link javax.money.MonetaryOperator} for rounding {@link javax.money.MonetaryAmount}
     * instances given a currency.
     *
     * @param currencyUnit The currency, which determines the required precision. As
     *                     {@link java.math.RoundingMode}, by default, {@link java.math.RoundingMode#HALF_UP}
     *                     is sued.
     * @param providers    the optional provider list and ordering to be used
     * @return a new instance {@link javax.money.MonetaryOperator} implementing the
     * rounding, never {@code null}.
     */
    default MonetaryRounding getRounding(CurrencyUnit currencyUnit, String... providers){
        MonetaryRounding op = getRounding(
                RoundingQueryBuilder.create().setProviders(providers).setCurrencyUnit(currencyUnit).build());
        return Optional.ofNullable(op).orElseThrow(() -> new IllegalStateException(
                "No rounding provided for CurrencyUnit: " + currencyUnit.getCurrencyCode()
        ));
    }


    /**
     * Access an {@link javax.money.MonetaryRounding} using the rounding name.
     *
     * @param roundingName The rounding name, not null.
     * @param providers    the optional provider list and ordering to be used
     * @return the corresponding {@link javax.money.MonetaryOperator} implementing the
     * rounding, never {@code null}.
     * @throws IllegalArgumentException if no such rounding is registered using a
     *                                  {@link javax.money.spi.RoundingProviderSpi} instance.
     */
    default MonetaryRounding getRounding(String roundingName, String... providers){
        MonetaryRounding op = getRounding(
                RoundingQueryBuilder.create().setProviders(providers).setRoundingName(roundingName).build()
        );
        return Optional.ofNullable(op).orElseThrow(
                () -> new MonetaryException("No rounding provided with rounding name: " + roundingName));
    }


    /**
     * Query a specific rounding with the given query. If multiple roundings match the query the first one is
     * selected, since the query allows to determine the providers and their ordering by setting {@link
     * RoundingQuery#getProviders()}.
     *
     * @param query the rounding query, not null.
     * @return the rounding found, or null, if no rounding matches the query.
     */
    default MonetaryRounding getRounding(RoundingQuery query){
        Collection<MonetaryRounding> roundings = getRoundings(query);
        if(roundings.isEmpty()){
            return null;
        }
        return roundings.iterator().next();
    }

    /**
     * Checks if any {@link javax.money.MonetaryRounding} is matching the given query.
     *
     * @param query the rounding query, not null.
     * @return true, if at least one rounding matches the query.
     */
    default boolean isAvailable(RoundingQuery query){
        return !getRoundings(query).isEmpty();
    }


}
