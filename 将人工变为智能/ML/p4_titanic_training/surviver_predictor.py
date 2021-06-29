import os
import joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier


if __name__ == '__main__':
    dataset_path = os.path.join('../p3_feature_constructor', 'train_data.csv')

    # 读取数据
    df = pd.read_csv(dataset_path)

    y = df.get('Survived')

    X = df.drop('Survived', axis=1)

    rf = RandomForestClassifier()

    # 拟合
    model = rf.fit(X, y)

    model_path = os.path.join('../model', 'rf.pkl')

    joblib.dump(model, model_path, compress=3)
