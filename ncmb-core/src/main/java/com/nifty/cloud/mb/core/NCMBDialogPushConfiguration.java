package com.nifty.cloud.mb.core;

/**
 * 　プッシュ通知におけるダイアログ設定を管理するクラス
 * 
 */
public class NCMBDialogPushConfiguration {
	/**
	 * 表示形式　表示しない
	 */
	public static final int DIALOG_DISPLAY_NONE = 0x00;
	/**
	 * 表示形式　ダイアログ形式
	 */
	public static final int DIALOG_DISPLAY_DIALOG = 0x01;
	/**
	 * 表示形式　背景付ダイアログ
	 */
	public static final int DIALOG_DISPLAY_BACKGROUND = 0x02;
	/**
	 * 表示形式　オリジナルレイアウトダイアログ
	 */
	public static final int DIALOG_DISPLAY_ORIGINAL = 0x04;

	// 表示形式
	private int displayType;

	/**
	 * コンストラクタ<br>
	 * 表示形式に非表示を設定し、背景画像ファイルパスにnullを設定する
	 *
	 */
	public NCMBDialogPushConfiguration(){
		//デフォルト非表示
		this.displayType = DIALOG_DISPLAY_NONE;
	}

	/**
	 * コンストラクタ<br>
	 * 以下の表示形式を引数として設定することが可能<br>
	 *   ・DIALOG_DISPLAY_NONE<br>
	 *   ・DIALOG_DISPLAY_DIALOG<br>
	 *   ・DIALOG_DISPLAY_BACKGROUND<br>
	 *   ・DIALOG_DISPLAY_ORIGINAL
	 *
	 * @param displayType 表示形式
	 * @param filePath 背景画像ファイルパス
	 */
	public NCMBDialogPushConfiguration(int displayType, String filePath){
		this.displayType = displayType;
	}

	/**
	 * 表示形式を設定
	 *
	 * @param displayType 表示形式
	 */
	public void setDisplayType(int displayType){
		this.displayType = displayType;
	}

	/**
	 * 表示形式を取得
	 *
	 * @return 表示形式
	 */
	public int getDisplayType(){
		return this.displayType;
	}

}
