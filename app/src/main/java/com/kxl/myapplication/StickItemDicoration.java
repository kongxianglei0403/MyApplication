package com.kxl.myapplication;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.text.TextPaint;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

/**
 * Create by atu on 2020/3/11
 */
public class StickItemDicoration extends RecyclerView.ItemDecoration {

    private Bitmap bitmap;
    private Paint.FontMetrics fontMetrics;
    private int wight;
    private int itemDecorationHeight;
    private Paint paint;
    private ObtainTextCallback callback;
    private float itemDecorationPadding;
    private TextPaint textPaint;
    private Rect text_rect=new Rect();
    private Paint paint2;

    public StickItemDicoration(Context context,ObtainTextCallback callback) {
        this.callback = callback;
        wight=context.getResources().getDisplayMetrics().widthPixels;
        paint=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint.setColor(context.getResources().getColor(R.color.colorPrimary));
        itemDecorationHeight=dp2px(context, 30);
        itemDecorationPadding=dp2px(context, 10);
        this.callback = callback;

        paint2=new Paint(Paint.ANTI_ALIAS_FLAG|Paint.DITHER_FLAG);
        paint2.setColor(Color.parseColor("#52ff0000"));

        textPaint = new TextPaint(Paint.ANTI_ALIAS_FLAG | Paint.DITHER_FLAG);
        textPaint.setColor(Color.WHITE);
        textPaint.setTextAlign(Paint.Align.LEFT);
        textPaint.setTextSize(dp2px(context, 25));
        fontMetrics = new Paint.FontMetrics();
        textPaint.getFontMetrics(fontMetrics);

        bitmap= BitmapFactory.decodeResource(context.getResources(), R.mipmap.ic_launcher_round);
        ScaleBitmap();
    }

    private void ScaleBitmap() {
        Matrix matrix = new Matrix();
        float scale = bitmap.getWidth() > itemDecorationHeight?Float.valueOf(itemDecorationHeight) / Float.valueOf(bitmap.getHeight()):Float.valueOf(bitmap.getHeight() )/ Float.valueOf(itemDecorationHeight);
        matrix.postScale(scale,scale);
        bitmap = Bitmap.createBitmap(bitmap,0,0,bitmap.getWidth(),bitmap.getWidth(),matrix,false);
    }

    @Override
    public void onDraw(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDraw(c, parent, state);
        int count = parent.getChildCount();
        for (int i = 0;i < count; i++){
            View view = parent.getChildAt(i);
            //①分割线不能和item重合 要放在getItemOffsets 为分割线腾出的位置上
            int top = view.getTop() - itemDecorationHeight;
            int bottom = top + itemDecorationHeight;

            int position = parent.getChildAdapterPosition(view);
            String text = callback.getText(position);
            textPaint.getTextBounds(text,0,text.length(),text_rect);
            if (isFirstInGroup(position)){
                c.drawRect(0,top,wight,bottom,paint);
                c.drawText(text,bitmap.getWidth() + itemDecorationPadding,bottom - fontMetrics.descent,textPaint);
                c.drawBitmap(bitmap,itemDecorationPadding,bottom - bitmap.getHeight(),paint);
            }
        }

    }

    @Override
    public void onDrawOver(@NonNull Canvas c, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.onDrawOver(c, parent, state);
        //② 就是在满足什么条件之后 分割线动  不满足的时候固定不动
        View child = parent.getChildAt(0);
        //如果第一个item的bottom <= 分割线的高度
        int pos = parent.getChildAdapterPosition(child);
        String content = callback.getText(pos);
        if (child.getBottom() <= itemDecorationHeight && isFirstInGroup(pos + 1)){// 分割线随着recyclerview向上滑动 而滑动
            c.drawRect(0,0,wight,child.getBottom(),paint);
            c.drawText(content,bitmap.getWidth() + itemDecorationPadding,child.getBottom() - fontMetrics.descent,textPaint);
            c.drawBitmap(bitmap,itemDecorationPadding,child.getBottom() - bitmap.getHeight(),paint);
        }
        else {
            c.drawRect(0,0,wight,itemDecorationHeight,paint);
            c.drawText(content,itemDecorationPadding + bitmap.getWidth(),itemDecorationHeight - fontMetrics.descent,textPaint);
            c.drawBitmap(bitmap,itemDecorationPadding,itemDecorationHeight - bitmap.getHeight(),paint);
        }
    }

    @Override
    public void getItemOffsets(@NonNull Rect outRect, @NonNull View view, @NonNull RecyclerView parent, @NonNull RecyclerView.State state) {
        super.getItemOffsets(outRect, view, parent, state);
        //① 因为分割线在在条目的上方 所以要在每个item的上方留一个分割线的高度
        //出了first item外 当前item的text 与下一条item的text值不一样 就需要腾出位置
        int position = parent.getChildAdapterPosition(view);
        if (isFirstInGroup(position)) outRect.top = itemDecorationHeight;
    }

    interface ObtainTextCallback{
        String getText(int position);
    }

    private int dp2px(Context context,int dp){
        float scale = context.getResources().getDisplayMetrics().density;
        return (int)(scale * dp + 0.5f);
    }

    private boolean isFirstInGroup(int position){
        if (position == 0) return true;
        String last = callback.getText(position - 1);
        String currt = callback.getText(position);
        return !currt.equals(last);
    }
}
