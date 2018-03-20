package com.tg.tgt.helper;

import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.Context;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.AccelerateInterpolator;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.hyphenate.easeui.utils.PhoneUtil;
import com.tg.tgt.R;

import java.util.ArrayList;

/**
 * @author yangyu
 *	功能描述：标题按钮上的弹窗（继承自PopupWindow）
 */
public class MenuPopup extends PopupWindow {
	private Activity mContext;

	//列表弹窗的间隔
	protected final int LIST_PADDING = 10;

	//实例化一个矩形
	private Rect mRect = new Rect();

	//坐标的位置（x、y）
	private final int[] mLocation = new int[2];

	//屏幕的宽度和高度
	private int mScreenWidth,mScreenHeight;

	//判断是否需要添加或更新列表子类项
	private boolean mIsDirty;

	//位置不在中心
	private int popupGravity = Gravity.NO_GRAVITY;

	//弹窗子类项选中时的监听
	private OnItemOnClickListener mItemOnClickListener;

	//定义列表对象
	private ListView mListView;

	//定义弹窗子类项列表
	private ArrayList<ActionItem> mActionItems = new ArrayList<ActionItem>();

	public MenuPopup(Context context){
		//设置布局的参数
		this(context, LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
	}

	public enum MenuType{
		WHITE,
		BLACK
	}
	public MenuPopup(Context context, int width, int height){
		this(context, MenuType.WHITE, width, height);
	}

	private int txColor;
	public MenuPopup(Context context, MenuType type, int width, int height){
		this.mContext = (Activity) context;

		//设置可以获得焦点
		setFocusable(true);
		//设置弹窗内可点击
		setTouchable(true);
		//设置弹窗外可点击
		setOutsideTouchable(true);

		//获得屏幕的宽度和高度
		mScreenWidth = PhoneUtil.getScreenWidth(mContext);
		mScreenHeight = PhoneUtil.getScreenHeight(mContext);

		//设置弹窗的宽度和高度
		setWidth(width);
		setHeight(height);

		setBackgroundDrawable(new ColorDrawable());

		int res = R.layout.title_popup;
		txColor = mContext.getResources().getColor(R.color.tx_black_1);
		if(MenuType.BLACK == type){
			res = R.layout.title_popup_black;
			txColor = mContext.getResources().getColor(R.color.white);
		}
		//设置弹窗的布局界面
		setContentView(LayoutInflater.from(mContext).inflate(res, null));

		initUI();
	}

	/**
	 * 初始化弹窗列表
	 */
	private void initUI(){
		mListView = (ListView) getContentView().findViewById(R.id.title_list);

		mListView.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> arg0, View arg1, int index,long arg3) {
				//点击子类项后，弹窗消失
				dismiss();

				if(mItemOnClickListener != null)
					mItemOnClickListener.onItemClick(mActionItems.get(index), index);
			}
		});
	}

	/**
	 * 显示弹窗列表界面
	 */
	public void show(View view){
		//获得点击屏幕的位置坐标
		view.getLocationOnScreen(mLocation);

		int negaTop = mContext.getResources().getDimensionPixelSize(R.dimen.negative_10dp);
		//把menu往上挪
		mLocation[1]+=negaTop;
		//设置矩形的大小
		mRect.set(mLocation[0], mLocation[1], mLocation[0] + view.getWidth(),mLocation[1] + view.getHeight());

		//判断是否需要添加或更新列表子类项
		if(mIsDirty){
			populateActions();
		}

		//显示弹窗的位置
		showAtLocation(view, popupGravity, mRect.right-mRect.left, mRect.bottom);
		backgroundAlpha(.5f);
		setOnDismissListener(new poponDismissListener());
	}

	/**
	 * 设置弹窗列表子项
	 */
	private void populateActions(){
		mIsDirty = false;

		//设置列表的适配器
		mListView.setAdapter(new BaseAdapter() {
			@Override
			public View getView(int position, View convertView, ViewGroup parent) {
				TextView textView = null;

				int gap = mContext.getResources().getDimensionPixelOffset(R.dimen.common_10dp);
				if(convertView == null){
					textView = new TextView(mContext);
					textView.setTextColor(txColor);
					textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, mContext.getResources().getDimensionPixelSize(R.dimen.tx_3));
					//设置文本居中
					textView.setGravity(Gravity.CENTER);
					//设置文本域的范围
					textView.setPadding(gap, gap, gap, gap);
					//设置文本在一行内显示（不换行）
					textView.setSingleLine(true);
				}else{
					textView = (TextView) convertView;
				}

				ActionItem item = mActionItems.get(position);

				//设置文本文字
				textView.setText(item.mTitle);
				//设置文字与图标的间隔
				textView.setCompoundDrawablePadding(gap);
				//设置在文字的左边放一个图标
				textView.setCompoundDrawablesWithIntrinsicBounds(item.mDrawable, null , null, null);

				return textView;
			}

			@Override
			public long getItemId(int position) {
				return position;
			}

			@Override
			public Object getItem(int position) {
				return mActionItems.get(position);
			}

			@Override
			public int getCount() {
				return mActionItems.size();
			}
		}) ;
	}

	/**
	 * 添加子类项
	 */
	public void addAction(ActionItem action){
		if(action != null){
			mActionItems.add(action);
			mIsDirty = true;
		}
	}

	/**
	 * 清除子类项
	 */
	public void cleanAction(){
		if(mActionItems.isEmpty()){
			mActionItems.clear();
			mIsDirty = true;
		}
	}

	/**
	 * 根据位置得到子类项
	 */
	public ActionItem getAction(int position){
		if(position < 0 || position > mActionItems.size())
			return null;
		return mActionItems.get(position);
	}

	/**
	 * 设置监听事件
	 */
	public void setItemOnClickListener(OnItemOnClickListener onItemOnClickListener){
		this.mItemOnClickListener = onItemOnClickListener;
	}

	/**
	 * @author yangyu
	 *	功能描述：弹窗子类项按钮监听事件
	 */
	public static interface OnItemOnClickListener{
		public void onItemClick(ActionItem item , int position);
	}

		ValueAnimator bgAnim;
	/**
	 * 设置添加屏幕的背景透明度
	 * @param bgAlpha
	 */
	public void backgroundAlpha(float bgAlpha)
	{
		if(bgAnim!=null)
			bgAnim.cancel();
		final Window window = mContext.getWindow();
		final WindowManager.LayoutParams lp = window.getAttributes();
		bgAnim = ValueAnimator.ofFloat(lp.alpha, bgAlpha);
		bgAnim.setDuration(150);
		bgAnim.setInterpolator(new AccelerateInterpolator());
		bgAnim.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
			@Override
			public void onAnimationUpdate(ValueAnimator animation) {
				float animatedValue = (float) animation.getAnimatedValue();
				lp.alpha = animatedValue; //0.0-1.0
				if(animatedValue == 1){
					window.clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				}else {
					window.addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);
				}
			}
		});
		bgAnim.start();

	}

	class poponDismissListener implements PopupWindow.OnDismissListener{
		@Override
		public void onDismiss() {
			//Log.v("List_noteTypeActivity:", "我是关闭事件");
			backgroundAlpha(1f);
		}
	}
}
