import pandas as pd
from sklearn.model_selection import train_test_split
from sklearn.linear_model import LogisticRegression
import os

if __name__ == '__main__':
    dataset_path = os.path.join("../dataset", 'Iris.csv')

    # 读取iris.csv数据
    df = pd.read_csv(dataset_path)

    # 前十条示例数据
    print(df.head(10))

    # 移除id字段
    df.drop(labels=['Id'], axis=1, inplace=True)

    target = df.get('Species')

    # 训练数据集
    train = df.drop(labels=['Species'], axis=1)

    # 数据划分
    X_train, X_test, y_train, y_test = train_test_split(train, target, test_size=0.3)

    # 逻辑回归
    lr = LogisticRegression()

    # 拟合
    model = lr.fit(X_train, y_train)

    y_pred = model.predict(X_test)

    # 查看预测结果
    print(f"预测结果:{y_pred}")
