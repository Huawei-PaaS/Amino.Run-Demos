package sapphire.appexamples.minnietwitter.glue;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.example.minnietwitter.R;

public class CustomList extends ArrayAdapter<String>{
    private final Activity context;
    private final String[] tweets;
    public CustomList(Activity context,
                      String[] tweets) {
        super(context, R.layout.custom_layout, tweets);
        this.context = context;
        this.tweets = tweets;
    }

    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.custom_layout, null, true);
        TextView txtTitle = (TextView) rowView.findViewById(R.id.txt);
        txtTitle.setText(tweets[position]);
        return rowView;
    }
}
