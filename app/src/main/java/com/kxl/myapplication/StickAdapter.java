package com.kxl.myapplication;

import com.chad.library.adapter.base.BaseQuickAdapter;
import com.chad.library.adapter.base.BaseViewHolder;

import java.util.List;

/**
 * Create by atu on 2020/3/12
 */
public class StickAdapter extends BaseQuickAdapter<String, BaseViewHolder> {
    public StickAdapter(int layoutResId, List<String> dataList) {
        super(layoutResId,dataList);
    }

    @Override
    protected void convert(BaseViewHolder helper, String item) {
        helper.setText(R.id.tv_name,item);
    }

}
