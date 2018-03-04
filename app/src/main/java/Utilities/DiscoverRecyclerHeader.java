package Utilities;

import android.content.Context;
import android.util.AttributeSet;

import com.bartoszlipinski.recyclerviewheader2.RecyclerViewHeader;

/**
 * Created by domeniclavitola on 3/2/18.
 */

public class DiscoverRecyclerHeader extends RecyclerViewHeader {
    public DiscoverRecyclerHeader(Context context) {
        super(context);
    }

    public DiscoverRecyclerHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DiscoverRecyclerHeader(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    public boolean isLayoutRequested() {
        return true;
    }
}
