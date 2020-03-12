package com.guch8017.rediveEquipment.activity.unitDetailActivity;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.guch8017.rediveEquipment.R;
import com.guch8017.rediveEquipment.database.module.DBUnitComments;
import com.guch8017.rediveEquipment.database.module.DBUnitData;
import com.guch8017.rediveEquipment.database.module.DBUnitProfile;
import com.guch8017.rediveEquipment.util.Constant;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.imageaware.ImageViewAware;

import java.util.List;

public class UnitProfileFragment extends Fragment {
    private DBUnitProfile mProfile;
    private DBUnitData mData;
    private List<DBUnitComments> mComments;
    private Context mContext;

    static UnitProfileFragment getInstance(DBUnitProfile profile, DBUnitData data, List<DBUnitComments> comments){
        UnitProfileFragment fragment = new UnitProfileFragment();
        fragment.mProfile = profile;
        fragment.mComments = comments;
        fragment.mData = data;
        return fragment;
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             ViewGroup container, Bundle savedInstanceState){
        View root = inflater.inflate(R.layout.fragment_unit_detail_list, container, false);
        final ListView listView = root.findViewById(R.id.unit_detail_list);
        UnitProfileAdapter adapter = new UnitProfileAdapter(mContext, mProfile, mData, mComments);
        listView.setAdapter(adapter);
        return root;
    }

    @Override
    public void onAttach(Context context){
        super.onAttach(context);
        mContext = context;
    }


    private class UnitProfileAdapter extends BaseAdapter{
        private LayoutInflater mInflater;
        private DBUnitProfile profile;
        private DBUnitData data;
        private List<DBUnitComments> comments;
        //private DB_unlock_unit_condition condition;
        private ImageLoader imageLoader;
        private static final int TYPE_IMG_TEXT = 0;
        private static final int TYPE_TEXT_TEXT = 1;
        private static final int TYPE_TEXT = 2;
        private static final int TYPE_SINGLE_TEXT = 3;
        private final String[][] title = {{},{"真实姓名"},{"简介"},{"身高","体重"},{"生日","血型"},{"种族","年龄"},{"工会"},{"兴趣"},{"声优"}};
        private final DisplayImageOptions displayImageOptions = new
                DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .showImageOnLoading(R.drawable.ic_launcher_background).build();
        public UnitProfileAdapter(Context context, DBUnitProfile profile, DBUnitData data, List<DBUnitComments> comments){
            this.profile = profile;
            this.data = data;
            this.comments = comments;
            mInflater = LayoutInflater.from(context);
            imageLoader = ImageLoader.getInstance();
        }

        @Override
        public int getCount(){
            int count = 9;
            if(comments != null){
                count += comments.size();
            }
            return count;
        }

        @Override
        public int getViewTypeCount(){
            return 4;
        }

