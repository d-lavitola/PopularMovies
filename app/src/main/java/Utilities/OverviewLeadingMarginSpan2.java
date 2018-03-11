package Utilities;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.text.Layout;
import android.text.style.LeadingMarginSpan;

/**
 * Created by domeniclavitola on 2/28/18.
 *
 * Class for giving a textview a leading margin that will wrap around another view to avoid
 * overlapping.
 */

public class OverviewLeadingMarginSpan2 implements LeadingMarginSpan.LeadingMarginSpan2 {
    private int margin;
    private int lines;

    /** OverviewLeadingMargin constructor method. */
    public OverviewLeadingMarginSpan2(int lines, int margin) {
        this.margin = margin ;
        this.lines = lines ;
    }

    /* Returns the value on which the indent should be added */
    @Override
    public int getLeadingMargin (boolean first) {
        if (first) {
            /*
             * This indent will be applied to the number of rows
             * returned by getLeadingMarginLineCount ()
                    */
            return margin ;
        } else {
            // Indent for all other lines
            return 0 ;
        }
    }

    /** Blank drawLeadingMargin method */
    @Override
    public void drawLeadingMargin(Canvas canvas, Paint paint, int i, int i1, int i2, int i3, int i4, CharSequence charSequence, int i5, int i6, boolean b, Layout layout) {

    }

    /** Returns the number of lines to be indented by getLeadingMargin (true) */
    @Override
    public int getLeadingMarginLineCount ( ) {
        return lines ;
    }
}
