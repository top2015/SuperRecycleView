package com.gwb.superrecycleview.ui.wedgit;

import android.content.Context;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.util.Log;
import android.util.SparseIntArray;
import android.view.View;
import android.view.ViewGroup;

public class FlowLayout extends ViewGroup {
    private SparseIntArray col_nums;
    private SparseIntArray col_withline;
    private static final String TAG = "FlowLayout";

    public FlowLayout(Context context) {
        this(context, null);
    }

    public FlowLayout(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public FlowLayout(Context context, @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int withMode = MeasureSpec.getMode(widthMeasureSpec);
        int heightMode = MeasureSpec.getMode(heightMeasureSpec);
        int withSize = MeasureSpec.getSize(widthMeasureSpec);
        int heightSize = MeasureSpec.getSize(heightMeasureSpec);

        int width = 0;
        int height = 0;

        int widthLine = 0;
        col_nums = new SparseIntArray();
        col_withline = new SparseIntArray();
        int colNums = 0;
        int rowNums = 0;
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            childView.measure(0, 0);
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight() + lp.topMargin + lp.bottomMargin;
            // TODO: 2018/6/11 换行
            if (widthLine + childWidth > withSize) {
                height += childHeight;
                width = Math.max(widthLine, width);
                col_withline.put(rowNums, widthLine);
                // TODO: 2018/6/11 这边要特别注意，要在获取最大值的后面
                widthLine = childWidth;
                Log.d(TAG, "onMeasure: 换行=" + height);
                col_nums.put(rowNums, colNums);
                colNums = 1;
                rowNums++;
            } else {
                Log.d(TAG, "onMeasure: widthLine=" + widthLine);
                widthLine += childWidth;
                colNums++;
            }
            if (i == getChildCount() - 1) {
                col_withline.put(rowNums, widthLine);
                col_nums.put(rowNums, colNums);
                width = Math.max(widthLine, width);
                height += childHeight;
                Log.d(TAG, "onMeasure: 最后一行=" + height);
            }
        }
        // TODO: 2018/6/19 考虑到padding的情况存在
        width += getPaddingLeft() + getPaddingRight();
        height += getPaddingTop() + getPaddingBottom();
        setMeasuredDimension(withMode == MeasureSpec.EXACTLY ? withSize : width
            , heightMode == MeasureSpec.EXACTLY ? heightSize : height);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        if (col_nums.size() == 0) {
            return;
        }
        Log.e("TTT", "FlowLayout->onLayout:" + col_nums.toString());
        Log.e("TTT", "FlowLayout->onLayout:" + col_withline.toString());
        int top = getPaddingTop();
        int left = getPaddingLeft();
        int width = getMeasuredWidth();
        //行数
        int row = 0;
        //记录总个数
        int nums = 0;
        int count = col_nums.get(row);
        int withline = col_withline.get(row);
        count = col_nums.get(row);
        int margin = (width - withline) / (2 * count);
        Log.e("TTT", "FlowLayout->onLayout:" + margin);
        for (int i = 0; i < getChildCount(); i++) {
            View childView = getChildAt(i);
            if (childView.getVisibility() == GONE) {
                continue;
            }
            MarginLayoutParams lp = (MarginLayoutParams) childView.getLayoutParams();
            int childWidth = childView.getMeasuredWidth() + lp.leftMargin + lp.rightMargin;
            int childHeight = childView.getMeasuredHeight();
            if ((i - nums) > count - 1) {
                //换行
                nums += col_nums.get(row);
                row++;
                withline = col_withline.get(row);
                count = col_nums.get(row);
                margin = (width - withline) / (2 * count);
                left = getPaddingLeft()+margin;
                top += childHeight + lp.topMargin + lp.bottomMargin;
            }

            int lc = left+lp.leftMargin;
            int tc = top + lp.topMargin;
            int rc = lc + childWidth +lp.rightMargin;
            int bc = tc + childHeight + lp.bottomMargin;
            childView.layout(lc, tc, rc, bc);
            left = rc + margin;

        }

    }

}
