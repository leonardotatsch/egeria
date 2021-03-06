/* SPDX-License-Identifier: Apache-2.0 */
/* Copyright Contributors to the ODPi Egeria project. */
package org.odpi.openmetadata.frameworks.connectors.properties.beans;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.io.Serializable;

import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.NONE;
import static com.fasterxml.jackson.annotation.JsonAutoDetect.Visibility.PUBLIC_ONLY;

/**
 * A StarRating defines the rating that a user has placed against an asset. This ranges from not recommended
 * through to five stars (excellent).
 */
@JsonAutoDetect(getterVisibility=PUBLIC_ONLY, setterVisibility=PUBLIC_ONLY, fieldVisibility=NONE)
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown=true)
public enum StarRating implements Serializable
{
    NOT_RECOMMENDED (0, "X", "Not recommended"),
    ONE_STAR        (1, "*", "Poor"),
    TWO_STARS       (2, "**", "Usable"),
    THREE_STARS     (3, "***", "Good"),
    FOUR_STARS      (4, "****", "Very Good"),
    FIVE_STARS      (5, "*****", "Excellent");

    private static final long     serialVersionUID = 1L;

    private int            starRatingCode;
    private String         starRatingSymbol;
    private String         starRatingDescription;


    /**
     * Typical Constructor
     *
     * @param starRatingCode ordinal
     * @param starRatingSymbol short name
     * @param starRatingDescription longer explanation
     */
    StarRating(int     starRatingCode, String   starRatingSymbol, String   starRatingDescription)
    {
        /*
         * Save the values supplied
         */
        this.starRatingCode = starRatingCode;
        this.starRatingSymbol = starRatingSymbol;
        this.starRatingDescription = starRatingDescription;
    }


    /**
     * Return the code for this enum instance
     *
     * @return int star rating code
     */
    public int getOrdinal()
    {
        return starRatingCode;
    }


    /**
     * Return the default symbol for this enum instance.
     *
     * @return String default symbol
     */
    public String getName()
    {
        return starRatingSymbol;
    }


    /**
     * Return the default description for the star rating for this enum instance.
     *
     * @return String default description
     */
    public String getDescription()
    {
        return starRatingDescription;
    }


    /**
     * Standard toString method.
     *
     * @return print out of variables in a JSON-style
     */
    @Override
    public String toString()
    {
        return "StarRating{" +
                "starRatingCode=" + starRatingCode +
                ", starRatingSymbol='" + starRatingSymbol + '\'' +
                ", starRatingDescription='" + starRatingDescription + '\'' +
                '}';
    }
}