package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.module.DBBox;

import java.util.ArrayList;
import java.util.List;

public class BoxDetailActivity extends AppCompatActivity {
    private static final String TAG = "BoxDetailActivity";
    private int boxId;
    private ListView listView;
    private BoxDatabase mDatabase;
    private DBBox box;
    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        boxId = getIntent().getIntExtra("boxId", -1);
        mDatabase = new BoxDatabase(this);
        box = mDatabase.getBox(boxId);
        if(box == null){
            Log.e(TAG, "无法获取Box数据");
            return;
            //TODO: 错误页面
        }
        setContentView(R.layout.activity_box_detail);
        listView = findViewById(R.id.boxDetailList);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        modifyBoxName();
                        break;
                    case 1:
                        //TODO: 修改图片
                        break;
                    case 2:
                        Intent intent = new Intent(BoxDetailActivity.this, BoxCharacterActivity.class);
                        intent.putExtra("boxId", box.id);
                        startActivity(intent);
                        break;
                    case 3:
                        Intent intent1 = new Intent(BoxDetailActivity.this, EquipRequirementActivity.class);
                        intent1.putExtra("boxId", box.id);
                        intent1.putExtra("isPiece", false);
                        startActivity(intent1);
                        break;
                    case 4:
                        Intent intent2 = new Intent(BoxDetailActivity.this, EquipRequirementActivity.class);
                        intent2.putExtra("boxId", box.id);
                        intent2.putExtra("isPiece", true);
                        startActivity(intent2);
                        break;
                    case 6:
                        Intent intent4 = new Intent(BoxDetailActivity.this, LinearSolverActivity.class);
                        intent4.putExtra("boxId", box.id);
                        startActivity(intent4);
                        break;
                    case 7:
                        mDatabase.deleteBox(box.id);
                        finish();
                    default:
                        break;
                }

                // TODO: Box设置/浏览按钮响应
            }
        });
        ArrayList<MenuItem> list = new ArrayList<>();
        list.add(new MenuItem(getString(R.string.modify_name), R.drawable.ic_name));
        list.add(new MenuItem(getString(R.string.modify_image), R.drawable.ic_box_image));
        list.add(new MenuItem(getString(R.string.box_character), R.drawable.ic_character));
        list.add(new MenuItem(getString(R.string.equip_requirement), R.drawable.ic_equipment));
        list.add(new MenuItem(getString(R.string.piece_requirement), R.drawable.ic_equipment));
        list.add(new MenuItem(getString(R.string.piece_own), R.drawable.ic_piece_owned));
        list.add(new MenuItem(getString(R.string.map_plan), R.drawable.ic_map_plan));
        list.add(new MenuItem(getString(R.string.delete_box), R.drawable.ic_delete));

        BDLAdapter adapter = new BDLAdapter(this, R.layout.box_setup_item, list);
        listView.setAdapter(adapter);
        listView.setDivider(null);
    }

    private class MenuItem{
        String name;
        int id;
        MenuItem(String n, int i){
            name = n;
            id = i;
        }
    }

    private void modifyBoxName(){
        final EditText editText = new EditText(this);
        editText.setText(box.title);
        editText.setMaxLines(1);
        editText.setFilters(new InputFilter[]{new InputFilter.LengthFilter(10)});
        new AlertDialog.Builder(this).setView(editText).setTitle(getString(R.string.modify_name))
                .setPositiveButton(getString(R.string.confirm), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        box.title = editText.getText().toString();
                        mDatabase.modifyBox(box);
                    }
                }).setNegativeButton(getString(R.string.cancel), null).show();
    }

    class BDLAdapter extends ArrayAdapter<MenuItem> {
        private int mResourceId;
        BDLAdapter(Context context, int resourceID, List<MenuItem> list){
            super(context, resourceID, list);
            mResourceId = resourceID;
        }

        @Override
        @NonNull
        public View getView(int position, View convertView, @NonNull ViewGroup parent){
            BoxSetupViewHolder vh = new BoxSetupViewHolder();
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(mResourceId, parent, false);
                vh.itemText = convertView.findViewById(R.id.box_setup_item);
                vh.itemImg = convertView.findViewById(R.id.box_setup_img);
                convertView.setTag(vh);
            }else{
                vh = (BoxSetupViewHolder)convertView.getTag();
            }
            MenuItem item = getItem(position);
            vh.itemText.setText(item.name);
            vh.itemImg.setImageDrawable(getContext().getDrawable(item.id));
            return convertView;
        }

        class BoxSetupViewHolder{
            TextView itemText;
            ImageView itemImg;
        }
    }
}
