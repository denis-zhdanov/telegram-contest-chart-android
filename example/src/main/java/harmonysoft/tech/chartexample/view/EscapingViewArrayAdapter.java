package harmonysoft.tech.chartexample.view;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import tech.harmonysoft.android.leonardo.util.LeonardoUtil;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Set;

/**
 * @author Denis Zhdanov
 * @since 20/3/19
 */
public class EscapingViewArrayAdapter<T> extends ArrayAdapter<T> {

    private Set<View> mDropDownViews = Collections.newSetFromMap(new IdentityHashMap<>());

    public EscapingViewArrayAdapter(Context context, int resource, List<T> objects) {
        super(context, resource, objects);
    }

    @Override
    public View getDropDownView(int position, @Nullable View convertView, ViewGroup parent) {
        View result = super.getDropDownView(position, convertView, parent);
        int color = LeonardoUtil.getColor(getContext().getApplicationContext(), android.R.attr.windowBackground);
        result.setBackgroundColor(color);
        mDropDownViews.add(result);
        return result;
    }

    @Nonnull
    public Set<View> getDropDownViews() {
        return mDropDownViews;
    }
}
