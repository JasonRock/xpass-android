package com.jason.xpass.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import com.jason.xpass.model.ItemInfo;

import java.util.List;

/**
 * Description:
 * <p/>
 * Created by js.lee on 5/4/16.
 */
public class ItemInfoAdapter extends ArrayAdapter<ItemInfo> {

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private List<ItemInfo> objects;

    public ItemInfoAdapter(Context context, int resource, List<ItemInfo> objects) {
        super(context, resource, objects);
        this.context = context;
        this.objects = objects;
    }

    public int getCount(){
        return objects.size();
    }

    public ItemInfo getItem(int position){
        return objects.get(position);
    }

    public long getItemId(int position){
        return position;
    }

    // And the "magic" goes here
    // This is for the "passive" state of the spinner
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // I created a dynamic TextView here, but you can reference your own  custom layout for each spinner item
        TextView label = new TextView(context);
        label.setTextColor(Color.BLACK);
        // Then you can get the current item using the values array (Users array) and the current position
        // You can NOW reference each method you has created in your bean object (User class)
        String itemDesc = objects.get(position).getItemDesc();
        label.setText(itemDesc == null ? "EMPTY" : itemDesc);

        // And finally return your dynamic (or custom) view for each spinner item
        return label;
    }

}
