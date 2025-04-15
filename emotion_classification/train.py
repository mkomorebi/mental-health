import numpy as np
import matplotlib.pyplot as plt
import seaborn as sns
from tensorflow.keras.models import Sequential
from tensorflow.keras.layers import Conv2D, MaxPooling2D, GlobalAveragePooling2D, Dense, Dropout
from tensorflow.keras.optimizers import Adam
from sklearn.model_selection import train_test_split
from sklearn.metrics import classification_report, confusion_matrix
from tensorflow.keras.utils import to_categorical
import pickle

# ==========================
# 参数配置
# ==========================
n_mfcc = 40  # 与preprocess.py中保持一致

# ==========================
# 加载处理后的特征和标签
# ==========================
X = np.load('processed_data/X_pad.npy')  # shape: (样本数, 时间步, 特征数)
y = np.load('processed_data/y_categorical.npy')  # shape: (样本数, 类别数)

# 加载标签编码器
with open('processed_data/label_encoder.pkl', 'rb') as f:
    label_encoder = pickle.load(f)

print("X shape:", X.shape)
print("y shape:", y.shape)

# 添加通道维度，变为 (样本数, 时间步, 特征数, 1)
X = np.expand_dims(X, axis=-1)

# ==========================
# 划分训练集和测试集
# ==========================
X_train, X_test, y_train, y_test = train_test_split(
    X, y, test_size=0.2, random_state=42, stratify=y
)

print(f"\n训练集样本数: {X_train.shape[0]}")
print(f"测试集样本数: {X_test.shape[0]}")

# ==========================
# 构建 CNN 模型
# ==========================
model = Sequential()

model.add(Conv2D(32, kernel_size=(3, 3), activation='relu', 
                input_shape=(X.shape[1], n_mfcc, 1)))
model.add(MaxPooling2D(pool_size=(2, 2)))

model.add(Conv2D(64, kernel_size=(3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))

model.add(Conv2D(128, kernel_size=(3, 3), activation='relu'))
model.add(MaxPooling2D(pool_size=(2, 2)))

model.add(GlobalAveragePooling2D())
model.add(Dense(128, activation='relu'))
model.add(Dropout(0.3))
model.add(Dense(y.shape[1], activation='softmax'))

# ==========================
# 编译并训练模型
# ==========================
model.compile(optimizer=Adam(learning_rate=0.001),
              loss='categorical_crossentropy',
              metrics=['accuracy'])

print("\n🚀 正在训练模型 ...")
history = model.fit(
    X_train, y_train,
    epochs=10,
    batch_size=64,
    validation_data=(X_test, y_test),
    verbose=1
)

# ==========================
# 模型评估
# ==========================
print("\n🔍 模型评估结果:")

# 1. 使用model.evaluate评估测试集
test_loss, test_acc = model.evaluate(X_test, y_test, verbose=0)
print(f"✅ 测试集准确率：{test_acc:.4f}")
print(f"✅ 测试集损失：{test_loss:.4f}")

# 2. 预测测试集结果
y_pred = model.predict(X_test)
y_pred_classes = np.argmax(y_pred, axis=1)
y_true_classes = np.argmax(y_test, axis=1)

# 3. 分类报告
print("\n📊 分类报告:")
print(classification_report(y_true_classes, y_pred_classes, 
                          target_names=label_encoder.classes_))

# 4. 混淆矩阵
conf_mat = confusion_matrix(y_true_classes, y_pred_classes)
plt.figure(figsize=(10, 8))
sns.heatmap(conf_mat, annot=True, fmt='d', 
            xticklabels=label_encoder.classes_, 
            yticklabels=label_encoder.classes_,
            cmap='Blues')
plt.title('混淆矩阵')
plt.xlabel('预测标签')
plt.ylabel('真实标签')
plt.savefig('confusion_matrix.png')
plt.close()
print("📈 混淆矩阵已保存为 confusion_matrix.png")

# 5. 各类别预测数量统计图
plt.figure(figsize=(10, 6))
unique, counts = np.unique(y_pred_classes, return_counts=True)
plt.bar(label_encoder.classes_[unique], counts)
plt.title('各类别预测数量统计')
plt.xlabel('情感类别')
plt.ylabel('预测数量')
plt.savefig('prediction_distribution.png')
plt.close()
print("📊 各类别预测数量统计图已保存为 prediction_distribution.png")

# 6. 训练过程曲线
plt.figure(figsize=(12, 5))
plt.subplot(1, 2, 1)
plt.plot(history.history['accuracy'], label='训练准确率')
plt.plot(history.history['val_accuracy'], label='验证准确率')
plt.title('准确率曲线')
plt.xlabel('Epoch')
plt.ylabel('Accuracy')
plt.legend()

plt.subplot(1, 2, 2)
plt.plot(history.history['loss'], label='训练损失')
plt.plot(history.history['val_loss'], label='验证损失')
plt.title('损失曲线')
plt.xlabel('Epoch')
plt.ylabel('Loss')
plt.legend()
plt.savefig('training_history.png')
plt.close()
print("📉 训练过程曲线已保存为 training_history.png")

# ==========================
# 保存模型
# ==========================
model.save('cnn_model_split.keras') 
print("\n📦 模型已保存为 cnn_model_split.keras")