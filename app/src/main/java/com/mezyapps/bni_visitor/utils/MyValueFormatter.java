package com.mezyapps.bni_visitor.utils;

import com.github.mikephil.charting.utils.ValueFormatter;

public class MyValueFormatter  implements ValueFormatter {
    @Override
    public String getFormattedValue(float value) {
        return "" + ((int) value);
    }
}
