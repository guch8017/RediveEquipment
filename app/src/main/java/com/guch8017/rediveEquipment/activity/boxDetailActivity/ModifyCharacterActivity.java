package com.guch8017.rediveEquipment.activity.boxDetailActivity;

import android.content.Intent;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.BoxDatabase;
import com.guch8017.rediveEquipment.database.DatabaseReflector;
import com.guch8017.rediveEquipment.database.module.DBCharacter;
import com.guch8017.rediveEquipment.database.module.DBUnitPromotion;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class ModifyCharacterActivity extends AppCompatActivity {
    private static final String TAG = "ModifyCharacterActivity";
    int mUnitId;
    int mBoxId;
    List<DBUnitPromotion> mUnitPromotion;
    int maxRank;
    boolean isNew;
    DBCharacter character;
    BoxDatabase mDatabase;

    @Override
    public void onCreate(Bundle savedInstanceBundle){
        super.onCreate(savedInstanceBundle);
        Intent intent = getIntent();
        mUnitId = intent.getIntExtra("unitId", -1);
        mBoxId = intent.getIntExtra("boxId", -1);
        isNew = intent.getBooleanExtra("isNew", true);
        DatabaseReflector databaseReflector = new DatabaseReflector(this);
        mDatabase = new BoxDatabase(this);
        mUnitPromotion = (List<DBUnitPromotion>) databaseReflector.reflectClass(DBUnitPromotion.class.getName(), DBUnitPromotion.tableName,
                "unit_id = " + mUnitId);
        mUnitPromotion.sort(new Comparator<DBUnitPromotion>() {
            @Override
            public int compare(DBUnitPromotion o1, DBUnitPromotion o2) {
                if(o1.promotion_level == o2.promotion_level) return 0;
                return (o1.promotion_level > o2.promotion_level) ? 1 : -1;
            }
        });
        maxRank = mUnitPromotion.size();
        if(isNew){
            character = new DBCharacter();
            character.targetEquip = 0;
            character.currentEquip = 0;
            character.targetRank = maxRank;
            character.currentRank = 1;
            character.characterId = mUnitId;
            character.boxId = mBoxId;
            setTitle(getString(R.string.new_char));
        }else{
            character = mDatabase.getCharacter(mBoxId, mUnitId);
            if(character == null) return;
            if(character.targetRank > maxRank) character.targetRank = maxRank;
            if(character.currentRank > maxRank) character.currentRank = maxRank;
            setTitle(getString(R.string.modify_char));
        }
        setContentView(R.layout.activity_box_char_modifier);
        setCurrentRankImg();
        setTargetRankImg();
        setOnClickListener();
        ((TextView)findViewById(R.id.charName)).setText(String.valueOf(mUnitId));
        ArrayList<String> rankList = new ArrayList<>();
        for(int i=1;i<=maxRank;++i){
            rankList.add("  " + i + "  ");
        }
        Spinner currentRankSpinner = findViewById(R.id.currentRankSpinner);
        Spinner targetRankSpinner = findViewById(R.id.targetRankSpinner);
        currentRankSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rankList));
        targetRankSpinner.setAdapter(new ArrayAdapter<String>(this, android.R.layout.simple_spinner_dropdown_item, rankList));
        currentRankSpinner.setSelection(character.currentRank - 1, true);
        targetRankSpinner.setSelection(character.targetRank - 1, true);
        currentRankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                character.currentRank = position + 1;
                setCurrentRankImg();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        targetRankSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                character.targetRank = position + 1;
                setTargetRankImg();
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((Button)findViewById(R.id.save_char_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(saveChanges()){
                    ModifyCharacterActivity.this.finish();
                }
            }
        });
        ((Button)findViewById(R.id.cancel_char_btn)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ModifyCharacterActivity.this.finish();
            }
        });
    }

    private void setCurrentRankImg(){

        try {
            Class rankEquipClass = Class.forName(DBUnitPromotion.class.getName());

            for(int i=0;i<6;++i){
                final int viewId = getResources().getIdentifier("cequipment_list_image_"+(i+1),"id",getPackageName());
                Field field = rankEquipClass.getField("equip_slot_"+(i+1));
                ImageView img = findViewById(viewId);
                int equipId = field.getInt(mUnitPromotion.get(character.currentRank - 1));
                ImageLoader.getInstance().displayImage(Constant.equipImageUrl(equipId),
                        new ImageViewAware(img, false), Constant.displayImageOption);
                int equipStatus = 1 << i;
                if(equipId == 999999){
                    character.currentEquip &= (i ^ 0b111111);
                }
                img.clearColorFilter();
                if((character.currentEquip & equipStatus) == 0){
                    setColorFilter(img);
                }
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void setTargetRankImg(){

        try {
            Class rankEquipClass = Class.forName(DBUnitPromotion.class.getName());

            for(int i=0;i<6;++i){
                final int viewId = getResources().getIdentifier("tequipment_list_image_"+(i+1),"id",getPackageName());
                Field field = rankEquipClass.getField("equip_slot_"+(i+1));
                ImageView img = findViewById(viewId);
                int equipId = field.getInt(mUnitPromotion.get(character.targetRank - 1));
                ImageLoader.getInstance().displayImage(Constant.equipImageUrl(equipId),
                        new ImageViewAware(img, false), Constant.displayImageOption);
                int equipStatus = 1 << i;
                if(equipId == 999999){
                    character.targetEquip &= (equipStatus ^ 0b111111);
                }
                img.clearColorFilter();
                if((character.targetEquip & equipStatus) == 0){
                    setColorFilter(img);
                }
            }
        }catch (Exception e){
            Toast.makeText(this, e.getMessage(),Toast.LENGTH_LONG).show();
        }
    }

    private void setOnClickListener(){
        for(int i=0;i<6;++i){
            final int cid = i;
            final int tViewId = getResources().getIdentifier("tequipment_list_image_"+(i+1),"id",getPackageName());
            final int cViewId = getResources().getIdentifier("cequipment_list_image_"+(i+1),"id",getPackageName());
            ((ImageView)findViewById(tViewId)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Class rankEquipClass = Class.forName(DBUnitPromotion.class.getName());
                        Field field = rankEquipClass.getField("equip_slot_"+(cid + 1));
                        if(field.getInt(mUnitPromotion.get(character.targetRank - 1)) == 999999){
                            Log.i(TAG, "装备不存在，装备状态将不被修改");
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    int equipStatus = 1 << cid;

                    if((character.targetEquip & equipStatus) != 0){
                        character.targetEquip &= (equipStatus ^ 0b111111);
                        setColorFilter((ImageView)v);
                    }else{
                        character.targetEquip |= equipStatus;
                        ((ImageView)v).clearColorFilter();
                    }
                }
            });
            ((ImageView)findViewById(cViewId)).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    try{
                        Class rankEquipClass = Class.forName(DBUnitPromotion.class.getName());
                        Field field = rankEquipClass.getField("equip_slot_"+(cid + 1));
                        if(field.getInt(mUnitPromotion.get(character.currentRank - 1)) == 999999){
                            Log.i(TAG, "装备不存在，装备状态将不被修改");
                            return;
                        }
                    }catch (Exception e){
                        e.printStackTrace();
                    }
                    int equipStatus = 1 << cid;

                    if((character.currentEquip & equipStatus) != 0){
                        character.currentEquip &= (equipStatus ^ 0b111111);
                        setColorFilter((ImageView)v);
                    }else{
                        character.currentEquip |= equipStatus;
                        ((ImageView)v).clearColorFilter();
                    }
                }
            });
        }
    }

    private void setColorFilter(ImageView v){
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);
        v.setColorFilter(new ColorMatrixColorFilter(matrix));
    }

    private boolean saveChanges(){
        if(character.targetRank < character.currentRank){
            Toast.makeText(this, R.string.ERR_TARGET_SMALLER, Toast.LENGTH_LONG).show();
            return false;
        }else if(character.targetRank == character.currentRank){
            for(int i = 1; i <= 0b100000; i <<= 1){
                if((character.currentEquip & i) != 0 && (character.targetEquip & i) == 0){
                    Toast.makeText(this, R.string.ERR_TARGET_EQUIP, Toast.LENGTH_LONG).show();
                    return false;
                }
            }
        }
        if(isNew){
            mDatabase.addCharacter(mBoxId, character);
        }else{
            mDatabase.modifyCharacter(character);
        }
        return true;
    }
}
