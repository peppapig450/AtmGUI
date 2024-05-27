package com.nick.atmInterface;

import java.util.Random;

public class BankGenerator {

    public String generateAccountNumber() {
        String base = "GH";
        int digits = 14;

        StringBuilder numbs = new StringBuilder(digits);

        for (int i = 0; i < digits; i++) {
            numbs.append((char) (Math.random() * 10 + '0'));
        }

        return base + numbs.toString();
    }

    /*
     * add space between account number so its human readable
     */
    public String convertToHumanFormat(String accountNumber) {
        return accountNumber.replaceAll("....(?!$)", "$0 ");
    }

    /*
     * remove space between account number so its machine readable
     */
    public String convertToMachineFormat(String accountNumber) {
        return accountNumber.replaceAll("\\s+", "");
    }

    /*
     * @param bin
     * The bank identification number, a set digits at the start of the credit card
     * number, used to identify the bank that is issuing the credit card.
     *
     * @param length
     * The total length (i.e. including the BIN) of the credit card number.
     *
     * @return
     * A randomly generated, valid, credit card number.
     */

}
