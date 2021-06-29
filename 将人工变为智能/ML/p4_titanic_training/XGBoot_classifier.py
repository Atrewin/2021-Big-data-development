import os
import joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier

from xgboost import XGBClassifier
from sklearn.datasets import load_svmlight_file #用于直接读取svmlight文件形式， 否则就需要使用xgboost.DMatrix(文件名)来读取这种格式的文件
from sklearn.metrics import accuracy_score
from matplotlib import pyplot
from xgboost import plot_importance #显示特征重要性




if __name__ == '__main__':
    dataset_path = os.path.join('../p3_feature_constructor', 'train_data.csv')

    # 读取数据
    df = pd.read_csv(dataset_path)

    y = df.get('Survived')

    X = df.drop('Survived', axis=1)

    num_round = 100
    bst1 = XGBClassifier(max_depth=2, learning_rate=1, n_estimators=num_round,  # 弱分类树太少的话取不到更多的特征重要性
                         silent=True, objective='binary:logistic')

    # 拟合
    model = bst1.fit(X, y)

    model_path = os.path.join('../model', 'bst1.pkl')

    joblib.dump(model, model_path, compress=3)

    plot_importance(bst1)#打印重要程度结果。
    pyplot.show()