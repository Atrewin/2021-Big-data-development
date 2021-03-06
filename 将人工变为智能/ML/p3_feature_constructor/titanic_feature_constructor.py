import os
import pandas as pd
import re
import numpy as np
from sklearn.ensemble import RandomForestClassifier
from sklearn.model_selection import train_test_split
from sklearn.preprocessing import OneHotEncoder

if __name__ == '__main__':
    dataset_path = os.path.join('../p2_data_preprocess', 'preprocess_data.csv')

    # 读取数据
    df = pd.read_csv(dataset_path)

    # 显示所有列
    pd.set_option('display.max_columns', None)

    # 前十条示例数据
    print(df.head(10))

    # 特征提取
    df['Title'] = df.get('Name').apply(lambda x: re.search(' ([A-Z][a-z]+)\.', x).group(1))

    # 统计计数
    print(df.get('Title').value_counts())

    # 定义 Title 社会地位字典，减少分类数
    title_dict = {
        "Capt": "Officer",
        "Col": "Officer",
        "Major": "Officer",
        "Dr": "Officer",
        "Rev": "Officer",
        "Jonkheer": "Royalty",
        "Don": "Royalty",
        "Sir": "Royalty",
        "Countess": "Royalty",
        "Dona": "Royalty",
        "Lady": "Royalty",
        "Mme": "Mrs",
        "Ms": "Mrs",
        "Mrs": "Mrs",
        "Mlle": "Miss",
        "Miss": "Miss",
        "Mr": "Mr",
        "Master": "Master"
    }

    df['Title'] = df.get('Title').map(title_dict)

    print(df.get('Title').value_counts())

    df.drop('Name', axis=1, inplace=True)

    print(df.info())
    df_num = df.select_dtypes(include=[np.number])
    df_cat = df.select_dtypes(exclude=[np.number])

    # 独热编码
    enc = OneHotEncoder(handle_unknown='ignore')
    cat_enc_data = enc.fit_transform(df_cat).toarray()
    df_cat_enc = pd.DataFrame(data=cat_enc_data, columns=enc.get_feature_names(df_cat.columns))
    print(df_cat_enc)

    # 合并数值类型和分类类型
    df = pd.merge(df_num, df_cat_enc, left_index=True, right_index=True)
    print(df.info())

    y = df.get('Survived')
    X = df.drop('Survived', axis=1)
    # 特征重要性
    rf = RandomForestClassifier()
    rf.fit(X, y)

    importance = dict(zip(X.columns, rf.feature_importances_))
    importance = sorted(importance.items(), key=lambda kv: (kv[1], kv[0]), reverse=True)
    print(importance)

    # 数据划分
    X_train, X_test, y_train, y_test = train_test_split(X, y, test_size=0.3)
    train_df = pd.merge(X_train, y_train, left_index=True, right_index=True)
    test_df = pd.merge(X_test, y_test, left_index=True, right_index=True)

    train_df.to_csv("train_data.csv", index=False)
    test_df.to_csv("test_data.csv", index=False)


