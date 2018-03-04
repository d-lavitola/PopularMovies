package Utilities;

import android.content.Context;
import android.support.v7.widget.GridLayoutManager;
import android.util.AttributeSet;

/**
 * Created by domeniclavitola on 3/2/18.
 */

public class DiscoverGridLayoutManager extends GridLayoutManager {
    public DiscoverGridLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    public DiscoverGridLayoutManager(Context context, int spanCount) {
        super(context, spanCount);
    }

    public DiscoverGridLayoutManager(Context context, int spanCount, int orientation, boolean reverseLayout) {
        super(context, spanCount, orientation, reverseLayout);
    }

    @Override
    public boolean isAutoMeasureEnabled() {
        return true;
    }
}
