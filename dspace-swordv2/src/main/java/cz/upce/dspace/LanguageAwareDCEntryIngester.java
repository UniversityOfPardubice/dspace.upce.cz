/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package cz.upce.dspace;

import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.dspace.content.DCValue;
import org.dspace.sword2.DSpaceSwordException;
import org.dspace.sword2.SimpleDCEntryIngester;

/**
 *
 * @author lusl0338
 */
public class LanguageAwareDCEntryIngester extends SimpleDCEntryIngester {

    private static final Pattern LANGUAGE_FIELD_PATTERN = Pattern.compile("(.*)\\[(.*)\\]");

    @Override
    public DCValue makeDCValue(final String field, final String value) throws DSpaceSwordException {
        Matcher matcher = LANGUAGE_FIELD_PATTERN.matcher(field);
        String strippedField = field;
        String language = null;
        if (matcher.matches()) {
            strippedField = matcher.group(1);
            language = matcher.group(2);
        }
        DCValue dCValue = super.makeDCValue(strippedField, value);
        dCValue.language = language;
        return dCValue;
    }

}

