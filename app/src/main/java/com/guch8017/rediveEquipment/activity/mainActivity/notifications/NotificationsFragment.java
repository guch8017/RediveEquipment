package com.guch8017.rediveEquipment.activity.mainActivity.notifications;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.activity.boxDetailActivity.BoxDetailActivity;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.module.DBBox;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class NotificationsFragment extends Fragment {
    private BoxDatabase mDatabase;
    private BoxAdapter mAdapter;

    public View onCreateView(@NonNull final LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState) {
        mDatabase = new BoxDatabase(getContext());
        View root = inflater.inflate(R.layout.fragment_notifications, container, false);
        ListView listView = root.findViewById(R.id.box_list);
        mAdapter = new BoxAdapter(mDatabase.getBoxList());
        listView.setAdapter(mAdapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Object obj = parent.getAdapter().getItem(position);
                if(obj == null){
                    mDatabase.addBox();
                    mAdapter.SetData(mDatabase.getBoxList());
                }else {
                    int boxId = ((DBBox)obj).id;
                    Intent intent = new Intent(NotificationsFragment.this.getContext(), BoxDetailActivity.class);
                    intent.putExtra("boxId", boxId);
                    startActivity(intent);
                }
            }
        });
        return root;
    }

    @Override
    public void onResume(){
        super.onResume();
        if(BoxDatabase.dataModified){
            BoxDatabase.dataModified = false;
            mAdapter.SetData(mDatabase.getBoxList());
        }
    }

    class BoxAdapter extends BaseAdapter{
        private List<DBBox> boxes;

        BoxAdapter(List<DBBox> boxes){
            this.boxes = boxes;
        }

        @Override
        public int getCount(){
            return boxes.size() + 1;
        }

        @Override
        public Object getItem(int position){
            if(position == boxes.size()){
                return null;
            }
            else{
                return boxes.get(position);
            }
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            DBBox box;
            BoxViewHolder viewHolder;
            if(position == boxes.size()){
                box = new DBBox();
                box.title = "Add a new box";  // TODO: Replace with @string
                box.image_id = 0;
            }else{
                box = boxes.get(position);
            }

            if(convertView == null){
                convertView = LayoutInflater.from(NotificationsFragment.this.getContext()).inflate(R.layout.equipment_detail_item_1, parent, false);
                viewHolder = new BoxViewHolder();
                viewHolder.image = convertView.findViewById(R.id.equipment_image);
                viewHolder.name = convertView.findViewById(R.id.equipment_name);
                convertView.setTag(viewHolder);
            }else {
                viewHolder = (BoxViewHolder) convertView.getTag();
            }
            viewHolder.name.setText(box.title);
            ImageLoader.getInstance().displayImage(Constant.unitImageUrl(box.image_id, 3),
                    new ImageViewAware(viewHolder.image, false),
                    Constant.displayImageOption);
            return convertView;
        }

        private void SetData(List<DBBox> boxes){
            this.boxes.clear();
            this.boxes.addAll(boxes);
            notifyDataSetChanged();
        }

        class BoxViewHolder{
            ImageView image;
            TextView name;
        }
    }
}