/*
 * Copyright 2017 FUJITSU CLOUD TECHNOLOGIES LIMITED All Rights Reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.nifty.cloud.mb.core;

/**
 * NCMBException is a class that defines the error to be returened from NIF Cloud mobile backend
 */
public class NCMBException extends Exception {

    private static final long serialVersionUID = 1L;

    private String code;

    /**
     * E000001 SDK 汎用エラー (not API)
     */
    public static final String GENERIC_ERROR = "E000001";

    /**
     * E100001 レスポンスシグネチャ不正
     */
    public static final String INVALID_RESPONSE_SIGNATURE = "E100001";

    /**
     * E400001 JSON形式不正
     */
    public static final String INVALID_JSON = "E400001";

    /**
     * E400002 型が不正
     */
    public static final String INVALID_TYPE = "E400002";

    /**
     * E400003 必須項目で未入力
     */
    public static final String REQUIRED = "E400003";

    /**
     * E400004 フォーマットが不正
     */
    public static final String INVALID_FORMAT = "E400004";

    /**
     * E400005 有効な値でない
     */
    public static final String NOT_EFFICIENT_VALUE = "E400005";

    /**
     * E400006 存在しない値
     */
    public static final String MISSING_VALUE = "E400006";

    /**
     * E401001 Header不正による認証エラー
     */
    public static final String INVALID_AUTH_HEADER = "E401001";

    /**
     * E401002 ID/Pass認証エラー
     */
    public static final String AUTH_FAILURE = "E401002";

    /**
     * E401003 OAuth認証エラー
     */
    public static final String OAUTH_FAILURE = "E401003";

    /**
     * E403001 ＡＣＬによるアクセス権なし
     */
    public static final String OPERATION_FORBIDDEN_BY_ACL = "E403001";

    /**
     * E403002 コラボレータ/管理者（サポート）権限なし
     */
    public static final String OPERATION_FORBIDDEN_BY_USER_TYPE = "E403002";

    /**
     * E403003 禁止されているオペレーション
     */
    public static final String OPERATION_FORBIDDEN = "E403003";

    /**
     * E403004 ワンタイムキー有効期限切れ
     */
    public static final String EXPIRED_ONETIME_KEY = "E403004";

    /**
     * E403005 設定不可の項目
     */
    public static final String INVALID_SETTING_NAME = "E403005";

    /**
     * E404001 該当データなし
     */
    public static final String DATA_NOT_FOUND = "E404001";

    /**
     * E404002 該当サービスなし
     */
    public static final String SERVICE_NOT_FOUND = "E404002";

    /**
     * E404003 該当フィールドなし
     */
    public static final String FIELD_NOT_FOUND = "E404003";

    /**
     * E409001 重複エラー<br>
     * 項目によって一意の内容が異なる<br>
     */
    public static final String DUPLICATE_VALUE = "E409001";

    /**
     * E413001 1ファイルあたりのサイズ上限エラー
     */
    public static final String FILE_TOO_LARGE = "E413001";

    /**
     * E429001 使用制限（APIコール数、PUSH通知数、ストレージ容量）超過
     */
    public static final String RESTRICTED = "E429001";

    /**
     * E500001 内部エラー
     */
    public static final String INTERNAL_SERVER_ERROR = "E500001";

    /**
     * E502001 ストレージエラー<br>
     * NIF Cloud ストレージでエラーが発生した場合のエラー
     */
    public static final String STORAGE_ERROR = "E502001";

    /**
     * コンストラクタ
     *
     * @param code    エラーコード
     * @param message エラーメッセージ
     */
    public NCMBException(String code, String message) {
        super(message);
        this.code = code;
    }

    /**
     * コンストラクタ
     *
     * @param cause   例外
     * @param message エラーメッセージ
     */
    public NCMBException(Throwable cause, String message) {
        super(message, cause);
        this.code = "";
    }

    /**
     * コンストラクタ
     *
     * @param cause 例外
     */
    public NCMBException(Throwable cause) {
        super(cause);
        this.code = "";
    }

    /**
     * 例外のコード番号を取得
     *
     * @return エラーコード
     */
    public String getCode() {
        return this.code;
    }

}
