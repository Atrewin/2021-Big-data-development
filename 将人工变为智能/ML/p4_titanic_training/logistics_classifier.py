

import os
import joblib
import pandas as pd
from sklearn.ensemble import RandomForestClassifier


import pandas as pd
import statsmodels.api as sm
import pylab as pl
import numpy as np
from matplotlib import pyplot
from xgboost import plot_importance #显示特征重要性




if __name__ == '__main__':
    dataset_path = os.path.join('../p3_feature_constructor', 'train_data.csv')

    # 读取数据
    df = pd.read_csv(dataset_path)

    y = df.get('Survived')

    X = df.drop('Survived', axis=1)

    # 指定作为训练变量的列，不含目标列`admit`


    logit = sm.Logit(y, X)

    # 拟合模型
    result = logit.fit()

    model_path = os.path.join('../model', 'logit.pkl')

    joblib.dump(logit, model_path, compress=3)
