package com.forms.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.support.v7.widget.AppCompatEditText;
import android.text.Editable;
import android.text.Selection;
import android.text.Spannable;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;

import com.forms.xxxapp.R;

/**
 * 带清空图标的EditText
 * 
 * @author wujunliu
 *
 */
public class ClearEditText extends AppCompatEditText implements OnFocusChangeListener, TextWatcher {

	/**
	 * 删除按钮的引用
	 */
	private Drawable mClearDrawable;
	//输入表情前EditText中的文本  
    private String inputAfterText;  
    //是否重置了EditText的内容  
    private boolean resetText; 
    /**
	 * 控件是否有焦点
	 */
	private boolean hasFoucs;

	public ClearEditText(Context context) {
		this(context, null);  
	}

	public ClearEditText(Context context, AttributeSet attrs) {
		// 这里构造方法也很重要，不加这个很多属性不能再XML里面定义
		this(context, attrs, android.R.attr.editTextStyle);  
	}

	public ClearEditText(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	@SuppressWarnings("deprecation")
	private void init() {
		// 获取EditText的DrawableRight,假如没有设置我们就使用默认的图片
		mClearDrawable = getCompoundDrawables()[2];
		if (mClearDrawable == null) {
			// throw new NullPointerException("You can add drawableRight
			// attribute in XML");
			mClearDrawable = getResources().getDrawable(R.mipmap.ic_delete);
		}

		mClearDrawable.setBounds(0, 0, mClearDrawable.getIntrinsicWidth(), mClearDrawable.getIntrinsicHeight());
		// 默认设置隐藏图标
		setClearIconVisible(false);
		// 设置焦点改变的监听
		setOnFocusChangeListener(this);
		// 设置输入框里面内容发生改变的监听
		addTextChangedListener(this);
	}

	/**
	 * 因为我们不能直接给EditText设置点击事件，所以我们用记住我们按下的位置来模拟点击事件 当我们按下的位置 在 EditText的宽度 -
	 * 图标到控件右边的间距 - 图标的宽度 和 EditText的宽度 - 图标到控件右边的间距之间我们就算点击了图标，竖直方向就没有考虑
	 */
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		if (event.getAction() == MotionEvent.ACTION_UP) {
			if (getCompoundDrawables()[2] != null) {

				boolean touchable = event.getX() > (getWidth() - getTotalPaddingRight())
						&& (event.getX() < ((getWidth() - getPaddingRight())));

				if (touchable) {
					this.setText("");
				}
			}
		}

		return super.onTouchEvent(event);
	}

	/**
	 * 当ClearEditText焦点发生变化的时候，判断里面字符串长度设置清除图标的显示与隐藏
	 */
	@Override
	public void onFocusChange(View v, boolean hasFocus) {
		this.hasFoucs = hasFocus;
		if (hasFocus) {
			setClearIconVisible(getText().length() > 0);
		} else {
			setClearIconVisible(false);
		}
	}

	/**
	 * 设置清除图标的显示与隐藏，调用setCompoundDrawables为EditText绘制上去
	 * 
	 * @param visible
	 */
	protected void setClearIconVisible(boolean visible) {
		Drawable right = visible ? mClearDrawable : null;
		setCompoundDrawables(getCompoundDrawables()[0], getCompoundDrawables()[1], right, getCompoundDrawables()[3]);
	}

	/**
	 * 当输入框里面内容发生变化的时候回调的方法
	 */
	@Override
	public void onTextChanged(CharSequence s, int start, int before, int count) {
		if (hasFoucs) {
			setClearIconVisible(s.length() > 0);
		}
		if (!resetText) {  
			if (count - before >= 2) {// 表情符号的字符长度最小为2
				CharSequence input = s.subSequence(start + before, start + count);
				if (containsEmoji(input.toString())) {
					resetText = true;
					//ToastUtils.show(mContext, "不支持输入Emoji表情符号", Toast.LENGTH_SHORT);
					// 是表情符号就将文本还原为输入表情符号之前的内容
					setText(inputAfterText);
					CharSequence text = getText();
					if (text instanceof Spannable) {
						Spannable spanText = (Spannable) text;
						Selection.setSelection(spanText, text.length());
					}
				}
			}
        } else {  
            resetText = false;  
        }  
	}

	@Override
	public void beforeTextChanged(CharSequence s, int start, int count, int after) {
		if (!resetText) {  
            getSelectionEnd();  
            // 这里用s.toString()而不直接用s是因为如果用s，  
            // 那么，inputAfterText和s在内存中指向的是同一个地址，s改变了，  
            // inputAfterText也就改变了，那么表情过滤就失败了  
            inputAfterText= s.toString();  
        } 
	}

	@Override
	public void afterTextChanged(Editable s) {

	}

	/**
	 * 设置晃动动画
	 */
	public void setShakeAnimation(int counts) {
		this.setAnimation(shakeAnimation(counts));
	}

	/**
	 * 晃动动画
	 * 
	 * @param counts
	 *            1秒钟晃动多少下
	 * @return
	 */
	public static Animation shakeAnimation(int counts) {
		Animation translateAnimation = new TranslateAnimation(0, 10, 0, 0);
		translateAnimation.setInterpolator(new CycleInterpolator(counts));
		translateAnimation.setDuration(1000);
		return translateAnimation;
	}
	
	/** 
     * 检测是否有emoji表情 
     * 
     * @param source 
     * @return 
     */  
    public static boolean containsEmoji(String source) {  
        int len = source.length();  
        for (int i = 0; i < len; i++) {  
            char codePoint = source.charAt(i);  
            if (!isEmojiCharacter(codePoint)) { //如果不能匹配,则该字符是Emoji表情  
                return true;  
            }  
        }  
        return false;  
    }  
  
    /** 
     * 判断是否是Emoji 
     * 
     * @param codePoint 比较的单个字符 
     * @return 
     */  
    private static boolean isEmojiCharacter(char codePoint) {  
        return (codePoint == 0x0) || (codePoint == 0x9) || (codePoint == 0xA) ||  
                (codePoint == 0xD) || ((codePoint >= 0x20) && (codePoint <= 0xD7FF)) ||  
                ((codePoint >= 0xE000) && (codePoint <= 0xFFFD)) || ((codePoint >= 0x10000)  
                && (codePoint <= 0x10FFFF));  
    }  

}
