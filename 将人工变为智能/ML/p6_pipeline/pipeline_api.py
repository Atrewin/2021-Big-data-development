import os
import pandas as pd
from sklearn.ensemble import RandomForestClassifier
from sklearn.impute import SimpleImputer
from sklearn.metrics import precision_score, recall_score, f1_score, confusion_matrix, classification_report, \
    accuracy_score
from sklearn.pipeline import Pipeline
from sklearn.preprocessing import OneHotEncoder, StandardScaler
from sklearn_pandas import gen_features, DataFrameMapper

if __name__ == '__main__':
    # 显示所有行
    pd.set_option('display.max_rows', None)

    train_path = os.path.join('../dataset', 'titanic', 'train.csv')
    test_path = os.path.join('../dataset', 'titanic', 'test.csv')
    y_test_path = os.path.join('../dataset', 'titanic', 'y_test.csv')

    # 读取数据
    train_df = pd.read_csv(train_path)
    test_df = pd.read_csv(test_path)
    y_test_df = pd.read_csv(y_test_path)
    y_test = y_test_df.get('Survived')

    y = train_df.get('Survived')

    X = train_df.drop('Survived', axis=1)

    categorical_features = ['Sex', 'Embarked']
    numerical_features = ['Pclass', 'Age', 'SibSp', 'Parch', 'Fare']
    features_def = []
    if categorical_features and len(categorical_features) > 0:
        for feature in categorical_features:
            categorical_feature_def = gen_features(
                columns=[[feature]],
                classes=[
                    {'class': SimpleImputer, 'strategy': 'most_frequent'},
                    {'class': OneHotEncoder, 'handle_unknown': 'ignore'}
                ]
            )
            features_def = features_def + categorical_feature_def

    if numerical_features and len(numerical_features) > 0:
        for feature in numerical_features:
            numerical_feature_def = gen_features(
                columns=[[feature]],
                classes=[
                    {'class': SimpleImputer, 'strategy': 'mean'},
                    {'class': StandardScaler},
                ]
            )
            features_def = features_def + numerical_feature_def

    preprocess = ('Preprocess', DataFrameMapper(features_def, df_out=True))
    estimator = ('Estimator', RandomForestClassifier())

    steps = [preprocess, estimator]

    pipeline = Pipeline(steps=steps)

    model = pipeline.fit(X, y)

    y_pred = pipeline.predict(test_df)
    test_df['Survived'] = y_test
    test_df['prediction'] = y_pred


    # 查看预测结果
    print(f"预测结果:{test_df[['PassengerId', 'Survived', 'prediction']]}")

    # 评估
    # 准确率
    accuracy_score_value = accuracy_score(y_test, y_pred)
    print(f"准确率:{accuracy_score_value}")

    precision_score_value = precision_score(y_test, y_pred)
    print(f"精确率:{precision_score_value}")

    recall_score_value = recall_score(y_test, y_pred)
    print(f"召回率:{recall_score_value}")

    f1_score_value = f1_score(y_test, y_pred)
    print(f"f1值:{f1_score_value}")

    confusion_matrix_value = confusion_matrix(y_test, y_pred)
    print(f"混淆矩阵:{confusion_matrix_value}")

    report = classification_report(y_test, y_pred)
    print(f"分类报告:{report}")
