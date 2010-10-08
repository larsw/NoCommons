package boss.nocommons.person;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

/**
 * This class calulates valid Fodselsnummer instances for a given date.
 * 
 * @author Per K. Mengshoel
 */
public class FodselsnummerCalculator {

    private FodselsnummerCalculator() {
        super();
    }

    /**
     * Returns a List with valid Fodselsnummer instances for a given Date and
     * gender.
     * 
     * @param date
     *            The Date instance
     * @param female
     *            true for Fodelsenummer for women, false for men
     * @return A List with Fodelsnummer instances
     */
    public static List getFodselsnummerForDateAndGender(Date date,
            boolean female) {
        List result = getFodselsnummerForDate(date);
        splitByGender(female, result);
        return result;
    }

    private static void splitByGender(boolean female, List result) {
        Iterator iter = result.iterator();
        while (iter.hasNext()) {
            Fodselsnummer f = (Fodselsnummer) iter.next();
            if (f.isFemale() != female) {
                iter.remove();
            }
        }
    }

    /**
     * Returns a List with valid Fodselsnummer instances for a given Date.
     * 
     * @param date
     *            The Date instance
     * @return A List with Fodelsnummer instances
     */
    public static List getFodselsnummerForDate(Date date) {
        if (date == null) {
            throw new IllegalArgumentException();
        }
        DateFormat df = new SimpleDateFormat("ddMMyy");
        String century = getCentury(date);
        String dateString = df.format(date);
        List result = new ArrayList();
        for (int i = 999; i >= 0; i--) {
            StringBuffer sb = new StringBuffer(dateString);
            if (i < 10) {
                sb.append("00");
            } else if (i < 100) {
                sb.append("0");
            }
            sb.append(i);
            Fodselsnummer f = new Fodselsnummer(sb.toString());
            try {
                sb
                        .append(FodselsnummerValidator
                                .calculateFirstChecksumDigit(f));
                f = new Fodselsnummer(sb.toString());
                sb.append(FodselsnummerValidator
                        .calculateSecondChecksumDigit(f));
                f = new Fodselsnummer(sb.toString());
                String centuryByIndividnummer = f.getCentury();
                if (centuryByIndividnummer != null
                        && centuryByIndividnummer.equals(century)) {
                    result.add(f);
                }
            } catch (IllegalArgumentException e) {
                // ignore
            }
        }
        return result;
    }

    private static String getCentury(Date date) {
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        int year = c.get(Calendar.YEAR);
        return Integer.toString(year).substring(0, 2);
    }

}
