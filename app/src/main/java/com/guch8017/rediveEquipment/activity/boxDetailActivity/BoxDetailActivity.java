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
        ArrayList<String> list = new ArrayList<>();
        list.add(getString(R.string.modify_name));
        list.add(getString(R.string.modify_image));
        list.add(getString(R.string.box_character));
        list.add(getString(R.string.equip_requirement));
        list.add(getString(R.string.piece_requirement));
        list.add(getString(R.string.piece_own));
        list.add(getString(R.string.map_plan));
        list.add(getString(R.string.delete_box));
        BDLAdapter adapter = new BDLAdapter(this, R.layout.box_setup_item, list);
        listView.setAdapter(adapter);
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

    class BDLAdapter extends ArrayAdapter<String> {
        private int mResourceId;
        BDLAdapter(Context context, int resourceID, List<String> list){
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
                convertView.setTag(vh);
            }else{
                vh = (BoxSetupViewHolder)convertView.getTag();
            }
            vh.itemText.setText(getItem(position));
            return convertView;
        }

        class BoxSetupViewHolder{
            TextView itemText;
        }
    }
}