        @Override
        public int getItemViewType(int position){
            if(position == 0) return TYPE_IMG_TEXT;
            if(position == 1 || position == 2 || (position > 5 && position < 9)) return TYPE_TEXT;
            if(position > 2 && position < 6) return TYPE_TEXT_TEXT;
            else return TYPE_SINGLE_TEXT;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            int itemType = getItemViewType(position);
            ViewHolderType1 holder1 = null;
            ViewHolderType2 holder2 = null;
            ViewHolderType3 holder3 = null;
            ViewHolderType4 holder4 = null;

            if(convertView == null){
                switch (itemType){
                    case TYPE_IMG_TEXT:
                        convertView = mInflater.inflate(R.layout.unit_detail_item1, parent, false);
                        holder1 = new ViewHolderType1();
                        holder1.textView = convertView.findViewById(R.id.unit_detail_description);
                        holder1.imageView = convertView.findViewById(R.id.unit_detail_image);
                        convertView.setTag(holder1);
                        break;
                    case TYPE_TEXT:
                        convertView = mInflater.inflate(R.layout.unit_detail_item2,parent,false);
                        holder2 = new ViewHolderType2();
                        holder2.content = convertView.findViewById(R.id.unit_detail_item2_content);
                        holder2.title = convertView.findViewById(R.id.unit_detail_item2_title);
                        convertView.setTag(holder2);
                        break;
                    case TYPE_TEXT_TEXT:
                        convertView = mInflater.inflate(R.layout.unit_detail_item3, parent, false);
                        holder3 = new ViewHolderType3();
                        holder3.title1 = convertView.findViewById(R.id.unit_detail_item3_title1);
                        holder3.title2 = convertView.findViewById(R.id.unit_detail_item3_title2);
                        holder3.content1 = convertView.findViewById(R.id.unit_detail_item3_content1);
                        holder3.content2 = convertView.findViewById(R.id.unit_detail_item3_content2);
                        convertView.setTag(holder3);
                        break;
                    case TYPE_SINGLE_TEXT:
                        convertView = mInflater.inflate(R.layout.unit_detail_item4, parent, false);
                        holder4 = new ViewHolderType4();
                        holder4.comment = convertView.findViewById(R.id.unit_comment);
                        convertView.setTag(holder4);
                        break;
                }
            }else{
                switch (itemType){
                    case TYPE_IMG_TEXT:
                        holder1 = (ViewHolderType1) convertView.getTag();
                        break;
                    case TYPE_TEXT:
                        holder2 = (ViewHolderType2) convertView.getTag();
                        break;
                    case TYPE_TEXT_TEXT:
                        holder3 = (ViewHolderType3) convertView.getTag();
                        break;
                    case TYPE_SINGLE_TEXT:
                        holder4 = (ViewHolderType4) convertView.getTag();
                }
            }

            switch (itemType){
                case TYPE_IMG_TEXT:
                    holder1.textView.setText(data.comment.replace("\\n", "\n"));
                    imageLoader.displayImage(Constant.unitImageUrl(profile.unit_id, 1), //TODO:如果设置为三星头像就是31.webp
                            new ImageViewAware(holder1.imageView,false),displayImageOptions);
                    break;
                case TYPE_TEXT:
                    if(position>8){
                        holder2.title.setEnabled(false);
                    }else {
                        holder2.title.setEnabled(true);
                        holder2.title.setText(title[position][0]);
                    }
                    switch (position){
                        case 1:
                            holder2.content.setText(profile.unit_name);
                            break;
                        case 2:
                            holder2.content.setText(profile.catch_copy);
                            break;
                        case 6:
                            holder2.content.setText(profile.guild);
                            break;
                        case 7:
                            holder2.content.setText(profile.favorite);
                            break;
                        case 8:
                            holder2.content.setText(profile.voice);
                            break;
                        default:
                            if(position > 8){
                                holder2.content.setText("");
                            }else{
                                holder2.content.setText("Unknown Error");
                            }
                    }
                    break;
                case TYPE_TEXT_TEXT:
                    holder3.title1.setText(title[position][0]);
                    holder3.title2.setText(title[position][1]);
                    switch (position){
                        case 3:
                            holder3.content1.setText(profile.height);
                            holder3.content2.setText(profile.weight);
                            break;
                        case 4:
                            holder3.content1.setText(profile.birth_month+" 月 "+profile.birth_day+" 日");
                            holder3.content2.setText(profile.blood_type);
                            break;
                        case 5:
                            holder3.content1.setText(profile.race);
                            holder3.content2.setText(profile.age);
                            break;
                        default:
                            Log.w("Detail Adapter","Unknown Error");
                    }
                    break;
                case TYPE_SINGLE_TEXT:
                    holder4.comment.setText(comments.get(position - 9).description.
                            replaceAll("\\\\n","\n"));
                    break;
            }

            return convertView;
        }

        @Override
        public Object getItem(int position){
            return 0;
        }

        @Override
        public long getItemId(int position){
            return position;
        }

        class ViewHolderType1 {
            ImageView imageView;
            TextView textView;
        }

        class ViewHolderType2 {
            TextView title;
            TextView content;
        }

        class ViewHolderType3 {
            TextView title1, title2;
            TextView content1, content2;
        }

        class ViewHolderType4 {
            TextView comment;
        }

    }
}
