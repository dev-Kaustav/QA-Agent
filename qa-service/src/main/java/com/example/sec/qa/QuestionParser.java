package com.example.sec.qa;

import java.util.LinkedHashSet;
import java.util.Locale;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.springframework.stereotype.Component;

/**
 * Extracts metadata such as ticker symbols, years and form types from a question.
 */
@Component
public class QuestionParser {

    private static final Pattern YEAR = Pattern.compile("\\b(19|20)\\d{2}\\b");
    private static final Pattern FORM = Pattern.compile("\\b(10-K|10-Q|8-K|S-1)\\b", Pattern.CASE_INSENSITIVE);
    private static final Pattern TICKER = Pattern.compile("\\b[A-Z]{1,5}\\b");

    /**
     * Parses the question and returns detected metadata along with the remaining
     * text once metadata tokens are removed.
     */
    public ParsedQuestion parse(String question) {
        if (question == null) {
            return new ParsedQuestion(Set.of(), Set.of(), Set.of(), "");
        }
        String remainder = question;
        Set<Integer> years = new LinkedHashSet<>();
        Matcher mYear = YEAR.matcher(remainder);
        while (mYear.find()) {
            years.add(Integer.parseInt(mYear.group()));
        }
        remainder = mYear.replaceAll(" ");

        Set<String> formTypes = new LinkedHashSet<>();
        Matcher mForm = FORM.matcher(remainder);
        while (mForm.find()) {
            formTypes.add(mForm.group().toUpperCase(Locale.ROOT));
        }
        remainder = mForm.replaceAll(" ");

        Set<String> tickers = new LinkedHashSet<>();
        Matcher mTicker = TICKER.matcher(remainder);
        while (mTicker.find()) {
            String t = mTicker.group();
            tickers.add(t.toUpperCase(Locale.ROOT));
        }
        remainder = TICKER.matcher(remainder).replaceAll(" ");
        remainder = remainder.replaceAll("\\s+", " ").trim();

        return new ParsedQuestion(tickers, years, formTypes, remainder);
    }
}
